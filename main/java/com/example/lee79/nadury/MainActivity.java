package com.example.lee79.nadury;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    EditText searchEdt;
    Button searchBtn;
    TMapData tmapdata;
    String strData = null;
    private TMapGpsManager tMapGps = null;
    private boolean m_bTrackingMode = true;
    private TMapView tMapView = null;
    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();
    private static int mMarkerID;

    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBtn = findViewById(R.id.searchBtn);
        searchEdt = findViewById(R.id.searchEdt);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("d72d4fac-59a7-4465-87e5-abadd92188f9");
        linearLayoutTmap.addView(tMapView);

        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(15);
        tMapGps = new TMapGpsManager(MainActivity.this);
        tMapGps.setMinTime(1000);
        tMapGps.setMinDistance(5);
        //tMapGps.setProvider(tMapGps.NETWORK_PROVIDER);
        tMapGps.setProvider(tMapGps.GPS_PROVIDER);
        tMapGps.OpenGps();


        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        tmapdata = new TMapData();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strData = searchEdt.getText().toString();
                addPoint();
            }
        });
        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                Toast.makeText(MainActivity.this, "클릭", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addPoint() {
        tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapMarkerItem markerItem1 = new TMapMarkerItem();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mapmarker);
                    bitmap = bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 90, bitmap.getHeight() / 90, true);
                    markerItem1.setPosition(0.5f, 1.0f);

                    TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
                    TMapPoint point = item.getPOIPoint();

                    m_mapPoint.add(new MapPoint(item.getPOIName(),item.getPOIPoint().getLatitude(),item.getPOIPoint().getLongitude()));

                    markerItem1.setTMapPoint(point);
                    markerItem1.setCanShowCallout(true);
                    markerItem1.setCalloutTitle(m_mapPoint.get(i).getName());
                    markerItem1.setAutoCalloutVisible(true);

                    Bitmap bitmap_i = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.hue);
                    bitmap_i = bitmap_i.createScaledBitmap(bitmap_i, bitmap_i.getWidth() / 90, bitmap_i.getHeight() / 90, true);
                    markerItem1.setCalloutRightButtonImage(bitmap_i);
                    String strID = String.format("pmarker%d",mMarkerID++);

                    tMapView.addMarkerItem(strID, markerItem1);
                    mArrayMarkerID.add(strID);
                    tMapView.setCenterPoint(poiItem.get(1).getPOIPoint().getLongitude(), poiItem.get(1).getPOIPoint().getLatitude());
                    Log.d("TAG",poiItem.get(1).upperAddrName);
                }
            }
        });
    }
}
