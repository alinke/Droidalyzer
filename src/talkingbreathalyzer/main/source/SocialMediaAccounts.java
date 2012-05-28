package talkingbreathalyzer.main.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import oauth.signpost.OAuth;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookConnector;
import com.facebook.android.SessionEvents;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;

public class SocialMediaAccounts extends Activity {

	private SharedPreferences prefs;
	private final Handler mTwitterHandler = new Handler();	
	private TextView twitterLoginStatus;
	private Button twitterClearCredentialsButton;
    private String twitterLoggedIn;
    private String twitterLoggedOut;
    private String twitterCompleteString;   
    private Button facebookLogoutButton_ ;
    private Button tweetPost;
    
    private TextView fbloginStatus;
    private Button fbPost;
    private Button fbclearCredentialsButton;
    private String fbLoggedIn; 
    private String fbLoggedOut; 
    private String fbCompleteString; 
    private final Handler mFacebookHandler = new Handler();
    private FacebookConnector facebookConnector;
	
    private static final String FACEBOOK_APPID = "305557519478133";
	private static final String[] FACEBOOK_PERMISSIONS = { "offline_access", "publish_stream", "user_photos", "publish_checkins"};
	
	private static final String TAG = "SocialMediaAccounts";
	
    final Runnable mUpdateTwitterNotification = new Runnable() {
        public void run() {
        	Toast.makeText(getBaseContext(), twitterCompleteString, Toast.LENGTH_LONG).show();
        }
    };
    
    final Runnable mUpdateFacebookNotification = new Runnable() {
        public void run() {
        	Toast.makeText(getBaseContext(), fbCompleteString, Toast.LENGTH_LONG).show();
        }
    };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socialaccounts);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //force only portrait mode
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ///*************Twitter ************************************************
        twitterLoginStatus = (TextView)findViewById(R.id.twitter_login_status); 
        tweetPost = (Button) findViewById(R.id.twitter_btn_post);  
        twitterClearCredentialsButton = (Button) findViewById(R.id.twitter_btn_clear_credentials);
        twitterLoggedIn = getResources().getString(R.string.twitter_logged_in); //we use these to let the user know if logged into facebook currently or not
        twitterLoggedOut = getResources().getString(R.string.twitter_logged_out2);
        twitterCompleteString = getResources().getString(R.string.twitterCompleteString);
        //************************************************************************
        
        //*************Facebook **************************************************
        fbloginStatus = (TextView)findViewById(R.id.fb_login_status);  
        fbPost = (Button) findViewById(R.id.fb_btn_post);
        fbclearCredentialsButton = (Button) findViewById(R.id.facebookLogoutButton);
        fbLoggedIn = getResources().getString(R.string.fb_logged_in); //we use these to let the user know if logged into facebook currently or not
        fbLoggedOut = getResources().getString(R.string.fb_logged_out2);
        fbCompleteString = getResources().getString(R.string.fbCompleteString);
        //*************************************************************************
        
            
        tweetPost.setOnClickListener(new View.OnClickListener() {
        	/**
        	 * Send a tweet. If the user hasn't authenticated to Tweeter yet, he'll be redirected via a browser
        	 * to the twitter login page. Once the user authenticated, he'll authorize the Android application to send
        	 * tweets on the users behalf.
        	 */
            public void onClick(View v) {
            	if (TwitterUtils.isAuthenticated(prefs)) {
            		//sendTweet();
            	} else {
    				Intent i = new Intent(getApplicationContext(), PrepareRequestTokenActivity.class);
    				//i.putExtra("tweet_msg",getTweetMsg());
    				startActivity(i);
            	}
            	
            }
        });       
        
        twitterClearCredentialsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	twitterclearCredentials();
            	twitterupdateLoginStatus();
            }
        });
      
        
        this.facebookConnector = new FacebookConnector(FACEBOOK_APPID, this, getApplicationContext(), FACEBOOK_PERMISSIONS);
        
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
        
        fbclearCredentialsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	fbclearCredentials();
            	fbupdateLoginStatus();
            }
        });
        
        
        twitterupdateLoginStatus();
		fbupdateLoginStatus();  
       
	} //end function
	
	@Override
	protected void onResume() {
		super.onResume();
		twitterupdateLoginStatus();
		fbupdateLoginStatus();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.facebookConnector.getFacebook().authorizeCallback(requestCode, resultCode, data);
	}
	
	private String getTweetMsg() {
		return "Test Tweet from Alcohol Detector at " + new Date().toLocaleString();
	}	
	
	public void sendTweet() {
		Thread t = new Thread() {
	        public void run() {
	        	
	        	try {
	        		//TwitterUtils.sendTweet(prefs,getTweetMsg());
	        		//mTwitterHandler.post(mUpdateTwitterNotification);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
	        }

	    };
	    t.start();
	}

	private void twitterclearCredentials() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
	}
	
	private void fbclearCredentials() {
		try {
			facebookConnector.getFacebook().logout(getApplicationContext());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void postMessage() {
		
		if (facebookConnector.getFacebook().isSessionValid()) {
			//postMessageInThread();
			fbupdateLoginStatus();
		} else {
			SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {
				
				@Override
				public void onAuthSucceed() {
					//postMessageInThread();
					fbupdateLoginStatus();
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
		    		String post = "Test Post from Alcohol Detector at " + new Date().toLocaleString();
		    		facebookConnector.postMessageOnWall(post);
					mFacebookHandler.post(mUpdateFacebookNotification);
				} catch (Exception ex) {
					Log.e(TAG, "Error sending msg",ex);
				}
		    }
		};
		t.start();
	}
	
	public void twitterupdateLoginStatus() {
			
			if (TwitterUtils.isAuthenticated(prefs) == true) {			//user already logged into twitter
				twitterLoginStatus.setText(twitterLoggedIn);
				twitterClearCredentialsButton.setVisibility(View.VISIBLE); //logout button
				tweetPost.setVisibility(View.GONE); //login button
			}
			else {
				twitterLoginStatus.setText(twitterLoggedOut);  //user is logged out of twitter
				twitterClearCredentialsButton.setVisibility(View.GONE); //logout button
				tweetPost.setVisibility(View.VISIBLE); //login button
			}
		}
	
	public void fbupdateLoginStatus() {
		if (facebookConnector.getFacebook().isSessionValid() == true) { //user already logged into facebook so don't show login button but show logout button
			fbloginStatus.setText(fbLoggedIn);
			fbclearCredentialsButton.setVisibility(View.VISIBLE); //logout button
			fbPost.setVisibility(View.GONE); //login button
		}
		
		else {
			fbloginStatus.setText(fbLoggedOut); //user is not logged into facebook
			//fbclearCredentialsButton.setVisibility(View.INVISIBLE); 
			fbclearCredentialsButton.setVisibility(View.GONE); //logout button
			fbPost.setVisibility(View.VISIBLE); //login button
		}
	}
	
	
	
}
