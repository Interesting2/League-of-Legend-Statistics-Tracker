package major_project.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RedditToken {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_in")
    @Expose
    private Integer expiresIn;
    @SerializedName("scope")
    @Expose
    private String scope;

    public String getAccessToken() {
    return accessToken;
    }

    public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
    }

    public String getTokenType() {
    return tokenType;
    }

    public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
    return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
    }

    public String getScope() {
    return scope;
    }

    public void setScope(String scope) {
    this.scope = scope;
    }

}