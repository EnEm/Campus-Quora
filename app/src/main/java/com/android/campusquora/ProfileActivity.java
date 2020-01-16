package com.android.campusquora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.campusquora.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String mProfilePicAddress;
    private TextInputLayout mProfileNameEditText;
    private TextInputLayout mProfileEmailEditText;
    private TextInputLayout mProfilePhoneEditText;
    private TextInputLayout mProfileBioEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate Called");
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mProfileNameEditText = findViewById(R.id.profileNameEditText);
        mProfileEmailEditText = findViewById(R.id.profileEmailEditText);
        mProfilePhoneEditText = findViewById(R.id.profilePhoneEditText);
        mProfileBioEditText = findViewById(R.id.profileBioEditText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart Called");
        updateUI(mCurrentUser);
    }

    private void updateUI(FirebaseUser user) {
        Log.v(LOG_TAG, "updateUI Called");
        if(user != null) {
            getDataFromFirestore(user);
        } else {
            finish();
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }
    }

    public void updateData(View view) {
        Log.v(LOG_TAG, "updateData Called");
        String profilePic = mProfilePicAddress;
        String name = Objects.requireNonNull(mProfileNameEditText.getEditText()).getText().toString();
        String email = Objects.requireNonNull(mProfileEmailEditText.getEditText()).getText().toString();
        String mobile = Objects.requireNonNull(mProfilePhoneEditText.getEditText()).getText().toString();
        String bio = Objects.requireNonNull(mProfileBioEditText.getEditText()).getText().toString();
        User userData = new User(mCurrentUser.getUid(), email);
        userData.setName(name);
        userData.setMobile(mobile);
        userData.setProfilePic(profilePic);
        userData.setBio(bio);
        updateUserDetailsOnFirestore(userData);
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    void updateUserDetailsOnFirestore(User userData) {
        Log.v(LOG_TAG, "updateUserDetailsOnFirestore Called");
        DocumentReference userDocRef = FirebaseFirestore.getInstance().document("users/" + userData.getUid());
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put(User.getFieldEmail(), userData.getEmail());
        dataToSave.put(User.getFieldName(), userData.getName());
        dataToSave.put(User.getFieldMobileNo(), userData.getMobile());
        dataToSave.put(User.getFieldProfilePic(), userData.getProfilePic());
        dataToSave.put(User.getFieldBio(), userData.getBio());
        userDocRef.set(dataToSave)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Data Updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void getDataFromFirestore(FirebaseUser currentUser) {
        Log.v(LOG_TAG, "getDataFromFirestore Called");
        DocumentReference userDocRef = FirebaseFirestore.getInstance().document("users/" + currentUser.getUid());
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot != null) {
                        if(documentSnapshot.exists()) {
                            String profilePic = documentSnapshot.getString(User.getFieldProfilePic());
                            String email = documentSnapshot.getString(User.getFieldEmail());
                            String name = documentSnapshot.getString(User.getFieldName());
                            String phone = documentSnapshot.getString(User.getFieldMobileNo());
                            String bio = documentSnapshot.getString(User.getFieldBio());
                            Log.d("QueryUtils", "email" + "name");
                            mProfilePicAddress = profilePic;
                            mProfileNameEditText.getEditText().setText(name);
                            mProfileEmailEditText.getEditText().setText(email);
                            mProfilePhoneEditText.getEditText().setText(phone);
                            mProfileBioEditText.getEditText().setText(bio);
                            Toast.makeText(getApplicationContext(), "Data Retrieved.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Data Retrieval Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String[] messages = task.getException().getMessage().split(":");
                    String message = messages[messages.length-1];
                    Toast.makeText( ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
