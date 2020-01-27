package message

type RequestOp string

// Represents the request message opcode
const (
	ForwardRequestRequest  RequestOp = "forward_request"
	ForwardResponseRequest RequestOp = "forward_response"
)

// Represents a request message
type Request struct {
	Op      RequestOp `json:"op"`
	Payload string    `json:"payload"`
}
