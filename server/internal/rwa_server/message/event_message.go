package message

type eventType string

const (
	sessionAssignmentEventType  eventType = "session_assignment"
	authenticatorJoinEventType  eventType = "authenticator_join"
	requestSubmissionEventType  eventType = "request_submission"
	responseSubmissionEventType eventType = "response_submission"
)

type Event interface{}

// Represents an event message
type event struct {
	Type eventType `json:"type"`
}

// Represents a session assignment event message
type sessionAssignmentEvent struct {
	event
	SessionId string `json:"session_id"`
}

// Represents a request submisssion event message
type requestSubmissionEvent struct {
	event
	Payload string `json:"payload"`
}

// Represents a response submission event message
type responseSubmissionEvent struct {
	event
	Payload string `json:"payload"`
}

// Initializes a new session assignment event message
func NewSessionAssignmentEvent(sessionId string) Event {
	return &sessionAssignmentEvent{
		event{sessionAssignmentEventType},
		sessionId,
	}
}

// Initializes a new authenticator join event message
func NewAuthenticatorJoinEvent() Event {
	return &event{authenticatorJoinEventType}
}

// Initializes a new request submisssion event message
func NewRequestSubmissionEvent(payload string) Event {
	return &requestSubmissionEvent{
		event{requestSubmissionEventType},
		payload,
	}
}

// Initializes a new response submisssion event message
func NewResponseSubmissionEvent(payload string) Event {
	return &responseSubmissionEvent{
		event{responseSubmissionEventType},
		payload,
	}
}
