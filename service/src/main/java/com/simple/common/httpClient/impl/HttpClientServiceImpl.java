package com.simple.common.httpClient.impl;

import com.simple.common.httpClient.IHttpClientService;
import com.simple.common.httpClient.util.HttpClientConfig;
import com.simple.common.httpClient.util.HttpClientUtil;
import com.simple.common.httpClient.util.HttpContentTypeEnum;
import com.simple.common.utils.JsonUtil;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HttpClientServiceImpl implements IHttpClientService {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientServiceImpl.class);

    private static PoolingHttpClientConnectionManager connectionManager;


    private static ScheduledExecutorService           monitorExecutor;

    public HttpClientServiceImpl() {
        connectionManager = new PoolingHttpClientConnectionManager();
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(HttpClientConfig.SO_TIMEOUT)
            .build();//????????????
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setMaxTotal(HttpClientConfig.MAX_TOTAL_CONNECTIONS);//???????????????
        connectionManager.setDefaultMaxPerRoute(HttpClientConfig.MAX_PER_ROUTE);//?????????????????????
        threadCloseConnection();//????????????????????????
    }

    /**
     * ??????????????????,???????????????????????????
     */
    private void threadCloseConnection() {
        //??????????????????,????????????????????????????????????
        monitorExecutor = Executors.newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //??????????????????
                connectionManager.closeExpiredConnections();
                //??????5s???????????????
                connectionManager.closeIdleConnections(HttpClientConfig.HttpIdelTimeout,
                    TimeUnit.MILLISECONDS);
            }
        }, HttpClientConfig.HttpMonitorInterval, HttpClientConfig.HttpMonitorInterval,
            TimeUnit.MILLISECONDS);
    }

    /**
     * ??????????????????
     * @param times  ????????????
     * @return
     */
    private HttpRequestRetryHandler retryHandler(final int times) {
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > times) {
                    //????????????3???,????????????
                    logger.error("httpClient??????????????????3???,????????????!");
                    return false;
                }
                if (e instanceof NoHttpResponseException) {
                    logger.error("?????????????????????,????????????!");
                    return true;
                }
                if (e instanceof SSLHandshakeException) {
                    logger.error("SSL????????????,???????????????!");
                    return false;
                }
                if (e instanceof InterruptedIOException) {
                    logger.error("http????????????,???????????????!");
                    return false;
                }
                if (e instanceof UnknownHostException) {
                    // ??????????????????
                    logger.error("server host unknown");
                    return false;
                }
                if (e instanceof ConnectTimeoutException) {
                    logger.error("http????????????,???????????????!");
                    return false;
                }
                if (e instanceof SSLException) {
                    logger.error("SSLException");
                    return false;
                }
                HttpClientContext context = HttpClientContext.adapt(httpContext);
                HttpRequest request = context.getRequest();
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    //???????????????????????????????????????
                    return true;
                }
                return false;
            }
        };
        return handler;
    }

    /**
     * ??????HttpClient????????????
     * @return
     */
    private CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager).setRetryHandler(retryHandler(3)).build();
        return httpClient;
    }

    /**
     * ??????httpClient???????????????????????????
     * @param
     */
    private RequestConfig setRequestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(HttpClientConfig.CONNECT_TIMEOUT)
            .setConnectTimeout(HttpClientConfig.CONNECT_TIMEOUT)
            .setSocketTimeout(HttpClientConfig.SOCKET_TIMEOUT).build();
        return requestConfig;
    }

    /**
     * post??????
     */
    @Override
    public String post(String url, HttpContentTypeEnum contentType, Object parameterMap) {
        //????????????
        String result = "";

        //??????post??????
        HttpPost httppost = new HttpPost(url);
        httppost.setConfig(setRequestConfig());
        CloseableHttpClient httpClient = getHttpClient();
        //????????????
        if (HttpContentTypeEnum.FORM.getValue().equals(contentType.getValue())) {
            List<NameValuePair> namePair = HttpClientUtil.getHttpPostParameter(parameterMap);
            try {
                httppost.setEntity(new UrlEncodedFormEntity(namePair, HttpClientConfig.UTF8));
            } catch (UnsupportedEncodingException e) {
                if (logger.isInfoEnabled()) {
                    logger.error("????????????????????????!", e);
                }
                result = JsonUtil.returnJsonUnSuccess("????????????????????????!");
                return result;
            }
        } else {
            StringEntity entity = new StringEntity(JsonUtil.writeObjectJSON(parameterMap),
                HttpClientConfig.UTF8);
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httppost.setEntity(entity);
        }
        //????????????
        result = HttpClientUtil.httpExecute(httpClient, httppost);
        return result;
    }

    /**
     * get??????
     */
    @Override
    public String get(String url, Object parameterMap) {
        //????????????
        String result = "";
        //??????????????????,url??????????????????
        String urlAll = url
                        + (parameterMap == null ? "" : "?"
                                                       + HttpClientUtil
                                                           .getHttpGetParameter(parameterMap));
        //??????get????????????
        HttpGet httpget = new HttpGet(urlAll);
        httpget.setConfig(setRequestConfig());
        CloseableHttpClient httpClient = getHttpClient();
        result = HttpClientUtil.httpExecute(httpClient, httpget);
        return result;
    }
}
