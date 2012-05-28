
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
import android.text.format.DateFormat;
//import java.text.DateFormat;
import java.text.SimpleDateFormat;

import oauth.signpost.OAuth;

public class tweetPost extends Activity {
	
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
	private String twitterLoggedIn;
	private String twitterLoggedOut;
	
	
	private TextView loginStatus;
	private TextView twitterPostMsg_;
	private final Handler mTwitterHandler = new Handler();
	private Button clearCredentialsButton;
	private String twitterCompleteString;
    
    final Runnable mUpdateTwitterNotification = new Runnable() {
        public void run() {
        	Toast.makeText(getBaseContext(), twitterCompleteString, Toast.LENGTH_LONG).show();
        }
  };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweetpost);        
        loginStatus = (TextView)findViewById(R.id.twitter_login_status);
        twitterPostMsg_ = (TextView)findViewById(R.id.twitterPostMsg);        
        Button tweetPost = (Button) findViewById(R.id.twitter_btn_post);
        clearCredentialsButton = (Button) findViewById(R.id.twitter_btn_clear_credentials);
        Button tweetCancel = (Button) findViewById(R.id.twitter_cancel);
        twitterLoggedIn = getResources().getString(R.string.twitter_logged_in); //we use these to let the user know if logged into facebook currently or not
        twitterLoggedOut = getResources().getString(R.string.twitter_logged_out);
        twitterCompleteString = getResources().getString(R.string.twitterCompleteString);
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //force only portrait mode
	     //******** preferences code
	     resources = this.getResources();
	     getPreferences();
	     //***************************
	     
	    setStatus(getTweetMsg());  //pre-populate the field and then let user change the post 
        
        tweetPost.setOnClickListener(new View.OnClickListener() {
        	/**
        	 * If the user hasn't authenticated to Twitter yet, he'll be redirected via a browser
        	 * to the Twitter login page. Once the user authenticated, he'll authorize the Android application to send
        	 * tweets on the users behalf.
        	 */
            public void onClick(View v) {
            	if (TwitterUtils.isAuthenticated(prefs)) {
	        		sendTweet();
	        	} else {
					Intent i = new Intent(getApplicationContext(), PrepareRequestTokenActivity.class);
					i.putExtra("tweet_msg",twitterPostMsg_.getText().toString());
					startActivity(i);
	        	}	
            }
        });

        tweetCancel.setOnClickListener(new View.OnClickListener() {
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
	
public void updateLoginStatus() {
		
		if (TwitterUtils.isAuthenticated(prefs) == true) {			
			loginStatus.setText(twitterLoggedIn);
			clearCredentialsButton.setVisibility(View.VISIBLE); 
		}
		else {
			loginStatus.setText(twitterLoggedOut);
			clearCredentialsButton.setVisibility(View.GONE); 
		}		
	}
	

	private String getTweetMsg() {
		
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
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
	
	public void sendTweet() {
		Thread t = new Thread() {
	        public void run() {
	        	
	        	try {
	        		post = twitterPostMsg_.getText().toString(); //tweet what is in the text box
	        		TwitterUtils.sendTweet(prefs,post);
	        		mTwitterHandler.post(mUpdateTwitterNotification);	        		
				} catch (Exception ex) {
				//	Toast.makeText(getBaseContext(), "Error, Tweet NOT Send", Toast.LENGTH_LONG).show();
					ex.printStackTrace();
				}
	        }

	    };
	    t.start();
	}

	
	private void clearCredentials() { //twitter logout
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
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
				twitterPostMsg_.setText(str);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateLoginStatus();
	}
	
	
	
}