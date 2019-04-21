package com.android.mediaplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.mediaplayer.R;
import com.android.mediaplayer.entity.Video;
import com.android.mediaplayer.utils.FileUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;



/**
 * Created by zzw on 2019/4/16.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{
    private List<Video> myFileList;
    private Context mContext;

    public VideoAdapter(List<Video> list, Context mContext) {
        super();
        myFileList = list;
        this.mContext=mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.localvideo_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Video video = myFileList.get(position);

        Glide.with(mContext)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(4000000)
                                .centerCrop()
                )
                .load(video.getUrl())
                .into(holder.imageView);
        holder.title.setText(video.getName());
        holder.size.setText(FileUtil.getFileSize(video.getSize()));
        holder.time.setText(FileUtil.getTotalTime(video.getDuration()));
        holder.itemView.setTag(position);


    }

    @Override
    public int getItemCount() {
        return myFileList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title,size,time;


        public ViewHolder(final View itemView) {
            super(itemView);
           imageView= itemView.findViewById(R.id.image);
           title=itemView.findViewById(R.id.name);
            size=itemView.findViewById(R.id.size);
            time=itemView.findViewById(R.id.time);

        }
    }



}
