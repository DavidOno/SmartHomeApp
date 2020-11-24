package de.smarthome.command.impl;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import de.smarthome.command.Request;

public class RequestImpl implements Request {

    private String uri;
    private HttpMethod httpMethod;
    private HttpEntity entity;
    private Class responseType;
    private boolean avoidSSL;

    public RequestImpl(String uri, HttpMethod httpMethod, HttpEntity entity, Class responseType) {
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.entity = entity;
        this.responseType = responseType;
    }

    public void setAvoidSSL() {
        avoidSSL = true;
    }

    @Override
    public ResponseEntity execute() {
        RestTemplate restTemplate = createRestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.exchange(uri, httpMethod, entity, responseType);
    }

    private RestTemplate createRestTemplate() {
        if(avoidSSL){
            return new RestTemplate(avoidSSL());
        }
        return new RestTemplate();
    }

    private HttpComponentsClientHttpRequestFactory avoidSSL() {
        HttpComponentsClientHttpRequestFactory requestFactory = null; //TODO: avoid null
        try {
            TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            };
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
            requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
        }catch(KeyManagementException | NoSuchAlgorithmException | KeyStoreException ex){
            //TODO: proper exception management
        }
        return requestFactory;
    }
}