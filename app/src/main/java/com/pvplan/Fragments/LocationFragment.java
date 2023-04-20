package com.pvplan.Fragments;

import static java.lang.Math.round;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.pvplan.MainActivity;
import com.pvplan.ProjectConfigurationActivity;
import com.pvplan.R;
import com.pvplan.UIComponents.ProjectsListRecyclerViewAdapter;
import com.pvplan.database.DataBaseHelper;
import com.pvplan.database.ProjectModel;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LocationFragment extends Fragment {
    ProjectConfigurationActivity parentActivity;
    DataBaseHelper dbh;
    private MapView map;
    ScaleBarOverlay mScaleBarOverlay;
    int projectId;
    TabLayout tabLayout;
    ViewPager2 viewPager2;

    private static final int CONSUMERS_TAB_POSITION = 1;

    public LocationFragment(ProjectConfigurationActivity parentActivity, int projectId, TabLayout tabLayout, ViewPager2 viewPager2) {
        super();
        this.parentActivity = parentActivity;
        this.projectId = projectId;
        this.tabLayout = tabLayout;
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location, container, false);

        dbh = new DataBaseHelper(v.getContext());

        Configuration.getInstance().setUserAgentValue("PV Planner");

        map = v.findViewById(R.id.osm);
        map.setDestroyMode(false);
        map.setTileSource(TileSourceFactory.MAPNIK); // pour le render
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.getZoomController().getDisplay().setPositions(true, CustomZoomButtonsDisplay.HorizontalPosition.LEFT, CustomZoomButtonsDisplay.VerticalPosition.BOTTOM);
        map.getZoomController().getDisplay().setMarginPadding(0.6f,0.7f);


        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setAlignRight(true);
        mScaleBarOverlay.setScaleBarOffset(20, 20);

        mScaleBarOverlay.drawLongitudeScale(true);//needed?
        mScaleBarOverlay.setEnableAdjustLength(true);
        map.getOverlays().add(this.mScaleBarOverlay);

        map.setMultiTouchControls(true);

        ProjectModel currProj = dbh.getProjectInfoById(projectId);
        Log.d("this project info", currProj.toString());
        GeoPoint startPoint;
        IMapController mapController;
        mapController = map.getController();
        double map_zoom = 4.0;
        if (currProj.getLatitude().equals("-1") || currProj.getLongitude().equals("-1")){
             startPoint = new GeoPoint(36.0, 15.0);
        }else{
            startPoint = new GeoPoint(Double.parseDouble(currProj.getLatitude()), Double.parseDouble(currProj.getLongitude()));
            map_zoom = 9.0;
        }
        mapController.setZoom(map_zoom);
        mapController.setCenter(startPoint);

        v.findViewById(R.id.set_location_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IGeoPoint point = map.getMapCenter();
                dbh.setProjectLocation(projectId, round(point.getLatitude()*1000)/1000.0, round(point.getLongitude()*1000)/1000.0);


                parentActivity.downloadDataAndSave(projectId);

                // move to consumers tab
                Log.d("Tabs switching", "moving from location to consumers");
                viewPager2.setCurrentItem(CONSUMERS_TAB_POSITION);
                tabLayout.getTabAt(CONSUMERS_TAB_POSITION).select();
            }
        });

//        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
//        items.add(new OverlayItem("Title", "Description", new GeoPoint(0.0d,0.0d))); // Lat/Lon decimal degrees

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
        Log.d("Loc frag", "paused");
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        Log.d("Loc frag", "resumed");
    }
}