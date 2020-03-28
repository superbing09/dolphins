package com.shakethetree.controller;

import com.shakethetree.configuration.dto.User;
import com.shakethetree.configuration.mapper.UserMapper;
import com.shakethetree.service.WechatApi;
import com.shakethetree.util.JsonMapper;
import com.shakethetree.util.RequestUtil;
import com.shakethetree.util.WXBizMsgCrypt;
import com.shakethetree.util.XMLParse;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author xubing
 * @description //TODO 设计说明
 * @date 19-8-20
 * @copyright 中网易企秀
 */
@Controller
public class HttpController {

    private static Logger logger = LoggerFactory.getLogger(HttpController.class);

    @Value("${wechat.wanchuanjihai.appId}")
    private String appId;
    @Value("${wechat.wanchuanjihai.encodingAESKey}")
    private String encodingAESKey;
    @Value("${wechat.wanchuanjihai.token}")
    private String token;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WechatApi wechatApi;

    @RequestMapping("wechat/menu")
    @ResponseBody
    public String createMenu() {
        return wechatApi.createMenus();
    }

    @RequestMapping("wechat/get")
    @ResponseBody
    public String getAccessToken() {
        String json = restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ appId +"&secret=" + encodingAESKey, String.class);
        return json;
    }

    @RequestMapping("wechat/material")
    @ResponseBody
    public String getMeterial() {
        return wechatApi.getMaterial();
    }


    @RequestMapping("wechat/msg")
    @ResponseBody
    public String wechatMsg(HttpServletRequest request) throws Exception {
        /* 接口验证 用
        String echostr = request.getParameter("echostr");
        return echostr;*/
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String signature = request.getParameter("msg_signature");
        String body = RequestUtil.readRequestBody(request);
        // 测试环境明文
        WXBizMsgCrypt crypt = new WXBizMsgCrypt(token, encodingAESKey, appId);
        String rec = crypt.decryptMsg(signature, timestamp, nonce, body);
        Map<String, String> map = XMLParse.parseXML(rec);
        String returnMsg = dealMsgType(map);
        return crypt.encryptMsg(returnMsg, timestamp, nonce);
    }

    private String dealMsgType(Map<String, String> map) {
        String msgType = map.get("MsgType");
        switch(msgType) {
            case "text":
                return XMLParse.reSendMsg(map.get("FromUserName"), map.get("ToUserName"), doText(map));
            case "event":
                return XMLParse.parseImageMsg(map.get("FromUserName"), map.get("ToUserName"),doEvent(map));
            default:
                return "success";
        }
    }

    private String doText(Map<String, String> map) {
        String content = map.get("Content");
        System.out.println("content:" + content);
        if(content.startsWith("礼包码")) {
            String[] code = content.split("[, ]");
            return sendGift(code[1]);
        }
        return checkAndSaveUid(content);
    }

    private String checkAndSaveUid(String uid) {
        String url = "http://statistics.pandadastudio.com/player/simpleInfo?uid=%1$s";
        String result = restTemplate.getForObject(String.format(url, uid), String.class);
        Map<String, Object> map = JsonMapper.getInstance().fromJson(result, Map.class);

        if(MapUtils.getInteger(map,"code", 100) == 100) {
            return "请输入正确忍三账号id【账号id：登录游戏->面板 名称下方的ID】";
        }
        String welcome = "欢迎%1$s大佬,%2$s,从今天起，您的礼包，万川集海阁为您安排！";

        // 用户信息落库
        Map<String, String> data = (Map<String, String>) MapUtils.getMap(map, "data");
        User user = new User();
        user.setName(MapUtils.getString(data, "name"));
        user.setUid(Long.valueOf(uid));
        user.setOpenid(MapUtils.getString(map, "FromUserName"));
        userMapper.insert(user);
        return String.format(welcome, data.get("title"), data.get("name"));
    }

    private String sendGift(String code) {
        List<User> userList = userMapper.findUser();
        String url = "http://statistics.pandadastudio.com/player/giftCode?uid=%1$s&code=%2$s";
        userList.forEach(user ->
            restTemplate.getForObject(String.format(url, user.getUid().toString(), code), String.class)
        );
        return "礼包领取完成，请注意查收！";
    }

    private String doEvent(Map<String, String> map) {
        if("subscribe".equalsIgnoreCase(map.get("Event"))) {
            // 返回的是文章code码
            return "欢迎来到万川集海阁，给我一个uid[游戏id]，还您所有忍界礼包！";
        }
        if("unsubscribe".equalsIgnoreCase(map.get("Event"))) {
            userMapper.delete(MapUtils.getString(map, "FromUserName"));
            return "success";
        }
        return "success";
    }
}