
package talkingbreathalyzer.main.source;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginButton;

import android.widget.Toast;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionStore;
import com.facebook.android.Utility;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;



public class action extends Activity implements OnClickListener {
	
	private Button callFriendButton_;
	private Button findFriendButton_;
	private Button callTaxiButton_;
	private Button TaxiMagicButton_;
	private Button whereamiButton_;
	private Button facebookPostButton_;
	private Button tweetButton_;
	private Button cancelButton_;
	private Button placesButton_;
	private Button drunkDialButton_;
	
	private TelephonyManager teleMgr = null;
	private SharedPreferences prefs;
	private Resources resources;	
	//private boolean call_taxi;	
	//private boolean call_friend;
	private String friend_phone;
	private String taxi_phone;	
	//private boolean find_friend;
	//private boolean whereami; 
	private boolean facebook_post;	 
	private boolean send_tweets;
	private boolean hideTaximagic;
	
	 public static final String APP_ID = "305557519478133";

	 private LoginButton mLoginButton;
	 private TextView mText;
     private ImageView mUserPic;
     private Handler mHandler;
     ProgressDialog dialog;
     private String phoneNumber;

     final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
     final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;

     private String graph_or_fql;

     private ListView list;
     String[] main_items = { "Update Status", "App Requests", "Get Friends", "Upload Photo",
            "Place Check-in", "Run FQL Query", "Graph API Explorer" };
     String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins"};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.action);
		
		 mHandler = new Handler();
         mText = (TextView) action.this.findViewById(R.id.txt);
         mUserPic = (ImageView) action.this.findViewById(R.id.user_pic);
         
         Bundle extras = getIntent().getExtras();
         String resultstring = extras.getString("BREATH_RESULT");
         
       
         
	        // Create the Facebook Object using the app id.
	        Utility.mFacebook = new Facebook(APP_ID);
	        // Instantiate the asynrunner object for asynchronous api calls.
	        Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
	        mLoginButton = (LoginButton) findViewById(R.id.login);
	      

	        // restore session if one exists
	        SessionStore.restore(Utility.mFacebook, this);
	        SessionEvents.addAuthListener(new FbAPIsAuthListener());
	        SessionEvents.addLogoutListener(new FbAPIsLogoutListener());
	        
	        mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE, Utility.mFacebook, permissions);
	        
	        if (Utility.mFacebook.isSessionValid()) {
	            requestUserData();	            
	        }
	        
	       // Bundle params = new Bundle();
          //  params.putString("fields", "name, picture, location");
       //     Utility.mAsyncRunner.request("me/friends", params,
                //    new FriendsRequestListener());
	        
	        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //force only portrait mode
	     //******** preferences code
	        resources = this.getResources();
	       setPreferences();
	     //***************************
		
	    teleMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	       
	    callFriendButton_ = (Button) findViewById(R.id.callFriendButton);
   	 	callFriendButton_.setOnClickListener(this); 
   	 	
   	 	findFriendButton_ = (Button) findViewById(R.id.findFriendButton);
   	 	findFriendButton_.setOnClickListener(this); 
   	 	
   	 	placesButton_ = (Button) findViewById(R.id.placesButton);
   	 	placesButton_.setOnClickListener(this); 
   	 	
   	 	callTaxiButton_ = (Button) findViewById(R.id.callTaxiButton);
   	 	callTaxiButton_.setOnClickListener(this); 
   	 	
   	 	TaxiMagicButton_ = (Button) findViewById(R.id.TaxiMagicButton);
   	 	TaxiMagicButton_.setOnClickListener(this); 
   	 	
   	 	whereamiButton_ = (Button) findViewById(R.id.whereamiButton);
		whereamiButton_.setOnClickListener(this); 
		
		facebookPostButton_ = (Button) findViewById(R.id.facebookPostButton);
		facebookPostButton_.setOnClickListener(this); 
		
		tweetButton_ = (Button) findViewById(R.id.tweetButton);
		tweetButton_.setOnClickListener(this); 		
		
		cancelButton_ = (Button) findViewById(R.id.cancelButton);
		cancelButton_.setOnClickListener(this); 
		
		drunkDialButton_ = (Button) findViewById(R.id.drunkDialButton);
		drunkDialButton_.setOnClickListener(this); 
		
		if (resultstring.equals("0") || resultstring.equals("1")) {          //show drunk dial button if tipsy or drunk		
			drunkDialButton_.setVisibility(View.GONE); 
		}
		
		if (facebook_post == false) {
			facebookPostButton_.setVisibility(View.GONE); 
		}
		
		if (send_tweets == false) {
			tweetButton_.setVisibility(View.GONE); 
		}
		
		if (hideTaximagic == true) { //taxi magic only available in the US, non-US users may want to hide the button
			TaxiMagicButton_.setVisibility(View.GONE); 
		}
	    
	    
	   // if (call_friend == false) {
	   // 	callFriendButton_.setVisibility(View.GONE); 
	   // }
	    
	   // if (find_friend == false) {
	   // 	findFriendButton_.setVisibility(View.GONE); 
	   // }	    	
	    
	   // if (call_taxi == false) {
	  //  	callTaxiButton_.setVisibility(View.GONE); 
	   // }
	   
	//	if (whereami == false ) {
	//		whereamiButton_.setVisibility(View.GONE); 
	//	}
		
		//if (checkInternetConnection() == false) { //we don't have internet so need to hide these buttons
			//whereamiButton_.setVisibility(View.GONE); 
			//findFriendButton_.setVisibility(View.GONE); 
	//	}
		
	}	 

	

	//Called when the LED button is clicked
	public void onClick(View v) {
		
		if (v.getId() == R.id.callFriendButton) {
			phoneCall(friend_phone);
		}
		
		if (v.getId() == R.id.findFriendButton) {
			
			  if (!Utility.mFacebook.isSessionValid()) {				 
			       AlertDialog.Builder alert=new AlertDialog.Builder(this);
			       alert.setTitle(getString(R.string.actions_facebook_login_title)).setIcon(R.drawable.icon).setMessage(getString(R.string.actions_facebook_login)).setNeutralButton(getString(R.string.OKText), null).show();			       
              } 
			  
			  else { 
			
			dialog = ProgressDialog.show(action.this, "",
                     getString(R.string.finding_friends), false, true,
                     new DialogInterface.OnCancelListener() {
                         @Override
                         public void onCancel(DialogInterface dialog) {
                             showToast(getString(R.string.finding_friends_cancel));
                         }
                     });
			  
			
			 // graph_or_fql = "graph";
             // Bundle params = new Bundle();
             // params.putString("fields", "name, picture, location");
             // Utility.mAsyncRunner.request("me/friends", params,
             //        new FriendsRequestListener());
              
              graph_or_fql = "fql";
              String query = "select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by current_location";
              Bundle params = new Bundle();
              params.putString("method", "fql.query");
              params.putString("query", query);
              Utility.mAsyncRunner.request(null, params,
                      new FriendsRequestListener());
             
              }
              
              
		}
		
		if (v.getId() == R.id.placesButton) {
			
			 if (!Utility.mFacebook.isSessionValid()) {				 
			       AlertDialog.Builder alert=new AlertDialog.Builder(this);
			       alert.setTitle(getString(R.string.actions_facebook_login_title)).setIcon(R.drawable.icon).setMessage(getString(R.string.actions_facebook_login)).setNeutralButton(getString(R.string.OKText), null).show();			       
            } 
			  
			  else { 
			
			Intent intent = new Intent()
			.setClass(this,
					talkingbreathalyzer.main.source.Places.class);

			this.startActivity(intent);
			}
		}
		
		
		
		if (v.getId() == R.id.callTaxiButton) {
			phoneCall(taxi_phone);
		}
		
		
		if (v.getId() == R.id.drunkDialButton) {
			
			
			 ContentResolver cr = getContentResolver();
		        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		        int size = cur.getCount();
		        boolean found = false;
		        Random rnd = new Random();
		      
			   while(!found) {	
				   int index = rnd.nextInt(size);    
				   cur.moveToPosition(index); //find a random contact and see if it has a phone number
				    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
			        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));			       
				 		if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				 			//this contact has a phone number so now get the phone number
				 			found = true;
					 		   if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{id}, null);
						 	        while (pCur.moveToNext()) {						 	        	
						 	        	phoneNumber = pCur.getString(pCur.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER)); 
						 	        	showToast(getString(R.string.drunkDialingPrompt) + " " + name);
						 	        } 
					 	        pCur.close();
					 		   }
				 	    }
		        	}
		   
			phoneCall(phoneNumber); //now make the call
		}
		
		if (v.getId() == R.id.TaxiMagicButton) {
			Intent intent = new Intent()
			.setClass(this,
					talkingbreathalyzer.main.source.getTaxi.class);

			this.startActivity(intent);
		}
	
		if (v.getId() == R.id.whereamiButton) {
			Intent intent = new Intent()
			.setClass(this,
					talkingbreathalyzer.main.source.whereami.class);

			this.startActivity(intent);
			
		}
		
		if (v.getId() == R.id.facebookPostButton) {
			
			if (!Utility.mFacebook.isSessionValid()) {				 
			       AlertDialog.Builder alert=new AlertDialog.Builder(this);
			       alert.setTitle(getString(R.string.actions_facebook_login_title)).setIcon(R.drawable.icon).setMessage(getString(R.string.actions_facebook_login)).setNeutralButton(getString(R.string.OKText), null).show();			       
			} 
			  
			else { 
			
				Intent intent = new Intent()
				.setClass(this,
						talkingbreathalyzer.main.source.facebookPost.class);
	
				this.startActivity(intent);
			}
		}
		
		if (v.getId() == R.id.tweetButton) {
			Intent intent = new Intent()
			.setClass(this,
					talkingbreathalyzer.main.source.tweetPost.class);

			this.startActivity(intent);
		}	
		
	
		if (v.getId() == R.id.cancelButton) {
			finish();
		}

	}
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch (requestCode) {
	        /*
	         * if this is the activity result from authorization flow, do a call
	         * back to authorizeCallback Source Tag: login_tag
	         */
	            case AUTHORIZE_ACTIVITY_RESULT_CODE: {
	                Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
	                break;
	            }
	            
	        }
	    }
	
	public class FriendsRequestListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            dialog.dismiss();
	            Intent myIntent = new Intent(getApplicationContext(), FriendsList.class);
	            myIntent.putExtra("API_RESPONSE", response);
	            myIntent.putExtra("METHOD", graph_or_fql);
	            startActivity(myIntent);
	        }

	        public void onFacebookError(FacebookError error) {
	            dialog.dismiss();
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_SHORT).show();
	        }
	        
	   }
	
	  public void phoneCall(String numtoCall) {
	        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:5551212"));
	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + numtoCall));
	        startActivity(intent);
	   }
	  
	  private boolean checkInternetConnection() { 
		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 
		 
		    // test for connection 
		    if (cm.getActiveNetworkInfo() != null 
		            && cm.getActiveNetworkInfo().isAvailable() 
		            && cm.getActiveNetworkInfo().isConnected()) { 
		        return true; 
		    } else { 
		        //no conection 
		        return false; 
		    } 
		} 
	  
	  private void setPreferences() //here is where we read the shared preferences into variables
	    
	  {
	     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

	    // call_taxi =  prefs.getBoolean("pref_taxi", false); //whether or not to call taxi on buzzed or drunk   
	   //  find_friend =  prefs.getBoolean("pref_findFriend", false); //whether or not to call taxi on buzzed or drunk 
	    // whereami = prefs.getBoolean("pref_whereami", false);
	   //  call_friend = prefs.getBoolean("pref_friend", false); //whether or not to call taxi on buzzed or drunk   
	   //  disable_sleep = prefs.getBoolean("pref_sleep", false);
	     send_tweets = prefs.getBoolean("pref_tweet", true);   
	   //  send_tweets_gps = prefs.getBoolean("pref_tweet_gps", false);        
	     facebook_post = prefs.getBoolean("pref_facebook_post", true);   //whether or not to post results to facebook
	     hideTaximagic = prefs.getBoolean("pref_hideTaximagic", false);
	   //  facebook_friends = prefs.getBoolean("pref_facebook_friends", false);  //whether or not to find facebook friends on buzzed or drunk
	     	      
	     friend_phone = prefs.getString(   
	  	        resources.getString(R.string.pref_friend_phone),
	  	        resources.getString(R.string.friend_phone)); 
	     
	     taxi_phone = prefs.getString(   
	    	        resources.getString(R.string.pref_taxi_phone),
	    	        resources.getString(R.string.taxi_phone)); 
	    } 
	  
	   
	  
	  public class FbAPIsAuthListener implements AuthListener {

	        @Override
	        public void onAuthSucceed() {
	            requestUserData();
	        }

	        @Override
	        public void onAuthFail(String error) {
	            mText.setText(getString(R.string.actions_facebook_loginfailed) + error); //Login Failed: error message
	        }
	    }

	    /*
	     * The Callback for notifying the application when log out starts and
	     * finishes.
	     */
	    public class FbAPIsLogoutListener implements LogoutListener {
	        @Override
	        public void onLogoutBegin() {
	        	mText.setText(getString(R.string.actions_facebook_loggingout)); //logging out...
	        }

	        @Override
	        public void onLogoutFinish() {
	        	mText.setText(getString(R.string.actions_facebook_loggedout)); //you are logged out of facebook
	            mUserPic.setImageBitmap(null);
	        }
	    }
	  
	    public void requestUserData() {
	    	mText.setText(getString(R.string.actions_facebook_fetching)); //fetching username and profile pic...
	        Bundle params = new Bundle();
	        params.putString("fields", "name, picture");
	        Utility.mAsyncRunner.request("me", params, new UserRequestListener());
	    }
	    
	    public class UserRequestListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            JSONObject jsonObject;
	            try {
	                jsonObject = new JSONObject(response);

	                final String picURL = jsonObject.getString("picture");
	                final String name = jsonObject.getString("name");
	                Utility.userUID = jsonObject.getString("id");

	                mHandler.post(new Runnable() {
	                    @Override
	                    public void run() {
	                           //mText.setText("Welcome " + name + "!");
	                           mText.setText(getString(R.string.actions_facebook_welcome) + " " + name); //welcome john smith
	                           mUserPic.setImageBitmap(Utility.getBitmap(picURL));
	                    }
	                });

	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }

	    }
	    
	    @Override
	    public void onResume() {
	        super.onResume();
	        if (Utility.mFacebook != null && !Utility.mFacebook.isSessionValid()) {
	               mText.setText(getString(R.string.actions_facebook_loggedout));
	               mUserPic.setImageBitmap(null);
	        }
	    }
	    
	    public void showToast(final String msg) {
	        mHandler.post(new Runnable() {
	            @Override
	            public void run() {
	                Toast toast = Toast.makeText(action.this, msg, Toast.LENGTH_LONG);
	                toast.show();
	            }
	        });
	    }
	    
	    public class FQLRequestListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            mHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                    Toast.makeText(getApplicationContext(), "Response: " + response,
	                            Toast.LENGTH_LONG).show();
	                }
	            });
	        }

	        public void onFacebookError(FacebookError error) {
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_LONG).show();
	        }
	    }
	       

	    class ViewHolder {
	        TextView main_list_item;
	    }
	    
	    
	  
	  
}
