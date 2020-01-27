/**
 * Represents the request payload type.
 */
export enum RequestAction {
    create = 'create',
    request = 'request'
}

/**
 * Represents decrypted payload from either RequestMessage or ResponseSubmissionEvent.
 */
export interface RequestPayload {
    action: RequestAction;
}

/**
 * Represents decrypted payload shipped in RequestMessage from client to proxy.
 */
export interface CreateRequestPayload extends RequestPayload {
    options: CredentialCreationOptions;
}

/**
 * Represents decrypted payload shipped in ResponseSubmissionEvent from proxy to client.
 */
export interface RequestRequestPayload extends RequestPayload {
    options: CredentialRequestOptions;
}