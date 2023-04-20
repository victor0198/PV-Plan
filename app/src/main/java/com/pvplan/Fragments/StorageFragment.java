package com.pvplan.Fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pvplan.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class StorageFragment extends Fragment {

    GraphView graphViewM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_storage, container, false);
        graphViewM = v.findViewById(R.id.graph_battery);

        Spinner profileSpinner = v.findViewById(R.id.chemistry_spinner);
        String[] items = new String[]{"Lead-acid","Li-Ion"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        profileSpinner.setAdapter(adapter);
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getContext(), "battery " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner capacitySpinner = v.findViewById(R.id.spinner_capacity);
        String[] capacityUnits = new String[]{"Ah","Wh","kWh"};
        ArrayAdapter<String> cpacityAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, capacityUnits);
        cpacityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        capacitySpinner.setAdapter(cpacityAdapter);
        capacitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getContext(), "battery cap." + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
                Resources r = getContext().getResources();

                if (position==0) {
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            75,
                            r.getDisplayMetrics()
                    );
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) graphViewM.getLayoutParams();
                    mlp.setMargins(mlp.leftMargin, px, mlp.rightMargin, mlp.bottomMargin);
                    graphViewM.requestLayout();

                    v.findViewById(R.id.voltage_label).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.voltage_value).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.voltage_unit).setVisibility(View.VISIBLE);
                }else{
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            24,
                            r.getDisplayMetrics()
                    );
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) graphViewM.getLayoutParams();
                    mlp.setMargins(mlp.leftMargin, px, mlp.rightMargin, mlp.bottomMargin);
                    graphViewM.requestLayout();
                    v.findViewById(R.id.voltage_label).setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.voltage_value).setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.voltage_unit).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        ArrayList<Float> profileMonthly = new ArrayList<Float>(
//                Arrays.asList(61.0f,
//                    6.0f,
//                    4.0f,
//                    5.0f,
//                    3.0f,
//                    4.0f,
//                    3.0f,
//                    4.0f,
//                    4.0f,
//                    6.0f));

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(43, 30),
                new DataPoint(49, 4),
                new DataPoint(55, 4),
                new DataPoint(61, 4),
                new DataPoint(67, 9),
                new DataPoint(73, 12),
                new DataPoint(79, 6),
                new DataPoint(85, 5),
                new DataPoint(91, 5),
                new DataPoint(97, 20),

        });
        graphViewM.addSeries(series);

// styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(50);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);

        graphViewM.setTitle("Percentage of days with this charge state");
//        graphViewM.setTitleColor(R.color.purple_200);
//        graphViewM.setTitleTextSize(40);
//        LineGraphSeries<DataPoint> seriesMonths = new LineGraphSeries<DataPoint>(new DataPoint[]{});
//        int month = 100;
//        for (Float value :
//                profileMonthly) {
//            seriesMonths.appendData(new DataPoint(month, value), true, 12);
//            month += 10;
//        }
//        graphViewM.addSeries(seriesMonths);
//        graphViewM.getViewport().setMinX(35);
//        graphViewM.getViewport().setMaxX(13);
//        graphViewM.getViewport().setMinY(0);
//        graphViewM.getViewport().setMaxY(105);
//        graphViewM.getViewport().setYAxisBoundsManual(true);
//        graphViewM.getViewport().setXAxisBoundsManual(true);

        return v;
    }
}