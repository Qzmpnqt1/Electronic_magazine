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
import com.google.firebase.database.Query;
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
    private final int maxClassSize = 10;
    private boolean classSizeNormal = true;

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
                if (role.equals("Ученик")) {
                    checkClassSize();
                }
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
        checkClassSize();

        if ((arrayFullName.length == 3) && (role != null) && (subjectOrSchoolClass != null) && !subjectOrSchoolClass.equals(" ")) {
            if (role.equals("Учитель")) {
                Teacher teacher = new Teacher(fullName, subjectOrSchoolClass);

                    DatabaseReference databaseReference = firebaseDatabase.getReference("students").child(userId);
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
            else if (role.equals("Ученик") && classSizeNormal) {
                Student student = new Student(fullName, subjectOrSchoolClass);

                DatabaseReference databaseReference = firebaseDatabase.getReference("students").child(userId);
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
            if (subjectOrSchoolClass.equals(" ")) {
                Toast.makeText(this, "Нельзя зарегестрироваться как учитель", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Введите данные полностью", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void loadSchoolSubject() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        schoolSubjectList = new ArrayList<>();
        databaseReference.child("fullSchoolSubject").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot subjectSnapshot: snapshot.getChildren()) {
                    Map<String, Object> subjectMap = (Map<String, Object>) subjectSnapshot.getValue();
                    if ((boolean) subjectMap.get("isAvailable")) {
                        schoolSubjectList.add((String) subjectMap.get("name"));
                    }
                }
                if (schoolSubjectList.isEmpty()) {
                    schoolSubjectList.add(" ");
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
        databaseReference.child("fullSchoolSubject").orderByChild("name").equalTo(subjectToRemove)
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

    private void checkClassSize() {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
        Query query = studentsRef.orderByChild("schoolClass").equalTo(subjectOrSchoolClass);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int classSize = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    classSize += 1;
                }
                if (classSize >= maxClassSize) {
                    Toast.makeText(DataEntryActivity.this, "Класс заполнен", Toast.LENGTH_SHORT).show();
                    classSizeNormal = false;
                }
                else {
                    classSizeNormal = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
