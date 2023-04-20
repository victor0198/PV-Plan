package com.pvplan;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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



//        makeTabWider(tabLayout, 0, 1.3f);
//        makeTabWider(tabLayout, 1, 1.7f);
//        makeTabWider(tabLayout, 2, 1.2f);
//        makeTabWider(tabLayout, 3, 1.3f);
//        makeTabWider(tabLayout, 4, 1.2f);


        // use the text in a TextView
//        TextView textView = (TextView) findViewById(R.id.id_p);
//        textView.setText("id:" + projectId.toString());

//        binding.locationLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startLocationSelector();
//            }
//        });
//
//        binding.locationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startLocationSelector();
//            }
//        });
//
//        binding.consumersBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), ConsumersActivity.class);
//                launcher.launch(intent);
//            }
//        });
//
//
//        binding.resultsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
//                intent.putExtra("ProjectID", projectId.toString());
//                launcher.launch(intent);
//            }
//        });

    }

    private void makeTabWider(TabLayout tabLayout, int position, Float weight){
        LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(position));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
        Log.d("weight",Float.valueOf(layoutParams.weight).toString());
        layoutParams.weight = weight;
        layout.setLayoutParams(layoutParams);
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

//    private void startLocationSelector(){
//        Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
//        intent.putExtra("ProjectID", projectId.toString());
//        launcher.launch(intent);
//    }
//    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if(result.getResultCode() == SECOND_ACTIVITY_REQUEST_CODE) {
//                        Intent intent = result.getData();
//                        if (intent!=null) {
//                            Log.d("Project activity :", "received result from activity");
//                        }
//                    }
//                }
//            }
//    );


    public void downloadDataAndSave(int projectId){
        DataBaseHelper dbh = new DataBaseHelper(this.getApplicationContext());

        uploadBriefData projectsListTask = new uploadBriefData(getApplicationContext(), dbh, projectId);
        projectsListTask.execute();
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
            Log.d("Async task", "Started");


            ProjectModel currProject = dbh.getProjectInfoById(projectId);
            Uri file1Uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/PV/"+currProject.getLatitude()+" "+currProject.getLatitude()+".csv");

            File file1download = new File(file1Uri.getPath());
            Log.d("File1 path",file1Uri.getPath());

            boolean file1Exists = false;

            if(!file1download.exists()){
                Log.d("File1 status", "does not exist, downloading");

                Uri uri = Uri.parse("https://re.jrc.ec.europa.eu/api/v5_2/PVcalc?lat="+currProject.getLatitude()+"&lon="+currProject.getLongitude()+"&raddatabase=PVGIS-SARAH2&browser=1&userhorizon=&usehorizon=1&outputformat=csv&js=1&select_database_grid=PVGIS-SARAH2&pvtechchoice=crystSi&peakpower=1&loss=14&mountingplace=free&optimalangles=1");

                DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationUri(file1Uri);

                downloadManager.enqueue(request);

                int trials = 15;
                while(trials>0){
                    try {
                        File file1downloaded = new File(file1Uri.getPath());
                        if(!file1downloaded.exists()){
                            Log.d("File1 status", "still not downloaded");
                            Thread.sleep(1000);
                            trials--;
                        }else{
                            Log.d("File1 status", "finished downloading!");
                            file1Exists = true;
                            break;
                        }
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }else{
                file1Exists = true;
                Log.d("File1 status", "already exists");
            }

            if(file1Exists){
                Log.d("FILE 1", "uploading to DB");
                uploadFile1Contents(file1Uri);

            }else{
                Log.e("File 1", "NOT DOWNLOADED in ? seconds!");
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

        public void uploadFile1Contents(Uri file1Uri){
            try {
                InputStream in = context.getContentResolver().openInputStream(file1Uri);

                BufferedReader r = new BufferedReader(new InputStreamReader(in));

                boolean nedded_data = false;
                for (String line; (line = r.readLine()) != null; ) {
                    String []s = line.split("\t");

                    if(line.contains("slope") && line.contains("optimum")){
                        Log.d("optimal slope:",s[s.length-1]); //

                        continue;
                    }

                    if(line.contains("azimuth") && line.contains("optimum")){
                        Log.d("optimal azimuth:",s[s.length-1]); //

                        continue;
                    }

                    ArrayList<String> record = new ArrayList<>();
                    boolean is_start_line = false;
                    for(int i=0; i<s.length; i++){
                        if(s[i].equals("Month")){
                            is_start_line = true;
                            i++;
                        }
                        if(s[i].equals("Year")){
                            nedded_data = false;
                            break;
                        }
                        if(nedded_data){
                            String value = s[i].replaceAll("\t","");
                            if(!value.equals(""))
                                record.add(value); //
                        }
                    }
                    if(is_start_line){
                        nedded_data = true;
                    }
                    if(!record.isEmpty())
                        Log.d("a line", record.toString());
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
