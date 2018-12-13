package com.cf.client;

import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author David
 */
public class HTTPClient {
    private static final String PROXY_HOST = "us806.nordvpn.com";
    private static final int PROXY_PORT = 80;
    private final CloseableHttpClient client;

    public HTTPClient() {
        client = HttpClients
                .custom()
                .build();
    }

    public HTTPClient(ProxySettings proxySettings) {
        HttpHost proxy = new HttpHost(proxySettings.getHost(), proxySettings.getPort());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(proxy),
                new UsernamePasswordCredentials(proxySettings.getUsername(), proxySettings.getPassword()));
        client = HttpClients
                .custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setProxy(proxy)
                .build();
    }

    public String postHttp(String url, List<NameValuePair> params, List<NameValuePair> headers) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
        post.getEntity().toString();

        if (headers != null) {
            for (NameValuePair header : headers) {
                post.addHeader(header.getName(), header.getValue());
            }
        }

        HttpResponse response = client.execute(post);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity);

        }
        return null;
    }

    public String getHttp(String url, List<NameValuePair> headers) throws IOException {
        HttpRequestBase request = new HttpGet(url);

        if (headers != null) {
            for (NameValuePair header : headers) {
                request.addHeader(header.getName(), header.getValue());
            }
        }

        HttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity);

        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(PROXY_HOST, PROXY_PORT),
                new UsernamePasswordCredentials("Ollaseniora28@gmail.com", "Ja5115!ke"));
//        credsProvider.setCredentials(
//                new AuthScope("httpbin.org", 80),
//                new UsernamePasswordCredentials("user", "passwd"));
        HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
        CloseableHttpClient httpclient = HttpClients
                .custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setProxy(proxy)
                .build();

        try {
            RequestConfig config = RequestConfig.custom()
//                    .setProxy(proxy)
                    .build();
//            HttpGet httpget = new HttpGet("/public?command=returnTicker");
            HttpGet httpget = new HttpGet("https://poloniex.com/public?command=returnTicker");
            httpget.setConfig(config);

//            System.out.println("Executing request " + httpget.getRequestLine() + " to " + target + " via " + proxy);
            System.out.println("Executing request " + httpget.getRequestLine() + " to " + httpget.getURI() + " via " + proxy);

            CloseableHttpResponse response = httpclient.execute(httpget);
//            CloseableHttpResponse response = httpclient.execute(target, httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
