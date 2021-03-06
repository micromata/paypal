package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.Transient;

/**
 * If you need to do direct PayPal calls inside your web page code, don't use the credentials (client_id and secret)!!!!!!!!
 * <br>
 * This AccessToken is valid for e. g. 9h. During this time you will receive the same token from PayPal if you try to get
 * an access token again.
 */
public class AccessTokenResponse {
    private String scope, nonce, accessToken, tokenType, appId;
    private int expiresInNotUpdated;
    private long created;
    private String originalPayPalResponse;

    public AccessTokenResponse() {
        created = System.currentTimeMillis() / 1000;
    }

    /**
     * Example: "https://uri.paypal.com/services/disputes/read-seller https://api.paypal.com/v1/payments/.* https://uri.paypal.com/services/applications/webhooks openid https://uri.paypal.com/services/disputes/update-seller",
     *
     * @return scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Example: "2018-11-20T23:04:52ZiZy....M",
     *
     * @return nonce
     */
    public String getNonce() {
        return nonce;
    }

    @JsonProperty(value = "access_token")
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @return "Bearer"
     */
    @JsonProperty(value = "token_type")
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Example: "APP-..."
     *
     * @return app id
     */
    @JsonProperty(value = "app_id")
    public String getAppId() {
        return appId;
    }

    /**
     * Expires in 32400 (9h) on initial call (if no valid access token is available).
     * <br>
     * This is the value returned by PayPal and will not be updated!
     * <br>
     * To get the current seconds this token will expire, please call {@link #getExpiresIn()}.
     *
     * @return Expires in seconds given by PayPal on request.
     */
    @JsonProperty(value = "expires_in")
    public int getExpiresInNotUpdated() {
        return expiresInNotUpdated;
    }

    /**
     * Only used for testing.
     *
     * @param expiresInNotUpdated
     */
    void setExpiresInNotUpdated(int expiresInNotUpdated) {
        this.expiresInNotUpdated = expiresInNotUpdated;
    }

    @Transient
    public long getExpiresIn() {
        return System.currentTimeMillis() / 1000 - created + expiresInNotUpdated;
    }

    /**
     * @return The date of creation (of instantiation of this class). This field is not given by PayPal, it's set
     * automatically on deserialization. The value is generated by {@link System#currentTimeMillis()} * 1000
     * (seconds since 01/01/1970).
     */
    public long getCreated() {
        return created;
    }

    /**
     * @return the original response from PayPal. This object is generated from this json string.
     */
    public String getOriginalPayPalResponse() {
        return originalPayPalResponse;
    }

    public void setOriginalPayPalResponse(String originalPayPalResponse) {
        this.originalPayPalResponse = originalPayPalResponse;
    }

    /**
     * This was a type, please use {@link #getOriginalPayPalResponse()} instead.
     * @return
     */
    @Deprecated
    public String getOrigninalPayPalResponse() {
        return this.originalPayPalResponse;
    }
}
