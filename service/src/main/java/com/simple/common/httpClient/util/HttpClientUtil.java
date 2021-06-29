package com.simple.common.httpClient.util;

import com.simple.common.utils.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * http请求工具类
 * @author hejinguo
 * @version $Id: HttpClientUtil.java, v 0.1 2019年11月17日 下午5:15:45
 */
public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 执行请求，并返回结果
     * @param request 
     * @return
     */
    public static String httpExecute(CloseableHttpClient httpClient, HttpUriRequest request) {
        //返回信息
        StringBuffer strBuffer = new StringBuffer();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            //请求成功
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    strBuffer.append(EntityUtils.toString(httpEntity));
                    EntityUtils.consume(httpEntity);
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.error("接口请求失败,请稍后再试!");
                }
                strBuffer.append(JsonUtil.returnJsonUnSuccess("接口请求失败,请稍后再试!"));
            }
        } catch (ClientProtocolException e) {
            if (logger.isInfoEnabled()) {
                logger.error("接口通信协议错误或返回数据异常!", e);
            }
            strBuffer.append(JsonUtil.returnJsonUnSuccess("程序异常失败接口通信协议错误或返回数据异常!"));
        } catch (IOException e) {
            if (logger.isInfoEnabled()) {
                logger.error("接口请求网络异常,请稍后再试!", e);
            }
            strBuffer.append(JsonUtil.returnJsonUnSuccess("接口请求网络异常,请稍后再试!"));
        } finally {
            if (request != null && request.isAborted()) {
                request.abort();
            }
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("httpClient管理流异常,异常信息--->{},错误信息--->{}", e.getMessage(), e);
            }
        }
        return strBuffer.toString();
    }

    /**
     *获取post请求时参数
     * @param obj 允许Obj 为HashMap和实体对象,如果为实体对象会将其转换成Json格式,key值即为实体对象名
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List<NameValuePair> getHttpPostParameter(Object obj) {
        List<NameValuePair> namePair = new ArrayList<NameValuePair>();
        if (obj == null) {
            return namePair;
        }
        NameValuePair nvp = null;
        if (obj instanceof HashMap) {//参数为HashMap
            Iterator it = ((HashMap) obj).entrySet().iterator();
            String key = "";
            String value = "";
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                key = (String) entry.getKey();
                value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
                nvp = new BasicNameValuePair(key, value);
                namePair.add(nvp);
            }
        } else {
            String className = obj.getClass().getName();
            String objName = className
                .substring(className.lastIndexOf(".") + 1, className.length());
            String shrotName = objName.substring(0, 1).toLowerCase() + objName.substring(1);
            String json = JsonUtil.writeObjectJSON(obj);
            nvp = new BasicNameValuePair(shrotName, json);
            namePair.add(nvp);
        }
        return namePair;
    }

    /**
     * 获取get请求参数
     * @param obj
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getHttpGetParameter(Object obj) {
        String result = "";
        if (obj == null) {
            return result;
        }
        StringBuffer param = new StringBuffer();
        if (obj instanceof HashMap) {//参数为HashMap
            Iterator it = ((HashMap) obj).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                param.append((String) entry.getKey()).append("=")
                    .append(entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
                param.append("&");
            }
            if (param != null && param.length() > 0) {
                result = param.substring(0, param.length() - 1);
            }
        } else {
            String className = obj.getClass().getName();
            String objName = className
                .substring(className.lastIndexOf(".") + 1, className.length());
            String shrotName = objName.substring(0, 1).toLowerCase() + objName.substring(1);
            String json = JsonUtil.writeObjectJSON(obj);
            result = shrotName + "=" + json;
        }
        return result;
    }
}
