package space.alpath.rwa.authenticator.android.rwa.credoptions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents WebAuthn CredentialRequestOptions.
 * https://www.w3.org/TR/webauthn/#credentialrequestoptions-extension
 */
public final class CredentialRequestOptions {
    private final PublicKeyCredentialRequestOptions publicKey;

    /**
     * Creates WebAuthn CredentialRequestOptions object. See WebAuthn reference on top for params.
     */
    public CredentialRequestOptions(@JsonProperty("publicKey") final PublicKeyCredentialRequestOptions publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKeyCredentialRequestOptions getPublicKey() {
        return publicKey;
    }
}
