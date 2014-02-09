package com.example.un_ps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperAPI;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.Constants;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

public class UnalMapActivity extends MapActivity
{
    private MapView mapView;
    private GraphHopperAPI hopper;
    private GeoPoint start;
    private GeoPoint end;
    private ListOverlay pathOverlay = new ListOverlay();
    private volatile boolean prepareInProgress = false;
    private volatile boolean shortestPathRunning = false;
    private String currentArea = "berlin";
    private String mapsFolder;
    private String mapFile;
    private Toast toast = null;
    
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
            Log.e("Latitud1", String.valueOf(start.latitude));
            Log.e("Longitud1", String.valueOf(start.longitude));
            Marker marker = createMarker(start, R.drawable.marker_green);
            if (marker != null)
            {
                pathOverlay.getOverlayItems().add(marker);
                mapView.redraw();
            }
            
            shortestPathRunning = true;
            Marker marker1 = createMarker(end, R.drawable.marker_red);
            if (marker1 != null)
            {
                pathOverlay.getOverlayItems().add(marker1);
                mapView.redraw();
            }
            Log.e("Latitud2", String.valueOf(end.latitude));
            Log.e("Longitud2", String.valueOf(end.longitude));
            
            calcPath(start.latitude, start.longitude, end.latitude,
                    end.longitude);
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        double LatPos = getIntent().getExtras().getDouble("LatPos");
		double LonPos = getIntent().getExtras().getDouble("LonPos");
		GeoPoint tmpPoint1 = new GeoPoint(LatPos,LonPos);
		start = tmpPoint1;
		
		double LatPos2 = getIntent().getExtras().getDouble("LatPos2");
		double LonPos2 = getIntent().getExtras().getDouble("LonPos2");
		GeoPoint tmpPoint2 = new GeoPoint(LatPos2,LonPos2);
		end = tmpPoint2;
		
        mapView = new MapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.getMapViewPosition().setCenter(start);
        mapView.getMapViewPosition().setZoomLevel((byte) 19);
        

        final EditText input = new EditText(this);
        input.setText(currentArea);
        mapsFolder = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/unps/maps/";
        if (!new File(mapsFolder).exists())
        {
            new File(mapsFolder).mkdirs();
        }

        // TODO get user confirmation to download
        // if (AndroidHelper.isFastDownload(this))
        initFiles("unal");
        
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
        setContentView(mapView);
        // TODO sometimes the center is wrong
        mapView.getOverlays().clear();
        mapView.getOverlays().add(pathOverlay);
        loadGraphStorage();
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
                    logUser("Finished loading graph. Drawing route.");
                    
                }

                finishPrepare();
            }
        }.execute();
    }

    private void finishPrepare()
    {
        prepareInProgress = false;
        traceMapRoute();
    }

    private Polyline createPolyline( GHResponse response )
    {
        int points = response.getPoints().getSize();
        List<GeoPoint> geoPoints = new ArrayList<GeoPoint>(points);
        PointList tmp = response.getPoints();
        for (int i = 0; i < response.getPoints().getSize(); i++)
        {
            geoPoints.add(new GeoPoint(tmp.getLatitude(i), tmp.getLongitude(i)));
        }
        PolygonalChain polygonalChain = new PolygonalChain(geoPoints);
        Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.BLUE);
        paintStroke.setAlpha(128);
        paintStroke.setStrokeWidth(8);
        paintStroke.setPathEffect(new DashPathEffect(new float[] { 25, 15 }, 0));

        return new Polyline(polygonalChain, paintStroke);
    }

    private Marker createMarker( GeoPoint p, int resource )
    {
        Drawable drawable = getResources().getDrawable(resource);
        return new Marker(p, Marker.boundCenterBottom(drawable));
    }

    public void calcPath( final double fromLat, final double fromLon,
            final double toLat, final double toLon )
    {

        log("calculating path ...");
        new AsyncTask<Void, Void, GHResponse>()
        {
            float time;

            protected GHResponse doInBackground( Void... v )
            {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).
                        setAlgorithm("dijkstrabi").
                        putHint("instructions", false).
                        putHint("douglas.minprecision", 1);
                req.setVehicle("FOOT");
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp;
            }

            protected void onPostExecute( GHResponse resp )
            {
                if (!resp.hasErrors())
                {
                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                            + toLon + " found path with distance:" + resp.getDistance()
                            / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
                            + time + " " + resp.getDebugInfo());
                    showToast("the route is " + (int) (resp.getDistance() / 100) / 10f
                            + "km long, time:" + resp.getTime() / 60f + "min, debug:" + time);

                    pathOverlay.getOverlayItems().add(createPolyline(resp));
                    mapView.redraw();
                } else
                {
                    showToast("Error:" + resp.getErrors());
                }
                shortestPathRunning = false;
            }
        }.execute();
    }

    private void log( String str )
    {
        Log.i("GH", str);
    }

    private void killToast() {
	    if (this.toast != null) {
	        this.toast.cancel();
	    }
	}
    
    private void showToast(String str) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(str);
        }

        // Showing toast finally
        this.toast.show();
    }
    
    private void logUser( String str )
    {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onPause() {
        killToast();
        super.onPause();
    }

}
