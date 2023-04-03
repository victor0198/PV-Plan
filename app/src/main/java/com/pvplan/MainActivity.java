package com.pvplan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.widget.Toast;

import com.pvplan.UIComponents.ProjectNameDialog;
import com.pvplan.UIComponents.ProjectsListRecyclerViewAdapter;
import com.pvplan.database.DataBaseHelper;
import com.pvplan.database.ProjectModel;
import com.pvplan.databinding.MainBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProjectNameDialog.ProjectNameDialogListener {

    MainBinding binding;
    ArrayList<ProjectModel> pm;
    ProjectsListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("PV Plan");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.pv_icon_50px);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readProjects();

        binding.addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

//                TODO: download the file containing the TMY data, on compute Button click
//                Uri uri = Uri.parse("https://re.jrc.ec.europa.eu/api/v5_2/seriescalc?lat=47.149&lon=28.825&raddatabase=PVGIS-SARAH2&browser=1&outputformat=csv&userhorizon=&usehorizon=1&angle=0&aspect=0&startyear=2005&endyear=2005&mountingplace=free&optimalinclination=0&optimalangles=0&js=1&select_database_hourly=PVGIS-SARAH2&hstartyear=2005&hendyear=2005&trackingtype=0&hourlyangle=0&hourlyaspect=0&pvcalculation=1&pvtechchoice=crystSi&peakpower=1&loss=14&components=1");
//
//                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//
//                DownloadManager.Request request = new DownloadManager.Request(uri);
//                request.setTitle("My File");
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//                request.setVisibleInDownloadsUi(false);
//                request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/PV/myfile.csv"));
//
//                downloadManager.enqueue(request);

        adapter = new ProjectsListRecyclerViewAdapter(this, pm, this);
        binding.projectsListRecycleView.setAdapter(adapter);
        binding.projectsListRecycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openSettings(){
        // Create the Intent object of this class Context() to Second_activity class
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        // start the Intent
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                openSettings();
                break;
            default: return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void readProjects(){
        DataBaseHelper dbh = new DataBaseHelper(binding.getRoot().getContext());
        pm = dbh.getProjectsList();
    }

    // TODO: Make electricity production predictions on remote thread
    // Example of AsyncTask with progress bar
//    public void startAsyncTask(){
//        BarAsyncTask barTask = new BarAsyncTask(this);
//        barTask.execute(0);
//    }
//
//    private static class BarAsyncTask extends AsyncTask<Integer, Integer, String> {
//        private WeakReference<MainActivity> activityWeakReference;
//
//        BarAsyncTask(MainActivity mainActivity){
//            activityWeakReference = new WeakReference<MainActivity>(mainActivity);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            MainActivity activity = activityWeakReference.get();
//            if (activity == null || activity.isFinishing()){
//                return;
//            }
//            activity.binding.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String doInBackground(Integer... integers) {
//            for(int i=0;i<integers[0];i++){
//                publishProgress(i*100/integers[0]);
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            return "Finished!";
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//
//            MainActivity activity = activityWeakReference.get();
//            if (activity == null || activity.isFinishing()){
//                return;
//            }
//            activity.binding.progressBar.setProgress(values[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//
//            MainActivity activity = activityWeakReference.get();
//            if (activity == null || activity.isFinishing()){
//                return;
//            }
//            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
//            activity.binding.progressBar.setProgress(0);
//            activity.binding.progressBar.setVisibility(View.INVISIBLE);
//
//            ProjectsListRecyclerViewAdapter adapter = new ProjectsListRecyclerViewAdapter(activity, activity.getProjects());
//            activity.binding.projectsListRecycleView.setAdapter(adapter);
//        }
//
//
//    }

    public void openDialog(){
        ProjectNameDialog projectNameDialog = new ProjectNameDialog();
        projectNameDialog.show(getSupportFragmentManager(), "project name dialog");
    }

    @Override
    public void applyText(String projectName) {
        DataBaseHelper dbh = new DataBaseHelper(binding.getRoot().getContext());
        dbh.addProject(binding.getRoot().getContext(), projectName, 0);

        resetRecycler();
    }

    public void resetRecycler(){
        LoadProjects projectsListTask = new LoadProjects(this);
        projectsListTask.execute();
    }

    private static class LoadProjects extends AsyncTask<Integer, Integer, String> {
        private WeakReference<MainActivity> activityWeakReference;

        LoadProjects(MainActivity mainActivity){
            activityWeakReference = new WeakReference<>(mainActivity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // ...
        }

        @Override
        protected String doInBackground(Integer... integers) {
            // ...
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

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }

            activity.readProjects();
            activity.binding.projectsListRecycleView.setAdapter(new ProjectsListRecyclerViewAdapter(activity, activity.pm, activity));
            activity.binding.projectsListRecycleView.setLayoutManager(new LinearLayoutManager(activity));
        }
    }

    public void runProjectLauncher(Intent intent) {
        projectLauncher.launch(intent);
    }

    public ActivityResultLauncher<Intent> projectLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    resetRecycler();
                }
            }
    );

}
