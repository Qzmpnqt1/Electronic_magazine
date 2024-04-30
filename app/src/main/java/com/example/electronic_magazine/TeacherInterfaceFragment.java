package com.example.electronic_magazine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherInterfaceFragment extends Fragment {
    private List<String> teacherClass;
    private ListView lvTeacherClass, lvStudentInClass;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_interface, container, false);
        lvTeacherClass = view.findViewById(R.id.lvTeacherClass);
        lvStudentInClass = view.findViewById(R.id.lvStudentInClass);

        String teacherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(teacherId);
        teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String teacherSubject = snapshot.child("subject").getValue(String.class);

                    DatabaseReference classesRef = FirebaseDatabase.getInstance().getReference("subjectClasses").child(teacherSubject);
                    classesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            teacherClass = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String className = dataSnapshot.getValue(String.class);
                                teacherClass.add(className);
                            }
                            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, teacherClass);
                            lvTeacherClass.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle cancelled event
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancelled event
            }
        });

        lvTeacherClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedClass = adapter.getItem(i);

                DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
                Query query = studentsRef.orderByChild("schoolClass").equalTo(selectedClass);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> studentsInClass = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String studentName = dataSnapshot.child("fullName").getValue(String.class);
                            studentsInClass.add(studentName);
                        }
                        ArrayAdapter<String> studentsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, studentsInClass);
                        lvStudentInClass.setAdapter(studentsAdapter);
                        if (!studentsInClass.isEmpty()) {
                            lvTeacherClass.setVisibility(View.GONE);
                            lvStudentInClass.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle cancelled event
                    }
                });
            }
        });

        lvStudentInClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new TeacherTrimesterFragment())
                        .commit();
            }
        });

        return view;
    }
}





