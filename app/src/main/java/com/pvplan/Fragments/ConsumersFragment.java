package com.pvplan.Fragments;

import static java.lang.Math.round;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pvplan.R;

import org.osmdroid.api.IGeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ConsumersFragment extends Fragment {
    private final static int RESULTS_TAB_POSITION = 4;
    int projectId;
    TabLayout tabLayout;
    ViewPager2 viewPager2;

    public ConsumersFragment(int projectId, TabLayout tabLayout, ViewPager2 viewPager2) {
        super();
        this.projectId = projectId;
        this.tabLayout = tabLayout;
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_consumers, container, false);

        ArrayList<Float> profileDaily = new ArrayList<Float>(
                Arrays.asList(0.0505f,0.0350f,0.0270f,0.0250f,0.0245f,0.0265f,0.0345f,0.0295f,
                0.0230f,0.0180f,0.0180f,0.0205f,0.0265f,0.0350f,0.0415f,0.0505f,
                0.0455f,0.0340f,0.0250f,0.0300f,0.0650f,0.1095f,0.1175f,0.0880f));
        GraphView graphView = v.findViewById(R.id.daily_consumption_profile_graph);
        graphView.setTitle("Daily");
        graphView.setTitleColor(R.color.purple_200);
        graphView.setTitleTextSize(40);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        int hour = 0;
        for (Float value :
                profileDaily) {
            series.appendData(new DataPoint(hour, value*41.66), true, 24);
            hour += 1;
        }
        graphView.addSeries(series);

        graphView.getViewport().setMinX(-0.9);
        graphView.getViewport().setMaxX(27);
        graphView.getViewport().setMinY(0);
//        graphView.getViewport().setMaxY(Double.valueOf(Collections.max(profileDaily))*1.1);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);

        ArrayList<Float> profileMonthly = new ArrayList<Float>(
                Arrays.asList(201.46f,
                        156f,
                        181.38f,
                        136.48f,
                        148.98f,
                        140.75f,
                        184.63f,
                        155.2f,
                        140.75f,
                        174.23f,
                        170.83f,
                        197.22f));
        GraphView graphViewM = v.findViewById(R.id.mothly_consumption_profile_graph);
        graphViewM.setTitle("Monthly");
        graphViewM.setTitleColor(R.color.purple_200);
        graphViewM.setTitleTextSize(40);
        LineGraphSeries<DataPoint> seriesMonths = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        int month = 1;
        for (Float value :
                profileMonthly) {
            seriesMonths.appendData(new DataPoint(month, value), true, 12);
            month += 1;
        }
        graphViewM.addSeries(seriesMonths);
        graphViewM.getViewport().setMinX(0.5);
        graphViewM.getViewport().setMaxX(13);
        graphViewM.getViewport().setMinY(0);
        graphViewM.getViewport().setMaxY(Double.valueOf(Collections.max(profileMonthly))*1.1);
        graphViewM.getViewport().setYAxisBoundsManual(true);
        graphViewM.getViewport().setXAxisBoundsManual(true);



        Spinner profileSpinner = v.findViewById(R.id.profile_spinner);
        String[] items = new String[]{"default profile","no consumption"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        profileSpinner.setAdapter(adapter);
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getContext(), "profile " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner dropdownPower = v.findViewById(R.id.unitPower);
        String[] powerUnits = new String[]{"W","kW"};
        ArrayAdapter<String> dropdownPowerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, powerUnits);
        dropdownPowerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownPower.setAdapter(dropdownPowerAdapter);
        dropdownPower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getContext(), "power " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner dropdownTime = v.findViewById(R.id.unitTime);
        String[] timeUnits = new String[]{"day","month","year"};
        ArrayAdapter<String> dropdownTimeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, timeUnits);
        dropdownTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownTime.setAdapter(dropdownTimeAdapter);
        dropdownTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getContext(), "time " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        v.findViewById(R.id.to_results).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewPager2.setCurrentItem(RESULTS_TAB_POSITION);
//                tabLayout.getTabAt(RESULTS_TAB_POSITION).select();
//            }
//        });

        return v;
    }
}