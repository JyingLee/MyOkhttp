package com.jying.myokhttp;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Jying on 2018/1/18.
 */

public class OkhttpTest extends AppCompatActivity {
    private String url = "http://192.168.191.1:8082/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doGet(View view) {
        MyOkhttp.doGet(url + "login?username=123&password=456", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e(response.body().string());
            }
        });
    }

    public void doPost(View view) {
        Map<String, String> maps = new HashMap<>();
        maps.put("username", "jying");
        maps.put("password", "jying");
        MyOkhttp.doPost(url + "login", maps, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e(response.body().string());
            }
        });
    }

    public void doPostJson(View view) {
        String json = "username=jying,password=jying";
        MyOkhttp.doPost(url + "postJson", json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e(response.body().string());
            }
        });
    }

    public void doPostFile(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        if (!file.exists()) {
            L.e("文件不存在");
            return;
        }
        MyOkhttp.doFile(url + "postFile", file, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e(response.body().string());
                L.e("上传单一文件成功");
            }
        });
    }

    public void doUpload(View view) {
        Map<String, String> maps = new HashMap<>();
        maps.put("username", "jying");
        maps.put("password", "jying");
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        if (!file.exists()) {
            L.e("文件不存在");
            return;
        }
        MyOkhttp.doFile(url + "doUpload", file.getPath(), file.getName(), maps, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e(response.body().string());
            }
        });
    }

    public void download(View view) {
        L.e("开始下载");
        String fileDir = String.valueOf(Environment.getExternalStorageDirectory());
        String fileName = "aalalala.png";
        MyOkhttp.download(url + "todo.png", fileDir, fileName);
    }

}
