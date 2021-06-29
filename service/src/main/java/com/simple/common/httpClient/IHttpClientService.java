package com.simple.common.httpClient;

import com.simple.common.httpClient.util.HttpContentTypeEnum;


public interface IHttpClientService {

    String post(String url, HttpContentTypeEnum contentType, Object parameterMap);

    String get(String url, Object parameterMap);
}
