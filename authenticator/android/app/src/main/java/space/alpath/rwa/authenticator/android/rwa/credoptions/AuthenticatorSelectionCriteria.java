package space.alpath.rwa.authenticator.android.rwa.credoptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import webauthnkit.core.data.AuthenticatorAttachment;
import webauthnkit.core.data.UserVerificationRequirement;

/**
 * Represents WebAuthn AuthenticatorSelectionCriteria.
 * https://www.w3.org/TR/webauthn/#authenticatorSelection
 */
public final class AuthenticatorSelectionCriteria {
    private final String authenticatorAttachment, userVerification;

    private final boolean requireResidentKey;

    /**
     * Creates WebAuthn AuthenticatorSelectionCriteria object. See WebAuthn reference on top for params.
     */
    public AuthenticatorSelectionCriteria(
            @JsonProperty(value = "authenticatorAttachment") final String authenticatorAttachment,
            @JsonProperty(value = "userVerification") final String userVerification,
            @JsonProperty(value = "requireResidentKey") final boolean requireResidentKey) {
        this.authenticatorAttachment = authenticatorAttachment;
        this.userVerification = userVerification;
        this.requireResidentKey = requireResidentKey;
    }

    /**
     * Transforms to WebAuthnKit equivalent of AuthenticatorSelectionCriteria.
     *
     * @return WebAuthnKit AuthenticatorSelectionCriteria
     */
    public webauthnkit.core.data.AuthenticatorSelectionCriteria toWebAuthnKit() {
        AuthenticatorAttachment wakAuthenticatorAttachment = null;
        if (authenticatorAttachment != null) {
            switch (authenticatorAttachment) {
                case "platform":
                    wakAuthenticatorAttachment = AuthenticatorAttachment.Platform;
                    break;
                case "cross-platform":
                    wakAuthenticatorAttachment = AuthenticatorAttachment.CrossPlatform;
                    break;
                default:
                    throw new RuntimeException("Type not supported");
            }
        }

        UserVerificationRequirement wakUserVerification;
        if (userVerification != null) {
            switch (userVerification) {
                case "discouraged":
                    wakUserVerification = UserVerificationRequirement.Discouraged;
                    break;
                case "preferred":
                    wakUserVerification = UserVerificationRequirement.Preferred;
                    break;
                case "required":
                    wakUserVerification = UserVerificationRequirement.Required;
                    break;
                default:
                    throw new RuntimeException("Type not supported");
            }
        } else {
            wakUserVerification = UserVerificationRequirement.Preferred;
        }

        return new webauthnkit.core.data.AuthenticatorSelectionCriteria(
                wakAuthenticatorAttachment,
                requireResidentKey,
                wakUserVerification
        );
    }

    public String getAuthenticatorAttachment() {
        return authenticatorAttachment;
    }

    public String getUserVerification() {
        return userVerification != null ? userVerification : "preferred";
    }

    public boolean isRequireResidentKey() {
        // Will return false (as specified) if not set
        return requireResidentKey;
    }
}
