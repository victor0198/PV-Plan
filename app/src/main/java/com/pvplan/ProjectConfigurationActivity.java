package com.pvplan;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.pvplan.Fragments.LocationFragment;
import com.pvplan.UIComponents.ProjectConfigPageAdapter;
import com.pvplan.database.DataBaseHelper;
import com.pvplan.database.ProjectModel;
import com.pvplan.databinding.ProjectConfigurationBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProjectConfigurationActivity extends AppCompatActivity {

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 198;
    ProjectConfigurationBinding binding;
    Integer projectId;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProjectConfigPageAdapter projectConfigPageAdapter;
    static DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProjectConfigurationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        String recId = intent.getStringExtra(Intent.EXTRA_INDEX);
        projectId = Integer.valueOf(recId);

        DataBaseHelper dbh = new DataBaseHelper(binding.getRoot().getContext());
        ProjectModel current_pm = dbh.getProjectInfoById(projectId);
        setTitle(current_pm.getName());

        tabLayout = binding.projectTabLayout;
        viewPager2 = binding.projectConfigViewpager;
        projectConfigPageAdapter = new ProjectConfigPageAdapter(this,this, projectId, tabLayout, viewPager2);
        viewPager2.setAdapter(projectConfigPageAdapter);

        viewPager2.setUserInputEnabled(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("fragment position clocked", Integer.toString(tab.getPosition()));
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        tabLayout.getChildAt(0).setVisibility(View.INVISIBLE);

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        disableTab(tabLayout, 1);
        disableTab(tabLayout, 2);
        disableTab(tabLayout, 3);
        disableTab(tabLayout, 4);

    }

    private void disableTab(TabLayout tabLayout, int position){
         ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(position).setEnabled(false);
         ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(position).setBackgroundColor(getResources().getColor(R.color.light_gray));
    }

    public void enableTab(TabLayout tabLayout, int position){
        ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(position).setEnabled(true);
        ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(position).setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void downloadDataAndSave(int projectId){

        DataBaseHelper dbh = new DataBaseHelper(this.getApplicationContext());

        uploadBriefData projectsListTask = new uploadBriefData(getApplicationContext(), dbh, projectId);
        projectsListTask.execute(2);
    }

    public void computeStorage() {
        DataBaseHelper dbh = new DataBaseHelper(this.getApplicationContext());
        Double lpd = dbh.getLowestPerfD(projectId);
        ProjectModel tmpP = dbh.getProjectInfoById(projectId);
        Double dischargeFactor = 1.66d;
        Double storage = lpd * tmpP.getPower() / 0.7744d * dischargeFactor;
        Log.d("OPTIMAL Storage", storage.toString());
        dbh.updateOptimalStorage(projectId, Double.valueOf(storage*100).intValue()/100d);
        dbh.setBattery(projectId, Double.valueOf(storage*100).intValue()/100d);
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
            // ...
            Log.d("Async task", "Started. " + integers[0]);
            ProjectModel currProject = dbh.getProjectInfoById(projectId);

            if(integers[0]==1 || (integers[0]==2 && !dbh.monthlyIsAvailable(projectId))) {
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
                    file1Exists = checkDownloadComplete(file1Uri, 15, dwn1Id);
                } else {
                    Log.d("File1 status", "already exists");
                }

                // upload file 1 into DB
                if (file1Exists) {
                    Log.d("FILE 1", "uploading to DB");
                    uploadFile1Contents(file1Uri);
                } else {
                    Log.e("File 1", "NOT DOWNLOADED in ? seconds!");
                }

                File myObj = new File(file1Uri.getPath());
                if (myObj.exists() && myObj.delete()) {
                    Log.d("FILE 1","Deleted the file: " + myObj.getName());
                } else {
                    Log.d("FILE 1","Failed to delete the file.");
                }
            }

            if(integers[0]==2 && !dbh.tmyIsAvailable(projectId)) {
                // download file 2
                Uri file2Uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/PV/tmp/" + currProject.getLatitude() + " " + currProject.getLongitude() + " TMY.csv");
                Log.d("File2 path", file2Uri.getPath());

                File file2download = new File(file2Uri.getPath());
                boolean file2Exists = true;

                long dwn2Id = 0;
                boolean wasDwn = false;
                if (!file2download.exists()) {
                    Log.d("File2 status", "does not exist, downloading");
                    Uri uri = Uri.parse("https://re.jrc.ec.europa.eu/api/v5_2/tmy?lat=" + currProject.getLatitude() + "&lon=" + currProject.getLongitude() + "&usehorizon=1&browser=1&outputformat=csv&startyear=2005&endyear=2020&userhorizon=&js=1&period=1");
                    wasDwn = true;
                    dwn2Id = downloadFile(uri, file2Uri);
                    file2Exists = checkDownloadComplete(file2Uri, 25, dwn2Id);
                } else {
                    Log.d("File2 status", "already exists");
                }

                // upload file 2 into DB
                if (file2Exists) {
                    Log.d("FILE 2", "uploading to DB");
                    uploadFile2Contents(file2Uri);
                    if (wasDwn)
                        downloadManager.remove(dwn2Id);
                } else {
                    Log.e("File 2", "NOT DOWNLOADED in ? seconds!");
                }
            }

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

        public boolean checkDownloadComplete(Uri fileUri, int waitSeconds, long dwnId){
            while(waitSeconds>0){
                try {
                    File file1downloaded = new File(fileUri.getPath());
                    if(!file1downloaded.exists()){
                        Log.d("File status", "still not downloaded");
                        Thread.sleep(1000);
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
            ProjectModel updPM = dbh.getProjectInfoById(projectId);
            dbh.addOptimalAngles(projectId, Double.parseDouble(updPM.getSlope()), Double.parseDouble(updPM.getAzimuth()));
        }

        public void uploadFile2Contents(Uri file2Uri){
            dbh.deleteTMY(projectId);
            try {
                InputStream in = context.getContentResolver().openInputStream(file2Uri);

                BufferedReader r = new BufferedReader(new InputStreamReader(in));

                boolean nedded_data = false;
                int read_lines = 20;
                for (String line; (line = r.readLine()) != null && read_lines>0; ) {
                    read_lines--;
                    String []s = line.split(",");

                    if(s[0].equals("month")){
                        nedded_data = true;
                        continue;
                    }
                    if(s[0].contains("time")){
                        nedded_data = false;
                        break;
                    }
                    if(nedded_data) {

                        dbh.addTMYRecord(context, projectId,
                                Integer.parseInt(s[0]),
                                Integer.parseInt(s[1])
                        );
                        Log.d("tmy file data:", s[0]+":"+s[1]);
                    }
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Log.d("download", "complete");
        }
    };

}
