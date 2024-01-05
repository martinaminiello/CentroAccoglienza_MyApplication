package com.example.centroaccoglienza_myapplication.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.centroaccoglienza_myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class DocumentiFragment extends Fragment {

    View view;
    Button selectFile, upload;
    TextView notification;
    FirebaseDatabase database;
    FirebaseStorage storage;
    int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1;
    Uri pdfUri;

    ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<UploadedFile> fileList;
    private FileAdapter fileAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_documenti, container, false);
        selectFile = view.findViewById(R.id.selectFile);
        upload = view.findViewById(R.id.upload);
        notification = view.findViewById(R.id.notification);


        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        fileList = new ArrayList<>();
        fileAdapter = new FileAdapter(fileList,this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileAdapter);

        fetchDataFromDatabase();

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectPdf();
                } else
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfUri!=null){
                    uploadFile(pdfUri);
                }
                else
                    Toast.makeText(requireContext(),"Seleziona un file",Toast.LENGTH_SHORT).show();
            }
        });

        fileAdapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UploadedFile uploadedFile) {
                // Handle item click (e.g., open the file or perform some action)
                downloadFile(uploadedFile.getFileUrl(), uploadedFile.getFileName());
            }

            @Override
            public void onDeleteClick(UploadedFile uploadedFile) {
                // Handle delete click (e.g., show a confirmation dialog and delete the file)
                showDeleteConfirmationDialog(uploadedFile);
            }
        });


        return view;

    }

    private void fetchDataFromDatabase() {
        DatabaseReference reference = database.getReference();

        // Use SingleValueEventListener to fetch data only once
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fileList.clear(); // Clear the existing list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Iterate through the database snapshots and add them to the list
                    String fileName = snapshot.getKey();
                    String fileUrl = snapshot.getValue(String.class);
                    UploadedFile uploadedFile = new UploadedFile(fileName, fileUrl);
                    fileList.add(uploadedFile);
                }
                // Notify the adapter that the data set has changed
                fileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Toast.makeText(requireContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFile(Uri pdfUri) {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Caricamento in corso");
        progressDialog.setProgress(0);
        progressDialog.show();

        // Get the actual file name from the Uri
        String fileName = getFileNameFromUri(pdfUri);

        StorageReference storageReference = storage.getReference();
        StorageReference fileReference = storageReference.child("Uploads").child(fileName);

        // Check if the file with the same name already exists
        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // File with the same name exists, delete it before uploading the new one
                fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully, now proceed with the upload
                        performUpload(fileReference, pdfUri, fileName);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to delete existing file
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Failed to delete existing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // File with the same name doesn't exist, proceed with the upload
                performUpload(fileReference, pdfUri, fileName);
            }
        });
    }

    // Helper method to perform the actual file upload
    // Helper method to perform the actual file upload
    private void performUpload(StorageReference fileReference, Uri pdfUri, String fileName) {
        UploadTask uploadTask = fileReference.putFile(pdfUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // File uploaded successfully, you can add your onSuccess code here
                // For example, updating the UI or displaying a success message
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "File caricato!", Toast.LENGTH_SHORT).show();

                // Remove the existing file from the fileList
                removeExistingFile(fileName);

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        String url = downloadUri.toString();
                        DatabaseReference reference = database.getReference();
                        reference.child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    UploadedFile uploadedFile = new UploadedFile(fileName, url);
                                    fileList.add(uploadedFile);
                                    fileAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(requireContext(), "Errore nel caricare il file", Toast.LENGTH_SHORT).show();
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        exception.printStackTrace(); // Print the stack trace for more details
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Errore nel caricare il file nel database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int currentProgress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure during upload
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to remove the existing file from the fileList
    private void removeExistingFile(String fileName) {
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).getFileName().equals(fileName)) {
                fileList.remove(i);
                fileAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }


    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        result = sanitizeFileName(result); // Sanitize the original file name
        return result;
    }



    @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == 9 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPdf();
            } else
                Toast.makeText(requireContext(), "Si prega di fornire i permessi",Toast.LENGTH_SHORT).show();
        }
        private void selectPdf () {
             Intent intent= new Intent();
             intent.setType("application/pdf");
             intent.setAction(Intent.ACTION_GET_CONTENT);
             startActivityForResult(intent,86);
        }

    public void downloadFile(String fileUrl, String fileName) {
        // Create a storage reference from the file URL
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);

        // Get the download URL for the file
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                String downloadUrl = uri.toString();
                String filename=uri.getPath();
                openBrowser(downloadUrl, filename);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occurred while fetching the download URL
                Toast.makeText(requireContext(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileNameFromPath(String filePath) {
        int lastSlashIndex = filePath.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < filePath.length() - 1) {
            return filePath.substring(lastSlashIndex + 1);
        } else {
            return filePath;
        }
    }
    private void openBrowser(String url, String fileName) {
        // Create a DownloadManager request to download the PDF file
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String fileNameOnly = getFileNameFromPath(fileName);
        request.setTitle(fileNameOnly);
        request.setDescription("Downloading"); // Set the description for the notification
        request.allowScanningByMediaScanner(); // Allow the MediaScanner to scan the downloaded file
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Set the destination for the downloaded file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Get the DownloadManager service and enqueue the request
        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        } else {
            // Handle the case where DownloadManager is not available
            Toast.makeText(requireContext(), "DownloadManager not available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 86 && resultCode == RESULT_OK && data != null){
            pdfUri = data.getData();
            String fileName = getFileNameFromUri(pdfUri);
            notification.setText(fileName);
        } else {
            Toast.makeText(requireContext(), "Seleziona un file", Toast.LENGTH_SHORT).show();
        }
    }

    private String sanitizeFileName(String originalFileName) {
        // Replace invalid characters with underscores
        return originalFileName.replaceAll("[.#$\\[\\]]", "_");
    }

    private void showDeleteConfirmationDialog(final UploadedFile uploadedFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Elimina File");
        builder.setMessage("Sei sicuro di voler eliminare questo file?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call a method to delete the file from Firebase and update the UI
                deleteFile(uploadedFile);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteFile(UploadedFile uploadedFile) {
        // Get references to Firebase Storage and Realtime Database
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the file name
        String fileName = uploadedFile.getFileName();

        // Create references to the Storage and Database locations
        StorageReference fileReference = storageReference.child("Uploads").child(fileName);
        DatabaseReference fileDatabaseReference = databaseReference.child(fileName);

        // Delete the file from Firebase Storage
        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully from Storage, now delete from Database
                fileDatabaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully from Database
                        // Remove the file from the local list and update the UI
                        fileList.remove(uploadedFile);
                        fileAdapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "File eliminato", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to delete from Database
                        Toast.makeText(requireContext(), "Failed to delete file from Database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to delete from Storage
                Toast.makeText(requireContext(), "Failed to delete file from Storage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

