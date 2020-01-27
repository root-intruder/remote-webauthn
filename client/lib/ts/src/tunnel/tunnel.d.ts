import { EventMessage } from '../message/event-message.model';
/**
 * Represents a connection with a Remote WebAuthn proxy server. Handles all related logic.
 */
export declare class Tunnel {
    private readonly serverUrl;
    private key?;
    private keyString?;
    private payloadHash?;
    private sessionId?;
    private socket?;
    private sessionAssignmentEventHandler?;
    private responseSubmissionEventHandler?;
    authenticatorJoinEventHandler?: (data: EventMessage) => void;
    /**
     * Initialises new tunnel instance.
     * @param serverUrl proxy server url
     */
    constructor(serverUrl: string);
    /**
     * Connects to previously defined proxy url. Also generates random symmetric encryption key.
     * @return sessionId obtained from proxy
     */
    connect(): Promise<string>;
    /**
     * Sends given RequestMessage over established WebSocket tunnel.
     * @param request RequestMessage to send
     */
    private sendRequest;
    /**
     * Returns signature for given WebAuthn credentialsCreate request.
     * @param options WebAuthn request
     * @return WebAuthn response
     */
    credentialsCreate(options: {}): Promise<{}>;
    /**
     * Returns signature for given WebAuthn credentialsGet request.
     * @param options WebAuthn request
     * @return WebAuthn response
     */
    credentialsGet(options: {}): Promise<{}>;
    /**
     * Returns url for qr code generation. May be null if sessionId, key or payloadHash not set.
     * @param baseURL url prefix
     * @return url for qr code generation
     */
    getQrUrl(baseURL: string): string | undefined;
}
