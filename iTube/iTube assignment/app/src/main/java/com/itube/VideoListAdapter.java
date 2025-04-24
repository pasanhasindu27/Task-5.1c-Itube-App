package com.itube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private Context context;
    private List<String> videoUrls;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String videoUrl);
    }

    public VideoListAdapter(Context context, List<String> videoUrls, OnItemClickListener itemClickListener) {
        this.context = context;
        this.videoUrls = videoUrls;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_url, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String videoUrl = videoUrls.get(position);
        holder.videoUrlTextView.setText(videoUrl);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(videoUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView videoUrlTextView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoUrlTextView = itemView.findViewById(R.id.videoUrlTextView);
        }
    }
}


