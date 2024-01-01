package com.example.centroaccoglienza_myapplication.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.centroaccoglienza_myapplication.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoModel> videoList;
    private OnDeleteClickListener onDeleteClickListener;
    private String targetFolder; // New member variable

    // Constructor
    public VideoAdapter(List<VideoModel> videoList, OnDeleteClickListener onDeleteClickListener, String targetFolder) {
        this.videoList = videoList;
        this.onDeleteClickListener = onDeleteClickListener;
        this.targetFolder = targetFolder; // Set the targetFolder
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(VideoModel videoModel, String targetFolder);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel videoModel = videoList.get(position);

        // Use Glide or another image loading library to load video thumbnails
        Glide.with(holder.itemView.getContext())
                .load(videoModel.getVideoUrl())
                .placeholder(R.drawable.video_placeholder)
                .into(holder.videoThumbnailImageView);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Invoke the method to show the delete confirmation dialog
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(videoModel, targetFolder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnailImageView;
        public ImageButton deleteButton;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnailImageView = itemView.findViewById(R.id.videoThumbnailImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }


    }

    // Add onDeleteClick method to implement the interface
    public void onDeleteClick(VideoModel videoModel, String targetFolder) {
        if (onDeleteClickListener != null) {
            onDeleteClickListener.onDeleteClick(videoModel, targetFolder);
        }
    }
}