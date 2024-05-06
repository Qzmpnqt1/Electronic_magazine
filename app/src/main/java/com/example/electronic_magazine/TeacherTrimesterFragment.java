package com.example.electronic_magazine;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.electronic_magazine.databinding.FragmentTeacherTrimestrBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TeacherTrimesterFragment extends Fragment {
    private String subject, studentFullName;
    private DatabaseReference gradesRef;
    private DatabaseReference studentRef;
    private FragmentTeacherTrimestrBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeacherTrimestrBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);

        if (month >= 8 && month <= 10) {
            showFirstTrimester();
        }
        else if (month >= 11 || month <= 1) {
            showSecondTrimester();
        }
        else if (month >= 2 && month <= 4) {
            showThirdTrimester();
        }

        subject = getArguments().getString("teacherSubject");
        studentFullName = getArguments().getString("studentFullName");

        List<String> markList = Arrays.asList("2", "3", "4", "5", "н");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, markList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spMark1.setAdapter(adapter);
        binding.spMark2.setAdapter(adapter);
        binding.spMark3.setAdapter(adapter);

        Query query = FirebaseDatabase.getInstance().getReference("students").orderByChild("fullName").equalTo(studentFullName);
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

        //------------------------------------------------------------------------------------------
        binding.bFirstTrimester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mark = binding.spMark1.getSelectedItem().toString();
                addGrade(subject, mark, "firstTrimester");
            }
        });
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        binding.bSecondTrimester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mark = binding.spMark2.getSelectedItem().toString();
                addGrade(subject, mark, "secondTrimester");
            }
        });
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        binding.bThirdTrimester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mark = binding.spMark3.getSelectedItem().toString();
                addGrade(subject, mark, "thirdTrimester");
            }
        });
        //------------------------------------------------------------------------------------------

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
                    binding.tvDisplayingRatingsFirstTrimester.setHint("Не аттестация");
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
                    binding.tvDisplayingRatingsSecondTrimester.setHint("Не аттестация");
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
                    binding.tvDisplayingRatingsThirdTrimester.setHint("Не аттестация");
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

    private void addGrade(String subject, String mark, String trimester) {
        // Получите текущую дату и время
        Calendar now = Calendar.getInstance();

        // Определите начало текущей недели (понедельник)
        now.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        long weekStart = now.getTimeInMillis();

        // Получите список оценок за текущий триместр
        DatabaseReference gradesRef = studentRef.child("grades").child(subject).child(trimester);
        gradesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Grade>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Grade>>() {};
                ArrayList<Grade> marks = dataSnapshot.getValue(genericTypeIndicator);
                if (marks == null) {
                    marks = new ArrayList<>();
                }

                int weekMarksCount = 0;
                for (Grade grade : marks) {
                    if (grade.getTimestamp() >= weekStart) {
                        weekMarksCount++;
                    }
                }

                if (weekMarksCount < 5) {
                    // Добавьте новую оценку в базу данных
                    Grade grade = new Grade(mark, System.currentTimeMillis());
                    marks.add(grade);
                    gradesRef.setValue(marks);

                    // Отобразите список оценок за текущий триместр
                    List<String> marksStrings = marks.stream().map(Grade::getMark).collect(Collectors.toList());
                    String marksString = String.join("   ", marksStrings);
                    switch (trimester) {
                        case "firstTrimester":
                            binding.tvDisplayingRatingsFirstTrimester.setText(marksString);
                            break;
                        case "secondTrimester":
                            binding.tvDisplayingRatingsSecondTrimester.setText(marksString);
                            break;
                        case "thirdTrimester":
                            binding.tvDisplayingRatingsThirdTrimester.setText(marksString);
                            break;
                    }

                    updateAverageMarkAndFinalMark(trimester, marks);
                } else {
                    // Выведите сообщение об ошибке
                    Toast.makeText(getContext(), "Максимальное количество оценок за неделю достигнуто", Toast.LENGTH_SHORT).show();
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

    private void showFirstTrimester() {
        binding.spMark2.setVisibility(View.GONE);
        binding.bSecondTrimester.setVisibility(View.GONE);
        binding.spMark3.setVisibility(View.GONE);
        binding.bThirdTrimester.setVisibility(View.GONE);
    }

    private void showSecondTrimester() {
        binding.spMark1.setVisibility(View.GONE);
        binding.bFirstTrimester.setVisibility(View.GONE);
        binding.spMark3.setVisibility(View.GONE);
        binding.bThirdTrimester.setVisibility(View.GONE);
    }
    private void showThirdTrimester() {
        binding.spMark1.setVisibility(View.GONE);
        binding.bFirstTrimester.setVisibility(View.GONE);
        binding.spMark2.setVisibility(View.GONE);
        binding.bSecondTrimester.setVisibility(View.GONE);
    }
}
