"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.generatePayloadHash = exports.decryptPayload = exports.encryptPayload = exports.encodeKey = exports.generateKey = void 0;
/**
 * Generates random AES CBC key.
 * @return AES CBC key
 */
async function generateKey() {
    return window.crypto.subtle.generateKey({
        name: 'AES-CBC',
        length: 256
    }, true, ['encrypt', 'decrypt']);
}
exports.generateKey = generateKey;
/**
 * Encodes provided key as base64.
 * @param key encodable key
 * @return key in base64 format
 */
async function encodeKey(key) {
    const exportedKey = await window.crypto.subtle.exportKey('raw', key);
    return btoa(String.fromCharCode(...new Uint8Array(exportedKey)));
}
exports.encodeKey = encodeKey;
/**
 * Encrypts payload with given key.
 * @param payload RequestPayload to encrypt
 * @param key encryption key
 * @return encrypted payload in base64
 */
async function encryptPayload(payload, key) {
    const iv = window.crypto.getRandomValues(new Uint8Array(16));
    const ciphertext = await window.crypto.subtle.encrypt({
        name: 'AES-CBC',
        iv: iv
    }, key, new TextEncoder().encode(JSON.stringify(payload)));
    return btoa(String.fromCharCode(...iv, ...new Uint8Array(ciphertext)));
}
exports.encryptPayload = encryptPayload;
/**
 * Decrypts payload ciphertext with given key.
 * @param input payload ciphertext (including iv) as base64
 * @param key decryption key
 * @return decrypted payload
 */
async function decryptPayload(input, key) {
    const ciphertextWithIv = atob(input).split('').map(c => c.charCodeAt(0));
    const iv = new Uint8Array(ciphertextWithIv.splice(0, 16));
    const ciphertext = new Uint8Array(ciphertextWithIv);
    const payload = await window.crypto.subtle.decrypt({
        name: 'AES-CBC',
        iv: iv
    }, key, ciphertext);
    return JSON.parse(new TextDecoder().decode(payload));
}
exports.decryptPayload = decryptPayload;
/**
 * Generates SHA256 digest from encrypted payload.
 * @param input payload ciphertext (including iv)
 * @return digest in base64 format
 */
async function generatePayloadHash(input) {
    const ciphertextWithIv = atob(input).split('').map(c => c.charCodeAt(0));
    const digest = await crypto.subtle.digest('SHA-256', new Uint8Array(ciphertextWithIv));
    return btoa(String.fromCharCode(...new Uint8Array(digest)));
}
exports.generatePayloadHash = generatePayloadHash;
//# sourceMappingURL=cc-pk-cryptor.js.map