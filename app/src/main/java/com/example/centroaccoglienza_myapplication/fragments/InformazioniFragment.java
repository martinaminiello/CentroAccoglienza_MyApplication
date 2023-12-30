package com.example.centroaccoglienza_myapplication.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.centroaccoglienza_myapplication.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class InformazioniFragment extends Fragment {

    EditText editNome;
    EditText editIndirizzo;
    EditText editLink;
    EditText editEmail;
    EditText editTel;
    EditText editDescr;

    View view;
    Button saveButton;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentRef = db.collection("CentroAccoglienza").document("C001");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_informazioni, container, false);

        editNome=view.findViewById(R.id.editNome);
        editEmail=view.findViewById(R.id.editEmail);
        editIndirizzo=view.findViewById(R.id.editIndirizzo);
        editTel=view.findViewById(R.id.editTel);
        editLink=view.findViewById(R.id.editLink);
        editDescr=view.findViewById(R.id.editDescription);
         saveButton = view.findViewById(R.id.saveBtn);

        fetchDataCentre();

        saveButton.setOnClickListener(v -> saveDataToFirestore());

        return view;
    }

    public void fetchDataCentre(){
        fetchName();
        fetchAddress();
        fetchEmail();
        fetchWeb();
        fetchTel();
        fetchDescr();
    }


    public void fetchName(){
        documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to document changes", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String nameCentre = documentSnapshot.getString("Nome");

                //UPDATE TEXTVIEW
                editNome.setText(nameCentre);
            }
        });
    }

    public void fetchAddress(){
        documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to document changes", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String address = documentSnapshot.getString("Indirizzo");

                //UPDATE TEXTVIEW
                editIndirizzo.setText(address);
            }
        });
    }

    public void fetchWeb(){
        documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to document changes", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String link = documentSnapshot.getString("Sito web");

                //UPDATE TEXTVIEW
                editLink.setText(link);
            }
        });
    }
    public void fetchEmail(){
        documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to document changes", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String email = documentSnapshot.getString("Email");

                //UPDATE TEXTVIEW
                editEmail.setText(email);
            }
        });
    }

    public void fetchTel(){
        documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to document changes", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String tel = documentSnapshot.getString("Telefono");

                //UPDATE TEXTVIEW
                editTel.setText(tel);
            }
        });
    }

    public void fetchDescr(){

       documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to document changes", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String description = documentSnapshot.getString("Descrizione");

                //UPDATE TEXTVIEW
                getActivity().runOnUiThread(() -> {
                    // UPDATE EDITTEXT
                    editDescr.setText(description);
                });
            }
        });
    }

    private void saveDataToFirestore() {
        String nome = editNome.getText().toString();
        String indirizzo = editIndirizzo.getText().toString();
        String link = editLink.getText().toString();
        String email = editEmail.getText().toString();
        String tel = editTel.getText().toString();
        String descr = editDescr.getText().toString();

        // Create a map with the new values
        Map<String, Object> newData = new HashMap<>();
        newData.put("Nome", nome);
        newData.put("Indirizzo", indirizzo);
        newData.put("Sito web", link);
        newData.put("Email", email);
        newData.put("Telefono", tel);
        newData.put("Descrizione", descr);

        // Update the Firestore document
        documentRef.update(newData)
                .addOnSuccessListener(aVoid ->{
                    Log.d(TAG, "Document updated successfully");
                    Toast.makeText(getContext(), "I dati sono stati aggiornati!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Errore: " + e.getMessage()));
    }

}