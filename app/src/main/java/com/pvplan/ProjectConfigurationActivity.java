package com.pvplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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
import com.pvplan.databinding.ProjectConfigurationBinding;

public class ProjectConfigurationActivity extends AppCompatActivity {

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 198;
    ProjectConfigurationBinding binding;
    Integer projectId;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProjectConfigPageAdapter projectConfigPageAdapter;

    LocationFragment locationFragment;

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
        setTitle(dbh.getProjectInfoById(projectId).getName());

        tabLayout = binding.projectTabLayout;
        viewPager2 = binding.projectConfigViewpager;
        projectConfigPageAdapter = new ProjectConfigPageAdapter(this, projectId, tabLayout, viewPager2);
        viewPager2.setAdapter(projectConfigPageAdapter);

        viewPager2.setUserInputEnabled(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



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

}
