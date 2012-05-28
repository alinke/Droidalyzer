
package talkingbreathalyzer.main.source;

import android.app.Activity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.facebook.android.Facebook;
import com.facebook.android.FacebookConnector;
import com.facebook.android.SessionEvents;
import android.content.res.Resources;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.telephony.TelephonyManager;

public class facebookPost extends Activity {

	private static final String FACEBOOK_APPID = "305557519478133";
	private static final String[] FACEBOOK_PERMISSIONS = { "offline_access", "publish_stream", "user_photos", "publish_checkins"};
	private static final String TAG = "FacebookPost";
	//private String faceBookPostMsg = "something went wrong, could not get alcohol detection results";
	
	private Resources resources;
	
	private SharedPreferences prefs;
	private SharedPreferences resultPrefs;
	private String tweet_no_drinks;
	private String tweet_few_drinks;
	private String tweet_buzzed;	
	private String tweet_drunk;
	private String tweet_temp;	
	private String post;
	private boolean send_tweets_gps;
	private int result;
	private int level;
	private String fbLoggedIn;
	private String fbLoggedOut;
	
	
	private final Handler mFacebookHandler = new Handler();
	private TextView loginStatus;
	private TextView facebookPostMsg_;
	private FacebookConnector facebookConnector;
	private Button clearCredentialsButton;
	private String fbCompleteString;
	
	
    final Runnable mUpdateFacebookNotification = new Runnable() {
        public void run() {
        	Toast.makeText(getBaseContext(), fbCompleteString, Toast.LENGTH_LONG).show();
        }
    };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebookpost);        
        loginStatus = (TextView)findViewById(R.id.fb_login_status);
        facebookPostMsg_ = (TextView)findViewById(R.id.facebookPostMsg);        
        Button fbPost = (Button) findViewById(R.id.fb_btn_post);
        clearCredentialsButton = (Button) findViewById(R.id.fb_btn_clear_credentials);
        Button fbCancel = (Button) findViewById(R.id.fb_cancel);
        fbLoggedIn = getResources().getString(R.string.fb_logged_in); //we use these to let the user know if logged into facebook currently or not
        fbLoggedOut = getResources().getString(R.string.fb_logged_out);
        fbCompleteString = getResources().getString(R.string.fbCompleteString);
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //force only portrait mode
	     //******** preferences code
	     resources = this.getResources();
	     getPreferences();
	     //***************************
	     
	     setStatus(populateFacebookMsg());  //pre-populate the field and then let user change the post 
        
        this.facebookConnector = new FacebookConnector(FACEBOOK_APPID, this, getApplicationContext(), FACEBOOK_PERMISSIONS);
        
        clearCredentialsButton.setVisibility(View.GONE); //we no longer need this since we've got the facebook login on the accounts activity
        
        fbPost.setOnClickListener(new View.OnClickListener() {
        	/**
        	 * If the user hasn't authenticated to Facebook yet, he'll be redirected via a browser
        	 * to the Facebook login page. Once the user authenticated, he'll authorize the Android application to send
        	 * tweets on the users behalf.
        	 */
            public void onClick(View v) {
        		postMessage();
            }
        });

        fbCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        clearCredentialsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	clearCredentials();
            	updateLoginStatus();
            }
        });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.facebookConnector.getFacebook().authorizeCallback(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		updateLoginStatus();
	}
	
	public void updateLoginStatus() {
		if (facebookConnector.getFacebook().isSessionValid() == true) {
			loginStatus.setText(fbLoggedIn);
			//clearCredentialsButton.setVisibility(View.VISIBLE); 
			clearCredentialsButton.setVisibility(View.GONE); //no longer need this button
		}
		
		else {
			loginStatus.setText(fbLoggedOut);
			clearCredentialsButton.setVisibility(View.GONE); 
		}
	}
	

	private String populateFacebookMsg() {
		
		//here we need to facebook post the field as the user can add stuff if they want
		
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
		String dateString = formatter.format(new Date());
		
		if (result == 0){			
			tweet_temp = "Alcohol Detector Result: " + tweet_no_drinks + " at " + dateString; 	
		}
		
		if (result == 1) {
			tweet_temp = "Alcohol Detector Result: " + tweet_few_drinks + " at " + dateString;		
		}
		
		if (result == 2) {
			tweet_temp = "Alcohol Detector Result: " + tweet_buzzed + " at " + dateString;
		}
		
		if (result == 3) {
			tweet_temp = "Alcohol Detector Result: " + tweet_drunk + " at " + dateString;
		}
		
		if (result == 20) {
			tweet_temp = "Oops, there has been a problem, the alcohol detection results were not passed to the Facebook post code";
		}
		return tweet_temp;
		
	}	
	
	public void postMessage() {
		
		if (facebookConnector.getFacebook().isSessionValid()) {
			postMessageInThread();
		} else {
			SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {
				
				@Override
				public void onAuthSucceed() {
					postMessageInThread();
				}
				
				@Override
				public void onAuthFail(String error) {
					
				}
			};
			SessionEvents.addAuthListener(listener);
			facebookConnector.login();
		}
	}

	private void postMessageInThread() {
		Thread t = new Thread() {
			public void run() {
		    	
		    	try {
		    		String test = tweet_drunk;
		    		post = facebookPostMsg_.getText().toString();
		    		facebookConnector.postMessageOnWall(post);
					mFacebookHandler.post(mUpdateFacebookNotification);
				} catch (Exception ex) {
					Log.e(TAG, "Error sending msg",ex);
				}
		    }
		};
		t.start();
	}

	private void clearCredentials() {
		try {
			facebookConnector.getFacebook().logout(getApplicationContext());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void getPreferences() //here is where we read the shared preferences into variables
    
	  {
	     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

	     tweet_no_drinks = prefs.getString( 
	  	        resources.getString(R.string.pref_tweet_no_drinks), 	        
	  	        resources.getString(R.string.tweet_no_drinks)); 
	      
	      tweet_few_drinks = prefs.getString(   
	   	        resources.getString(R.string.pref_tweet_few_drinks),
	   	        resources.getString(R.string.tweet_few_drinks));  
	      
	      tweet_buzzed = prefs.getString(   
	    	        resources.getString(R.string.pref_tweet_buzzed),
	    	        resources.getString(R.string.tweet_buzzed));  
	      
	      tweet_drunk = prefs.getString(   
	     	        resources.getString(R.string.pref_tweet_drunk),
	     	        resources.getString(R.string.tweet_drunk));    
	      
	      send_tweets_gps = prefs.getBoolean("pref_tweet_gps", false); 
	      
	      //***** let's get here the last breathalyzer results ********
	      resultPrefs = getSharedPreferences("resultsData",MODE_PRIVATE); 		
		  result = Integer.valueOf(resultPrefs.getString("pref_result", null));
		  level  = Integer.valueOf(resultPrefs.getString("pref_level", null));
		 //*************************************************************  
	      
	    } 
	
	private void setStatus(final String str) {
		runOnUiThread(new Runnable() {
			public void run() {
				facebookPostMsg_.setText(str);
			}
		});
	}
	
	
	
}