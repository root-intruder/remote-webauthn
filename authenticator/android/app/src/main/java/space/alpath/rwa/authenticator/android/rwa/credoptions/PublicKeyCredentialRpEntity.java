package space.alpath.rwa.authenticator.android.rwa.credoptions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents WebAuthn PublicKeyCredentialRpEntity.
 * https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialrpentity
 */
public final class PublicKeyCredentialRpEntity {
    private final String id, name, icon;

    /**
     * Creates WebAuthn PublicKeyCredentialRpEntity object. See WebAuthn reference on top for params.
     */
    public PublicKeyCredentialRpEntity(@JsonProperty(value = "id", required = true) final String id,
                                       @JsonProperty(value = "name", required = true) final String name,
                                       @JsonProperty(value = "icon") final String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    /**
     * Transforms to WebAuthnKit equivalent of PublicKeyCredentialRpEntity.
     *
     * @return WebAuthnKit PublicKeyCredentialRpEntity
     */
    public webauthnkit.core.data.PublicKeyCredentialRpEntity toWebAuthnKit() {
        return new webauthnkit.core.data.PublicKeyCredentialRpEntity(id, name, icon);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}
