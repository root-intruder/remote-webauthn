package space.alpath.rwa.authenticator.android.rwa.message.response;

/**
 * Represents answer (event message) payload for credentials create operation.
 */
public final class CreateResponsePayload extends ResponsePayload {
    private final Object response;

    /**
     * Creates new credentials create answer payload.
     *
     * @param response WebAuthn credentials create answer object
     */
    public CreateResponsePayload(final Object response) {
        super("create");
        this.response = response;
    }

    /**
     * Returns WebAuthn credentials create answer object.
     *
     * @return WebAuthn credentials create answer object
     */
    public Object getResponse() {
        return response;
    }
}
