package space.alpath.rwa.authenticator.android.rwa.credoptions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents WebAuthn CredentialCreationOptions.
 * https://www.w3.org/TR/webauthn/#credentialcreationoptions-extension
 */
public final class CredentialCreationOptions {
    private final PublicKeyCredentialCreationOptions publicKey;

    /**
     * Creates WebAuthn CredentialCreationOptions object. See WebAuthn reference on top for params.
     */
    public CredentialCreationOptions(@JsonProperty("publicKey") final PublicKeyCredentialCreationOptions publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKeyCredentialCreationOptions getPublicKey() {
        return publicKey;
    }
}
