package space.alpath.rwa.authenticator.android.rwa.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import space.alpath.rwa.authenticator.android.rwa.credoptions.CredentialCreationOptions;

/**
 * Represents request (request message) payload for credentials create operation.
 */
public final class CreateRequestPayload extends RequestPayload {
    private final CredentialCreationOptions options;

    /**
     * Creates new credentials create request payload.
     *
     * @param options credentials create options
     */
    public CreateRequestPayload(@JsonProperty(value = "options", required = true) CredentialCreationOptions options) {
        super("create");
        this.options = options;
    }

    /**
     * Returns credentials create options.
     *
     * @return credentials create options
     */
    public CredentialCreationOptions getOptions() {
        return options;
    }
}
