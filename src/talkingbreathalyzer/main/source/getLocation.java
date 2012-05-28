package talkingbreathalyzer.main.source;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;


public class getLocation extends MapActivity {
LocationManager mLocationManager;
Location mLocation;
TextView myLocationText_;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.location);
	MapView mapView = (MapView) findViewById(R.id.map1);
	myLocationText_ = (TextView) findViewById(R.id.myLocationText);
	mLocationManager = (LocationManager)
	getSystemService(Context.LOCATION_SERVICE);
	Criteria criteria = new Criteria();
	criteria.setAccuracy(Criteria.ACCURACY_FINE);
	criteria.setPowerRequirement(Criteria.POWER_LOW);
	String locationprovider =
	mLocationManager.getBestProvider(criteria,true);
	mLocation =
	mLocationManager.getLastKnownLocation(locationprovider);
	//tv.setText("Last location lat:" + mLocation.getLatitude()
	//+ " long:" + mLocation.getLongitude());
	
	List<Address> addresses; //here we will convert the lat, long coordinates to a real address
	try {
		Geocoder mGC = new Geocoder(this, Locale.ENGLISH);
		addresses = mGC.getFromLocation(mLocation.getLatitude(),
		mLocation.getLongitude(), 1);
		if(addresses != null) {
		Address currentAddr = addresses.get(0);
		StringBuilder mSB = new StringBuilder("Your Approximate Location:\n");
		for(int i=0; i<currentAddr.getMaxAddressLineIndex(); i++) {
		mSB.append(currentAddr.getAddressLine(i)).append("\n");
	}
		myLocationText_.setText(mSB.toString());
	}
	} catch(IOException e) {
		myLocationText_.setText(e.getMessage());
	}
}
	

	protected boolean isRouteDisplayed() {
	// this method is required
	return false;
	}
}
