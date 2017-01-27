package com.example.nickgao.rcproject;

import android.app.Application;
import android.content.Context;

public class RingCentralApp extends Application{
    /**
     * Defines logging tag.
     */
	private static final String TAG = "[RC]RingCentralApp";

	/**
	 * Keeps application execution context.
	 */
	private static Context sApplicationContext = null;
	
	
    @Override
    public void onCreate() {
        super.onCreate();      
        // Fixed bug AB-8845 Polling didn't work for Messages 
        // LoginScreen.setLoginIn();
        sApplicationContext = getApplicationContext();
    }
    
    public static Context getContextRC(){    	
        return sApplicationContext;
    }

}
