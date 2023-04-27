package com.pvplan.Fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pvplan.ProjectConfigurationActivity;
import com.pvplan.R;
import com.pvplan.database.DataBaseHelper;
import com.pvplan.database.MonthlyBatteryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class StorageFragment extends Fragment {
    private final static int RESULTS_TAB_POSITION = 4;
    ProjectConfigurationActivity parentActivity;
    static int projectId;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    static DataBaseHelper dbh;
    GraphView graphViewM;
    static View v;

    String[] months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public StorageFragment(ProjectConfigurationActivity parentActivity, int projectId, TabLayout tabLayout, ViewPager2 viewPager2) {
        super();
        this.parentActivity = parentActivity;
        StorageFragment.projectId = projectId;
        this.tabLayout = tabLayout;
        this.viewPager2 = viewPager2;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_storage, container, false);
        graphViewM = v.findViewById(R.id.graph_battery);

        dbh = new DataBaseHelper(v.getContext());

        Spinner profileSpinner = v.findViewById(R.id.chemistry_spinner);
        String[] items = new String[]{"Li-Ion","Lead-acid"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        profileSpinner.setAdapter(adapter);
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(getContext(), "battery " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner capacitySpinner = v.findViewById(R.id.spinner_capacity);
        String[] capacityUnits = new String[]{"kWh","Ah"};
        ArrayAdapter<String> capacityAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, capacityUnits);
        capacityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        capacitySpinner.setAdapter(capacityAdapter);

        capacitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(getContext(), "battery cap." + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
                Resources r = getContext().getResources();

                if (position>0) {
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            75,
                            r.getDisplayMetrics()
                    );
                    Button approximate_storage = v.findViewById(R.id.approximate_storage);
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) approximate_storage.getLayoutParams();
                    mlp.setMargins(mlp.leftMargin, px, mlp.rightMargin, mlp.bottomMargin);
                    approximate_storage.requestLayout();

                    v.findViewById(R.id.voltage_label).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.voltage_value).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.voltage_unit).setVisibility(View.VISIBLE);
                }else{
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            24,
                            r.getDisplayMetrics()
                    );
                    Button approximate_storage = v.findViewById(R.id.approximate_storage);
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) approximate_storage.getLayoutParams();
                    mlp.setMargins(mlp.leftMargin, px, mlp.rightMargin, mlp.bottomMargin);
                    approximate_storage.requestLayout();
                    v.findViewById(R.id.voltage_label).setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.voltage_value).setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.voltage_unit).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        EditText storage = v.findViewById(R.id.battery_capacity_input);
        storage.setText(dbh.getOptimalById(projectId).getS_power());

        v.findViewById(R.id.apply_storage).setOnClickListener(view -> {
            Log.d("Storage chosen", "..");

            // move to array tab
            Log.d("Tabs switching", "moving from storage to results");
            parentActivity.enableTab(tabLayout, RESULTS_TAB_POSITION);
            viewPager2.setCurrentItem(RESULTS_TAB_POSITION);
            tabLayout.getTabAt(RESULTS_TAB_POSITION).select();
        });

//        graphViewM.setTitle("Percentage of days with this charge state");

        Button approximateBtn = v.findViewById(R.id.approximate_storage);
        approximateBtn.setOnClickListener(view ->{
            // TODO compute optimal storage size
            dbh.updateOptimalStorage(projectId, 8.0d);

            // TODO redraw

        });

        return v;
    }

    public void initGraph() {
        ArrayList<MonthlyBatteryModel> mbm = dbh.getBatteryInfo(projectId);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {});

        graphViewM.removeAllSeries();

        int month = 1;
        for (MonthlyBatteryModel anmbm :
                mbm) {
            series.appendData(new DataPoint(month, anmbm.getBatteryFull()), true, 12);
            month += 1;
        }

        series.setSpacing(35);
        series.setAnimated(true);
        graphViewM.addSeries(series);
        graphViewM.setTitle("Days when the battery reaches full charge");
        graphViewM.setTitleColor(R.color.purple_200);
        graphViewM.setTitleTextSize(45);

//        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {});
//
//        month = 1;
//        for (MonthlyBatteryModel anmbm :
//                mbm) {
//            Log.d("month",String.valueOf(month));
//            series2.appendData(
//                    new DataPoint(month, anmbm.getBatteryEmpty()),
//                    true,
//                    12);
//            month += 1;
//        }
//
//
//
//        series2.setColor(Color.RED);
//        series2.setSpacing(35);
//        series2.setAnimated(true);
//        graphViewM.getSecondScale().addSeries(series2);
        // the y bounds are always manual for second scale
//        graphViewM.getSecondScale().setMinY(0);
//        graphViewM.getSecondScale().setMaxY(100);

        // legend
//        series.setTitle("foo");
//        series2.setTitle("bar");
//        graphViewM.getLegendRenderer().setVisible(true);
//        graphViewM.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        // custom label formatter to show currency "EUR"
        graphViewM.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {

                    // show normal x values
                    if(value<1 || value>12)
                        return "";
                    else
                        return months[Double.valueOf(value).intValue()-1];
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " %";
                }
            }
        });
        graphViewM.getViewport().setMinX(0);
        graphViewM.getViewport().setMaxX(13);
//        graphViewM.getViewport().setMinY(0);
//        graphViewM.getViewport().setMaxY(Collections.max(profileMonthly) * 1.1);
//        graphViewM.getViewport().setYAxisBoundsManual(true);
        graphViewM.getViewport().setXAxisBoundsManual(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        EditText storage = v.findViewById(R.id.battery_capacity_input);
        storage.setText(dbh.getOptimalById(projectId).getS_power());
        initGraph();
    }
}