package com.example.electronic_magazine;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronic_magazine.Grade;
import com.example.electronic_magazine.databinding.FragmentDeleteOrRefactorBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteOrRefactorFragment extends Fragment {
    private String subject;
    private String trimester;
    private DatabaseReference studentRef;
    private FragmentDeleteOrRefactorBinding binding;
    private ArrayAdapter<String> markTimeAdapter;
    private List<String> markTimes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDeleteOrRefactorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        subject = getArguments().getString("teacherSubject");
        String studentFullName = getArguments().getString("studentFullName");
        trimester = getArguments().getString("trimester");
        String trimesterName = getArguments().getString("trimesterName");

        binding.tvNameTrimester.setText(trimesterName);

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

        markTimes = new ArrayList<>();
        markTimeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, markTimes);
        markTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spMarkTimeDelete.setAdapter(markTimeAdapter);
        binding.spMarkTimeRefactor.setAdapter(markTimeAdapter);

        binding.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long markTime = Long.parseLong(binding.spMarkTimeDelete.getSelectedItem().toString());
                deleteMark(markTime);
            }
        });

        List<String> markList = Arrays.asList("2", "3", "4", "5", "н");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, markList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spMark1.setAdapter(adapter);

        binding.bRefactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long markTime = Long.parseLong(binding.spMarkTimeRefactor.getSelectedItem().toString());
                String newMark = binding.spMark1.getSelectedItem().toString();
                refactorMark(markTime, newMark);
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
                markTimes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grade grade = snapshot.getValue(Grade.class);
                    grades.add(grade);
                    markTimes.add(grade.getTimestamp() + "");
                }
                markTimeAdapter.notifyDataSetChanged();
                String marksAndTimes = "";
                for (Grade grade : grades) {
                    marksAndTimes += grade.getMark() + " (" + grade.getTimestamp() + ")\n";
                }
                binding.tvMarkAndTimes.setText(marksAndTimes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TeacherTrimesterFragment", "onCancelled", databaseError.toException());
            }
        });
    }

    private void deleteMark(long markTime) {
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
                    if (grades.get(i).getTimestamp() == markTime) {
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

    private void refactorMark(long markTime, String newMark) {
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
                    if (grades.get(i).getTimestamp() == markTime) {
                        grades.get(i).setMark(newMark);
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(trimester, grades);
                        studentRef.child("grades").child(subject).updateChildren(updates);
                        Toast.makeText(getContext(), "Оценка успешно изменена", Toast.LENGTH_SHORT).show();
                        loadMarksAndTimes();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TeacherTrimesterFragment", "onCancelled", databaseError.toException());
            }
        });
    }
}
