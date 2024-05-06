package com.example.electronic_magazine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.electronic_magazine.databinding.FragmentTeacherTrimestrBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.example.electronic_magazine.databinding.FragmentStudentTrimesterBinding;

public class StudentTrimesterFragment extends Fragment {
    private String subject;
    private FragmentStudentTrimesterBinding binding;
    private DatabaseReference studentRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStudentTrimesterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        subject = getArguments().getString("subject");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
        studentRef = studentsRef.child(userId);

        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.child("fullName").getValue(String.class);
                Query query = FirebaseDatabase.getInstance().getReference("students").orderByChild("fullName").equalTo(fullName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                            // Найден узел студента
                            studentRef = studentSnapshot.getRef();
                        }

                        // Загрузите оценки для текущего предмета
                        loadGrades(subject);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TeacherTrimesterFragment", "onCancelled", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void loadGrades(String subject) {
        DatabaseReference gradesRef = studentRef.child("grades").child(subject);
        gradesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, ArrayList<Grade>>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, ArrayList<Grade>>>() {};
                HashMap<String, ArrayList<Grade>> grades = dataSnapshot.getValue(genericTypeIndicator);
                if (grades == null) {
                    return;
                }

                // Отобразите оценки за первый триместр
                ArrayList<Grade> firstTrimesterGrades = grades.get("firstTrimester");
                if (firstTrimesterGrades != null) {
                    List<String> marksStrings = firstTrimesterGrades.stream().map(Grade::getMark).collect(Collectors.toList());
                    String marksString = String.join("   ", marksStrings);
                    binding.tvDisplayingRatingsFirstTrimester.setText(marksString);
                    updateAverageMarkAndFinalMark("firstTrimester", firstTrimesterGrades);
                }
                else {
                    binding.tvDisplayingRatingsFirstTrimester.setText("Не аттестация!");
                    binding.tvAverageMarkFirstTrimester.setVisibility(View.GONE);
                    binding.tvFinalMarkFirstTrimester.setText("Итоговая оценка: 2");
                }

                // Отобразите оценки за второй триместр
                ArrayList<Grade> secondTrimesterGrades = grades.get("secondTrimester");
                if (secondTrimesterGrades != null) {
                    List<String> marksStrings = secondTrimesterGrades.stream().map(Grade::getMark).collect(Collectors.toList());
                    String marksString = String.join("   ", marksStrings);
                    binding.tvDisplayingRatingsSecondTrimester.setText(marksString);
                    updateAverageMarkAndFinalMark("secondTrimester", secondTrimesterGrades);
                }
                else {
                    binding.tvDisplayingRatingsSecondTrimester.setText("Не аттестация!");
                    binding.tvAverageMarkSecondTrimester.setVisibility(View.GONE);
                    binding.tvFinalMarkSecondTrimester.setText("Итоговая оценка: 2");
                }

                // Отобразите оценки за третий триместр
                ArrayList<Grade> thirdTrimesterGrades = grades.get("thirdTrimester");
                if (thirdTrimesterGrades != null) {
                    List<String> marksStrings = thirdTrimesterGrades.stream().map(Grade::getMark).collect(Collectors.toList());
                    String marksString = String.join("   ", marksStrings);
                    binding.tvDisplayingRatingsThirdTrimester.setText(marksString);
                    updateAverageMarkAndFinalMark("thirdTrimester", thirdTrimesterGrades);
                }
                else {
                    binding.tvDisplayingRatingsThirdTrimester.setText("Не аттестация!");
                    binding.tvAverageMarkThirdTrimester.setVisibility(View.GONE);
                    binding.tvFinalMarkThirdTrimester.setText("Итоговая оценка: 2");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TeacherTrimesterFragment", "onCancelled", databaseError.toException());
            }
        });
    }

    private void updateAverageMarkAndFinalMark(String trimester, ArrayList<Grade> grades) {
        float averageMark = calculateAverageMark(grades);
        String finalMark = calculateFinalMark(averageMark);

        switch (trimester) {
            case "firstTrimester":
                binding.tvAverageMarkFirstTrimester.setText("Средний балл: " + averageMark);
                binding.tvFinalMarkFirstTrimester.setText("Итоговая оценка: " + finalMark);
                break;
            case "secondTrimester":
                binding.tvAverageMarkSecondTrimester.setText("Средний балл: " + averageMark);
                binding.tvFinalMarkSecondTrimester.setText("Итоговая оценка: " + finalMark);
                break;
            case "thirdTrimester":
                binding.tvAverageMarkThirdTrimester.setText("Средний балл: " + averageMark);
                binding.tvFinalMarkThirdTrimester.setText("Итоговая оценка: " + finalMark);
                break;
        }
    }

    private float calculateAverageMark(ArrayList<Grade> grades) {
        int total = 0;
        int count = 0;

        for (Grade grade : grades) {
            if (!grade.getMark().equals("н")) {
                total += Integer.parseInt(grade.getMark());
                count++;
            }
        }

        if (count == 0) {
            return 2;
        }

        double average = (double) total / count;
        return (float) Math.round(average * 100) / 100;
    }

    private String calculateFinalMark(float averageMark) {
        return String.valueOf(Math.round(averageMark));
    }
}