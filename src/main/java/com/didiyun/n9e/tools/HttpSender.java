package com.didiyun.n9e.tools;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yinpeng on 2020/07/11
 */
public class HttpSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSender.class);
    volatile static OkHttpClient okHttpClient = null;
    private String ipAddr = null;

    public HttpSender(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    /**
     * @param requestBody 发送到POST请求的body体
     * @deprecated 固定发送POST  header内将Connection改为close
     * @throws IOException
     */
    public void sendHttp(String requestBody) throws IOException {
        if (okHttpClient == null) {
            okHttpClient = getOkHttpClientInstance();
        }
        MediaType mediaType = MediaType.parse(HttpSenderConstant.HTTP_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(ipAddr)
                .post(RequestBody.create(mediaType, requestBody)).headers(setHeaders())
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException(String.format("http response code not equals [%s]", response.code()));
    }

    private Headers setHeaders() {
        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add(HttpSenderConstant.HTTP_HEADER_CONNECTION, HttpSenderConstant.HTTP_HEADER_CONNECTION_CLOSE);
        return headersbuilder.build();
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
     * @param content 写入错误文件的内容
     */
    public void writeContent(String content) {
        String filePath = System.getProperty("user.dir") + HttpSenderConstant.FILE_NAME;
        LOGGER.info("Write failure data to {}", filePath);
        File file = new File(filePath);
        try {
            if (!file.exists()) file.createNewFile();
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), HttpSenderConstant.FILE_APPEND);
            BufferedWriter out = new BufferedWriter(fileWriter);
            try {
                out.write(getNowStr());
                out.write(content);
                out.write(System.getProperty("line.separator"));
            } finally {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            LOGGER.error("write file error {}", e.getMessage());
        }
    }

    private String getNowStr() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(HttpSenderConstant.DATE_FORMAT);
        return format.format(date);
    }

    /**
     * @deprecated HttpSender常量数据
     */
    private interface HttpSenderConstant {
        boolean FILE_APPEND = true;
        String FILE_NAME = "n9e-data.log";
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        String HTTP_HEADER_CONNECTION = "Connection";
        String HTTP_HEADER_CONNECTION_CLOSE = "close";
        String HTTP_MEDIA_TYPE = "text/x-markdown; charset=utf-8";
    }
}
