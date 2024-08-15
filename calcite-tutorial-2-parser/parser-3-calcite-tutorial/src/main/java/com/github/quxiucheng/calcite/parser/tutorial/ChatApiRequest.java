package com.github.quxiucheng.calcite.parser.tutorial;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ChatApiRequest {

  public static class Result {
    private int code;
    private String message;

    public Result(int code, String message) {
      this.code = code;
      this.message = message;
    }

    // Getters
    public int getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }
  }

  public static Result sendChatMessage(String message) {
    String url = "https://spark-api-open.xf-yun.com/v1/chat/completions";
    String apiKey = "Bearer 123456"; // 替换成你自己的API密钥

    // 创建请求体
    JSONObject data = new JSONObject();
    JSONObject userMessage = new JSONObject();
    userMessage.put("role", "user");
    userMessage.put("content", message);

    JSONArray messages = new JSONArray();
    messages.put(userMessage);
    data.put("model", "generalv3.5");
    data.put("messages", messages);
    data.put("stream", true);

    try {
      // 创建URL对象
      URL obj = new URL(url);
      // 打开连接
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      // 设置请求方法
      con.setRequestMethod("POST");
      // 设置内容类型
      con.setRequestProperty("Content-Type", "application/json");
      // 设置认证信息
      con.setRequestProperty("Authorization", apiKey);
      // 发送POST请求必须设置
      con.setDoOutput(true);

      // 获取输出流并写入请求体
      try(OutputStream os = con.getOutputStream()) {
        byte[] input = data.toString().getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // 读取响应
      int responseCode = con.getResponseCode();
      try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }

        // 将响应转换为Result对象
        JSONObject jsonResponse = new JSONObject(response.toString());
        int resultCode = (Integer) jsonResponse.get("code");
        String resultMessage = (String) jsonResponse.get("message");

        return new Result(resultCode, resultMessage);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new Result(500, "Internal Server Error");
    }
  }

  public static void main(String[] args) {
    Result result = sendChatMessage("你是谁");
    System.out.println("Code: " + result.getCode());
    System.out.println("Message: " + result.getMessage());
  }
}
