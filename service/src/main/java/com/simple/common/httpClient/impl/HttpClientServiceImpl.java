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
            .build();//超时时间
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setMaxTotal(HttpClientConfig.MAX_TOTAL_CONNECTIONS);//最大连接数
        connectionManager.setDefaultMaxPerRoute(HttpClientConfig.MAX_PER_ROUTE);//路由连接默认数
        threadCloseConnection();//监控关闭空闲连接
    }

    /**
     * 开启线程监控,对空闲连接进行关闭
     */
    private void threadCloseConnection() {
        //开启监控线程,对异常和空闲线程进行关闭
        monitorExecutor = Executors.newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //关闭异常连接
                connectionManager.closeExpiredConnections();
                //关闭5s空闲的连接
                connectionManager.closeIdleConnections(HttpClientConfig.HttpIdelTimeout,
                    TimeUnit.MILLISECONDS);
            }
        }, HttpClientConfig.HttpMonitorInterval, HttpClientConfig.HttpMonitorInterval,
            TimeUnit.MILLISECONDS);
    }

    /**
     * 请求失败重试
     * @param times  重试次数
     * @return
     */
    private HttpRequestRetryHandler retryHandler(final int times) {
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > times) {
                    //重试超过3次,放弃请求
                    logger.error("httpClient重复请求超过3次,请求失败!");
                    return false;
                }
                if (e instanceof NoHttpResponseException) {
                    logger.error("调用接口未响应,重新调用!");
                    return true;
                }
                if (e instanceof SSLHandshakeException) {
                    logger.error("SSL握手异常,不重新调用!");
                    return false;
                }
                if (e instanceof InterruptedIOException) {
                    logger.error("http请求超时,不重新调用!");
                    return false;
                }
                if (e instanceof UnknownHostException) {
                    // 服务器不可达
                    logger.error("server host unknown");
                    return false;
                }
                if (e instanceof ConnectTimeoutException) {
                    logger.error("http连接超时,不重新调用!");
                    return false;
                }
                if (e instanceof SSLException) {
                    logger.error("SSLException");
                    return false;
                }
                HttpClientContext context = HttpClientContext.adapt(httpContext);
                HttpRequest request = context.getRequest();
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    //如果请求不是关闭连接的请求
                    return true;
                }
                return false;
            }
        };
        return handler;
    }

    /**
     * 获取HttpClient请求对象
     * @return
     */
    private CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager).setRetryHandler(retryHandler(3)).build();
        return httpClient;
    }

    /**
     * 获取httpClient并设置请求超时时间
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
     * post请求
     */
    @Override
    public String post(String url, HttpContentTypeEnum contentType, Object parameterMap) {
        //返回信息
        String result = "";

        //创建post请求
        HttpPost httppost = new HttpPost(url);
        httppost.setConfig(setRequestConfig());
        CloseableHttpClient httpClient = getHttpClient();
        //封装参数
        if (HttpContentTypeEnum.FORM.getValue().equals(contentType.getValue())) {
            List<NameValuePair> namePair = HttpClientUtil.getHttpPostParameter(parameterMap);
            try {
                httppost.setEntity(new UrlEncodedFormEntity(namePair, HttpClientConfig.UTF8));
            } catch (UnsupportedEncodingException e) {
                if (logger.isInfoEnabled()) {
                    logger.error("请求参数转换异常!", e);
                }
                result = JsonUtil.returnJsonUnSuccess("请求参数转换异常!");
                return result;
            }
        } else {
            StringEntity entity = new StringEntity(JsonUtil.writeObjectJSON(parameterMap),
                HttpClientConfig.UTF8);
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httppost.setEntity(entity);
        }
        //执行请求
        result = HttpClientUtil.httpExecute(httpClient, httppost);
        return result;
    }

    /**
     * get请求
     */
    @Override
    public String get(String url, Object parameterMap) {
        //返回信息
        String result = "";
        //获取请求参数,url接口完整路径
        String urlAll = url
                        + (parameterMap == null ? "" : "?"
                                                       + HttpClientUtil
                                                           .getHttpGetParameter(parameterMap));
        //创建get请求对象
        HttpGet httpget = new HttpGet(urlAll);
        httpget.setConfig(setRequestConfig());
        CloseableHttpClient httpClient = getHttpClient();
        result = HttpClientUtil.httpExecute(httpClient, httpget);
        return result;
    }
}
