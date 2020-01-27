package space.alpath.rwa.authenticator.android.rwa.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a request message sent from client to server.
 */
public final class RequestMessage {
    private final String op, payload;

    /**
     * Creates a new request message.
     *
     * @param op      request message opcode
     * @param payload encrypted payload as base64
     */
    public RequestMessage(@JsonProperty(value = "op", required = true) final String op,
                          @JsonProperty(value = "payload") final String payload) {
        this.op = op;
        this.payload = payload;
    }

    /**
     * Returns opcode.
     *
     * @return opcode
     */
    public String getOp() {
        return op;
    }

    /**
     * Returns encrypted payload as base64.
     *
     * @return encrypted payload as base64
     */
    public String getPayload() {
        return payload;
    }
}
