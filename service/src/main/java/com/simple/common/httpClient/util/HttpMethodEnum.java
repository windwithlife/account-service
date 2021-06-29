package com.simple.common.httpClient.util;

/**
 * 请求方式
 * @author hejinguo
 * @version $Id: HttpMethodEnum.java, v 0.1 2019年11月17日 下午5:16:21
 */
public enum HttpMethodEnum {
    POST("POST"), GET("GET");

    private String value;

    HttpMethodEnum(String type) {
        this.value = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
