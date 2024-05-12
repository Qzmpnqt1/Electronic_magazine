package com.example.electronic_magazine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.electronic_magazine.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Вход и регистрация");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser != null) {
            showSigned();
            String userName = "Вы вошли как: " + cUser.getEmail();
            binding.tvUserName.setText(userName);
        }
        else {
            notSigned();
        }
    }

    public void onClickSignUp(View view) {
        if (!TextUtils.isEmpty(binding.edEmail.getText().toString()) && !TextUtils.isEmpty(binding.edPassword.getText().toString())) {
            mAuth.createUserWithEmailAndPassword(binding.edEmail.getText().toString(), binding.edPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), DataEntryActivity.class);
                                startActivity(intent);
                            }
                            else {
                                notSigned();
                                Toast.makeText(getApplicationContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Введите почту и пароль", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSignIn(View view) {
        if (!TextUtils.isEmpty(binding.edEmail.getText().toString()) && !TextUtils.isEmpty(binding.edPassword.getText().toString())) {
            mAuth.signInWithEmailAndPassword(binding.edEmail.getText().toString(), binding.edPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                showSigned();
                                Toast.makeText(getApplicationContext(), "Успешный вход", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                notSigned();
                                Toast.makeText(getApplicationContext(), "Ошибка входа", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Введите почту и пароль", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickContinue(View view) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference studentRef = firebaseDatabase.getReference("students").child(userId);
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent intentStudentInterface = new Intent(MainActivity.this,
                            UserInterfaceActivity.class);
                    intentStudentInterface.putExtra("fragment_name", "studentInterface");
                    startActivity(intentStudentInterface);
                }
                else {
                    DatabaseReference teacherRef = firebaseDatabase.getReference("teachers").child(userId);
                    teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Intent intentTeacherInterface = new Intent(MainActivity.this,
                                        UserInterfaceActivity.class);
                                intentTeacherInterface.putExtra("fragment_name", "teacherInterface");
                                startActivity(intentTeacherInterface);
                            }
                            else {
                                Intent intent = new Intent(MainActivity.this, DataEntryActivity.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClickSignOut(View view) {
        FirebaseAuth.getInstance().signOut();
        notSigned();
    }

    private void showSigned() {
        binding.tvUserName.setVisibility(View.VISIBLE);
        binding.bStart.setVisibility(View.VISIBLE);
        binding.bExit.setVisibility(View.VISIBLE);
        binding.edEmail.setVisibility(View.GONE);
        binding.edPassword.setVisibility(View.GONE);
        binding.bSignUp.setVisibility(View.GONE);
        binding.bSignIn.setVisibility(View.GONE);
    }

    private void notSigned() {
        binding.tvUserName.setVisibility(View.GONE);
        binding.bStart.setVisibility(View.GONE);
        binding.bExit.setVisibility(View.GONE);
        binding.edEmail.setVisibility(View.VISIBLE);
        binding.edPassword.setVisibility(View.VISIBLE);
        binding.bSignUp.setVisibility(View.VISIBLE);
        binding.bSignIn.setVisibility(View.VISIBLE);
    }
}