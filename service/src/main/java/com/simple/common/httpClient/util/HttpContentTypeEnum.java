package com.simple.common.httpClient.util;

/**
 * 请求数据类型
 * @author hejinguo
 * @version $Id: HttpContentTypeEnum.java, v 0.1 2019年11月17日 下午5:16:08
 */
public enum HttpContentTypeEnum {
    FORM("FROM"), JSON("JSON");

    private String value;

    HttpContentTypeEnum(String type) {
        this.value = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
