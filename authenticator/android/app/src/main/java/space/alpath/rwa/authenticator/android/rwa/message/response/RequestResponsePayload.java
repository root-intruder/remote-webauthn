package space.alpath.rwa.authenticator.android.rwa.message.response;

/**
 * Represents answer (event message) payload for credentials get operation.
 */
public final class RequestResponsePayload extends ResponsePayload {
    private final Object response;

    /**
     * Creates new credentials get answer payload.
     *
     * @param response WebAuthn credentials get answer object
     */
    public RequestResponsePayload(final Object response) {
        super("request");
        this.response = response;
    }

    /**
     * Returns WebAuthn credentials get answer object.
     *
     * @return WebAuthn credentials get answer object
     */
    public Object getResponse() {
        return response;
    }
}
