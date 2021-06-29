/**
 * DianMei.com Inc.
 * Copyright (c) 2015-2019 All Rights Reserved.
 */
package com.simple.common.httpClient.util;

/**
 * httpClient工具类
 * @author hejinguo
 * @version $Id: HttpClientConfig.java, v 0.1 2019年11月17日 下午5:15:31
 */
public class HttpClientConfig {
    public static final String UTF8                  = "UTF-8";
    /** 
     * 最大连接数(连接池设置) 
     */
    public final static int    MAX_TOTAL_CONNECTIONS = 500;

    /**
     * 路由基础的连接数
     */
    public final static int    MAX_PER_ROUTE         = 20;

    /** 
    * 与服务器建立连接超时时间 (45s)
    */
    public final static int    CONNECT_TIMEOUT       = 45000;

    /** 
     * 与服务器建立连接超时时间 (45s)
     */
    public final static int    SOCKET_TIMEOUT        = 45000;

    /**
    * 从服务器获取响应超时时间(45s)
    */
    public final static int    SO_TIMEOUT            = 45000;

    /**
     * 空闲连接时长(5s)
     */
    public final static int    HttpIdelTimeout       = 5000;

    /**
     * 检测频率(30s)
     */
    public final static int    HttpMonitorInterval   = 30000;
}
