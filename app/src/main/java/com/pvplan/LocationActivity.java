package com.pvplan;

import static java.lang.Math.round;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pvplan.database.DataBaseHelper;
import com.pvplan.databinding.LocationBinding;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.net.URL;
import java.util.ArrayList;

public class LocationActivity  extends AppCompatActivity {

    LocationBinding binding;
    private MapView map;
    ScaleBarOverlay mScaleBarOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        map = findViewById(R.id.osm);
        map.setTileSource(TileSourceFactory.MAPNIK); // pour le render
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.getZoomController().getDisplay().setPositions(true, CustomZoomButtonsDisplay.HorizontalPosition.LEFT, CustomZoomButtonsDisplay.VerticalPosition.BOTTOM);
        map.getZoomController().getDisplay().setMarginPadding(0.6f,0.7f);


        final DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setAlignRight(true);
        mScaleBarOverlay.setScaleBarOffset(20, 20);

        mScaleBarOverlay.drawLongitudeScale(true);//needed?
        mScaleBarOverlay.setEnableAdjustLength(true);
        map.getOverlays().add(this.mScaleBarOverlay);

        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(36.0, 15.0);
        IMapController mapController;
        mapController = map.getController();
        mapController.setZoom(4.0);
        mapController.setCenter(startPoint);

        binding.setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                IGeoPoint point = map.getMapCenter();
                String position = "Lat. " + String.valueOf(round(point.getLatitude()*10000)/10000.0) + ", Long. " + String.valueOf(round(point.getLongitude()*10000)/10000.0);
                intent.putExtra("coordinates_set", position);
                setResult(198, intent);

                Integer projectId = Integer.valueOf(getIntent().getStringExtra("ProjectID"));

                DataBaseHelper dbh = new DataBaseHelper(binding.getRoot().getContext());
                dbh.setProjectLocation(projectId, round(point.getLatitude()*10000)/10000.0, round(point.getLongitude()*10000)/10000.0);

                finish();
            }
        });

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", new GeoPoint(0.0d,0.0d))); // Lat/Lon decimal degrees

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

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }
}