package com.pvplan.Fragments;

import static java.lang.Math.floor;
import static java.lang.Math.round;

import android.app.DownloadManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pvplan.ProjectConfigurationActivity;
import com.pvplan.R;
import com.pvplan.database.ConsumptionModel;
import com.pvplan.database.DataBaseHelper;
import com.pvplan.database.MonthlyBatteryModel;
import com.pvplan.database.ProfileModel;
import com.pvplan.database.ProjectModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsumersFragment extends Fragment {
    private final static int ARRAY_TAB_POSITION = 2;
    ProjectConfigurationActivity parentActivity;
    int projectId;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    DataBaseHelper dbh;
    String[] powerUnits = new String[]{"KW","W"};
    String[] timeUnits = new String[]{"year","month","day"};
    String[] months = new String[]{"January","February","March","April","May","June",
            "July","August","September","October","November","December"};

    static String[] monthsShort = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec",""};
    int profile_selected = 0;
    int power_selected = 0;
    int time_selected = 0;
    int month_selected = 0;
    ArrayList<ProfileModel> profiles;
    double multiplier = 0;
    static View v;

    public ConsumersFragment(ProjectConfigurationActivity parentActivity, int projectId, TabLayout tabLayout, ViewPager2 viewPager2) {
        super();
        this.parentActivity = parentActivity;
        this.projectId = projectId;
        this.tabLayout = tabLayout;
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_consumers, container, false);

        dbh = new DataBaseHelper(v.getContext());
        profiles = dbh.getProfilesList();

        GraphView graphView = v.findViewById(R.id.daily_consumption_profile_graph);
        GraphView graphViewM = v.findViewById(R.id.mothly_consumption_profile_graph);
        loadGraphs(profiles.get(0).getId(), graphView, graphViewM, 1);

        Spinner profileSpinner = v.findViewById(R.id.profile_spinner);
        String[] items = new String[profiles.size()];
        for(int idx=0; idx<profiles.size(); idx++){
           items[idx] = profiles.get(idx).getName();
        }

        EditText energyInput = v.findViewById(R.id.energy_value);
        ConsumptionModel cm_show = dbh.getConsumption(projectId);

        if(cm_show.getProjectId() == 0){
            energyInput.setText("2000");
            multiplier = 2000;
        }else{
            Double value = cm_show.getValue();
            if (cm_show.getEnergyUnit().equals("W"))
                value *= 1000d;
            if (cm_show.getTimeUnit().equals("month"))
                value *= 12d;
            if (cm_show.getTimeUnit().equals("day"))
                value *= 365d;
            energyInput.setText(value.toString());
            multiplier = value;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        profileSpinner.setAdapter(adapter);
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Toast.makeText(getContext(), "profile " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
                profile_selected = position;
                loadGraphs(profiles.get(position).getId(), graphView, graphViewM, multiplier);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner dropdownPower = v.findViewById(R.id.unitPower);
        ArrayAdapter<String> dropdownPowerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, powerUnits);
        dropdownPowerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownPower.setAdapter(dropdownPowerAdapter);
        dropdownPower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Toast.makeText(getContext(), "power " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
                power_selected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner dropdownTime = v.findViewById(R.id.unitTime);
        ArrayAdapter<String> dropdownTimeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, timeUnits);
        dropdownTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownTime.setAdapter(dropdownTimeAdapter);
        dropdownTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Toast.makeText(getContext(), "time " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
                time_selected = position;

                Resources r = getContext().getResources();
                if (position!=0) {
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            62,
                            r.getDisplayMetrics()
                    );
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) profileSpinner.getLayoutParams();
                    mlp.setMargins(mlp.leftMargin, px, mlp.rightMargin, mlp.bottomMargin);
                    profileSpinner.requestLayout();

                    v.findViewById(R.id.textView8).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.spinner_month_consumption).setVisibility(View.VISIBLE);
                }else{
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            28,
                            r.getDisplayMetrics()
                    );
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) profileSpinner.getLayoutParams();
                    mlp.setMargins(mlp.leftMargin, px, mlp.rightMargin, mlp.bottomMargin);
                    profileSpinner.requestLayout();

                    v.findViewById(R.id.textView8).setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.spinner_month_consumption).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        v.findViewById(R.id.apply_consumption).setOnClickListener(view -> {
            Log.d("Consumer chosen", "power:" + power_selected + ", time:" + time_selected);

            EditText energy = v.findViewById(R.id.energy_value);
            Double energyInputValue = Double.parseDouble(energy.getText().toString());
            dbh.applyConsumption(getContext(), projectId, Float.parseFloat(energyInputValue.toString()),
                    powerUnits[power_selected], timeUnits[time_selected],
                    month_selected+1, profiles.get(profile_selected).getId());

            ConsumptionModel cm = dbh.getConsumption(projectId);
            Double WattsDay;
            if(cm.getEnergyUnit().equals("KW")){
                WattsDay = cm.getValue()*1000;
            }else{
                WattsDay = cm.getValue();
            }
            if(cm.getTimeUnit().equals("month")){
                WattsDay = WattsDay /30;
            }
            if(cm.getTimeUnit().equals("year")){
                WattsDay = WattsDay /365;
            }
            Double lpd = dbh.getLowestPerfD(projectId);
            Log.d("----wpd", WattsDay.toString());
            Log.d("----lpd", lpd.toString());
            Double neededP = WattsDay / lpd / 0.78;
            Integer x = Double.valueOf(neededP/10).intValue();
            dbh.updateOptimalPower(projectId, x/100d);
            dbh.setPower(projectId, x/100d);

            // move to array tab
            Log.d("Tabs switching", "moving from consumers to array");
            parentActivity.enableTab(tabLayout, ARRAY_TAB_POSITION);
            viewPager2.setCurrentItem(ARRAY_TAB_POSITION);
            tabLayout.getTabAt(ARRAY_TAB_POSITION).select();

            ConsumersFragment.uploadBriefData projectsListTask = new ConsumersFragment.uploadBriefData(parentActivity.getApplicationContext(), dbh, projectId);
            projectsListTask.execute();

        });

        Spinner monthSpinner = v.findViewById(R.id.spinner_month_consumption);
        ArrayAdapter<String> adapterMonths = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, months);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        monthSpinner.setAdapter(adapterMonths);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Toast.makeText(getContext(), "month " + Integer.valueOf(position).toString(), Toast.LENGTH_SHORT).show();
                month_selected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        energyInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    try{
                        multiplier = Double.parseDouble(energyInput.getText().toString());
                        loadGraphs(profiles.get(0).getId(), graphView, graphViewM, multiplier);
                    }catch (Exception e){
                        Log.e("Multiplier", e.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }

    private void loadGraphs(int profile_id, GraphView graphView, GraphView graphViewM, double multiplier) {
        ConsumptionModel cm = dbh.getConsumption(projectId);
        Double WattsDay;
        if(power_selected == 0){
            WattsDay = multiplier*1000;
        }else{
            WattsDay = multiplier;
        }
        if(time_selected == 1){
            WattsDay = WattsDay /30;
        }
        if(time_selected == 0){
            WattsDay = WattsDay /365;
        }

        graphView.removeAllSeries();
        graphViewM.removeAllSeries();

        ArrayList<Float> profileDaily = dbh.getDailyProfile(profile_id);
        graphView.setTitle("Hourly");
        graphView.setTitleColor(R.color.purple_200);
        graphView.setTitleTextSize(40);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        int hour = 0;
        for (Float value :
                profileDaily) {
            series.appendData(new DataPoint(hour, value*WattsDay/1000), true, 24);
            hour += 1;
        }
        graphView.addSeries(series);
        series.setColor(Color.RED);
        series.setAnimated(true);

//        graphView.getViewport().setMinX(-0.9);
        graphView.getViewport().setMaxX(24);
//        graphView.getViewport().setMinY(0);
//        graphView.getViewport().setMaxY(Collections.max(profileDaily) * multiplier *1.1);
//        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX) + ":00";
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " kW";
                }
            }
        });


        ArrayList<Float> profileMonthly = dbh.getMonthlyProfile(profile_id);
        graphViewM.setTitle("Monthly");
        graphViewM.setTitleColor(R.color.purple_200);
        graphViewM.setTitleTextSize(45);
        LineGraphSeries<DataPoint> seriesMonths = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        int month = 1;
        for (Float value :
                profileMonthly) {
            seriesMonths.appendData(new DataPoint(month, value/180*WattsDay/1000*30), true, 12);
            month += 1;
        }
        graphViewM.addSeries(seriesMonths);
        seriesMonths.setColor(Color.RED);
        seriesMonths.setAnimated(true);
//        graphViewM.getViewport().setMinX(0.5);
        graphViewM.getViewport().setMaxX(13.0d);
        graphViewM.getViewport().setMinY(Collections.min(profileMonthly) * 0.8d);
        graphViewM.getViewport().setMaxY(Collections.max(profileMonthly) * 1.2d);
        graphViewM.getViewport().setYAxisBoundsManual(true);
        graphViewM.getViewport().setXAxisBoundsManual(true);
        graphViewM.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    if(value<1)
                        return "";
                    else
                        return monthsShort[Double.valueOf(value).intValue()-1];
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " KW";
                }
            }
        });
    }


    private static class uploadBriefData extends AsyncTask<Integer, Integer, String> {

        Context context;
        DataBaseHelper dbh;
        int projectId;
        public uploadBriefData(Context context,DataBaseHelper dbh, int projectId) {
            super();
            this.context = context;
            this.dbh = dbh;
            this.projectId = projectId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // ...
        }

        @Override
        protected String doInBackground(Integer... integers) {
            publishProgress(10);
            // ...
            Log.d("Async task", "Started. ");
            ProjectModel currProject = dbh.getProjectInfoById(projectId);

            // TODO compute suggested battery size
            Integer batterySize = 15000;

            Double arrayPower = new Double(currProject.getPower()*1000);
            Log.d("PROJECT POWR", currProject.getPower().toString());
            Integer aP = arrayPower.intValue();
            Log.d("PROJECT POWR", aP.toString());


            ConsumptionModel cm = dbh.getConsumption(projectId);
            Double WattsDay;
            if(cm.getEnergyUnit().equals("KW")){
                WattsDay = cm.getValue()*1000;
            }else{
                WattsDay = cm.getValue();
            }
            if(cm.getTimeUnit().equals("month")){
                WattsDay = WattsDay /30;
            }
            if(cm.getTimeUnit().equals("year")){
                WattsDay = WattsDay /365;
            }
            Log.d("WATTS/D", WattsDay.toString());
            Integer consumptionDaily = Double.valueOf(WattsDay).intValue();

            String url = "https://re.jrc.ec.europa.eu/api/v5_2/SHScalc?lat=" + currProject.getLatitude() + "&lon=" + currProject.getLongitude() + "&raddatabase=PVGIS-SARAH2&browser=1&outputformat=csv&userhorizon=&usehorizon=1&angle=" + Double.valueOf(currProject.getSlope()).intValue() + "&aspect=" + Double.valueOf(currProject.getAzimuth()).intValue() + "&peakpower=" + aP.toString() + "&hourconsumptionfile=&js=1&select_database_offgrid=PVGIS-SARAH2&ipeakpower=" + aP.toString() + "&batterysize=" + batterySize + "&cutoff=40&consumptionday=" + consumptionDaily + "&shsangle=" + Double.valueOf(currProject.getSlope()).intValue() + "&shsaspect=" + Double.valueOf(currProject.getAzimuth()).intValue();

//             build the request
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.d("REQ 2 resp.", "createRegisterRequest() :: onResponse() ::" + response);
                            uploadFile2Contents(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("REQ 2 resp.", "createBookingRequest() :: onErrorResponse() ::" + error);
                }
            });

            // make request
            RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
            requestQueue.add(stringRequest);

            Log.d("Async task", "Finishing..");
            return "Finished!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // ...
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);


            // TODO compute optimal storage size
            dbh.updateOptimalStorage(projectId, 0d);
            dbh.setBattery(projectId, 0d);

            Log.d("Async task", string);
            //
        }

        public void uploadFile2Contents(String response){
            ArrayList<MonthlyBatteryModel> mbm = new ArrayList<>();
            try {
                String lines[] = response.split("\n");

                boolean nedded_data = false;
                int count_months = 1;
                for (int i = 0; i<lines.length;i++ ) {
                    String []s = lines[i].split("\t");

                    Log.d("GRID s[0]", s[0]);
                    if(s[0].equals("month")){
                        nedded_data = true;
                        continue;
                    }
                    if(count_months>12){
                        nedded_data = false;
                        break;
                    }
                    if(nedded_data) {

                        for(int j=0; j<s.length; j++){
                            String value = s[j].replaceAll("\t","");
                            Log.d("grid file data:", value);

                        }
                        Log.d("grid file data:", s[0]+"|"+s[2]+"|"+s[4]+"|"+s[6]+"|"+s[8]);
                        MonthlyBatteryModel tmp = new MonthlyBatteryModel(projectId,
                                Integer.parseInt(s[0]),
                                Double.parseDouble(s[2]),
                                Double.parseDouble(s[4]),
                                Double.parseDouble(s[6]),
                                Double.parseDouble(s[8])
                        );
                        mbm.add(tmp);
                        count_months++;
                    }
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
            dbh.applyMonthlyBattery(projectId, mbm);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(month_selected==0) {
            Spinner profileSpinner = v.findViewById(R.id.profile_spinner);
            Resources r = getContext().getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    28,
                    r.getDisplayMetrics()
            );
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) profileSpinner.getLayoutParams();
            mlp.setMargins(mlp.leftMargin, px, mlp.rightMargin, mlp.bottomMargin);
            profileSpinner.requestLayout();

            v.findViewById(R.id.textView8).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.spinner_month_consumption).setVisibility(View.INVISIBLE);
        }
    }
}