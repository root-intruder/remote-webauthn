import {RequestPayload} from '../message/request-payload.model';

/**
 * Generates random AES CBC key.
 * @return AES CBC key
 */
export async function generateKey(): Promise<CryptoKey> {
    return window.crypto.subtle.generateKey({
        name: 'AES-CBC',
        length: 256
    }, true, ['encrypt', 'decrypt']);
}

/**
 * Encodes provided key as base64.
 * @param key encodable key
 * @return key in base64 format
 */
export async function encodeKey(key: CryptoKey): Promise<string> {
    const exportedKey = await window.crypto.subtle.exportKey('raw', key);

    return btoa(String.fromCharCode(...new Uint8Array(exportedKey)));
}

/**
 * Encrypts payload with given key.
 * @param payload RequestPayload to encrypt
 * @param key encryption key
 * @return encrypted payload in base64
 */
export async function encryptPayload(payload: RequestPayload, key: CryptoKey): Promise<string> {
    const iv = window.crypto.getRandomValues(new Uint8Array(16))
    const ciphertext = await window.crypto.subtle.encrypt({
        name: 'AES-CBC',
        iv: iv
    }, key, new TextEncoder().encode(JSON.stringify(payload)));

    return btoa(String.fromCharCode(...iv, ...new Uint8Array(ciphertext)));
}

/**
 * Decrypts payload ciphertext with given key.
 * @param input payload ciphertext (including iv) as base64
 * @param key decryption key
 * @return decrypted payload
 */
export async function decryptPayload(input: string, key: CryptoKey): Promise<{}> {
    const ciphertextWithIv = atob(input).split('').map(c => c.charCodeAt(0))
    const iv = new Uint8Array(ciphertextWithIv.splice(0, 16));
    const ciphertext = new Uint8Array(ciphertextWithIv);

    const payload = await window.crypto.subtle.decrypt({
        name: 'AES-CBC',
        iv: iv
    }, key, ciphertext);

    return JSON.parse(new TextDecoder().decode(payload));
}

/**
 * Generates SHA256 digest from encrypted payload.
 * @param input payload ciphertext (including iv)
 * @return digest in base64 format
 */
export async function generatePayloadHash(input: string): Promise<string> {
    const ciphertextWithIv = atob(input).split('').map(c => c.charCodeAt(0))
    const digest = await crypto.subtle.digest('SHA-256', new Uint8Array(ciphertextWithIv));

    return btoa(String.fromCharCode(...new Uint8Array(digest)));
}
