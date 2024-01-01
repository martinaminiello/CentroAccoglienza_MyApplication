package com.example.centroaccoglienza_myapplication.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.centroaccoglienza_myapplication.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {

    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST_GEN = 1;
    private static final int PICK_VIDEO_REQUEST_DONNA = 2;
    private View view;
    private Uri selectedVideoUri;
    private StorageReference storageReference;
    private VideoAdapter videoAdapterGen;
    private VideoAdapter videoAdapterDonna;
    private List<VideoModel> videoListGen;
    private List<VideoModel> videoListDonna;
    private RecyclerView recyclerViewGen;
    private RecyclerView recyclerViewDonna;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video, container, false);

        Button uploadButton = view.findViewById(R.id.uploadButton);
        Button uploadButton2 = view.findViewById(R.id.uploadButton2);
        recyclerViewGen = view.findViewById(R.id.recyclerViewGen);
        recyclerViewDonna = view.findViewById(R.id.recyclerViewDonna);
        storageReference = FirebaseStorage.getInstance().getReference();

        videoListGen = new ArrayList<>();
        videoAdapterGen = new VideoAdapter(videoListGen);

        videoListDonna = new ArrayList<>();
        videoAdapterDonna = new VideoAdapter(videoListDonna);

        recyclerViewGen.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGen.setAdapter(videoAdapterGen);

        recyclerViewDonna.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewDonna.setAdapter(videoAdapterDonna);

        storageReference = FirebaseStorage.getInstance().getReference();

        fetchVideoUrlsGen();
        fetchVideoUrlsDonna();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading video...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openVideoChooser(PICK_VIDEO_REQUEST_GEN);
            }
        });

        uploadButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openVideoChooser(PICK_VIDEO_REQUEST_DONNA);
            }
        });

        return view;
    }

    private void openVideoChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), requestCode);
    }

    private void openVideoChooserDonna() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                if (requestCode == PICK_VIDEO_REQUEST_GEN) {
                    uploadVideo(selectedVideoUri, "videos");
                } else if (requestCode == PICK_VIDEO_REQUEST_DONNA) {
                    uploadVideo(selectedVideoUri, "videosDonna");
                }
            } else {
                Toast.makeText(getContext(), "Failed to get selected video", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadVideo(Uri videoUri, String targetFolder) {
        if (videoUri != null) {
            String videoFileName = "video_" + System.currentTimeMillis() + ".mp4";

            progressDialog.show();

            // Upload the video to Firebase Storage
            storageReference.child(targetFolder + "/" + videoFileName).putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Video uploaded successfully", Toast.LENGTH_SHORT).show();

                        if ("videosDonna".equals(targetFolder)) {
                            // Fetch videos from "videosDonna" and update the recyclerViewDonna
                            fetchVideoUrlsDonna();
                        } else {
                            // Fetch videos from "videos" and update the recyclerViewGen
                            fetchVideoUrlsGen();
                        }

                        progressDialog.dismiss();
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(getContext(), "Failed to upload video", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }) .addOnProgressListener(snapshot -> {
                        // Update progress in the progress dialog
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setProgress((int) progress);
                    });
        }
    }

    private void fetchVideoUrlsGen() {
        videoListGen.clear();

        StorageReference videosRef = storageReference.child("videos");

        videosRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            VideoModel videoModel = new VideoModel(downloadUrl.toString());
                            videoListGen.add(videoModel);
                            videoAdapterGen.notifyDataSetChanged();
                        }).addOnFailureListener(exception -> {
                            Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getContext(), "Failed to list videos", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchVideoUrlsDonna() {
        videoListDonna.clear();

        StorageReference videosRef = storageReference.child("videosDonna");

        videosRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            VideoModel videoModel = new VideoModel(downloadUrl.toString());
                            videoListDonna.add(videoModel);
                            videoAdapterDonna.notifyDataSetChanged();
                        }).addOnFailureListener(exception -> {
                            Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getContext(), "Failed to list Donna videos", Toast.LENGTH_SHORT).show();
                });
    }


}
