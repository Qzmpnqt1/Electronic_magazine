package com.example.electronic_magazine;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteOrRefactorFragment extends Fragment {

    private TextView tvMarkAndTimes;
    private EditText edMarkTimeDelete;
    private Button bDelete;
    private String subject;
    private String studentFullName;
    private String trimester;
    private DatabaseReference studentRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_or_refactor, container, false);

        tvMarkAndTimes = view.findViewById(R.id.tvMarkAndTimes);
        edMarkTimeDelete = view.findViewById(R.id.edMarkTimeDelete);
        bDelete = view.findViewById(R.id.bDelete);

        subject = getArguments().getString("teacherSubject");
        studentFullName = getArguments().getString("studentFullName");
        trimester = getArguments().getString("trimester");

        // Инициализируйте studentRef по полному имени студента
        Query query = FirebaseDatabase.getInstance().getReference("students").orderByChild("fullName").equalTo(studentFullName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    // Найден узел студента
                    studentRef = studentSnapshot.getRef();
                    if (studentRef != null) {
                        loadMarksAndTimes();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DeleteOrRefactorFragment", "onCancelled", databaseError.toException());
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String markTime = edMarkTimeDelete.getText().toString();
                deleteMark(markTime);
            }
        });

        return view;
    }

    private void loadMarksAndTimes() {
        DatabaseReference gradesRef = studentRef.child("grades").child(subject).child(trimester);

        gradesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Grade> grades = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grade grade = snapshot.getValue(Grade.class);
                    grades.add(grade);
                }
                String marksAndTimes = "";
                for (Grade grade : grades) {
                    marksAndTimes += grade.getMark() + " (" + grade.getTimestamp() + ")\n";
                }
                tvMarkAndTimes.setText(marksAndTimes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TeacherTrimesterFragment", "onCancelled", databaseError.toException());
            }
        });
    }


    private void deleteMark(String markTime) {
        DatabaseReference gradesRef = studentRef.child("grades").child(subject).child(trimester);
        gradesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Grade> grades = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grade grade = snapshot.getValue(Grade.class);
                    grades.add(grade);
                }
                for (int i = 0; i < grades.size(); i++) {
                    if (grades.get(i).getTimestamp() == Long.parseLong(markTime)) {
                        grades.remove(i);
                        break;
                    }
                }
                Map<String, Object> updates = new HashMap<>();
                updates.put(trimester, grades);
                studentRef.child("grades").child(subject).updateChildren(updates);
                Toast.makeText(getContext(), "Оценка успешно удалена", Toast.LENGTH_SHORT).show();
                loadMarksAndTimes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TeacherTrimesterFragment", "onCancelled", databaseError.toException());
            }
        });
    }
}