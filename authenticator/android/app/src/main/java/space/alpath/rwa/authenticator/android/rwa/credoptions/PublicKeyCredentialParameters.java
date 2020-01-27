package space.alpath.rwa.authenticator.android.rwa.credoptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import webauthnkit.core.data.PublicKeyCredentialType;

/**
 * Represents WebAuthn PublicKeyCredentialParameters.
 * https://www.w3.org/TR/webauthn/#credential-params
 */
public final class PublicKeyCredentialParameters {
    private final String type;
    private final Long alg;

    /**
     * Creates WebAuthn PublicKeyCredentialParameters object. See WebAuthn reference on top for params.
     */
    public PublicKeyCredentialParameters(@JsonProperty(value = "type", required = true) final String type,
                                         @JsonProperty(value = "alg", required = true) final Long alg) {
        this.type = type;
        this.alg = alg;
    }

    /**
     * Transforms to WebAuthnKit equivalent of PublicKeyCredentialParameters.
     *
     * @return WebAuthnKit PublicKeyCredentialParameters
     */
    public webauthnkit.core.data.PublicKeyCredentialParameters toWebAuthnKit() {
        PublicKeyCredentialType wakType;
        switch (type) {
            case "public-key":
                wakType = PublicKeyCredentialType.PublicKey;
                break;
            default:
                throw new RuntimeException("Type not supported");
        }

        return new webauthnkit.core.data.PublicKeyCredentialParameters(
                wakType,
                alg.intValue()
        );
    }

    public String getType() {
        return type;
    }

    public long getAlg() {
        return alg;
    }
}
