package de.smarthome.command.gira;

import android.util.Log;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import de.smarthome.command.impl.RestTemplateCreater;
import de.smarthome.server.StandardErrorHandler;

public class StandardRestTemplateCreater implements RestTemplateCreater {

    private static final String TAG = "StandardRestTemplateCreater";
    private boolean isSSLVerificationSkipped = true;
    private ResponseErrorHandler errorHandler = new StandardErrorHandler();

    @Override
    public RestTemplate create() {
        RestTemplate restTemplate = new RestTemplate();
        if(isSSLVerificationSkipped){
            restTemplate = new RestTemplate(avoidSSLVerification());
        }
        restTemplate.setErrorHandler(errorHandler);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory avoidSSLVerification() {
        HttpComponentsClientHttpRequestFactory requestFactory = null;
        try {
            TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new AllowAllHostnameVerifier());
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
            requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
        }catch(KeyManagementException | NoSuchAlgorithmException | KeyStoreException ex){
            Log.d(TAG, "Avoiding SSL-Verification failed");
            ex.printStackTrace();
        }
        return requestFactory;
    }

}
