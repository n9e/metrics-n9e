package com.didiyun.n9e.example;

import com.alibaba.fastjson.JSONObject;
import com.didiyun.n9e.tools.HttpSender;
import okhttp3.*;

import java.io.IOException;

/**
 * Created by yinpeng on 2020/07/11
 */
public class HttpSenderExample {

    volatile static OkHttpClient okHttpClient = null;
    static String ipAddr = "http://127.0.0.1:8080?a=123";

    public static void main(String[] args) {
        HttpSender sender = new HttpSender(ipAddr);
        JSONObject obj = new JSONObject();
        obj.put("daxaing","MMMMMM");
        try{
            sender.sendHttp(obj.toJSONString());
        }catch (Exception e){
            System.out.println("xxx/xx");
            sender.writeContent(obj.toJSONString());
        }
    }

    public static void send1000000(){
        for (int i=0;i<1000000;i++){
            MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
            String requestBody = "I am Jdqm. index="+i;
            Request request = new Request.Builder()
                    .url(ipAddr)
                    .post(RequestBody.create(mediaType, requestBody))
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {

                public void onFailure(Call call, IOException e) {
                    System.out.println("onFailure: " + e.getMessage());
                }

                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(response.protocol() + " " +response.code() + " " + response.message());
                    Headers headers = response.headers();
                    for (int i = 0; i < headers.size(); i++) {
                        System.out.println(headers.name(i) + ":" + headers.value(i));
                    }
                    System.out.println("onResponse: " + response.body().string());
                }
            });
        }
    }

    public static void sender(){
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        String requestBody = "I am Jdqm.";
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, requestBody)).headers(setHeaders())
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {

            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure: " + e.getMessage());
            }


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.protocol() + " " +response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    System.out.println(headers.name(i) + ":" + headers.value(i));
                }
                System.out.println("onResponse: " + response.body().string());
            }
        });
    }



    public static Headers setHeaders() {
        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add("Connection", "close");
        Headers headers = headersbuilder.build();
        return headers;
    }


    public static OkHttpClient getOkHttpClientInstance() {
        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient =  new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }

}
