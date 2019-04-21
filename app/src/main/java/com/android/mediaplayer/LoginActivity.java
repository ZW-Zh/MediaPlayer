package com.android.mediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.mediaplayer.constant.Url;
import com.android.mediaplayer.entity.LoginInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by zzw on 2019/4/17.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button login, register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("") && password.getText().toString().equals("")) {
                    Toasty.warning(LoginActivity.this, "账号或密码不为空！", Toast.LENGTH_SHORT, true).show();
                } else {
                    String url = Url.root+"loginServlet";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("name", username.getText().toString())
                            .add("pwd", password.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();

                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            System.out.println(e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            System.out.println(result);
                            Looper.prepare();
                            if (result.equals("error1")) {
                                Toasty.error(LoginActivity.this, "密码错误！", Toasty.LENGTH_SHORT).show();
                            } else if (result.equals("error2")) {
                                Toasty.error(LoginActivity.this, "账号不存在！", Toasty.LENGTH_SHORT).show();
                            } else {
                                Toasty.success(LoginActivity.this, "登录成功！", Toasty.LENGTH_SHORT).show();
                                LoginInfo loginInfo=new Gson().fromJson(result,new TypeToken<LoginInfo>(){}.getType());
                                System.out.println(loginInfo.getUserId());
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                intent.putExtra("logininfo",loginInfo);
                                startActivity(intent);
                                finish();
                            }
                            Looper.loop();
                        }
                    });
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


        String url= Url.root+"setMediaServlet";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }
}
