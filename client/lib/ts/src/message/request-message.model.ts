export enum RequestOp {
    forwardRequest = 'forward_request'
}

/**
 * Represents a request from client to proxy.
 */
export interface RequestMessage {
    op: RequestOp;
    payload: string;
}
