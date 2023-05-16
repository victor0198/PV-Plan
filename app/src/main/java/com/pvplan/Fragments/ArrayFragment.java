package com.pvplan.Fragments;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.pvplan.database.ProjectModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ArrayFragment extends Fragment {
    private final static int STORAGE_TAB_POSITION = 3;
    ProjectConfigurationActivity parentActivity;
    static int projectId;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    static DataBaseHelper dbh;
    static View v;
    static GraphView graphViewM;
    static DownloadManager downloadManager;
    private static ProgressBar actProg;
    static String[] months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec",""};

    public ArrayFragment(ProjectConfigurationActivity parentActivity, int projectId, TabLayout tabLayout, ViewPager2 viewPager2) {
        super();
        this.parentActivity = parentActivity;
        ArrayFragment.projectId = projectId;
        this.tabLayout = tabLayout;
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        downloadManager = (DownloadManager) parentActivity.getSystemService(DOWNLOAD_SERVICE);
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_array, container, false);
        graphViewM = v.findViewById(R.id.graph_pv_year);
        actProg = v.findViewById(R.id.progress_array);

        dbh = new DataBaseHelper(v.getContext());

        v.findViewById(R.id.compute_array).setOnClickListener(view -> {

            EditText tiltInput = v.findViewById(R.id.panel_tilt_input);
            double slope = Double.parseDouble(String.valueOf(tiltInput.getText()));
            EditText azimuthInput = v.findViewById(R.id.azimuth_input);
            double azimuth = Double.parseDouble(String.valueOf(azimuthInput.getText()));
            dbh.setArraySlope(projectId, slope);
            dbh.setArrayAzimuth(projectId, azimuth);

            uploadBriefData projectsListTask = new uploadBriefData(parentActivity.getApplicationContext(), dbh, projectId);
            projectsListTask.execute(1);

        });

        EditText panelsPower = v.findViewById(R.id.array_power_input);
        panelsPower.setText(dbh.getOptimalById(projectId).getP_power());

        panelsPower.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    try{
                        Double multiplier = Double.parseDouble(panelsPower.getText().toString());
                        Log.d("Panels power", multiplier + "kW");
                        dbh.setPower(projectId, multiplier);
                        Double p = Double.parseDouble(dbh.getOptimalById(projectId).getP_power());
                        Log.d("mul", multiplier.toString());
                        Log.d("p", p.toString());
                        CheckBox P_powerChkBx = (CheckBox) v.findViewById( R.id.check_opt_power );
                        String strMultiplier = panelsPower.getText().toString();
                        if((p!=multiplier || strMultiplier.endsWith("."))){
                            P_powerChkBx.setChecked(false);
                        }
                        if((p==multiplier || !strMultiplier.endsWith("."))){
                            P_powerChkBx.setChecked(true);
                        }
                        refreshUI();
                    }catch (Exception e){
                        Log.e("Multiplier", e.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText slopeInput = v.findViewById(R.id.panel_tilt_input);
        slopeInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    try{
                        double tilt = Double.parseDouble(slopeInput.getText().toString());
                        dbh.setArraySlope(projectId, tilt);
                        Double slope = Double.parseDouble(dbh.getOptimalById(projectId).getSlope());
                        CheckBox check_opt_slope = (CheckBox) v.findViewById( R.id.check_opt_slope );
                        if(slope==tilt){
                            check_opt_slope.setChecked(true);
                        }else{
                            check_opt_slope.setChecked(false);
                        }
//                        refreshUI();
                    }catch (Exception e){
                        Log.e("panel_tilt_input", e.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText azimuth_input = v.findViewById(R.id.azimuth_input);
        azimuth_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    try{
                        double azimuth = Double.parseDouble(azimuth_input.getText().toString());
                        dbh.setArrayAzimuth(projectId, azimuth);
                        Double azimuthOptimal = Double.parseDouble(dbh.getOptimalById(projectId).getAzimuth());
                        CheckBox check_opt_azimuth = (CheckBox) v.findViewById( R.id.check_opt_azimuth );
                        if(azimuthOptimal==azimuth){
                            check_opt_azimuth.setChecked(true);
                        }else{
                            check_opt_azimuth.setChecked(false);
                        }
//                        refreshUI();
                    }catch (Exception e){
                        Log.e("azimuth_input", e.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        v.findViewById(R.id.apply_array).setOnClickListener(view -> {
            Log.d("Array chosen", "..");
            // TODO compute storage capacity
            parentActivity.computeStorage();

            // move to array tab
            Log.d("Tabs switching", "moving from array to storage");
            parentActivity.enableTab(tabLayout, STORAGE_TAB_POSITION);
            viewPager2.setCurrentItem(STORAGE_TAB_POSITION);
            tabLayout.getTabAt(STORAGE_TAB_POSITION).select();
        });

        CheckBox P_powerChkBx = (CheckBox) v.findViewById( R.id.check_opt_power );
        P_powerChkBx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked )
            {
                EditText panelsPower1 = v.findViewById(R.id.array_power_input);
                Double p = Double.parseDouble(dbh.getOptimalById(projectId).getP_power());
                dbh.setPower(projectId, p);
                panelsPower1.setText(p.toString());
            }

        });

        CheckBox slopeChkBx = (CheckBox) v.findViewById( R.id.check_opt_slope );
        slopeChkBx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked )
            {
                dbh.setArraySlope(projectId, Double.parseDouble(dbh.getOptimalById(projectId).getSlope()));
                slopeInput.setText(dbh.getOptimalById(projectId).getSlope());
            }

        });

        CheckBox azimuthChkBx = (CheckBox) v.findViewById( R.id.check_opt_azimuth );
        azimuthChkBx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ( isChecked )
            {
                dbh.setArrayAzimuth(projectId, Double.parseDouble(dbh.getOptimalById(projectId).getAzimuth()));
                azimuth_input.setText(dbh.getOptimalById(projectId).getAzimuth());
            }

        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("RESUMED", "from array fragment");
        EditText panelsPower = v.findViewById(R.id.array_power_input);
        Double P_power = Double.parseDouble(dbh.getOptimalById(projectId).getP_power());
        dbh.setPower(projectId, P_power);
        panelsPower.setText(P_power.toString());
    }

    private static void refreshUI(){
        Log.d("Refresh UI", "from array fragment");
        ProjectModel pm = dbh.getProjectInfoById(projectId);
        EditText tilt = v.findViewById(R.id.panel_tilt_input);
        Log.d("TILT", pm.getSlope());
        tilt.setText(pm.getSlope());
        EditText azimuth = v.findViewById(R.id.azimuth_input);
        Log.d("AZIM", pm.getAzimuth());
        azimuth.setText(pm.getAzimuth());

        EditText panelsPower = v.findViewById(R.id.array_power_input);
        Double power = Double.parseDouble(panelsPower.getText().toString());

        ArrayList<Float> monthlyData = dbh.getMonthlyData(projectId);
        Log.d("GOT monthly data:", monthlyData.toString());

//        ArrayList<Float> profileMonthly = dbh.getMonthlyProfile(projectId);

        graphViewM.removeAllSeries();
        graphViewM.setTitle("Monthly energy production and consumption");
        graphViewM.setTitleColor(R.color.purple_200);
        graphViewM.setTitleTextSize(40);
        LineGraphSeries<DataPoint> seriesMonths = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        int month = 1;
        for (Float value :
                monthlyData) {
            seriesMonths.appendData(new DataPoint(month, value*power), true, 12);
            month += 1;
        }
        graphViewM.addSeries(seriesMonths);
        seriesMonths.setAnimated(true);

        LineGraphSeries<DataPoint> seriesMonthsIdeal = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        month = 1;
        for (Float value :
                monthlyData) {
            seriesMonthsIdeal.appendData(new DataPoint(month, value*power*0.85), true, 12);
            month += 1;
        }
        graphViewM.addSeries(seriesMonthsIdeal);
        seriesMonthsIdeal.setColor(Color.argb(255,30,200,30));
        seriesMonthsIdeal.setAnimated(true);

        ConsumptionModel cm = dbh.getConsumption(projectId);
        // TODO works only with KW/year
        Double kWattsMonth = cm.getValue()/12;
        ArrayList<Float> profileMonthly = dbh.getMonthlyProfile(1);
        LineGraphSeries<DataPoint> seriesMonths2 = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        month = 1;
        for (Float value :
                profileMonthly) {
            seriesMonths2.appendData(new DataPoint(month, value/180*kWattsMonth), true, 12);
            month += 1;
        }
        graphViewM.addSeries(seriesMonths2);
        seriesMonths2.setColor(Color.RED);
        seriesMonths2.setAnimated(true);

        graphViewM.getViewport().setMinX(0.5);
        graphViewM.getViewport().setMaxX(12.5);
        graphViewM.getViewport().setMinY(0d);
        graphViewM.getViewport().setMaxY(Double.valueOf(Collections.max(monthlyData))*power*1.1);
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
                        return months[Double.valueOf(value).intValue()-1];
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " kW";
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
            actProg.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            publishProgress(10);
            // ...
            Log.d("Async task", "Started. " + integers[0]);
            ProjectModel currProject = dbh.getProjectInfoById(projectId);

// ======================================================================================
            // download file 1
            Uri file1Uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/PV/tmp/" + currProject.getLatitude() + " " + currProject.getLongitude() + ".csv");
            Log.d("File1 path", file1Uri.getPath());

            File file1download = new File(file1Uri.getPath());
            boolean file1Exists = true;
            if (!file1download.exists()) {
                Log.d("File1 status", "does not exist, downloading");
                Uri uri = Uri.parse("https://re.jrc.ec.europa.eu/api/v5_2/PVcalc?lat=" + currProject.getLatitude() + "&lon=" + currProject.getLongitude() + "&raddatabase=PVGIS-SARAH2&browser=1&userhorizon=&usehorizon=1&outputformat=csv&js=1&select_database_grid=PVGIS-SARAH2&pvtechchoice=crystSi&peakpower=1&loss=14&mountingplace=free&optimalangles=1");
                if (currProject.getSlope().equals("-1") || currProject.getAzimuth().equals("-181")){
                    Log.d("File 1", "getting optimal angles");
                }else{
                    uri = Uri.parse("https://re.jrc.ec.europa.eu/api/v5_2/PVcalc?lat=" + currProject.getLatitude() + "&lon=" + currProject.getLongitude() + "&raddatabase=PVGIS-SARAH2&browser=1&userhorizon=&usehorizon=1&outputformat=csv&js=1&select_database_grid=PVGIS-SARAH2&pvtechchoice=crystSi&peakpower=1&loss=14&mountingplace=free&angle=" +
                            Double.valueOf(currProject.getSlope()).intValue() + "&aspect=" + Double.valueOf(currProject.getAzimuth()).intValue() );
                    Log.d("File 1", "getting custom angles:" + Double.valueOf(currProject.getAzimuth()).intValue());
                }
                long dwn1Id = downloadFile(uri, file1Uri);
                publishProgress(20);
                file1Exists = checkDownloadComplete(file1Uri, 15, dwn1Id, 1);
            } else {
                Log.d("File1 status", "already exists");
            }
            try {
                Thread.sleep(100);
                Log.d("WAITING-", "wait 1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // upload file 1 into DB
            if (file1Exists) {
                Log.d("FILE 1", "uploading to DB");
                publishProgress(50);
                uploadFile1Contents(file1Uri);
            } else {
                Log.e("File 1", "NOT DOWNLOADED in ? seconds!");
            }
            try {
                Thread.sleep(100);
                Log.d("WAITING-", "wait 2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            publishProgress(80);
            File myObj = new File(file1Uri.getPath());
            if (myObj.exists() && myObj.delete()) {
                Log.d("FILE 1","Deleted the file: " + myObj.getName());
            } else {
                Log.d("FILE 1","Failed to delete the file.");
            }
            Log.d("WAITING-", "wait 3");
            actProg.setVisibility(View.INVISIBLE);

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
            Log.d("WAITING-", "wait 4");

//            refreshUI();
//            try {
//                Log.d("WAITING-", "wait ");
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            Log.d("CHANGING", "panel power");
            CheckBox cb = v.findViewById(R.id.check_opt_power);
            if(cb.isChecked()){
                Log.d("CHANGING", "input data");
                EditText panelsPower = v.findViewById(R.id.array_power_input);
                Double P_power = Double.parseDouble(dbh.getOptimalById(projectId).getP_power());
                dbh.setPower(projectId, P_power);
                panelsPower.setText(P_power.toString());
            }
// ======================================================================================

//            // download file 2
//            Uri file2Uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/PV/tmp/" + currProject.getLatitude() + " " + currProject.getLongitude() + " Grid.csv");
//            Log.d("File2 path", file2Uri.getPath());
////
//            File file2download = new File(file2Uri.getPath());
//            boolean file2Exists = true;
//
//            long dwn2Id = 0;
////            boolean wasDwn = false;
//            if (!file2download.exists()) {
//                Log.d("File2 status", "does not exist, downloading");
//                Integer batterySize = 5000;
//                Double arrayPower = new Double(currProject.getPower()*1000);
//                Integer consumptionDaily = 3000;
//                Uri uri = Uri.parse("https://re.jrc.ec.europa.eu/api/v5_2/SHScalc?lat=" + currProject.getLatitude() + "&lon=" + currProject.getLongitude() + "&raddatabase=PVGIS-SARAH2&browser=1&outputformat=csv&userhorizon=&usehorizon=1&angle=" + Double.valueOf(currProject.getSlope()).intValue() + "&aspect=" + Double.valueOf(currProject.getAzimuth()).intValue() + "&peakpower=200&hourconsumptionfile=&js=1&select_database_offgrid=PVGIS-SARAH2&ipeakpower=" + arrayPower.intValue() + "&batterysize=" + batterySize + "&cutoff=40&consumptionday=" + consumptionDaily + "&shsangle=" + Double.valueOf(currProject.getSlope()).intValue() + "&shsaspect=" + Double.valueOf(currProject.getAzimuth()).intValue());
////                "https://re.jrc.ec.europa.eu/api/v5_2/SHScalc?lat=" + currProject.getLatitude() + "&lon=" + currProject.getLongitude() + "&raddatabase=PVGIS-SARAH2&browser=1&outputformat=csv&userhorizon=&usehorizon=1&angle=35&aspect=0&peakpower=200&hourconsumptionfile=&js=1&select_database_offgrid=PVGIS-SARAH2&ipeakpower=200&batterysize=600&cutoff=40&consumptionday=300&cbconsumptionfile=consumptionfile&consumptionFileInput=hourconsumption_norm%282%29.csv&shsangle=35&shsaspect=0"
////                "https://re.jrc.ec.europa.eu/api/v5_2/SHScalc?lat=" + currProject.getLatitude() + "&lon=" + currProject.getLongitude() + "&raddatabase=PVGIS-SARAH2&browser=1&outputformat=csv&userhorizon=&usehorizon=1&angle=35&aspect=0&peakpower=200&hourconsumptionfile=0.0505%2C0.035%2C0.027%2C0.025%2C0.0245%2C0.0265%2C0.0345%2C0.0295%2C0.023%2C0.018%2C0.018%2C0.0205%2C0.0265%2C0.035%2C0.0415%2C0.0505%2C0.0455%2C0.034%2C0.025%2C0.03%2C0.065%2C0.1095%2C0.1175%2C0.088&js=1&select_database_offgrid=PVGIS-SARAH2&ipeakpower=200&batterysize=600&cutoff=40&consumptionday=300&cbconsumptionfile=consumptionfile&consumptionFileInput=hourconsumption_norm%282%29.csv&shsangle=35&shsaspect=0"
////                "https://re.jrc.ec.europa.eu/api/v5_2/SHScalc?outputformat=basic&lat=47.062&lon=28.729&raddatabase=PVGIS-SARAH2&browser=0&peakpower=200&batterysize=600&cutoff=40&consumptionday=250&angle=35&aspect=0&usehorizon=1&userhorizon=&hourconsumption=0.0505,0.035,0.027,0.025,0.0245,0.0265,0.0345,0.0295,0.023,0.018,0.018,0.0205,0.0265,0.035,0.0415,0.0505,0.0455,0.034,0.025,0.03,0.065,0.1095,0.1175,0.088&js=1"
////                wasDwn = true;
//                dwn2Id = downloadFile(uri, file2Uri);
//                file2Exists = checkDownloadComplete(file2Uri, 25, dwn2Id, 1);
//            } else {
//                Log.d("File2 status", "already exists");
//            }
//
//            // upload file 2 into DB
//            if (file2Exists) {
//                Log.d("FILE 2", "uploading to DB");
//                uploadFile2Contents(file2Uri);
////                if (wasDwn)
////                    downloadManager.remove(dwn2Id);
//            } else {
//                Log.e("File 2", "NOT DOWNLOADED in ? seconds!");
//            }
            Log.d("WAITING-", "wait 5");

            // TODO FIX!
            Integer batterySize = 15000;
            Double arrayPower = new Double(currProject.getPower()*1000);
            Log.d("PROJECT POWR", currProject.getPower().toString());
            Integer aP = arrayPower.intValue();
            Log.d("PROJECT POWR", aP.toString());
            Integer consumptionDaily = 6000;

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
            actProg.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Log.d("Async task", string);
            //
        }

        public long downloadFile(Uri uri, Uri fileUri){
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setVisibleInDownloadsUi(false);
            request.setDestinationUri(fileUri);

            return downloadManager.enqueue(request);
        }

        public boolean checkDownloadComplete(Uri fileUri, int waitSeconds, long dwnId, int wait){
            while(waitSeconds>0){
                try {
                    File file1downloaded = new File(fileUri.getPath());
                    if(!file1downloaded.exists()){
                        Log.d("File status", "still not downloaded");
                        Thread.sleep(1000*wait);
                        waitSeconds--;
                    }else{
                        Log.d("File status", "finished downloading!");
                        return true;
                    }
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        }

        public void uploadFile1Contents(Uri file1Uri){
            dbh.deleteMonthly(projectId);
            try {
                InputStream in = context.getContentResolver().openInputStream(file1Uri);

                BufferedReader r = new BufferedReader(new InputStreamReader(in));

                boolean nedded_data = false;
                for (String line; (line = r.readLine()) != null; ) {
                    String []s = line.split("\t");

                    if(line.contains("slope") && line.contains("optimum")){
                        Double x = Double.parseDouble(s[s.length-1]);
                        Log.d("optimal slope:", x.toString()); //
                        dbh.setArraySlope(projectId, Double.parseDouble(s[s.length-1]));
                        continue;
                    }

                    if(line.contains("azimuth") && line.contains("optimum")){
                        Double x = Double.parseDouble(s[s.length-1]);
                        Log.d("optimal azimuth:", x.toString()); //
                        dbh.setArrayAzimuth(projectId, Double.parseDouble(s[s.length-1]));
                        continue;
                    }

                    ArrayList<String> record = new ArrayList<>();
                    boolean is_start_line = false;

                    if(s[0].equals("Month")){
                        is_start_line = true;
                    }
                    if(s[0].equals("Year")){
                        nedded_data = false;
                        break;
                    }
                    if(nedded_data) {

                        for(int i=0; i<s.length; i++){
                            String value = s[i].replaceAll("\t","");
                            if(!value.isEmpty())
                                record.add(value);
                        }
                    }
                    if(is_start_line){
                        nedded_data = true;
                    }
                    if(!record.isEmpty()){
                        Log.d("a line", record.toString());
                        dbh.addMonthlyRecord(context, projectId,
                                Integer.parseInt(record.get(0)),
                                Double.parseDouble(record.get(1)),
                                Double.parseDouble(record.get(2))
                        );
                    }
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void uploadFile2Contents(String response){
            ArrayList<MonthlyBatteryModel> mbm = new ArrayList<>();
            try {
//                Log.d("GRID", "1");
//                InputStream in = context.getContentResolver().openInputStream(file2Uri);
//                Log.d("GRID", "2");
//                BufferedReader r = new BufferedReader(new InputStreamReader(in));
//                Log.d("GRID", "3");

                String lines[] = response.split("\n");

                boolean nedded_data = false;
                int count_months = 1;
                for (int i = 0; i<lines.length;i++ ) {
                    Thread.sleep(100);
//                    Log.d("GRID", "4");
                    String []s = lines[i].split("\t");

//                    if(line.contains("slope") && line.contains("optimum")){
//                        Double x = Double.parseDouble(s[s.length-1]);
//                        Log.d("optimal slope:", x.toString()); //
//                        dbh.setArraySlope(projectId, Double.parseDouble(s[s.length-1]));
//                        continue;
//                    }
//
//                    if(line.contains("azimuth") && line.contains("optimum")){
//                        Double x = Double.parseDouble(s[s.length-1]);
//                        Log.d("optimal azimuth:", x.toString()); //
//                        dbh.setArrayAzimuth(projectId, Double.parseDouble(s[s.length-1]));
//                        continue;
//                    }
//
//                    ArrayList<String> record = new ArrayList<>();
//                    boolean is_start_line = false;
//
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
//                        dbh.addTMYRecord(context, projectId,
//                                Integer.parseInt(s[0]),
//                                Integer.parseInt(s[1])
//                        );
                    }
//                    if(!record.isEmpty()){
//                        Log.d("a line", record.toString());

//                    }
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
            dbh.applyMonthlyBattery(projectId, mbm);
        }
    }
}