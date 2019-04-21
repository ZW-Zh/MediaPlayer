package com.android.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mediaplayer.adapter.AdapterFragment;
import com.android.mediaplayer.constant.Url;
import com.android.mediaplayer.entity.LoginInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private List<String> list_Titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        initUi();
        initSearch();
    }

    private void initUi() {
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bnv);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //根据navagatin.xml中item的id进行case
                    case R.id.localmusic:
                        viewPager.setCurrentItem(0);
                        //跳到对应ViewPager的page
                        break;
                    case R.id.webmusic:
                        viewPager.setCurrentItem(1);

                        break;
                    case R.id.localvideo:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.webvideo:
                        viewPager.setCurrentItem(3);
                        break;
                }

                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                bottomNavigationView.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        list_Titles.add("本地音乐");
        list_Titles.add("音乐推送");
        list_Titles.add("本地视频");
        list_Titles.add("网络视频");


        LocalMusicFragment musicFragment = new LocalMusicFragment();
        LocalVideoFragment videoFragment = new LocalVideoFragment();
        WebMusicFragment webMusicFragment = new WebMusicFragment();
        WebVideoFragment webVideoFragment = new WebVideoFragment();
        fragments.add(musicFragment);
        fragments.add(webMusicFragment);
        fragments.add(videoFragment);
        fragments.add(webVideoFragment);

        LoginInfo loginInfo = (LoginInfo) getIntent().getSerializableExtra("logininfo");
        Bundle bundle = new Bundle();
        bundle.putSerializable("logininfo", loginInfo);//这里的values就是我们要传的值
        webMusicFragment.setArguments(bundle);

        viewPager.setOffscreenPageLimit(2); //设置向左和向右都缓存limit个页面
        AdapterFragment adapter = new AdapterFragment(getSupportFragmentManager(), fragments, list_Titles);
        viewPager.setAdapter(adapter);
    }

    private void initSearch() {
        EditText editText = findViewById(R.id.search);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    Toast.makeText(MainActivity.this,"你点击了回车", Toast.LENGTH_SHORT).show();
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    Intent intent=new Intent(MainActivity.this,WebViewActivity.class);
                    intent.putExtra("search",editText.getText().toString());
                    startActivity(intent);
                }
                return false;
            }
        });

    }

}

