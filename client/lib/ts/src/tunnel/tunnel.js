"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.Tunnel = void 0;
const request_message_model_1 = require("../message/request-message.model");
const event_message_model_1 = require("../message/event-message.model");
const request_payload_model_1 = require("../message/request-payload.model");
const cc_pk_cryptor_1 = require("../credentials-container/cc-pk-cryptor");
/**
 * Represents a connection with a Remote WebAuthn proxy server. Handles all related logic.
 */
class Tunnel {
    /**
     * Initialises new tunnel instance.
     * @param serverUrl proxy server url
     */
    constructor(serverUrl) {
        this.serverUrl = serverUrl;
    }
    /**
     * Connects to previously defined proxy url. Also generates random symmetric encryption key.
     * @return sessionId obtained from proxy
     */
    async connect() {
        this.key = await cc_pk_cryptor_1.generateKey();
        this.keyString = await cc_pk_cryptor_1.encodeKey(this.key);
        this.socket = new WebSocket(this.serverUrl);
        this.socket.onmessage = msg => {
            const event = JSON.parse(msg.data);
            switch (event.type) {
                case event_message_model_1.EventType.sessionAssignmentEvent:
                    if (this.sessionAssignmentEventHandler) {
                        const sessionAssignmentEvent = event;
                        this.sessionId = sessionAssignmentEvent.session_id;
                        this.sessionAssignmentEventHandler(sessionAssignmentEvent);
                        this.sessionAssignmentEventHandler = undefined;
                    }
                    else {
                        console.warn('Missing handler for sessionAssignmentEvent');
                    }
                    break;
                case event_message_model_1.EventType.authenticatorJoinEvent:
                    if (this.authenticatorJoinEventHandler) {
                        this.authenticatorJoinEventHandler(event);
                        this.authenticatorJoinEventHandler = undefined;
                    }
                    else {
                        console.info('No handler for authenticatorJoinEvent');
                    }
                    break;
                case event_message_model_1.EventType.responseSubmissionEvent:
                    if (this.responseSubmissionEventHandler) {
                        this.responseSubmissionEventHandler(event);
                        this.responseSubmissionEventHandler = undefined;
                    }
                    else {
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
        const assignmentData = await new Promise(resolve => {
            this.sessionAssignmentEventHandler = data => resolve(data);
        });
        return assignmentData.session_id;
    }
    /**
     * Sends given RequestMessage over established WebSocket tunnel.
     * @param request RequestMessage to send
     */
    sendRequest(request) {
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
    async credentialsCreate(options) {
        if (this.key === undefined) {
            throw new Error('Symmetric encryption key not set');
        }
        const payload = { action: request_payload_model_1.RequestAction.create, options };
        const encryptedPayload = await cc_pk_cryptor_1.encryptPayload(payload, this.key);
        const request = { op: request_message_model_1.RequestOp.forwardRequest, payload: encryptedPayload };
        this.payloadHash = await cc_pk_cryptor_1.generatePayloadHash(encryptedPayload);
        this.sendRequest(request);
        const responseData = await new Promise(resolve => {
            this.responseSubmissionEventHandler = data => resolve(data);
        });
        if (this.socket) {
            this.socket.close();
            this.socket = undefined;
        }
        return cc_pk_cryptor_1.decryptPayload(responseData.payload, this.key);
    }
    /**
     * Returns signature for given WebAuthn credentialsGet request.
     * @param options WebAuthn request
     * @return WebAuthn response
     */
    async credentialsGet(options) {
        if (this.key === undefined) {
            throw new Error('Symmetric encryption key not set');
        }
        const payload = { action: request_payload_model_1.RequestAction.request, options };
        const request = {
            op: request_message_model_1.RequestOp.forwardRequest,
            payload: await cc_pk_cryptor_1.encryptPayload(payload, this.key)
        };
        this.sendRequest(request);
        const responseData = await new Promise(resolve => {
            this.responseSubmissionEventHandler = data => resolve(data);
        });
        if (this.socket) {
            this.socket.close();
            this.socket = undefined;
        }
        return cc_pk_cryptor_1.decryptPayload(responseData.payload, this.key);
    }
    /**
     * Returns url for qr code generation. May be null if sessionId, key or payloadHash not set.
     * @param baseURL url prefix
     * @return url for qr code generation
     */
    getQrUrl(baseURL) {
        if (!this.sessionId || !this.keyString || !this.payloadHash) {
            return undefined;
        }
        return baseURL
            + '#' + this.serverUrl.replace(';', '')
            + ';' + this.sessionId.replace(';', '')
            + ';' + this.keyString
            + ';' + this.payloadHash;
    }
}
exports.Tunnel = Tunnel;
//# sourceMappingURL=tunnel.js.map