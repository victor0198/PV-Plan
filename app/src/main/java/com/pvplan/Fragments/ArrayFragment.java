package com.pvplan.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pvplan.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ArrayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_array, container, false);

        ArrayList<Float> profileDaily = new ArrayList<Float>(
                Arrays.asList(1993.68f,
                    3238.06f,
                    5498.05f,
                    6844.16f,
                    7532.49f,
                    8118.55f,
                    8427.06f,
                    8040.95f,
                    6549.86f,
                    4719.8f,
                    2500.08f,
                    1905.87f));

        GraphView graphView = v.findViewById(R.id.graph_pv_year);
        graphView.setTitle("Monthly energy production");
        graphView.setTitleColor(R.color.purple_200);
        graphView.setTitleTextSize(40);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        int month = 0;
        for (Float value :
                profileDaily) {
            series.appendData(new DataPoint(month+1, value), true, 12);
            month += 1;
        }
        graphView.addSeries(series);

        graphView.getViewport().setMinX(0.8);
        graphView.getViewport().setMaxX(12.5);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);


        ArrayList<Float> profileMonthly = new ArrayList<Float>(
                Arrays.asList(0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    20.96f,
                    137.49f,
                    211.88f,
                    248.03f,
                    247.83f,
                    228.45f,
                    191.52f,
                    134.67f,
                    37.35f,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f));
        GraphView graphViewM = v.findViewById(R.id.graph_pv_day);
        graphViewM.setTitle("Daily energy production");
        graphViewM.setTitleColor(R.color.purple_200);
        graphViewM.setTitleTextSize(40);
        LineGraphSeries<DataPoint> seriesMonths = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        int hour = 1;
        for (Float value :
                profileMonthly) {
            seriesMonths.appendData(new DataPoint(hour, value*7.2f), true, 24);
            hour += 1;
        }
        graphViewM.addSeries(seriesMonths);
        graphViewM.getViewport().setMinX(-0.5);
        graphViewM.getViewport().setMaxX(25);
        graphViewM.getViewport().setMinY(0);
//        graphViewM.getViewport().setMaxY(Double.valueOf(Collections.max(profileMonthly))*1.1);
        graphViewM.getViewport().setYAxisBoundsManual(true);
        graphViewM.getViewport().setXAxisBoundsManual(true);





        Spinner profileSpinner = v.findViewById(R.id.spinner_moth_pv);
        String[] items = new String[]{"July","February"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        profileSpinner.setAdapter(adapter);
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getContext(), "month pv " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return v;
    }
}