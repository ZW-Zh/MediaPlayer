package com.android.mediaplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.mediaplayer.adapter.MusicAdapter;
import com.android.mediaplayer.constant.Url;
import com.android.mediaplayer.entity.Music;
import com.android.mediaplayer.entity.WebMusic;
import com.android.mediaplayer.utils.Common;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by zzw on 2019/4/18.
 */

public class LikedMusicActivity extends AppCompatActivity {
    private ListView listView;
    private List<Music> webMusicList=new ArrayList<>();
    private MusicAdapter adapter;
    private int userId;
    private FloatingActionButton btn;
    private ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.likemusic_layout);
        userId=getIntent().getIntExtra("userid",0);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        listView=findViewById(R.id.list);
        btn=findViewById(R.id.floatbutton);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (Music m : Common.likeMusicList
                        ) {
                    m.isPlaying = false;
                }
                int position= Common.likeMusicList.indexOf(webMusicList.get(i));
                Common.likeMusicList.get(position).isPlaying = true;
                //更新界面
                adapter.notifyDataSetChanged();
                //intent实现页面的跳转，getActivity()获取当前的activity， MusicActivity.class将要调转的activity
                Intent intent = new Intent(LikedMusicActivity.this, MusicActivity.class);
                //使用putExtra（）传值
                intent.putExtra("position", position);
                intent.putExtra("userid",userId);
                intent.putExtra("mode",1);
                startActivity(intent);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMusic();
            }
        });
        setMusic();
        adapter=new MusicAdapter(LikedMusicActivity.this,webMusicList);
        listView.setAdapter(adapter);


    }
    private void refresh(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void setMusic(){
        Common.likeMusicList.clear();
        webMusicList.clear();
        String url = Url.root+"getLikedMusic";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", String.valueOf(userId))
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
                List<WebMusic> musicList=new Gson().fromJson(result,new TypeToken<List<WebMusic>>(){}.getType());
                for(WebMusic m:musicList){
                    Music music=new Music();
                    music.setId(m.getId());
                    music.setLike(true);
                    music.setPath(Url.root+"Media/"+m.getPath());
                    music.setAlbum(m.getAlbum());
                    music.setArtist(m.getArtist());
                    music.setLrcPath(Url.root+"Media/"+m.getLrcPath());
                    music.setTitle(m.getTitle());
                    music.setLength(m.getLength());
                    Glide.with(LikedMusicActivity.this)
                            .asBitmap() //指定格式为Bitmap
                            .load(Url.root+"Media/"+m.getAlbumBip())
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    //加载失败
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    music.setAlbumBip(resource);
                                    Common.likeMusicList.add(music);
                                    webMusicList.add(music);
                                    refresh();
                                    return false;
                                }
                            })
                            .into(100,100);//加载原图大小
                }
            }
        });
    }
}
