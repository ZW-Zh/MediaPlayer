package com.android.mediaplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mediaplayer.R;
import com.android.mediaplayer.entity.Video;
import com.android.mediaplayer.utils.FileUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by zzw on 2019/4/18.
 */

public class WebVideoAdapter extends BaseAdapter{
//    private List<Video> myFileList;
//    private Context mContext;
//    public WebVideoAdapter(List<Video> list, Context mContext) {
//        super();
//        myFileList = list;
//        this.mContext=mContext;
//    }
//
//
//
//    @Override
//    public int getCount() {
//        return myFileList.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        View v = null;
//        ViewHolder viewHolder = null;
//        //缓存原理，程序运行到这里判断convertView是否为空
//        if (view == null) {
//            //绑定行布局文件，就是绑定我们需要适配的模板
//            v = LayoutInflater.from(mContext).inflate(R.layout.video_item, null);
//            //实例化ViewHolder
//            viewHolder = new ViewHolder();
//            viewHolder.imageView = v.findViewById(R.id.videoImg);
//            viewHolder.time = v.findViewById(R.id.time);
//            viewHolder.title = v.findViewById(R.id.name);
//            v.setTag(viewHolder);
//
//        } else {
//            v = view;
//            viewHolder = (ViewHolder) v.getTag();
//        }
//        //赋值 准确的是绑定赋值的中介
//        viewHolder.title.setText(myFileList.get(i).getName());
//        viewHolder.time.setText(FileUtil.getTotalTime(myFileList.get(i).getDuration()));
//        Glide.with(mContext)
//                .setDefaultRequestOptions(
//                        new RequestOptions()
//                                .frame(4000000)
//                                .centerCrop()
//                )
//                .load(myFileList.get(i).getUrl())
//                .into(viewHolder.imageView);
//        return v;
//    }
//    class ViewHolder  {
//        ImageView imageView;
//        TextView title,time;
//    }
        //定义两个属性，用List集合存放Music类
        private Context context;
        private List<Video> videoList;

        //创建MusicAdapter的构造方法，在LogicFragment需要调用MusicAdapter的构造方法来创建适配器
    public WebVideoAdapter(Context context, List<Video> videoList) {
            this.context = context;
            this.videoList = videoList;

        }

        //这里需要返回musicList.size()
        @Override
        public int getCount() {
            return videoList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //在getView（）方法中是实现对模板的绑定，赋值
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //声明View和ViewHolder的对象
            View view = null;
            ViewHolder viewHolder = null;
            //缓存原理，程序运行到这里判断convertView是否为空
            if (convertView == null) {
                //绑定行布局文件，就是绑定我们需要适配的模板
                view = LayoutInflater.from(context).inflate(R.layout.video_item, null);
                //实例化ViewHolder
                viewHolder = new ViewHolder();
                viewHolder.imageView = view.findViewById(R.id.videoImg);
            //viewHolder.time = view.findViewById(R.id.time);
                viewHolder.title = view.findViewById(R.id.name);
                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            //赋值 准确的是绑定赋值的中介
            //viewHolder.time.setText(FileUtil.getTotalTime(videoList.get(position).getDuration()));
            viewHolder.title.setText(videoList.get(position).getName());
            Glide.with(context)
                .setDefaultRequestOptions(
                    new RequestOptions()
                            .frame(4000000)
                            .centerCrop()
            )
                    .load(videoList.get(position).getUrl())
                    .into(viewHolder.imageView);

            return view;
        }

        //创建一个类ViewHolder，用来存放music_item.xml中的控件
        class ViewHolder {
            ImageView imageView;
            TextView title;
        }

}

