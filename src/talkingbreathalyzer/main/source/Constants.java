//package com.ecs.android.sample.twitter;
package talkingbreathalyzer.main.source;

public class Constants {

	public static final String CONSUMER_KEY = "rRKNnOuEYiKYOSlPz4N9oQ";
	public static final String CONSUMER_SECRET= "yWdCMzrRUU703zqxGQgjPcianuMuZYtkZxZ3tyrdhW4";
	
	public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow-twitter";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

}

