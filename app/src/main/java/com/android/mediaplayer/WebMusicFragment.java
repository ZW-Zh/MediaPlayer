package com.android.mediaplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.mediaplayer.adapter.MusicAdapter;
import com.android.mediaplayer.constant.Url;
import com.android.mediaplayer.entity.LoginInfo;
import com.android.mediaplayer.entity.Music;
import com.android.mediaplayer.entity.MusicType;
import com.android.mediaplayer.entity.WebMusic;
import com.android.mediaplayer.utils.Common;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by zzw on 2019/4/16.
 */

public class WebMusicFragment extends Fragment {
    private List<Music> webMusicList=new ArrayList<>();
    private ListView listView;
    private MusicAdapter adapter;
    private FloatingActionButton btn,likeBtn,search;
    private PopupWindow popupWindow;
    private LoginInfo loginInfo;
    private View popupView;
    private TabLayout tabLayout;
    List<MusicType> typeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webmusic_layout, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            loginInfo = (LoginInfo) bundle.getSerializable("logininfo");
            System.out.println("userId" + loginInfo.getUserId());

        }
        likeBtn=view.findViewById(R.id.likebtn);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),LikedMusicActivity.class);
                intent.putExtra("userid",loginInfo.getUserId());
                startActivity(intent);
            }
        });
        listView=view.findViewById(R.id.list);
        btn=view.findViewById(R.id.floatbutton);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (Music m : Common.musicList
                        ) {
                    m.isPlaying = false;
                }
                int position= Common.musicList.indexOf(webMusicList.get(i));
                Common.musicList.get(position).isPlaying = true;
                //更新界面
                adapter.notifyDataSetChanged();
                //intent实现页面的跳转，getActivity()获取当前的activity， MusicActivity.class将要调转的activity
                Intent intent = new Intent(getActivity(), MusicActivity.class);
                //使用putExtra（）传值
                intent.putExtra("position", position);
                intent.putExtra("userid",loginInfo.getUserId());
                intent.putExtra("mode",0);
                startActivity(intent);
            }
        });

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

            }
        });
        adapter=new MusicAdapter(getContext(),webMusicList);
        listView.setAdapter(adapter);

        search=view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });


        tabLayout=view.findViewById(R.id.tablayout);
        String url= Url.root+"getTypeServlet";
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
                typeList=new Gson().fromJson(result,new TypeToken<List<MusicType>>(){}.getType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(MusicType type:typeList){
                            tabLayout.addTab(tabLayout.newTab().setText(type.getType()).setTag(type.getId()));
                        }
                    }
                });
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getMusic((int)tab.getTag());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    private void getMusic(int type){
        Common.musicList.clear();
        webMusicList.clear();
        String url= Url.root+"getMusicServlet";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("type", String.valueOf(type))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
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
                List<WebMusic> musicList=new Gson().fromJson(result,new TypeToken<List<WebMusic>>(){}.getType());
                for(WebMusic m:musicList){
                    Music music=new Music();
                    music.setId(m.getId());

                    boolean flag=false;
                    for(int id:loginInfo.getMusicId()){
                        if(id==m.getId()){
                            music.setLike(true);
                            flag=true;
                        }
                    }
                    if(!flag){
                        music.setLike(false);
                    }
                    music.setPath(Url.root+"Media/"+m.getPath());
                    music.setAlbum(m.getAlbum());
                    music.setArtist(m.getArtist());
                    music.setLrcPath(Url.root+"Media/"+m.getLrcPath());
                    music.setTitle(m.getTitle());
                    music.setLength(m.getLength());
                    Glide.with(getContext())
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
                                    //System.out.println("123123"+music.getId());
                                    Common.musicList.add(music);
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
    private void refresh(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void pop(){
        lightoff();
        if (popupWindow == null) {
            popupView = View.inflate(getContext(), R.layout.search_popwindow, null);
            // 参数2,3：指明popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lighton();
                }
            });
            popupWindow.setAnimationStyle(R.style.take_photo_anim);
            // 设置背景图片， 必须设置，不然动画没作用
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setFocusable(true);

            // 设置点击popupwindow外屏幕其它地方消失
            popupWindow.setOutsideTouchable(true);
            popupView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            popupView.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 打开系统图库选择图片
                    EditText editText=popupView.findViewById(R.id.search);
                    if("".equals(editText.getText().toString())){
                        Toasty.warning(getContext(),"搜索不为空！",Toasty.LENGTH_SHORT).show();
                    }else {
                        Intent intent=new Intent(getContext(),SearchResultActivity.class);
                        intent.putExtra("search",editText.getText().toString());
                        intent.putExtra("userid",loginInfo.getUserId());
                        startActivity(intent);
                    }
                    editText.setText("");
                    popupWindow.dismiss();
                    lighton();
                }
            });
        }

        // 在点击之后设置popupwindow的销毁
        if (popupWindow.isShowing()) {
            lighton();
            popupWindow.dismiss();

        }

        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(getActivity().findViewById(R.id.layout), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);

    }
    /**
     * 设置手机屏幕亮度变暗
     */
    private void lightoff() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.3f;
                getActivity().getWindow().setAttributes(lp);
            }
        }, 250);

    }

    /**
     * 设置手机屏幕亮度显示正常
     */
    private void lighton() {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 1f;
        getActivity().getWindow().setAttributes(lp);
    }
}
