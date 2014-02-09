package com.example.un_ps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class RecoveryPassActivity extends Activity {
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        // Set View to register.xml
	        setContentView(R.layout.recovery_screen);
	 
	       
	    }
	 
	 public void goBack (View View){
	    	
	    	finish();
	    	
	    }
	}

