package space.alpath.rwa.authenticator.android.rwa.credoptions;

import android.util.Base64;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import webauthnkit.core.data.AuthenticatorTransport;
import webauthnkit.core.data.PublicKeyCredentialType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents WebAuthn PublicKeyCredentialDescriptor.
 * https://www.w3.org/TR/webauthn/#dictdef-publickeycredentialdescriptor
 */
public final class PublicKeyCredentialDescriptor {
    private final byte[] id;
    private final String type;
    private final List<String> transports;

    /**
     * Creates WebAuthn PublicKeyCredentialDescriptor object. See WebAuthn reference on top for params.
     */
    public PublicKeyCredentialDescriptor(@JsonProperty(value = "id", required = true) final String b64Id,
                                         @JsonProperty(value = "type", required = true) final String type,
                                         @JsonProperty(value = "transports") final List<String> transports) {
        this.id = Base64.decode(b64Id, Base64.DEFAULT);
        this.type = type;
        this.transports = transports;
    }

    public PublicKeyCredentialDescriptor(final webauthnkit.core.data.PublicKeyCredentialDescriptor wakCredential) {
        this.id = wakCredential.getId();
        this.type = wakCredential.getType().toString();

        this.transports = new ArrayList<>();
        for (final AuthenticatorTransport wakTransport : wakCredential.getTransports()) {
            this.transports.add(wakTransport.toString());
        }
    }

    /**
     * Transforms to WebAuthnKit equivalent of PublicKeyCredentialDescriptor.
     *
     * @return WebAuthnKit PublicKeyCredentialDescriptor
     */
    public webauthnkit.core.data.PublicKeyCredentialDescriptor toWebAuthnKit() {
        PublicKeyCredentialType wakType;
        switch (type) {
            case "public-key":
                wakType = PublicKeyCredentialType.PublicKey;
                break;
            default:
                throw new RuntimeException("Type not supported");
        }

        final List<AuthenticatorTransport> wakTransports = new ArrayList<>();
        if (transports != null) {
            for (final String transport : transports) {
                AuthenticatorTransport wakTransport;
                switch (transport) {
                    case "usb":
                        wakTransport = AuthenticatorTransport.USB;
                        break;
                    case "nfc":
                        wakTransport = AuthenticatorTransport.NFC;
                        break;
                    case "ble":
                        wakTransport = AuthenticatorTransport.BLE;
                        break;
                    case "internal":
                        wakTransport = AuthenticatorTransport.Internal;
                        break;
                    default:
                        throw new RuntimeException("Type not supported");
                }

                wakTransports.add(wakTransport);
            }
        }

        return new webauthnkit.core.data.PublicKeyCredentialDescriptor(
                wakType,
                id,
                wakTransports
        );
    }

    @JsonIgnore
    public byte[] getId() {
        return id;
    }

    @JsonProperty("id")
    public String getB64Id() {
        return Base64.encodeToString(id, Base64.DEFAULT);
    }

    public String getType() {
        return type;
    }

    public List<String> getTransports() {
        return transports;
    }
}
