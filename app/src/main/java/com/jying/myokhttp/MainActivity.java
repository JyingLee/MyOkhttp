package com.jying.myokhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
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
 * okhttp原始用法
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.image1)
    ImageView image;
    @BindView(R.id.get)
    Button doGet;
    @BindView(R.id.post)
    Button doPost;
    @BindView(R.id.postJson)
    Button doPostJson;
    OkHttpClient okHttpClient = new OkHttpClient();
    private String url = "http://192.168.191.1:8082/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public void doGet(View view) throws IOException {
        //1.拿到okhttpClient对象
//        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url + "login?username=123&password=456").build();
        //3.将request封装成Call
        executeRequest(request);
//        Call call = okHttpClient.newCall(request);
        //4.执行Call
//        Response response=call.execute(); //同步请求
//        call.enqueue(new Callback() { //异步请求
//            @Override
//            public void onFailure(Call call, IOException e) {
//                L.e(e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                L.e("get:"+response.body().string());
//            }
//        });
    }

    public void doPost(View view) {
        RequestBody formBody = new FormBody.Builder()
                .add("username", "132")
                .add("password", "555")
                .build();
        Request request = new Request.Builder()
                .url(url + "login")
                .post(formBody)
                .build();
        executeRequest(request);
    }

    public void doPostJson(View view) {
        RequestBody jsonBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), "{username=123,password=456}");
        Request request = new Request.Builder()
                .url(url + "postJson")
                .post(jsonBody)
                .build();
        executeRequest(request);
    }

    public void doPostFile(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        if (!file.exists()) {
            L.e("文件不存在");
            return;
        }
        //具体的MediaType搜索mime type
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        Request request = new Request.Builder()
                .url(url + "postFile")
                .post(fileBody)
                .build();
        executeRequest(request);
    }

    public void doUpload(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        if (!file.exists()) {
            L.e("文件不存在");
            return;
        }
        RequestBody uploadBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("username", "333")
                .addFormDataPart("password", "555")
                .addFormDataPart("mPhoto", "ttt.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder()
                .url(url + "doUpload")
                .post(uploadBody)
                .build();
        executeRequest(request);
    }

    public void download(View view) {
        //2.构造request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url + "todo.png").build();
        //3.将request封装成Call
        Call call = okHttpClient.newCall(request);
        //4.执行Call
        call.enqueue(new Callback() { //异步请求
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream in = response.body().byteStream();
                File file = new File(Environment.getExternalStorageDirectory(), "aaa.png");
                FileOutputStream out = new FileOutputStream(file);
                byte[] bytes = new byte[128];
                int len = 0;
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                }
                in.close();
                out.flush();
                out.close();
                L.e("下载成功");
            }
        });
    }

    public void loadImage(View view) {
        //2.构造request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url + "todo.png").build();
        //3.将request封装成Call
        Call call = okHttpClient.newCall(request);
        //4.执行Call
        call.enqueue(new Callback() { //异步请求
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream in = response.body().byteStream();
                //如果图片过大，可进行二次采样
                final Bitmap bitmap = BitmapFactory.decodeStream(in);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        image.setImageBitmap(bitmap);
                    }
                });
                L.e("下载成功");
            }
        });
    }

    private void executeRequest(Request request) {
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e("test:" + response.body().string());
            }
        });
    }

}
