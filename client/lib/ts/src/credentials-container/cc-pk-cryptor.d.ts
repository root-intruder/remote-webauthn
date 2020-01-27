import { RequestPayload } from '../message/request-payload.model';
/**
 * Generates random AES CBC key.
 * @return AES CBC key
 */
export declare function generateKey(): Promise<CryptoKey>;
/**
 * Encodes provided key as base64.
 * @param key encodable key
 * @return key in base64 format
 */
export declare function encodeKey(key: CryptoKey): Promise<string>;
/**
 * Encrypts payload with given key.
 * @param payload RequestPayload to encrypt
 * @param key encryption key
 * @return encrypted payload in base64
 */
export declare function encryptPayload(payload: RequestPayload, key: CryptoKey): Promise<string>;
/**
 * Decrypts payload ciphertext with given key.
 * @param input payload ciphertext (including iv) as base64
 * @param key decryption key
 * @return decrypted payload
 */
export declare function decryptPayload(input: string, key: CryptoKey): Promise<{}>;
/**
 * Generates SHA256 digest from encrypted payload.
 * @param input payload ciphertext (including iv)
 * @return digest in base64 format
 */
export declare function generatePayloadHash(input: string): Promise<string>;
