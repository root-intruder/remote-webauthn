export enum EventType {
    sessionAssignmentEvent = 'session_assignment',
    authenticatorJoinEvent = 'authenticator_join',
    responseSubmissionEvent = 'response_submission'
}

/**
 * Represents an event message sent from proxy server to client.
 */
export interface EventMessage {
    type: EventType;
}

/**
 * Represents an event fired by proxy when new session was assigned.
 */
export interface SessionAssignmentEvent extends EventMessage {
    session_id: string;
}

/**
 * Represents an event fired by proxy when response was submitted.
 */
export interface ResponseSubmissionEvent extends EventMessage {
    payload: string;
}
