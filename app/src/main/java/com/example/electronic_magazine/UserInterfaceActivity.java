package com.example.electronic_magazine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInterfaceActivity extends AppCompatActivity {
    Fragment userInterfaceFragment = null;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inteface);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        String fragmentName = getIntent().getStringExtra("fragment_name");
        assert fragmentName != null;
        if (fragmentName.equals("teacherInterface")) {
            DatabaseReference teacherRef = firebaseDatabase.getReference(Constant.TEACHERS).child(userId);
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        String subject = snapshot.child(("subject")).getValue(String.class);

                        actionBar = getSupportActionBar();
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        actionBar.setHomeButtonEnabled(true);
                        actionBar.setTitle(fullName);
                        actionBar.setSubtitle(subject);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            userInterfaceFragment = new TeacherInterfaceFragment();
        }
        else if (fragmentName.equals("studentInterface")) {
            DatabaseReference studentRef = firebaseDatabase.getReference("students").child(userId);
            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        String schoolClass = snapshot.child(("schoolClass")).getValue(String.class);

                        actionBar = getSupportActionBar();
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        actionBar.setHomeButtonEnabled(true);
                        actionBar.setTitle(fullName);
                        actionBar.setSubtitle(schoolClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            userInterfaceFragment = new StudentInterfaceFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, userInterfaceFragment)
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
