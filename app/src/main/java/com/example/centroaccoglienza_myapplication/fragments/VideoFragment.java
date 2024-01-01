package com.example.centroaccoglienza_myapplication.fragments;

import static android.app.Activity.RESULT_OK;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {

    private static final int PICK_VIDEO_REQUEST = 1;
    private View view;
    private Uri selectedVideoUri;
    private StorageReference storageReference;
    private VideoAdapter videoAdapter;
    private List<VideoModel> videoList;
    private RecyclerView recyclerViewGen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video, container, false);

        Button uploadButton = view.findViewById(R.id.uploadButton);
        recyclerViewGen=view.findViewById(R.id.recyclerViewGen);
        storageReference = FirebaseStorage.getInstance().getReference();

        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoList);

        recyclerViewGen.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGen.setAdapter(videoAdapter);


        storageReference = FirebaseStorage.getInstance().getReference();

        fetchVideoUrls();
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoChooser();
            }
        });

        return view;
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            // Now you can use the selectedVideoUri to work with the user-selected video.
            if (selectedVideoUri != null) {
                uploadVideoToFirestore(selectedVideoUri);
            } else {
                Toast.makeText(getContext(), "Failed to get selected video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadVideoToFirestore(Uri videoUri) {
        if (videoUri != null) {
            // Create a unique filename for the video
            String videoFileName = "video_" + System.currentTimeMillis() + ".mp4";

            // Get a reference to the location where you want to store the video
            StorageReference videoRef = storageReference.child("videos/" + videoFileName);

            // Upload the video to Firebase Storage
            videoRef.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Video uploaded successfully
                        Toast.makeText(getContext(), "Video uploaded successfully", Toast.LENGTH_SHORT).show();

                        // You can get the download URL of the uploaded video
                        videoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String videoDownloadUrl = downloadUri.toString();
                            // Now you can store this download URL in Firestore or perform any other actions

                            // Refresh the video list after uploading
                            fetchVideoUrls();
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        Toast.makeText(getContext(), "Failed to upload video", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void fetchVideoUrls() {
        // Clear the existing list before fetching new videos
        videoList.clear();

        // Reference to the "videos" folder in Firebase Storage
        StorageReference videosRef = storageReference.child("videos");

        // List all items in the "videos" folder
        videosRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        // Get the download URL for each video
                        item.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            // Add the video to the list
                            VideoModel videoModel = new VideoModel(downloadUrl.toString());
                            videoList.add(videoModel);

                            // Notify the adapter that data has changed
                            videoAdapter.notifyDataSetChanged();
                        }).addOnFailureListener(exception -> {
                            // Handle failure to get download URL
                            Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(exception -> {
                    // Handle failure to list items in the "videos" folder
                    Toast.makeText(getContext(), "Failed to list videos", Toast.LENGTH_SHORT).show();
                });
    }


}
