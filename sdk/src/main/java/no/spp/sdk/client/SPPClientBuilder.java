package no.spp.sdk.client;

import no.spp.sdk.exception.SPPClientException;
import no.spp.sdk.net.HTTPClient;
import no.spp.sdk.net.URLConnectionClient;
import no.spp.sdk.oauth.ClientCredentials;
import no.spp.sdk.oauth.OauthCredentials;
import no.spp.sdk.oauth.OauthHelper;

/**
 * This is the parent for building SPP clients. 
 * There are different clients so make sure to use the correct 
 * builder implementation. 
 */
public abstract class SPPClientBuilder <T extends SPPClient> {

    ClientCredentials clientCredentials;
    OauthCredentials oauthCredentials;
    String sppBaseUrl = null;

    // Fields initialized with sensible defaults
    Boolean autoRefreshToken = true;
    String apiVersion = "2";
    HTTPClient httpClient = new URLConnectionClient();

    public SPPClientBuilder(ClientCredentials clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    /**
     * Mandatory base url.
     *
     * @param baseUrl
     * @return
     */
    public SPPClientBuilder withBaseUrl(String baseUrl) {
        this.sppBaseUrl = baseUrl;
        return this;
    }

    /** Override the default api version. Default = 2
     *
     * @param apiVersion
     * @return
     */
    public SPPClientBuilder withApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /** Override the default setting to autoRefresh access tokens. Default is true.
     *
     * @param autoRefreshToken
     * @return
     */
    public SPPClientBuilder autoRefreshToken(boolean autoRefreshToken) {
        this.autoRefreshToken = autoRefreshToken;
        return this;
    }

    /** Override the default HTTPClient. Default is a {@link no.spp.sdk.net.URLConnectionClient}
     *
     * @param httpClient
     * @return
     */
    public SPPClientBuilder withHTTPClient(HTTPClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    /**
     * Endmarker for the builder.
     *
     * @return The configured SPPClient object.
     * @throws no.spp.sdk.exception.SPPClientException
     */

    public T build() throws SPPClientException {
        if(sppBaseUrl == null) {
            throw new IllegalStateException("sppBaseUrl must be set");
        }
        SppApi api = new SppApi(httpClient, apiVersion, sppBaseUrl);
        OauthHelper oauthHelper = new OauthHelper(sppBaseUrl, clientCredentials, httpClient);
        SPPClientAPISecurity sppClientAPISecurity = new SPPClientAPISecurity(clientCredentials);
        T userClient = createClient(api, oauthHelper, sppClientAPISecurity);
        userClient.setAutoRefreshToken(autoRefreshToken);
        return userClient;
    }

    /**
     * Factory method for the client implementation. 
     * @param api
     * @param oauthHelper
     * @return
     * @throws SPPClientException
     */
    protected abstract T createClient(SppApi api, OauthHelper oauthHelper, SPPClientAPISecurity sppClientAPISecurity) throws SPPClientException ;
}
