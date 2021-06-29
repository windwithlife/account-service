package com.simple.common.wechat;

import com.alibaba.fastjson.JSONObject;

import com.simple.common.error.ServiceException;
import com.simple.common.httpClient.HttpClientHelp;
import com.simple.common.httpClient.util.HttpContentTypeEnum;
import com.simple.common.httpClient.util.HttpMethodEnum;
import com.simple.common.props.ApplicationConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class WechatHelper {
    private static final Logger logger                             = LoggerFactory.getLogger(WechatHelper.class);
    public static String getWechatOpenId(String code){
        //step1:获取并替换url地址
        String requestUrl = ApplicationConfig.wecaht_meetinglive_jscode2session
                .replace("APPID", ApplicationConfig.wecaht_meetinglive_appid)
                .replace("SECRET", ApplicationConfig.wecaht_meetinglive_appsecret)
                .replace("JSCODE", code);

        String result = HttpClientHelp.getInstance().submit(requestUrl, HttpMethodEnum.GET, HttpContentTypeEnum.FORM, null);
        if (StringUtils.isEmpty(result)) {
            throw new ServiceException("获取微信信息失败,请重新打开!");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        int error_code = jsonObject.getIntValue("errcode");
        if (error_code != 0) {
            throw new ServiceException("获取微信信息失败,请重新打开!");
        }
        //step3:获取openId信息
        String openId = jsonObject.getString("openid");
        if (StringUtils.isBlank(openId)) {
            throw new ServiceException("获取微信信息失败,请重新打开!");
        }
        String sessionKey = jsonObject.getString("session_key");
        return  openId;
    }
    public static String getWechatAccessToken(){
        //请求地址
        String requestUrl = ApplicationConfig.wecaht_meetinglive_meetinglive_accesstoken
                .replace("APPID", ApplicationConfig.wecaht_meetinglive_appid)
                .replace("APPSECRET", ApplicationConfig.wecaht_meetinglive_appsecret);
        //step2:请求并返回信息
        String result = HttpClientHelp.getInstance().submit(requestUrl, HttpMethodEnum.GET,
                HttpContentTypeEnum.FORM, null);
        if (StringUtils.isBlank(result)) {
            //logger.error("获取微信AccessToken调用接口返回结果为空!");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        int err_code = jsonObject.getIntValue("errcode");
        if (err_code != 0) {
            String errmsg = jsonObject.getString("errmsg");
            logger.error("获取微信AccessToken调用接口返回异常状态,结果信息：errcode--->{},errmsg--->{}", err_code,
                    errmsg);
            return null;
        }
        String accessToken = jsonObject.getString("access_token");
        int expiresIn = jsonObject.getInteger("expires_in");
        String refreshToken = jsonObject.getString("refresh_token");
        String scope = jsonObject.getString("scope");

        return accessToken;
    }

    public static WechatAccessToken getWechatOauth2AccessToken(String code) {
        //step1:调用接口获取accessToken
        String requestUrl = ApplicationConfig.oauthAccessToken
                .replace("APPID", ApplicationConfig.publicAppId)
                .replace("APPSECRET", ApplicationConfig.publicSecret).replace("CODE", code);
        //step2:请求并返回信息
        String result = HttpClientHelp.getInstance().submit(requestUrl, HttpMethodEnum.GET,
                HttpContentTypeEnum.FORM, null);
        if (StringUtils.isBlank(result)) {
            logger.error("获取微信AccessToken调用接口返回结果为空!");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        int errcode = jsonObject.getIntValue("errcode");
        if (errcode != 0) {
            String errmsg = jsonObject.getString("errmsg");
            logger.error("获取微信AccessToken调用接口返回异常状态,结果信息：errcode--->{},errmsg--->{}", errcode,
                    errmsg);
            return null;
        }
        String openId = jsonObject.getString("openid");
        if (StringUtils.isBlank(openId)) {
            logger.error("获取公众号授权信息返回openId为空!参数信息:code-->{},openId--->{},result-->{}", code,
                    openId, result);
            return null;
        }
        String accessToken = jsonObject.getString("access_token");
        Integer expiresIn = jsonObject.getInteger("expires_in");
        String refreshToken = jsonObject.getString("refresh_token");
        String scope = jsonObject.getString("scope");
        WechatAccessToken wechatAccessToken = WechatAccessToken
                .builder()
                .openId(openId)
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .scope(scope)
                .build();
        return wechatAccessToken;
    }
    public static String getWechatOauthUrl(String callbackUrl) {

        //step  1:获取请求参数
        if (StringUtils.isBlank(callbackUrl)) {
            return null;
        }
        String requestUrl = ApplicationConfig.userAauth2
                .replace("APPID", ApplicationConfig.publicAppId).replace("REDIRECT_URI", callbackUrl)
                .replace("SCOPE", "snsapi_userinfo");
        return requestUrl;

    }
    //根据accessToken与openId获取用户信息。
    public static WechatUserInfo getWechatPublisherUserInfo(String accessToken, String openId){
        //step1:获取并替换url地址
        String requestUrl = ApplicationConfig.oauthUserInfo.replace("ACCESS_TOKEN", accessToken)
                .replace("OPENID", openId);
        //step2:请求并返回信息
        String result = HttpClientHelp.getInstance().submit(requestUrl, HttpMethodEnum.GET,
                HttpContentTypeEnum.FORM, null);
        if (StringUtils.isEmpty(result)) {
            logger.error("获取公众号授权用户信息返回结果为空!参数信息:accessToken-->{},openId-->{},result-->{}",accessToken, openId, result);
            return null;
        }
        //转码返回结果
        try {
            result=new String(result.getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("获取公众号授权用户信息转码异常!", e);
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        int err_code = jsonObject.getIntValue("errcode");
        if (err_code != 0) {
            logger.error("获取公众号授权用户信息返回状态失败!参数信息:accessToken-->{},openId-->{},result-->{}",accessToken, openId, result);
            return null;
        }

        //获取用户信息
        String nickname = jsonObject.getString("nickname");
        Integer sex = jsonObject.getInteger("sex");
        String province = jsonObject.getString("province");
        String city = jsonObject.getString("city");
        String country = jsonObject.getString("country");
        String headImgUrl = jsonObject.getString("headimgurl");
        String unionId = jsonObject.getString("unionid");
        WechatUserInfo userInfo = WechatUserInfo.builder().nickName(nickname).openId(openId).unionId(unionId).headImgUrl(headImgUrl)
                .sex(sex).province(province).city(city).country(country).build();
        return userInfo;
    }
    public static WechatUserInfo getWechatPublisherUserInfoByCode(String code){
        WechatAccessToken token =  getWechatOauth2AccessToken(code);
        return getWechatPublisherUserInfo(token.getAccessToken(),token.getOpenId());

    }
}
