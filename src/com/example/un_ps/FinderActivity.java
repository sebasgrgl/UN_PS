package com.example.un_ps;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class FinderActivity extends Activity implements TextWatcher{
	
	AutoCompleteTextView mOrigin;
	AutoCompleteTextView mDestiny;
	
	public String BuildingsBase[];
	private String LatitudBase[];
	private String LongitudBase[];
	private double LatPos = 4.635200;
	private double LonPos = -74.082280;
	private double LatPos2 = 4.635200;
	private double LonPos2 = -74.082280;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finder);
		BuildingsBase = getIntent().getExtras().getStringArray("BuildingsBase");
		LatitudBase = getIntent().getExtras().getStringArray("LatitudBase");
		LongitudBase = getIntent().getExtras().getStringArray("LongitudBase");

		//
		//Text View to Origin
		//
		////////////////////////////////////////////////////////////////////////////////////////
		final AutoCompleteTextView mOrigin = 
				(AutoCompleteTextView)findViewById(
		           R.id.origin_finder);
		mOrigin.addTextChangedListener(this);
		mOrigin.setAdapter(
				new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, BuildingsBase));
		mOrigin.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mOrigin.showDropDown();
				return false;
			}
		});
		mOrigin.setValidator(new AutoCompleteTextView.Validator() {
			
			@Override
			public boolean isValid(CharSequence text) {
				// TODO Auto-generated method stub
				Arrays.sort(BuildingsBase);
				if (Arrays.binarySearch(BuildingsBase, text.toString()) > 0) {
					return true;
				}
				return false;
			}
			
			@Override
			public CharSequence fixText(CharSequence invalidText) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////
		
		//
		//Text View to Destiny
		//
		////////////////////////////////////////////////////////////////////////////////////////
		final AutoCompleteTextView mDestiny = 
				(AutoCompleteTextView)findViewById(
		           R.id.destiny_finder);
		mDestiny.addTextChangedListener(this);
		mDestiny.setAdapter(
				new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, BuildingsBase));		
		mDestiny.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mDestiny.showDropDown();
				return false;
			}
		});
		mDestiny.setValidator(new AutoCompleteTextView.Validator() {
			
			@Override
			public boolean isValid(CharSequence text) {
				// TODO Auto-generated method stub
				Arrays.sort(BuildingsBase);
				if (Arrays.binarySearch(BuildingsBase, text.toString()) > 0) {
					return true;
				}
				return false;
			}
			
			@Override
			public CharSequence fixText(CharSequence invalidText) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		////////////////////////////////////////////////////////////////////////////////////////
		
	}

	@Override
	public void afterTextChanged(Editable arg0) {
	 // TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
	  int after) {
	 // TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	 // TODO Auto-generated method stub

	}
		
	public void goMap (View view) {
		int counter = 0;
		AutoCompleteTextView mOrigin = 
				(AutoCompleteTextView)findViewById(
		           R.id.origin_finder);
		String p = mOrigin.getText().toString();
		for(int i=0; i < BuildingsBase.length; i++) {
			String u = BuildingsBase[i].toString();
	        if( (u.equals( p )) && !(u.equals("  -  "))) {
	        	Log.e("Edificio Origen", u);
	        	LatPos = Double.parseDouble(LatitudBase[i].toString());
	        	LonPos = Double.parseDouble(LongitudBase[i].toString());
	        	Log.e("Ubicación Edificio", String.valueOf(LatPos) + "  " + String.valueOf(LonPos));
	        	counter++;
	        }
		}
		
		AutoCompleteTextView mDestiny = 
				(AutoCompleteTextView)findViewById(
		           R.id.destiny_finder);
		String p2 = mDestiny.getText().toString();
		for(int i=0; i < BuildingsBase.length; i++) {
			String u = BuildingsBase[i].toString();
	        if( ((u.equals( p2 )) && !(u.equals("  -  "))) && !(p.equals( p2 )) ) {
	        	Log.e("Edificio Destino", u);
	        	LatPos2 = Double.parseDouble(LatitudBase[i].toString());
	        	LonPos2 = Double.parseDouble(LongitudBase[i].toString());
	        	Log.e("Ubicación Edificio", String.valueOf(LatPos2) + "  " + String.valueOf(LonPos2));
	        	counter++;
	        }
		}
		if (counter == 2) {
			Intent openMap = new Intent(this, UnalMapActivity.class);
			openMap.putExtra("LatPos", LatPos);
			openMap.putExtra("LonPos", LonPos);
			openMap.putExtra("LatPos2", LatPos2);
			openMap.putExtra("LonPos2", LonPos2);
			startActivity(openMap);
		}
		else {
			Toast.makeText(this, R.string.invalid_path, Toast.LENGTH_LONG).show();
		}
	}

}
