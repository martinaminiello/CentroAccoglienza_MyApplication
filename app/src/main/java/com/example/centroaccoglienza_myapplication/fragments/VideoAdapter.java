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
    private OnDownloadClickListener onDownloadClickListener;
    // Constructor
    public VideoAdapter(List<VideoModel> videoList, OnDeleteClickListener onDeleteClickListener, OnDownloadClickListener onDownloadClickListener, String targetFolder) {
        this.videoList = videoList;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onDownloadClickListener = onDownloadClickListener; // Initialize the variable
        this.targetFolder = targetFolder;
    }

    public interface OnDownloadClickListener {

        // Add a new method for download click
        void onDownloadClick(VideoModel videoModel);
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
        holder.textNomeVideo.setText(videoModel.getName());

        // Use Glide or another image loading library to load video thumbnails
        Glide.with(holder.itemView.getContext())
                .load(videoModel.getVideoUrl())
                .placeholder(R.drawable.video_placeholder)
                .into(holder.videoThumbnailImageView);

        // Ensure that the aspect ratio is maintained in the ImageView
        holder.videoThumbnailImageView.setAdjustViewBounds(true);

        // Set a click listener for the filename TextView
        holder.textNomeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Invoke the method to download the video
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDownloadClick(videoModel);
                }
            }
        });

        // Set a click listener for the delete button
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

        TextView textNomeVideo;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnailImageView = itemView.findViewById(R.id.videoThumbnailImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            textNomeVideo=itemView.findViewById(R.id.NomeVideo);
        }


    }

    // Add onDeleteClick method to implement the interface
    public interface OnDeleteClickListener {
        void onDeleteClick(VideoModel videoModel, String targetFolder);

        // Add a new method for download click
        void onDownloadClick(VideoModel videoModel);
    }

       // Add a new method to set the onDeleteClickListener
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnDownloadClickListener(OnDownloadClickListener onDownloadClickListener) {
        this.onDownloadClickListener = onDownloadClickListener;
    }
}
