package com.example.un_ps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	
	static final int DIALOG_ABOUT_ID=1;
	
	public String Lat;
	public String Lon;
	public String Num;
	public String Name;
	public String Info;
	public String Desc;
	public String BuildingsBase[];
	private String LatitudBase[];
	private String LongitudBase[];
	public String DescriptionBase[];
	public String InformationBase[];
	
	public String loadJSONFromAsset() {
		StringBuilder stringBuilder = new StringBuilder();
        //String json = null;
        try {
        	InputStream inputStream = getAssets().open("listado_edificios.json");
        	
        	BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return stringBuilder.toString();

    }
	
	public void ReadBuildingJSONTask () {
		List<String> NameDataBase = new ArrayList<String>();
		List<String> LatDataBase = new ArrayList<String>();
		List<String> LonDataBase = new ArrayList<String>();
		List<String> InfoDataBase = new ArrayList<String>();
		List<String> DescDataBase = new ArrayList<String>();
		
		String myJson = loadJSONFromAsset();
		
            try {
                JSONArray jsonBuildingArray = new JSONArray(myJson);

				for (int i = 0; i < jsonBuildingArray.length(); i++) {
					
					JSONObject buildingItem = jsonBuildingArray.getJSONObject(i);
					Lat = buildingItem.getString("lat");
					Lon = buildingItem.getString("lon");
					Num = buildingItem.getString("num");
					Name = buildingItem.getString("name");
					Desc= buildingItem.getString("desc");
					Info= buildingItem.getString("info");
					NameDataBase.add(Num + " - " + Name);
					LatDataBase.add(Lat);
					InfoDataBase.add(Info);
					DescDataBase.add(Desc);
					LonDataBase.add(Lon);
					
				}
				String[] NameDataB = new String[ NameDataBase.size() ];
				NameDataBase.toArray(NameDataB);
				BuildingsBase= NameDataB;
				
				String[] InfoData = new String[ NameDataBase.size() ];
				InfoDataBase.toArray(InfoData);
				InformationBase = InfoData;
				
				String[] DescData = new String[ NameDataBase.size() ];
				DescDataBase.toArray(DescData);
				DescriptionBase = DescData;
				
				String[] LatData = new String[ NameDataBase.size() ];
				LatDataBase.toArray(LatData);
				LatitudBase = LatData;
				
				String[] LonData = new String[ NameDataBase.size() ];
				LonDataBase.toArray(LonData);
				LongitudBase = LonData;
				
				
            } catch (Exception e) {
                Log.d("ReadPlaceJSONFeedTask", e.getLocalizedMessage());
            }          
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {

		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch(id) {
		
		case DIALOG_ABOUT_ID:

			Context context = getApplicationContext();
		    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
		    View layout = inflater.inflate(R.layout.about_dialog, null); 		
		    builder.setView(layout);
		    builder.setPositiveButton(R.string.ok, null);	
		    dialog = builder.create();
		    break;
		}   

		return dialog;        

	}
	
	@Override		
	public	boolean	onOptionsItemSelected(MenuItem	item){
	 switch (item.getItemId()){
	 case R.id.about:
			showDialog(DIALOG_ABOUT_ID);
   	default:
			return super.onOptionsItemSelected(item);	 
	 }
	 
    }
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ReadBuildingJSONTask();
		AssetManager am = getAssets();
        
        try {
         
            File dir = new File(Environment.getExternalStorageDirectory() + "/unps/maps/unal-gh/");
         
            if (!dir.exists()) {
                dir.mkdirs();
            }
         
            String fileUnal = "unal.map";//DESTINATION_FILENAMES;
            String fileEdges = "edges";//DESTINATION_FILENAMES;
            String fileGeometry = "geometry";//DESTINATION_FILENAMES;
            String fileLocation = "locationIndex";//DESTINATION_FILENAMES;
            String fileNames = "names";//DESTINATION_FILENAMES;
            String fileNodes = "nodes";//DESTINATION_FILENAMES;
            String fileProperties = "properties";//DESTINATION_FILENAMES;
            
            File destinationFile = new File(Environment.getExternalStorageDirectory() + "/unps/maps/unal-gh/" + fileUnal);
            File destinationEdges = new File(Environment.getExternalStorageDirectory() + "/unps/maps/unal-gh/" + fileEdges);
            File destinationGeometry = new File(Environment.getExternalStorageDirectory() + "/unps/maps/unal-gh/" + fileGeometry);
            File destinationLocation = new File(Environment.getExternalStorageDirectory() + "/unps/maps/unal-gh/" + fileLocation);
            File destinationNames = new File(Environment.getExternalStorageDirectory() + "/unps/maps/unal-gh/" + fileNames);
            File destinationNodes = new File(Environment.getExternalStorageDirectory() + "/unps/maps/unal-gh/" + fileNodes);
            File destinationProperties = new File(Environment.getExternalStorageDirectory() + "/unps/maps/unal-gh/" + fileProperties);
            
            if (!destinationFile.exists()) {
                InputStream in = am.open("unal.mp3");//ORIGIN_FILENAME);
                FileOutputStream f = new FileOutputStream(destinationFile);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
            
            if (!destinationEdges.exists()) {
                InputStream in = am.open("edges");//ORIGIN_FILENAME);
                FileOutputStream f = new FileOutputStream(destinationEdges);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
            
            if (!destinationGeometry.exists()) {
                InputStream in = am.open("geometry");//ORIGIN_FILENAME);
                FileOutputStream f = new FileOutputStream(destinationGeometry);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
            
            if (!destinationLocation.exists()) {
                InputStream in = am.open("locationIndex");//ORIGIN_FILENAME);
                FileOutputStream f = new FileOutputStream(destinationLocation);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
            
            if (!destinationNames.exists()) {
                InputStream in = am.open("names");//ORIGIN_FILENAME);
                FileOutputStream f = new FileOutputStream(destinationNames);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
            
            if (!destinationNodes.exists()) {
                InputStream in = am.open("nodes");//ORIGIN_FILENAME);
                FileOutputStream f = new FileOutputStream(destinationNodes);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
            
            if (!destinationProperties.exists()) {
                InputStream in = am.open("properties");//ORIGIN_FILENAME);
                FileOutputStream f = new FileOutputStream(destinationProperties);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
         
        } catch (Exception e) {
            Log.e("CopyFileFromAssetsToSD", e.getMessage());
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void goFinder (View view) {
		Intent openFinder = new Intent(this, FinderActivity.class);
		openFinder.putExtra("BuildingsBase", BuildingsBase);
		openFinder.putExtra("LatitudBase", LatitudBase);
		openFinder.putExtra("LongitudBase", LongitudBase);
		startActivity(openFinder);	
	}
	
	public void goMap (View view) {
		Intent openMap = new Intent(this, MyMapActivity.class);
		openMap.putExtra("BuildingsBase", BuildingsBase);
		openMap.putExtra("LatitudBase", LatitudBase);
		openMap.putExtra("LongitudBase", LongitudBase);
		startActivity(openMap);
	}

	public void goEvents (View view) {
		startActivity(new Intent(this, TimetableActivity.class));		
	}
	
	public void goBuildings (View view) {
		startActivity(new Intent(this, ImageTextListViewActivity.class));	
		Intent openBuilding = new Intent(this,ImageTextListViewActivity.class);
		openBuilding.putExtra("BuildingsBase", BuildingsBase);
		openBuilding.putExtra("DescriptionBase", DescriptionBase);
		openBuilding.putExtra("InformationBase", InformationBase);
		startActivity(openBuilding);
	}
	 
	public void goTosia (View view) {
        goToUrl ( "http://www.sia.unal.edu.co/");
    }

    public void goTosinab (View view) {
        goToUrl ( "http://www.sinab.unal.edu.co/");
    }

    public void goToun (View view) {
        goToUrl ( "http://www.unal.edu.co/");
    }
    
    public void goTomail (View view) {
        goToUrl ( "https://login.unal.edu.co/sso/auth.htm");
    }
    
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
    
    @Override
    public void onBackPressed() 
    {
    }
}
