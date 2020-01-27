package space.alpath.rwa.authenticator.android.rwa.credoptions;

import android.util.Base64;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import webauthnkit.core.data.AttestationConveyancePreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Represents WebAuthn PublicKeyCredentialCreationOptions.
 * https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialcreationoptions
 */
public final class PublicKeyCredentialCreationOptions {
    private final PublicKeyCredentialRpEntity rp;
    private final PublicKeyCredentialUserEntity user;
    private final byte[] challenge;
    private final List<PublicKeyCredentialParameters> pubKeyCredParams;
    private final Long timeout;
    private final List<PublicKeyCredentialDescriptor> excludeCredentials;
    private final AuthenticatorSelectionCriteria authenticatorSelection;
    private final String attestation;
    // Extensions currently not supported

    /**
     * Creates WebAuthn PublicKeyCredentialCreationOptions object. See WebAuthn reference on top for params.
     */
    public PublicKeyCredentialCreationOptions(
            @JsonProperty(value = "rp", required = true) final PublicKeyCredentialRpEntity rp,
            @JsonProperty(value = "user", required = true) final PublicKeyCredentialUserEntity user,
            @JsonProperty(value = "challenge", required = true) final String b64Challenge,
            @JsonProperty(value = "pubKeyCredParams", required = true) final List<PublicKeyCredentialParameters> pubKeyCredParams,
            @JsonProperty(value = "timeout") final Long timeout,
            @JsonProperty(value = "excludeCredentials") final List<PublicKeyCredentialDescriptor> excludeCredentials,
            @JsonProperty(value = "authenticatorSelection") final AuthenticatorSelectionCriteria authenticatorSelection,
            @JsonProperty(value = "attestation") final String attestation) {
        this.rp = rp;
        this.user = user;
        this.challenge = Base64.decode(b64Challenge, Base64.DEFAULT);
        this.pubKeyCredParams = pubKeyCredParams;
        this.timeout = timeout;
        this.excludeCredentials = excludeCredentials;
        this.authenticatorSelection = authenticatorSelection;
        this.attestation = attestation;
    }

    /**
     * Transforms to WebAuthnKit equivalent of PublicKeyCredentialCreationOptions.
     *
     * @return WebAuthnKit PublicKeyCredentialCreationOptions
     */
    public webauthnkit.core.data.PublicKeyCredentialCreationOptions toWebAuthnKit() {
        AttestationConveyancePreference wakAttestation;
        if (attestation != null) {
            switch (attestation) {
                case "direct":
                    wakAttestation = AttestationConveyancePreference.Direct;
                    break;
                case "indirect":
                    wakAttestation = AttestationConveyancePreference.Indirect;
                    break;
                case "none":
                    wakAttestation = AttestationConveyancePreference.None;
                    break;
                default:
                    throw new RuntimeException("Type not supported");
            }
        } else {
            wakAttestation = AttestationConveyancePreference.None;
        }

        final List<webauthnkit.core.data.PublicKeyCredentialParameters> wakPubKeyCredParams = new ArrayList<>();
        for (final PublicKeyCredentialParameters parameter : pubKeyCredParams) {
            wakPubKeyCredParams.add(parameter.toWebAuthnKit());
        }

        final List<webauthnkit.core.data.PublicKeyCredentialDescriptor> wakExcludeCredentials = new ArrayList<>();
        if (excludeCredentials != null) {
            for (final PublicKeyCredentialDescriptor credential : excludeCredentials) {
                wakExcludeCredentials.add(credential.toWebAuthnKit());
            }
        }

        return new webauthnkit.core.data.PublicKeyCredentialCreationOptions(
                rp.toWebAuthnKit(),
                user.toWebAuthnKit(),
                challenge,
                wakPubKeyCredParams,
                timeout,
                wakExcludeCredentials,
                authenticatorSelection.toWebAuthnKit(),
                wakAttestation,
                new HashMap<String, Object>()
        );
    }

    public PublicKeyCredentialRpEntity getRp() {
        return rp;
    }

    public PublicKeyCredentialUserEntity getUser() {
        return user;
    }

    @JsonIgnore
    public byte[] getChallenge() {
        return challenge;
    }

    @JsonProperty("challenge")
    public String getB64Challenge() {
        return Base64.encodeToString(challenge, Base64.DEFAULT);
    }

    public List<PublicKeyCredentialParameters> getPubKeyCredParams() {
        return pubKeyCredParams;
    }

    public long getTimeout() {
        return timeout;
    }

    public List<PublicKeyCredentialDescriptor> getExcludeCredentials() {
        return excludeCredentials != null ? excludeCredentials : Collections.<PublicKeyCredentialDescriptor>emptyList();
    }

    public AuthenticatorSelectionCriteria getAuthenticatorSelection() {
        return authenticatorSelection;
    }

    public String getAttestation() {
        return attestation != null ? attestation : "none";
    }
}
