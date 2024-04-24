package com.example.electronic_magazine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.electronic_magazine.databinding.ActivityDataEntryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataEntryActivity extends AppCompatActivity {
    private ActivityDataEntryBinding binding;
    private List<String> schoolSubjectList;
    private List<String> classNumberList = Arrays.asList("1 класс", "2 класс", "3 класс", "4 класс", "5 класс",
            "6 класс", "7 класс", "8 класс", "9 класс", "10 класс", "11 класс");
    private String subjectOrSchoolClass, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataEntryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Заполнение личных данных");

        loadSchoolSubject();
        
        binding.rgRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.rbTeacher) {
                    loadSpinnerData(schoolSubjectList);
                    role = "Учитель";
                }
                else if (checkedId == R.id.rbStudent) {
                    loadSpinnerData(classNumberList);
                    role = "Ученик";
                }
            }
        });

        binding.spSubjectOrSchoolClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                subjectOrSchoolClass = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadSpinnerData(List<String> dataArray) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spSubjectOrSchoolClass.setAdapter(adapter);
    }

    public void onClickSaveData(View view) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fullName = binding.edFullName.getText().toString();
        String[] arrayFullName = fullName.split(" ");

        if ((arrayFullName.length == 3) && (role != null)) {
            if (role.equals("Учитель")) {
                Teacher teacher = new Teacher(fullName, subjectOrSchoolClass);

                DatabaseReference databaseReference = firebaseDatabase.getReference(Constant.TEACHERS).child(userId);
                databaseReference.setValue(teacher)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(DataEntryActivity.this, MainActivity.class);
                                startActivity(intent);
                                removeSchoolSubject();
                                Toast.makeText(DataEntryActivity.this, "Данные учителя успешно сохранены", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DataEntryActivity.this, "Ошибка при сохранении данных" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else if (role.equals("Ученик")) {
                Student student = new Student(fullName, subjectOrSchoolClass);

                DatabaseReference databaseReference = firebaseDatabase.getReference(Constant.STUDENTS).child(userId);
                databaseReference.setValue(student)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(DataEntryActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(DataEntryActivity.this, "Данные ученика успешно сохранены", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DataEntryActivity.this, "Ошибка при сохранении данных" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        else {
            Toast.makeText(this, "Заполните персональные данные полностью", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadSchoolSubject() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        schoolSubjectList = new ArrayList<>();
        databaseReference.child(Constant.FULL_SCHOOL_SUBJECTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot subjectSnapshot: snapshot.getChildren()) {
                    Map<String, Object> subjectMap = (Map<String, Object>) subjectSnapshot.getValue();
                    if ((boolean) subjectMap.get("isAvailable")) {
                        schoolSubjectList.add((String) subjectMap.get("name"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    public void removeSchoolSubject() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        String subjectToRemove = subjectOrSchoolClass;
        // Сохраняем выбранный предмет в переменной
        final String selectedSubject = subjectToRemove;

        // Обновляем значение "isAvailable" для выбранного предмета
        databaseReference.child(Constant.FULL_SCHOOL_SUBJECTS).orderByChild("name").equalTo(subjectToRemove)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot subjectSnapshot: snapshot.getChildren()) {
                    subjectSnapshot.getRef().child("isAvailable").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
