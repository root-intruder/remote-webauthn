package space.alpath.rwa.authenticator.android.rwa.credoptions;

import android.util.Base64;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents WebAuthn PublicKeyCredentialUserEntity.
 * https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialuserentity
 */
public final class PublicKeyCredentialUserEntity {
    private final byte[] id;
    private final String name, displayName, icon;

    /**
     * Creates WebAuthn PublicKeyCredentialUserEntity object. See WebAuthn reference on top for params.
     */
    public PublicKeyCredentialUserEntity(@JsonProperty(value = "id", required = true) final String b64Id,
                                         @JsonProperty(value = "name", required = true) final String name,
                                         @JsonProperty(value = "displayName", required = true) final String displayName,
                                         @JsonProperty(value = "icon") final String icon) {
        this.id = Base64.decode(b64Id, Base64.DEFAULT);
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
    }

    /**
     * Transforms to WebAuthnKit equivalent of PublicKeyCredentialUserEntity.
     *
     * @return WebAuthnKit PublicKeyCredentialUserEntity
     */
    public webauthnkit.core.data.PublicKeyCredentialUserEntity toWebAuthnKit() {
        return new webauthnkit.core.data.PublicKeyCredentialUserEntity(id, name, displayName, icon);
    }

    @JsonIgnore
    public byte[] getId() {
        return id;
    }

    @JsonProperty("id")
    public String getB64Id() {
        return Base64.encodeToString(id, Base64.DEFAULT);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }
}
