package com.jying.myokhttp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jying on 2018/1/18.
 */

public class MyOkhttp {
    private static OkHttpClient okHttpClient;

    private MyOkhttp() {
    }

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (MyOkhttp.class) {
                if (okHttpClient == null) {
                    return new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }

    /**
     * get方式
     *
     * @param url
     * @param callback
     */
    public static void doGet(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * post方式提交表单
     *
     * @param url
     * @param params
     * @param callback
     */
    public static void doPost(String url, Map<String, String> params, Callback callback) {
        FormBody.Builder formBody = new FormBody.Builder();
        for (String key : params.keySet()) {
            formBody.add(key, params.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * post方式提交表单
     *
     * @param url
     * @param json
     * @param callback
     */
    public static void doPost(String url, String json, Callback callback) {
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(jsonBody)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * post方式提交单个文件
     *
     * @param url
     * @param file
     * @param callback
     */
    public static void doFile(String url, File file, Callback callback) {
        if (!file.exists()) {
            L.e("file is not exists");
        }
        //具体的MediaType搜索mime type
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        Request request = new Request.Builder()
                .url(url)
                .post(fileBody)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * post方式提交文件和键值对
     *
     * @param url
     * @param pathName
     * @param fileName
     * @param params
     * @param callback
     */
    public static void doFile(String url, String pathName, String fileName, Map<String, String> params, Callback callback) {
        //judge file type
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        //build body
        MultipartBody.Builder body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("mPhoto", fileName, RequestBody.create(MEDIA_TYPE, new File(pathName))); //MEDIA_TYPE.type()第一个参数与服务器对应
        L.e("media.type:" + MEDIA_TYPE.type() + "----media_type;" + MEDIA_TYPE);
        for (String key : params.keySet()) {
            body.addFormDataPart(key, params.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body.build())
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 判断文件类型
     *
     * @param pathName
     * @return
     */
    private static String judgeType(String pathName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTyoe = fileNameMap.getContentTypeFor(pathName);
        if (contentTyoe == null) {
            contentTyoe = "application/octet-stream";
        }
        return contentTyoe;
    }

    /**
     * 下载文件
     *
     * @param url
     * @param fileDir  下载的文件目录
     * @param fileName 下载的文件名
     */
    public static void download(String url, final String fileDir, final String fileName) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    in = response.body().byteStream();
                    // analyze size
                    long totalSize = response.body().contentLength();
                    long sum = 0;
                    out = new FileOutputStream(new File(fileDir, fileName));
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = in.read(bytes)) != -1) {
                        sum += len;
                        //%
                        int progress = (int) (sum * 1.0f / totalSize * 100);
                        L.e("下载的百分比"+progress);
                        out.write(bytes, 0, len);
                    }
                    out.flush();
                    if (sum==totalSize){
                        L.e("下载成功");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) in.close();
                    if (out != null) out.close();
                }

            }
        });
    }

}
