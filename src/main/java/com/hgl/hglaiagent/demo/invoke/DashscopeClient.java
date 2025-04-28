package com.hgl.hglaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
   import cn.hutool.http.HttpUtil;
   import cn.hutool.json.JSONObject;

   /**
    * @author 请别把我整破防
    * @Desription: 使用HTTP接入灵积 AI
    */
   public class DashscopeClient {

       private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
       private static final String API_KEY = TestApiKey.API_KEY;

       public static void main(String[] args) {
           String response = sendChatCompletionRequest();
           System.out.println(response);
       }

       public static String sendChatCompletionRequest() {
           // 构建请求体
           JSONObject requestBody = new JSONObject();
           requestBody.set("model", "qwen-plus");
           requestBody.set("messages", new JSONObject[] {
                   new JSONObject().set("role", "system").set("content", "You are a helpful assistant."),
                   new JSONObject().set("role", "user").set("content", "你是谁？")
           });

           // 发送POST请求

           return HttpRequest.post(API_URL)
                   .header("Authorization", "Bearer " + API_KEY)
                   .header("Content-Type", "application/json")
                   .body(requestBody.toString())
                   .execute()
                   .body();
       }
   }
   