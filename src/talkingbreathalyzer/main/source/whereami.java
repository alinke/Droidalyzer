package talkingbreathalyzer.main.source;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.os.Bundle;

public class whereami extends MapActivity {
    
    MapView mapView = null;
    MapController mapController = null;
    MyLocationOverlay whereAmI = null;

    @Override
    protected boolean isLocationDisplayed() {
        return whereAmI.isMyLocationEnabled();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whereami);

        mapView = (MapView)findViewById(R.id.geoMap);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        mapController.setZoom(15);

        whereAmI = new MyCustomLocationOverlay(this, mapView);
		mapView.getOverlays().add(whereAmI);
		mapView.postInvalidate();
    }

    @Override
    public void onResume()
    {
    	super.onResume();
        whereAmI.enableMyLocation();
		whereAmI.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.setCenter(whereAmI.getMyLocation());
            }
        });
    }

    @Override
    public void onPause()
    {
    	super.onPause();
        whereAmI.disableMyLocation();
    }
}