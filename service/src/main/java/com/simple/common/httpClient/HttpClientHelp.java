package com.simple.common.httpClient;

import com.simple.common.httpClient.impl.HttpClientServiceImpl;
import com.simple.common.httpClient.util.HttpContentTypeEnum;
import com.simple.common.httpClient.util.HttpMethodEnum;
import com.simple.common.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;


public class HttpClientHelp {
    private static IHttpClientService httpClientService = null;

    private HttpClientHelp() {
        httpClientService = new HttpClientServiceImpl();
    }

    private static class HttpClientHelpHolder {
        private static HttpClientHelp INSTACE = new HttpClientHelp();
    }

    public static HttpClientHelp getInstance() {
        return HttpClientHelpHolder.INSTACE;
    }

    /**
     * 调用service服务端接口方法
     * @param url 调用接口的URL地址
     * @param submitType 提交方式(get,post)
     * @param contentType 请求数据格式(from,json)
     * @param parameterMap 需要提交的参数数据,map中key值需要对应service服务端中对应方法的参数名,value值必须为String或Json字符串
     * @return 返回统一为json格式字符串
     */
    public String submit(String url, HttpMethodEnum submitType, HttpContentTypeEnum contentType,
                         Object parameterMap) {
        //step1:校验参数是否为空
        if (StringUtils.isBlank(url)) {
            return JsonUtil.returnJsonUnSuccess("请求URL地址不能为空!");
        }
        //step2:根据提交方式调用不同的方法
        if (submitType.getValue() == HttpMethodEnum.GET.getValue()) {
            return httpClientService.get(url, parameterMap);
        } else if (submitType.getValue() == HttpMethodEnum.POST.getValue()) {
            return httpClientService.post(url, contentType, parameterMap);
        } else {
            return JsonUtil.returnJsonUnSuccess("参数submitType参数类型错误!");
        }
    }
}
