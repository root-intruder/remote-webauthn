package space.alpath.rwa.authenticator.android.rwa

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.jackson.JacksonMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import space.alpath.rwa.authenticator.android.rwa.message.MessageService
import space.alpath.rwa.authenticator.android.rwa.message.RequestMessage
import space.alpath.rwa.authenticator.android.rwa.message.request.RequestPayload
import space.alpath.rwa.authenticator.android.rwa.message.response.ResponsePayload
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Represents a connection with a Remote WebAuthn proxy.
 */
class Tunnel(private val client: OkHttpClient, private val encodedKey: String, private val encodedPayloadHash: String) {
    var requestSubmissionListener: ((RequestPayload?) -> Unit)? = null
    var service: MessageService? = null

    /**
     * Connects to the proxy with given url.
     * @param proxy to connect to
     */
    @SuppressLint("CheckResult")
    fun connect(url: String) {
        val socket = Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(url))
            .addMessageAdapterFactory(JacksonMessageAdapter.Factory())
            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
            .build()

        val service = socket.create<MessageService>()
        this.service = service

        service.observeWebSocketEvent().subscribe { event ->
            Log.i("WebSocketStatus", event.toString())
        }

        service.observeEventMessages().subscribe { message ->
            when (message.type) {
                "request_submission" -> this.requestSubmissionListener?.invoke(
                    this.decryptPayload(message.payload)
                )
            }
        }
    }

    /**
     * Decrypts request message payload. Uses symmetric key and hash from class instance.
     * @param input encrypted payload in base64 format
     * @return decrypted RequestPayload, may be null if mapping error occurs
     */
    private fun decryptPayload(input: String): RequestPayload? {
        val ciphertextWithIv = Base64.decode(input, Base64.DEFAULT)
        val key = SecretKeySpec(Base64.decode(encodedKey, Base64.DEFAULT), "AES")
        val iv = IvParameterSpec(ciphertextWithIv, 0, 16)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val payloadBytes = cipher.doFinal(ciphertextWithIv, 16, ciphertextWithIv.size - 16)

        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(ciphertextWithIv)

        if (!Arrays.equals(digest.digest(), Base64.decode(encodedPayloadHash, Base64.DEFAULT)))
            throw Exception("Hash of submitted payload is not correct")

        return ObjectMapper().readValue(payloadBytes, RequestPayload::class.java)
    }

    /**
     * Encrypts event message payload. Uses symmetric key from class instance.
     * @param input decrypted RequestPayload
     * @return encrypted payload in base64 format
     */
    private fun encryptPayload(payload: ResponsePayload): String {
        val payloadBytes = ObjectMapper().writeValueAsBytes(payload)
        val key = SecretKeySpec(Base64.decode(encodedKey, Base64.DEFAULT), "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertextWithIv = cipher.iv + cipher.doFinal(payloadBytes)

        return Base64.encodeToString(ciphertextWithIv, Base64.DEFAULT)
    }

    /**
     * Encrypts and sends event message over WebSocket tunnel.
     * @param op event message opcode
     * @param payload decrypted ResponsePayload
     */
    fun send(op: String, payload: ResponsePayload) {
        if (service == null)
            throw Exception("WebSocket service is not yet initialized")

        service!!.sendRequest(RequestMessage(op, encryptPayload(payload)))
    }
}