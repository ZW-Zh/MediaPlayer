package com.android.mediaplayer;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.mediaplayer.adapter.LocalMusicAdapter;
import com.android.mediaplayer.adapter.MusicAdapter;
import com.android.mediaplayer.entity.Music;

import com.android.mediaplayer.utils.Common;
import com.master.permissionhelper.PermissionHelper;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

/**
 * Created by zzw on 2019/4/16.
 */

public class LocalMusicFragment extends Fragment {
    private PermissionHelper permissionHelper;
    private ListView listView;
    private SwipeRecyclerView swipeRecyclerView;
    private LocalMusicAdapter adapter;
    private List<Music> musicList = new ArrayList<>();                                                          //将Music放入List集合中，并实例化List<Music>
    //private List<ListView> listViewList;
    //private MusicAdapter adapter;
    private FloatingActionButton btn;

    public LocalMusicFragment() {
        // Required empty public constructor
    }

    //LoginFrangment中的onCreate()方法
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new LocalMusicAdapter(musicList, getContext());
        View view = inflater.inflate(R.layout.local_music_layout, container, false);        //创建View对象，返回view

        // 关于权限的代码
        permissionHelper = new PermissionHelper(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                initListView();
                adapter.notifyDataSetChanged();
                //获取权限后扫描数据库获取信息
                Log.d(TAG, "onPermissionGranted() called");
            }

            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {
                Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",", grantedPermission) + "]");
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "onPermissionDenied() called");
            }

            @Override
            public void onPermissionDeniedBySystem() {
                Log.d(TAG, "onPermissionDeniedBySystem() called");
            }
        });
// 权限代码结束

        btn = view.findViewById(R.id.floatbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initListView();
                adapter.notifyDataSetChanged();
            }
        });
        //对Listview进行监听
        swipeRecyclerView = view.findViewById(R.id.list);
        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                for (Music m : Common.localMusicList
                        ) {
                    m.isPlaying = false;
                }
                Common.localMusicList.get(adapterPosition).isPlaying = true;
                //更新界面
                adapter.notifyDataSetChanged();
                //intent实现页面的跳转，getActivity()获取当前的activity， MusicActivity.class将要调转的activity
                Intent intent = new Intent(getActivity(), MusicActivity.class);
                //使用putExtra（）传值
                intent.putExtra("position", adapterPosition);
                intent.putExtra("mode", 2);
                startActivity(intent);
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
                deleteFile(musicList.get(position).getPath(), getContext());
                Toasty.success(getContext(), "删除" + musicList.get(position).getTitle() + "成功", Toasty.LENGTH_SHORT).show();
                initListView();
                adapter.notifyDataSetChanged();
            }
        };
        swipeRecyclerView.setOnItemMenuClickListener(mItemMenuClickListener);
        //initListView();
        for (Music m : musicList) {
            System.out.println(m);
        }

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        swipeRecyclerView.setLayoutManager(manager);
        swipeRecyclerView.setAdapter(adapter);


//        listView = view.findViewById(R.id.list);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {             //将listView的每一个item实现监听
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for (Music m : Common.localMusicList
//                        ) {
//                    m.isPlaying = false;
//                }
//                Common.localMusicList.get(position).isPlaying = true;
//                //更新界面
//                adapter.notifyDataSetChanged();
//                //intent实现页面的跳转，getActivity()获取当前的activity， MusicActivity.class将要调转的activity
//                Intent intent = new Intent(getActivity(), MusicActivity.class);
//                //使用putExtra（）传值
//                intent.putExtra("position", position);
//                intent.putExtra("mode",2);
//                startActivity(intent);
//            }
//        });
//
//        adapter = new MusicAdapter(getActivity(), musicList);                //创建MusicAdapter的对象，实现自定义适配器的创建
//        listView.setAdapter(adapter);                                                 //listView绑定适配器
        return view;
    }

    // 权限代码
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
// 权限代码结束

    //nitListView()实现对手机中MediaDataBase的扫描
    private void initListView() {
        musicList.clear();
        Common.localMusicList.clear();
        //获取ContentResolver的对象，并进行实例化
        ContentResolver resolver = getActivity().getContentResolver();
        //获取游标
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER); //创建游标MediaStore.Audio.Media.EXTERNAL_CONTENT_URI获取音频的文件，后面的是关于select筛选条件，这里填土null就可以了
        //游标归零
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));            //获取歌名
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));         //获取歌唱者
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));           //获取专辑名
                int albumID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));            //获取专辑图片id
                int length = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                //创建Music对象，并赋值
                Music music = new Music();
                music.setLength(length);
                music.setTitle(title);
                music.setArtist(artist);
                music.setAlbum(album);
                music.setPath(path);
                music.setAlbumBip(getAlbumArt(albumID));
                //将music放入musicList集合中
                Common.localMusicList.add(music);
                musicList.add(music);
                System.out.println(music);

            } while (cursor.moveToNext());
        } else {
            Toast.makeText(getActivity(), "本地没有音乐哦", Toast.LENGTH_SHORT).show();
        }
        cursor.close();                                                                         //关闭游标
    }

    //获取专辑图片的方法
    private Bitmap getAlbumArt(int album_id) {                              //前面我们只是获取了专辑图片id，在这里实现通过id获取掉专辑图片
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = getActivity().getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.touxiang1);
        }
        return bm;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public static void deleteFile(String localPath, Context context) {
        if (!TextUtils.isEmpty(localPath)) {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();
            String url = MediaStore.Audio.Media.DATA + "=?";
            int deleteRows = contentResolver.delete(uri, url, new String[]{localPath});
            //当生成图片时没有通知(插入到）媒体数据库，那么在图库里面看不到该图片，而且使用contentResolver.delete方法会返回0，此时使用file.delete方法删除文件
            File file = new File(localPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
