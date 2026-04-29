package com.dailyq.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.dailyq.config.WeChatConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WeChatUtil {

    private final WeChatConfig weChatConfig;

    public WeChatUtil(WeChatConfig weChatConfig) {
        this.weChatConfig = weChatConfig;
    }

    public JSONObject code2Session(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session" +
                "?appid=" + weChatConfig.getAppid() +
                "&secret=" + weChatConfig.getSecret() +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        
        log.info("请求微信登录: {}", url);
        String result = HttpUtil.get(url);
        log.info("微信登录响应: {}", result);
        
        return JSONObject.parseObject(result);
    }
}
