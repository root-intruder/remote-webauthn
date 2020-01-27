package space.alpath.rwa.authenticator.android.rwa.message.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents decrypted event message payload from server to client.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "action")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateRequestPayload.class, name = "create"),
        @JsonSubTypes.Type(value = RequestRequestPayload.class, name = "request")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestPayload {
    private final String action;

    /**
     * Creates new decrypted event message payload.
     *
     * @param action action to perform (create / get)
     */
    public RequestPayload(@JsonProperty(value = "action", required = true) final String action) {
        this.action = action;
    }

    /**
     * Returns the action to perform (create / get).
     *
     * @return action to perform (create / get)
     */
    public String getAction() {
        return action;
    }
}
