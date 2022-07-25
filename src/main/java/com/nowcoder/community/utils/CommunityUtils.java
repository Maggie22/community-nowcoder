package com.nowcoder.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtils {

    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String md5(String str){
        if(str == null)
            return null;
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map!=null){
            for(String key: map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code){
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }
}
