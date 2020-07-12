package com.didiyun.n9e.tools;

import com.sun.istack.internal.NotNull;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by yinpeng on 2020/07/11
 */
public class HttpSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSender.class);
    private volatile OkHttpClient okHttpClient = null;
    private String url;

    public HttpSender(@NotNull String url) {
        this.url = url;
    }

    public HttpSender(@NotNull String url,@NotNull OkHttpClient okHttpClient){
        this.url = url;
        this.okHttpClient = okHttpClient;
    }

    /**
     * @param requestBody 发送到POST请求的body体
     * @throws IOException
     */
    public void postJSON(String requestBody) throws IOException {
        if (okHttpClient == null) {
            okHttpClient = getOkHttpClientInstance();
        }
        MediaType mediaType = MediaType.parse(HttpSenderConstant.HTTP_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, requestBody))
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String body = response.body().string();
        if (!response.isSuccessful()) {
            LOGGER.warn("post metrics fail, response body: {}", body);
        } else {
            LOGGER.debug("post metrics succ, response body: {}", body);
        }
    }

    private OkHttpClient getOkHttpClientInstance() {
        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }

    /**
     * HttpSender常量数据
     */
    private interface HttpSenderConstant {
        String HTTP_MEDIA_TYPE = "text/x-markdown; charset=utf-8";
    }
}
