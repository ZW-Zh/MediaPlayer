package com.android.mediaplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mediaplayer.R;
import com.android.mediaplayer.entity.Music;

import java.util.List;

/**
 * Created by zzw on 2019/4/21.
 */

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder>{
    private List<Music> musicList;
    private Context mContext;
    public LocalMusicAdapter(List<Music> list, Context mContext) {
        super();
        this.musicList = list;
        this.mContext=mContext;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item,viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.titleTv.setText(musicList.get(i).getTitle());
        viewHolder.artistTv.setText(musicList.get(i).getArtist() + "-" + musicList.get(i).getAlbum());
        viewHolder.albumImgv.setImageBitmap(musicList.get(i).getAlbumBip());
        if (musicList.get(i).isPlaying) {
            viewHolder.isPlayingView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.isPlayingView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv;
        TextView artistTv;
        ImageView albumImgv;
        View isPlayingView;


        public ViewHolder(final View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.musicitem_title_tv);
            artistTv = itemView.findViewById(R.id.musicitem_artist_tv);
            albumImgv = itemView.findViewById(R.id.musicitem_album_imgv);
            isPlayingView = itemView.findViewById(R.id.musicitem_playing_v);
        }
    }
}

