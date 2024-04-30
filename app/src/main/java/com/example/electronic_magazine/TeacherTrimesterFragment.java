package com.example.electronic_magazine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeacherTrimesterFragment extends Fragment {
    private String markFirstTrimester, markSecondTrimester, markThirdTrimester;
    private List<String> markFirstTrimesterList, markSecondTrimesterList, markThirdTrimesterList;
    private List<String> markList = Arrays.asList(" ", "2", "3", "4", "5", "н");
    Spinner spMark1, spMark2, spMark3;
    Button bFirstTrimester, bSecondTrimester, bThirdTrimester;
    TextView tvDisplayingRatingsFirstTrimester, tvDisplayingRatingsSecondTrimester, tvDisplayingRatingsThirdTrimester;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_trimestr, container, false);

        markFirstTrimesterList = new ArrayList<>();
        markSecondTrimesterList = new ArrayList<>();
        markThirdTrimesterList = new ArrayList<>();

        tvDisplayingRatingsFirstTrimester = view.findViewById(R.id.tvDisplayingRatingsFirstTrimester);
        tvDisplayingRatingsSecondTrimester = view.findViewById(R.id.tvDisplayingRatingsSecondTrimester);
        tvDisplayingRatingsThirdTrimester = view.findViewById(R.id.tvDisplayingRatingsThirdTrimester);

        spMark1 = view.findViewById(R.id.spMark1);
        spMark2 = view.findViewById(R.id.spMark2);
        spMark3 = view.findViewById(R.id.spMark3);

        bFirstTrimester = view.findViewById(R.id.bFirstTrimester);
        bSecondTrimester = view.findViewById(R.id.bSecondTrimester);
        bThirdTrimester = view.findViewById(R.id.bThirdTrimester);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, markList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spMark1.setAdapter(adapter);
        spMark2.setAdapter(adapter);
        spMark3.setAdapter(adapter);

        //------------------------------------------------------------------------------------------
        spMark1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                markFirstTrimester = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bFirstTrimester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markFirstTrimesterList.add(markFirstTrimester);
                tvDisplayingRatingsFirstTrimester.setText(String.join("   ", markFirstTrimesterList));
            }
        });
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        spMark2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                markSecondTrimester = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bSecondTrimester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markSecondTrimesterList.add(markSecondTrimester);
                tvDisplayingRatingsSecondTrimester.setText(String.join("   ", markSecondTrimesterList));
            }
        });
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        spMark3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                markThirdTrimester = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bThirdTrimester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markThirdTrimesterList.add(markThirdTrimester);
                tvDisplayingRatingsThirdTrimester.setText(String.join("   ", markThirdTrimesterList));
            }
        });
        //------------------------------------------------------------------------------------------

        return view;
    }
}