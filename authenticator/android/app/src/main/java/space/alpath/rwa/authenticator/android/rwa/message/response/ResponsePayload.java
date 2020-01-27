package space.alpath.rwa.authenticator.android.rwa.message.response;

/**
 * Represents decrypted request message payload from client to server.
 */
public class ResponsePayload {
    private final String action;

    /**
     * Creates new decrypted request message payload.
     *
     * @param action action to perform on server
     */
    public ResponsePayload(final String action) {
        this.action = action;
    }

    /**
     * Returns the action to perform on server
     *
     * @return action to perform on server
     */
    public String getAction() {
        return action;
    }
}
