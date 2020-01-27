package space.alpath.rwa.authenticator.android.rwa.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an event message sent from server to client.
 */
public final class EventMessage {
    private final String type, payload;

    /**
     * Creates new event message.
     *
     * @param type    event message type
     * @param payload encrypted event message payload as base64
     */
    public EventMessage(@JsonProperty(value = "type", required = true) final String type,
                        @JsonProperty(value = "payload") final String payload) {
        this.type = type;
        this.payload = payload;
    }

    /**
     * Returns event message type.
     *
     * @return event message type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns encrypted event message payload as base64.
     *
     * @return encrypted event message payload as base64
     */
    public String getPayload() {
        return payload;
    }
}
