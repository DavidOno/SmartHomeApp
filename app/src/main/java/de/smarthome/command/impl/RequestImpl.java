package de.smarthome.command.impl;

import android.util.Log;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLContext;

import de.smarthome.SmartHomeApplication;
import de.smarthome.command.Request;
import de.smarthome.server.StandardErrorHandler;


public class RequestImpl implements Request {

    private static final String TAG = "RequestImpl";
    private final String uri;
    private final HttpMethod httpMethod;
    private final HttpEntity entity;
    private final Class responseType;
//    private boolean isSSLVerificationSkipped;
//    private ResponseErrorHandler errorHandler = new StandardErrorHandler();
    private RestTemplate restTemplate;

    public RequestImpl(String uri, HttpMethod httpMethod, HttpEntity entity, Class responseType, RestTemplate restTemplate) {
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.entity = entity;
        this.responseType = responseType;
//        isSSLVerificationSkipped = true;
        this.restTemplate = restTemplate;
    }



    @Override
    public ResponseEntity execute() {
//        checkNetworkConnection();
        Future<ResponseEntity> future = SmartHomeApplication.EXECUTOR_SERVICE.submit(() -> {
            //restTemplate = createRestTemplate(); //currently not needed
            //restTemplateCreater
            return restTemplate.exchange(uri, httpMethod, entity, responseType);
        });
        return getResponseEntity(future);
    }

    private void checkNetworkConnection() {
        boolean online = isOnline();
        if(!online){
            Log.d(TAG, "WARNING: No Network Connection");
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return false;
    }

    private ResponseEntity getResponseEntity(Future<ResponseEntity> future) {
        ResponseEntity responseEntity = null;
        try {
             responseEntity = future.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.d(TAG, "Request took too long");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return responseEntity;
    }

//    private RestTemplate createRestTemplate() {
//        RestTemplate restTemplate = new RestTemplate();
//        if(isSSLVerificationSkipped){
//            restTemplate = new RestTemplate(avoidSSLVerification());
//        }
//        restTemplate.setErrorHandler(errorHandler);
//        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//        return restTemplate;
//    }
//
//    private HttpComponentsClientHttpRequestFactory avoidSSLVerification() {
//        HttpComponentsClientHttpRequestFactory requestFactory = null;
//        try {
//            TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
//            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
//            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new AllowAllHostnameVerifier());
//            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
//            requestFactory = new HttpComponentsClientHttpRequestFactory();
//            requestFactory.setHttpClient(httpClient);
//        }catch(KeyManagementException | NoSuchAlgorithmException | KeyStoreException ex){
//            Log.d(TAG, "Avoiding SSL-Verification failed");
//            ex.printStackTrace();
//        }
//        return requestFactory;
////    }
//
//    public void setErrorHandler(ResponseErrorHandler errorHandler){
//        this.errorHandler = errorHandler;
//    }
//
//    public void setRestTemplate(RestTemplate restTemplate){
//        this.restTemplate = restTemplate;
//    }
}