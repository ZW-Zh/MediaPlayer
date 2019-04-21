package com.android.mediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.mediaplayer.adapter.VideoAdapter;
import com.android.mediaplayer.adapter.WebVideoAdapter;
import com.android.mediaplayer.constant.Url;
import com.android.mediaplayer.entity.Video;
import com.android.mediaplayer.entity.WebVideo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by zzw on 2019/4/16.
 */

public class WebVideoFragment extends Fragment {
    private List<Video> videoList = new ArrayList<>();
    private ListView listView;
    private WebVideoAdapter adapter;
    private FloatingActionButton btn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.localmedia_layout,container,false);
        getVideo();
        listView=view.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("aaa");
                Intent intent=new Intent(getContext(),VideoActivity.class);
                intent.putExtra("path",videoList.get(i).getUrl());
                intent.putExtra("title",videoList.get(i).getName());
                getContext().startActivity(intent);
            }
        });
        adapter=new WebVideoAdapter(getContext(),videoList);
        listView.setAdapter(adapter);


        btn= view.findViewById(R.id.floatbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                getVideo();
            }
        });
        return view;
    }
    private void getVideo(){
        videoList.clear();
        String url= Url.root+"getVideoServlet";
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
                String result=response.body().string();
                List<WebVideo> videos=new Gson().fromJson(result,new TypeToken<List<WebVideo>>(){}.getType());
                for(WebVideo v:videos) {
                    Video video = new Video();
                    video.setUrl(Url.root+"Media/"+v.getPath());
                    video.setDuration(v.getTime());
                    video.setName(v.getTitle());
                    videoList.add(video);
                }
                refresh();
            }
        });
    }
    private void refresh(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}

