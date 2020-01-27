package space.alpath.rwa.authenticator.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import space.alpath.rwa.authenticator.android.MainActivity
import space.alpath.rwa.authenticator.android.R
import space.alpath.rwa.authenticator.android.rwa.Tunnel
import space.alpath.rwa.authenticator.android.rwa.message.request.CreateRequestPayload
import space.alpath.rwa.authenticator.android.rwa.message.request.RequestRequestPayload
import space.alpath.rwa.authenticator.android.rwa.message.response.CreateResponsePayload
import space.alpath.rwa.authenticator.android.rwa.message.response.RequestResponsePayload
import webauthnkit.core.client.WebAuthnClient
import webauthnkit.core.data.GetAssertionResponse
import webauthnkit.core.data.MakeCredentialResponse

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        log(root, "Starting up...")
        val deepLinkUrl = (activity as MainActivity).deepLinkUrl
        if (deepLinkUrl != null) {
            log(root, "Invocation via deep link: $deepLinkUrl")

            val deepLinkComponents = deepLinkUrl.split(';')
            if (deepLinkComponents.size != 4) {
                log(root, "Deep link has unexpected arg count, aborting...")
                return root
            }

            // Extracting components from qr url
            val serverUrl = deepLinkComponents[0]
            val sessionId = deepLinkComponents[1]
            val symmetricKey = deepLinkComponents[2]
            val initialPayloadHash = deepLinkComponents[3]

            val client = OkHttpClient()
            val proxyUrl = String.format("%s/%s", serverUrl, sessionId);

            // Create tunnel, listen for event messages
            log(root, "Initializing tunnel...")
            val tunnel = Tunnel(client, symmetricKey, initialPayloadHash)
            tunnel.requestSubmissionListener = {
                when (it) {
                    is CreateRequestPayload -> {
                        val pkOptions = it.options.publicKey.toWebAuthnKit()
                        val c = WebAuthnClient.create(
                            activity!!,
                            pkOptions.rp.id!!,
                            (activity!! as MainActivity).consentUI!!
                        )

                        var webAuthnResponse: MakeCredentialResponse? = null
                        runBlocking {
                            webAuthnResponse = c.create(pkOptions)
                        }

                        tunnel.send("forward_response", CreateResponsePayload(webAuthnResponse))
                    }
                    is RequestRequestPayload -> {
                        val pkOptions = it.options.publicKey.toWebAuthnKit()
                        val c = WebAuthnClient.create(
                            activity!!,
                            pkOptions.rpId!!,
                            (activity!! as MainActivity).consentUI!!
                        )

                        var webAuthnResponse: GetAssertionResponse? = null
                        runBlocking {
                            webAuthnResponse = c.get(pkOptions)
                        }

                        tunnel.send("forward_response", RequestResponsePayload(webAuthnResponse))
                    }
                }
            }

            log(root, "Establishing connection to tunnel...")
            tunnel.connect(proxyUrl)
        }

        return root
    }

    private fun log(view: View, action: String) {
        val previousLog = view.findViewById<TextView>(R.id.logTextView).text
        view.findViewById<TextView>(R.id.logTextView).text = "$previousLog\n$action"
    }
}