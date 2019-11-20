package com.shakethetree.service;

import com.shakethetree.util.JsonMapper;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xubing
 * @description //TODO 设计说明
 * @date 19-10-26
 * @copyright 中网易企秀
 */
@Service
public class WechatApi {

    private static final String appId = "wx5743efca81aa7259";

    private static final String secret = "da1d0853928f56d2fb3b0a227e3b2ee5";

/*
    private static final String appId = "wx09e5e9551b287edc";

    private static final String secret = "13a968dcd5feb1bc70ab880c55e58e2a";
*/

    @Autowired
    private RestTemplate restTemplate;

    private String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%1$s&secret=%2$s";
        String json = restTemplate.getForObject(String.format(url, appId, secret), String.class);
        Map<String, String> accessToken = JsonMapper.getInstance().fromJson(json, Map.class);
        return MapUtils.getString(accessToken, "access_token");
    }


    public String getMaterial() {
        String materialUrl = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=%1$s";
        Map<String, Object> params = new HashMap(3);
        params.put("type", "image");
        params.put("offset", 0);
        params.put("count", 20);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
        return restTemplate.postForEntity(String.format(materialUrl, getAccessToken()), request, String.class).getBody();
    }

    public String createMenus() {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%1$s";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{\"button\":[{\"name\":\"菜单01\",\"sub_button\":[{\"type\":\"view\",\"name\":\"搜索1\",\"url\":\"http://www.soso.com/\"},{\"type\":\"view\",\"name\":\"搜索2\",\"url\":\"https://www.taptap.com/\"}]},{\"name\":\"菜单2\",\"sub_button\":[{\"type\":\"view\",\"name\":\"搜索\",\"url\":\"http://www.soso.com/\"}]},{\"name\":\"菜单3\",\"sub_button\":[{\"type\":\"view\",\"name\":\"搜索\",\"url\":\"http://www.soso.com/\"}]}]}";
        System.out.println(json);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        return restTemplate.postForEntity(String.format(url, getAccessToken()), request, String.class).getBody();
    }
}
