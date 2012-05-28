
package talkingbreathalyzer.main.source;

import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;


public class getTaxi extends Activity implements OnClickListener {
	
	private Button getTaxiButton_;	
	private Button taxiCancelButton_;
	private TelephonyManager teleMgr = null;
	
	private TextView taxiSMSMessage_;	
	private SharedPreferences prefs;
	private Resources resources;
	
	private String taxi_sms_number;
	private boolean call_taxi;
	private String smsBody;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.taxi_sms);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //force only portrait mode
	     //******** preferences code
	        resources = this.getResources();
	       setPreferences();
	     //***************************
		
	  //  teleMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    
	    taxiSMSMessage_ = (TextView)findViewById(R.id.taxiSMSMessage);
	    getTaxiButton_ = (Button) findViewById(R.id.getTaxiButton);
	    getTaxiButton_.setOnClickListener(this); 
	    taxiCancelButton_ = (Button) findViewById(R.id.taxiCancelButton);
	    taxiCancelButton_.setOnClickListener(this); 	    
		
	}	

	//Called when the LED button is clicked
	public void onClick(View v) {
		
		SmsManager sms = SmsManager.getDefault();
		smsBody = taxiSMSMessage_.getText().toString();
		
		if (v.getId() == R.id.getTaxiButton) {
			sms.sendTextMessage(taxi_sms_number, null, smsBody, null, null);
			Toast.makeText(getTaxi.this, "Message sent, you will receive an SMS confirmation shortly...", Toast.LENGTH_LONG).show();
		}
		
		if (v.getId() == R.id.taxiCancelButton) {
			finish();
		}
		
		
	}
	 
	  
	  private void setPreferences() //here is where we read the shared preferences into variables
	    
	  {
	     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	       
	     taxi_sms_number = prefs.getString( 
	  	        resources.getString(R.string.pref_taxi_sms), 	        
	  	        resources.getString(R.string.taxi_sms_default)); 
	    } 
}
