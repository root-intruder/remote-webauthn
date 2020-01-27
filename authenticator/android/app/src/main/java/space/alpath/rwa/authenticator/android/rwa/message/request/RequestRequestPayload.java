package space.alpath.rwa.authenticator.android.rwa.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import space.alpath.rwa.authenticator.android.rwa.credoptions.CredentialRequestOptions;

/**
 * Represents request (request message) payload for credentials get operation.
 */
public final class RequestRequestPayload extends RequestPayload {
    private final CredentialRequestOptions options;

    /**
     * Creates new credentials get request payload.
     *
     * @param options credentials get options
     */
    public RequestRequestPayload(@JsonProperty(value = "action") final String action,
                                 @JsonProperty(value = "options", required = true) CredentialRequestOptions options) {
        super("request");
        this.options = options;
    }

    /**
     * Returns credentials get options.
     *
     * @return credentials get options
     */
    public CredentialRequestOptions getOptions() {
        return options;
    }
}
