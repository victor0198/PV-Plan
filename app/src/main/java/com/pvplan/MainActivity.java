package com.pvplan;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pvplan.UIComponents.ProjectsListRecyclerViewAdapter;
import com.pvplan.database.DataBaseHelper;
import com.pvplan.database.ProjectModel;
import com.pvplan.databinding.ActivityMainBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Uri uri = Uri.parse("https://re.jrc.ec.europa.eu/api/v5_2/seriescalc?lat=47.149&lon=28.825&raddatabase=PVGIS-SARAH2&browser=1&outputformat=csv&userhorizon=&usehorizon=1&angle=0&aspect=0&startyear=2005&endyear=2005&mountingplace=free&optimalinclination=0&optimalangles=0&js=1&select_database_hourly=PVGIS-SARAH2&hstartyear=2005&hendyear=2005&trackingtype=0&hourlyangle=0&hourlyaspect=0&pvcalculation=1&pvtechchoice=crystSi&peakpower=1&loss=14&components=1");
//
//                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//
//                DownloadManager.Request request = new DownloadManager.Request(uri);
//                request.setTitle("My File");
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//                request.setVisibleInDownloadsUi(false);
//                request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/PV/myfile.csv"));
//
//                downloadmanager.enqueue(request);
                startAsyncTask(view);
            }
        });

        binding.databaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DB ->>>", "opening");
                DataBaseHelper dbh = new DataBaseHelper(binding.getRoot().getContext());
                Log.e("DB ->>>", "writing");
                dbh.addProject(binding.getRoot().getContext(), "proj1", 10);
            }
        });

        Log.e("DB ->>>", "opening");
        DataBaseHelper dbh = new DataBaseHelper(binding.getRoot().getContext());
        Log.e("DB ->>>", "reading");
        ArrayList<ProjectModel> pm = dbh.getCredentialsList();
        ProjectsListRecyclerViewAdapter adapter = new ProjectsListRecyclerViewAdapter(binding.getRoot().getContext(), pm);
        binding.projectsListRecycleView.setAdapter(adapter);
        binding.projectsListRecycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void startAsyncTask(View v){
        BarAsyncTask barTask = new BarAsyncTask(this);
        barTask.execute(100);
    }

    private static class BarAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<MainActivity> activityWeakReference;

        BarAsyncTask(MainActivity mainActivity){
            activityWeakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }
            activity.binding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for(int i=0;i<integers[0];i++){
                publishProgress(i*100/integers[0]);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            return "Finished!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }
            activity.binding.progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }
            Toast.makeText(activity, "s", Toast.LENGTH_SHORT).show();
            activity.binding.progressBar.setProgress(0);
            activity.binding.progressBar.setVisibility(View.INVISIBLE);


            Log.e("DB ->>>", "opening");
            DataBaseHelper dbh = new DataBaseHelper(activity.binding.getRoot().getContext());
            Log.e("DB ->>>", "reading");
            ArrayList<ProjectModel> pm = dbh.getCredentialsList();


            StringBuilder strBuilder = new StringBuilder();
            for (ProjectModel projectModel : pm) {
                strBuilder.append(projectModel.getName() + "," + projectModel.getPower() + "|");
            }


            String newString = strBuilder.toString();
            activity.binding.textOutUI.setText(newString);

            ProjectsListRecyclerViewAdapter adapter = new ProjectsListRecyclerViewAdapter(activity, pm);
            activity.binding.projectsListRecycleView.setAdapter(adapter);
        }


    }

}
