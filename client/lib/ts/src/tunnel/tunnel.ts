import {RequestMessage, RequestOp} from '../message/request-message.model';
import {EventMessage, EventType, ResponseSubmissionEvent, SessionAssignmentEvent} from '../message/event-message.model';
import {CreateRequestPayload, RequestAction, RequestRequestPayload} from '../message/request-payload.model';
import {
    decryptPayload,
    encodeKey,
    encryptPayload,
    generateKey,
    generatePayloadHash
} from '../credentials-container/cc-pk-cryptor';

/**
 * Represents a connection with a Remote WebAuthn proxy server. Handles all related logic.
 */
export class Tunnel {
    private readonly serverUrl: string;
    private key?: CryptoKey;
    private keyString?: string;
    private payloadHash?: string;
    private sessionId?: string;

    private socket?: WebSocket;
    private sessionAssignmentEventHandler?: (data: SessionAssignmentEvent) => void;
    private responseSubmissionEventHandler?: (data: ResponseSubmissionEvent) => void;

    public authenticatorJoinEventHandler?: (data: EventMessage) => void;

    /**
     * Initialises new tunnel instance.
     * @param serverUrl proxy server url
     */
    constructor(serverUrl: string) {
        this.serverUrl = serverUrl;
    }

    /**
     * Connects to previously defined proxy url. Also generates random symmetric encryption key.
     * @return sessionId obtained from proxy
     */
    public async connect(): Promise<string> {
        this.key = await generateKey();
        this.keyString = await encodeKey(this.key);

        this.socket = new WebSocket(this.serverUrl);
        this.socket.onmessage = msg => {
            const event: EventMessage = JSON.parse(msg.data);
            switch (event.type) {
                case EventType.sessionAssignmentEvent:
                    if (this.sessionAssignmentEventHandler) {
                        const sessionAssignmentEvent = event as SessionAssignmentEvent;
                        this.sessionId = sessionAssignmentEvent.session_id;

                        this.sessionAssignmentEventHandler(sessionAssignmentEvent);
                        this.sessionAssignmentEventHandler = undefined;
                    } else {
                        console.warn('Missing handler for sessionAssignmentEvent');
                    }
                    break;
                case EventType.authenticatorJoinEvent:
                    if (this.authenticatorJoinEventHandler) {
                        this.authenticatorJoinEventHandler(event);
                        this.authenticatorJoinEventHandler = undefined;
                    } else {
                        console.info('No handler for authenticatorJoinEvent');
                    }
                    break;
                case EventType.responseSubmissionEvent:
                    if (this.responseSubmissionEventHandler) {
                        this.responseSubmissionEventHandler(event as ResponseSubmissionEvent);
                        this.responseSubmissionEventHandler = undefined;
                    } else {
                        console.warn('Missing handler for responseSubmissionEvent');
                    }
                    break;
            }
        };
        this.socket.onerror = () => {
            console.log('Socket failure while ');
        };
        this.socket.onclose = () => {
            console.info('Socket connection closed by proxy');
            this.socket = undefined;
        };

        const assignmentData = await new Promise<SessionAssignmentEvent>(resolve => {
            this.sessionAssignmentEventHandler = data => resolve(data);
        });

        return assignmentData.session_id;
    }

    /**
     * Sends given RequestMessage over established WebSocket tunnel.
     * @param request RequestMessage to send
     */
    private sendRequest(request: RequestMessage) {
        if (this.socket === undefined) {
            throw new Error('Socket not initialized');
        }

        this.socket.send(JSON.stringify(request));
    }

    /**
     * Returns signature for given WebAuthn credentialsCreate request.
     * @param options WebAuthn request
     * @return WebAuthn response
     */
    public async credentialsCreate(options: {}): Promise<{}> {
        if (this.key === undefined) {
            throw new Error('Symmetric encryption key not set');
        }

        const payload: CreateRequestPayload = {action: RequestAction.create, options};
        const encryptedPayload = await encryptPayload(payload, this.key);
        const request: RequestMessage = {op: RequestOp.forwardRequest, payload: encryptedPayload};
        this.payloadHash = await generatePayloadHash(encryptedPayload);

        this.sendRequest(request);
        const responseData = await new Promise<ResponseSubmissionEvent>(resolve => {
            this.responseSubmissionEventHandler = data => resolve(data);
        });

        if (this.socket) {
            this.socket.close();
            this.socket = undefined;
        }

        return decryptPayload(responseData.payload, this.key);
    }

    /**
     * Returns signature for given WebAuthn credentialsGet request.
     * @param options WebAuthn request
     * @return WebAuthn response
     */
    public async credentialsGet(options: {}): Promise<{}> {
        if (this.key === undefined) {
            throw new Error('Symmetric encryption key not set');
        }

        const payload: RequestRequestPayload = {action: RequestAction.request, options};
        const encryptedPayload = await encryptPayload(payload, this.key);
        const request: RequestMessage = {op: RequestOp.forwardRequest, payload: encryptedPayload};
        this.payloadHash = await generatePayloadHash(encryptedPayload);

        this.sendRequest(request);
        const responseData = await new Promise<ResponseSubmissionEvent>(resolve => {
            this.responseSubmissionEventHandler = data => resolve(data);
        });

        if (this.socket) {
            this.socket.close();
            this.socket = undefined;
        }

        return decryptPayload(responseData.payload, this.key);
    }

    /**
     * Returns url for qr code generation. May be null if sessionId, key or payloadHash not set.
     * @param baseURL url prefix
     * @return url for qr code generation
     */
    public getQrUrl(baseURL: string): string | undefined {
        if (!this.sessionId || !this.keyString || !this.payloadHash) {
            return undefined;
        }

        return baseURL
            + '#' + this.serverUrl.replace(';', '')
            + ';' + this.sessionId.replace(';', '')
            + ';' + this.keyString
            + ';' + this.payloadHash
    }
}
