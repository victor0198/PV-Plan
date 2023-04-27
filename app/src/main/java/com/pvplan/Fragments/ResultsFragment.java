package com.pvplan.Fragments;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pvplan.R;
import com.pvplan.database.DataBaseHelper;
import com.pvplan.database.ProjectModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ResultsFragment extends Fragment {
    DataBaseHelper dbh;
    int projectId;

    public ResultsFragment(int projectId) {
        super();
        this.projectId = projectId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_results, container, false);

        dbh = new DataBaseHelper(fragmentView.getContext());
        ProjectModel currProject = dbh.getProjectInfoById(projectId);
        Log.d("Res",currProject.toString());



//        String line = "";
//        String splitBy = ",";
//        try
//        {
//            //parsing a CSV file into BufferedReader class constructor
//            BufferedReader br = new BufferedReader(new FileReader("CSVDemo.csv"));
//            while ((line = br.readLine()) != null)   //returns a Boolean value
//            {
//                String[] employee = line.split(splitBy);    // use comma as separator
//                System.out.println("Employee [First Name=" + employee[0] + ", Last Name=" + employee[1] + ", Designation=" + employee[2] + ", Contact=" + employee[3] + ", Salary= " + employee[4] + ", City= " + employee[5] +"]");
//            }
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        return fragmentView;
    }
}