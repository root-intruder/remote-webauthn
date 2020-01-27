package space.alpath.rwa.authenticator.android.rwa.message

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

/**
 * Represents a Scarlet WebSocket service receiving events and sending out requests.
 */
interface MessageService {
    /**
     * Fires whenever new web socket status messages arrive.
     */
    @Receive
    fun observeWebSocketEvent(): Flowable<WebSocket.Event>

    /**
     * Fires whenever new event messages arrive.
     */
    @Receive
    fun observeEventMessages(): Flowable<EventMessage>

    /**
     * Sends request message over WebSocket tunnel to proxy.
     * @param request request to send over tunnel
     */
    @Send
    fun sendRequest(request: RequestMessage)
}