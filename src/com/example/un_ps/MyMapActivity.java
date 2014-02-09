package com.example.un_ps;

import java.io.File;
import java.util.Arrays;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;

import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperAPI;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.Constants;


public class MyMapActivity extends MapActivity implements TextWatcher {
	
    LinearLayout searchPanel;
    Button searchButton;
    EditText searchText;
    public static final int SEARCH_ID = Menu.FIRST;
    //MapView mapView;
    public GeoPoint point;
	AutoCompleteTextView mPlace;
	public String BuildingsBase[];
	private String LatitudBase[];
	private String LongitudBase[];

	
    private MapView mapView;
	public Double lat=2.893078;
	public Double lon=-73.784507;
    private GraphHopperAPI hopper;
    private ListOverlay pathOverlay = new ListOverlay();
    private volatile boolean prepareInProgress = false;
    private volatile boolean shortestPathRunning = false;
    private String currentArea = "berlin";
    private String mapsFolder;
    private String mapFile;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
		BuildingsBase = getIntent().getExtras().getStringArray("BuildingsBase");
		LatitudBase = getIntent().getExtras().getStringArray("LatitudBase");
		LongitudBase = getIntent().getExtras().getStringArray("LongitudBase");
        
        searchPanel = (LinearLayout) findViewById(R.id.searchPanel);
        searchButton = (Button) findViewById(R.id.searchButton);
        
        final AutoCompleteTextView mPlace = 
				(AutoCompleteTextView)findViewById(
		           R.id.place_finder);
		mPlace.addTextChangedListener(this);
		mPlace.setAdapter(
				new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, BuildingsBase));
		mPlace.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mPlace.showDropDown();
				return false;
			}
		});
		mPlace.setValidator(new AutoCompleteTextView.Validator() {
			
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
        //mapView = (MapView) findViewById(R.id.mapView);
        //mapView.setMultiTouchControls(true);//Enable touch gestures
        
        //mapView = new MapView(this);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        
        //mapController = mapView.getController();
        //mapController.setZoom(2);
        
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	int counter =0;
            	AutoCompleteTextView mOrigin = 
        				(AutoCompleteTextView)findViewById(
        		           R.id.place_finder);
        		String p = mOrigin.getText().toString();
        		for(int i=0; i < BuildingsBase.length; i++) {
        			String u = BuildingsBase[i].toString();
        	        if( (u.equals( p )) && !(u.equals("  -  "))) {
        	        	Log.e("Edificio Origen", u);
        	        	lat = Double.parseDouble(LatitudBase[i].toString());
        	        	lon = Double.parseDouble(LongitudBase[i].toString());
        	        	Log.e("Ubicación Edificio", String.valueOf(lat) + "  " + String.valueOf(lon));
        	        	counter++;
        	        }
        		}
        		if (counter !=0){
            		GeoPoint tmpPoint2 = new GeoPoint(lat,lon);
            		point = tmpPoint2;
                    mapView.getMapViewPosition().setCenter(point);
            		mapView.getMapViewPosition().setZoomLevel((byte) 19);
            		
            		mapsFolder = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/unps/maps/";
                    if (!new File(mapsFolder).exists())
                    {
                        new File(mapsFolder).mkdirs();
                    }

                    // TODO get user confirmation to download
                    // if (AndroidHelper.isFastDownload(this))
                    //pathOverlay.getOverlayItems().clear();
                    Log.e("Latitud", String.valueOf(point.latitude));
                    Log.e("Longitud", String.valueOf(point.longitude));
                    Marker marker = createMarker(point, R.drawable.marker_green);
                    if (marker != null)
                    {
                        mapView.getOverlays().clear();
                        pathOverlay.getOverlayItems().clear();
                    	pathOverlay.getOverlayItems().add(marker);
                        mapView.getOverlays().add(pathOverlay);
                    	mapView.invalidate();
                        Log.e("Marcador", "No es null");
                    }
                    //initFiles("unal");
        		}
                } 
        });
        
    }
   	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, SEARCH_ID, 0, "Search");
        return result;
    }

   	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case SEARCH_ID:
                searchPanel.setVisibility(View.VISIBLE);
                break;
        }

        return result;
    }
   	
   	private boolean initFiles( String area )
    {
        // only return true if already loaded
        if (hopper != null)
            return true;

        if (prepareInProgress)
        {
            logUser("Preparation still in progress");
            return false;
        }
        prepareInProgress = true;
        currentArea = area;
        //downloadingFiles();
        loadMap();
        return false;
    }
   	
    private void logUser( String str )
    {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
    
    private void log( String str )
    {
        Log.i("GH", str);
    }
    
    void loadMap()
    {
        logUser("loading map");
        mapFile = mapsFolder + currentArea + "-gh/" + currentArea + ".map";
        FileOpenResult fileOpenResult = mapView.setMapFile(new File(mapFile));
        if (!fileOpenResult.isSuccess())
        {
            logUser(fileOpenResult.getErrorMessage());
            finishPrepare();
            return;
        }
        //setContentView(mapView);
        // TODO sometimes the center is wrong
        mapView.getOverlays().clear();
        mapView.getOverlays().add(pathOverlay);
        loadGraphStorage();
    }
    
    private void finishPrepare()
    {
        prepareInProgress = false;
        traceMapRoute();
    }
    
    void traceMapRoute()
    {
    	Log.e("Preparación", String.valueOf(prepareInProgress));
        if (!initFiles(currentArea))
            {
                return;
            }

            if (shortestPathRunning)
            {
                logUser("Calculation still in progress");
                return;
            }

            pathOverlay.getOverlayItems().clear();
            Log.e("Latitud", String.valueOf(point.latitude));
            Log.e("Longitud", String.valueOf(point.longitude));
            Marker marker = createMarker(point, R.drawable.flag_green);
            if (marker != null)
            {
                pathOverlay.getOverlayItems().clear();
            	pathOverlay.getOverlayItems().add(marker);
                mapView.redraw();
            }
    }
    
    void loadGraphStorage()
    {
        logUser("loading graph (" + Constants.VERSION + ") ... ");
        new GHAsyncTask<Void, Void, Path>()
        {
            protected Path saveDoInBackground( Void... v ) throws Exception
            {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.setCHShortcuts("fastest");
                tmpHopp.setEncodingManager(new EncodingManager("FOOT"));
                
                tmpHopp.load(mapsFolder + currentArea);
                log("found graph " + tmpHopp.getGraph().toString() + ", nodes:" + tmpHopp.getGraph().getNodes());
                hopper = tmpHopp;
               
                return null;
            }

            protected void onPostExecute( Path o )
            {
                if (hasError())
                {
                    logUser("An error happend while creating graph:"
                            + getErrorMessage());
                } else
                {
                    logUser("Finished loading graph");
                    
                }

                finishPrepare();
            }
        }.execute();
    }
    private Marker createMarker( GeoPoint p, int resource )
    {
        Drawable drawable = getResources().getDrawable(resource);
        return new Marker(p, Marker.boundCenterBottom(drawable));
    }
    
	@Override
	public void afterTextChanged(Editable s) {
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

}