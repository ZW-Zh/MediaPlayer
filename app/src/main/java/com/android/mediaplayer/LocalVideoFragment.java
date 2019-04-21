package com.android.mediaplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.mediaplayer.adapter.VideoAdapter;
import com.android.mediaplayer.entity.Video;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


/**
 * Created by zzw on 2019/4/16.
 */

public class LocalVideoFragment extends Fragment {
    private List<Video> videoList = new ArrayList<>();
    private SwipeRecyclerView swipeRecyclerView;
    private VideoAdapter adapter;
    private FloatingActionButton btn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.video_list,container,false);
        swipeRecyclerView=view.findViewById(R.id.videolist);
        getList();
        adapter = new VideoAdapter(videoList,getContext());

        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                Intent intent=new Intent(getContext(),VideoActivity.class);
                intent.putExtra("path",videoList.get(adapterPosition).getUrl());
                intent.putExtra("title",videoList.get(adapterPosition).getName());
                getContext().startActivity(intent);
            }
        });
        btn=view.findViewById(R.id.floatbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getList();
                adapter.notifyDataSetChanged();
            }
        });

        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {


                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                deleteItem.setBackgroundColor(Color.parseColor("#d81e06"));
                deleteItem.setText("删除");
                deleteItem.setTextColorResource(R.color.white);
                deleteItem.setWidth(200);
                deleteItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                rightMenu.addMenuItem(deleteItem); // 在Item右侧添加一个菜单。

                // 注意：哪边不想要菜单，那么不要添加即可。
            }
        };
        swipeRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);


        OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                menuBridge.closeMenu();
                deleteFile(videoList.get(position).getUrl(), getContext());
                Toasty.success(getContext(), "删除" + videoList.get(position).getName() + "成功", Toasty.LENGTH_SHORT).show();
                getList();
                adapter.notifyDataSetChanged();
            }
        };
        swipeRecyclerView.setOnItemMenuClickListener(mItemMenuClickListener);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        swipeRecyclerView.setLayoutManager(manager);
        swipeRecyclerView.setAdapter(adapter);
        return view;
    }
    public void getList() {
        videoList.clear();
        if (this != null) {
            Cursor cursor = this.getContext().getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String album = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long size = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    Video video = new Video();
                    video.setName(title);
                    video.setSize(size);
                    video.setUrl(path);
                    System.out.println(path);
                    video.setDuration(duration);
                    videoList.add(video);
                }
                cursor.close();
            }else {
                Toast.makeText(getActivity(), "本地没有视频哦！", Toast.LENGTH_SHORT).show();

            }
        }

    }
    public static void deleteFile(String localPath, Context context) {
        if (!TextUtils.isEmpty(localPath)) {
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();
            String url = MediaStore.Video.Media.DATA + "=?";
            int deleteRows = contentResolver.delete(uri, url, new String[]{localPath});
            //当生成图片时没有通知(插入到）媒体数据库，那么在图库里面看不到该图片，而且使用contentResolver.delete方法会返回0，此时使用file.delete方法删除文件
            File file = new File(localPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
