package com.example.un_ps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
public class ImageTextListViewActivity extends Activity implements
        OnItemClickListener {
   
    public String info[];
    public String descriptions[];
    public String titles[];
 
    public static final Integer[] images = { R.drawable.cade, R.drawable.e101, R.drawable.e102, R.drawable.e103, 
    	R.drawable.e104, R.drawable.e201, R.drawable.e205, R.drawable.e207, R.drawable.e210, R.drawable.no_disponible, 
    	R.drawable.e212, R.drawable.no_disponible, R.drawable.e214, R.drawable.e217, R.drawable.e224, R.drawable.e225, R.drawable.e229,
    	R.drawable.no_disponible, R.drawable.e231, R.drawable.e235, R.drawable.e238, R.drawable.e239, R.drawable.e251, R.drawable.no_disponible, 
    	R.drawable.no_disponible, R.drawable.e301, R.drawable.e303, R.drawable.e305, R.drawable.e309, R.drawable.e310, R.drawable.e311,
    	R.drawable.e314, R.drawable.e317, R.drawable.e401, R.drawable.no_disponible, R.drawable.e404, R.drawable.e405,
    	R.drawable.e406, R.drawable.e407, R.drawable.e408, R.drawable.e409, R.drawable.e410, R.drawable.e411, R.drawable.e412, R.drawable.e413,
    	R.drawable.e414, R.drawable.e421, R.drawable.e425, R.drawable.e426, R.drawable.e431, R.drawable.e433, R.drawable.e434, R.drawable.no_disponible,
    	R.drawable.no_disponible, R.drawable.e437, R.drawable.no_disponible, R.drawable.e450, R.drawable.e451, R.drawable.e452, R.drawable.e453,
    	R.drawable.e454, R.drawable.e471, R.drawable.no_disponible, R.drawable.no_disponible, R.drawable.e476, R.drawable.e477, R.drawable.no_disponible,
    	R.drawable.e481, R.drawable.no_disponible, R.drawable.e500, R.drawable.e500a, R.drawable.e500a, R.drawable.e500a, R.drawable.e500a,
    	R.drawable.e500a, R.drawable.e500f, R.drawable.e500f, R.drawable.e500f, R.drawable.e500f, R.drawable.e500f, R.drawable.e500f, R.drawable.e500f,
    	R.drawable.e500f, R.drawable.e500f, R.drawable.e500f, R.drawable.e500f, R.drawable.e501, R.drawable.no_disponible, R.drawable.e503, 
    	R.drawable.no_disponible, R.drawable.e505, R.drawable.e506, R.drawable.e507, R.drawable.e508, R.drawable.e510, R.drawable.no_disponible,
    	R.drawable.e532, R.drawable.no_disponible, R.drawable.no_disponible, R.drawable.no_disponible, R.drawable.no_disponible, R.drawable.e561,
    	R.drawable.e561, R.drawable.e561, R.drawable.e561, R.drawable.e561, R.drawable.e561, R.drawable.e561, R.drawable.e561, R.drawable.e561, 
    	R.drawable.e561, R.drawable.e571, R.drawable.e603, R.drawable.e606, R.drawable.e608, R.drawable.no_disponible, R.drawable.e610, 
    	R.drawable.e615, R.drawable.e621, R.drawable.e631, R.drawable.no_disponible, R.drawable.no_disponible, R.drawable.e701,R.drawable.no_disponible,
    	 R.drawable.e731, R.drawable.e761, R.drawable.e861,  R.drawable.e862, R.drawable.e901, R.drawable.e905, R.drawable.e910,};
 
    ListView listView;
    List<RowItem> rowItems;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building_list);
        
        info=getIntent().getExtras().getStringArray("InformationBase");
        descriptions=getIntent().getExtras().getStringArray("DescriptionBase");
        titles=getIntent().getExtras().getStringArray("BuildingsBase");
        
        rowItems = new ArrayList<RowItem>();
        for (int i = 1; i < titles.length; i++) {
            RowItem item = new RowItem(images[i-1], titles[i], null, info[i]);
            rowItems.add(item);
        }
 
        listView = (ListView) findViewById(R.id.list);
        CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                R.layout.list_single, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }
 
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
    	
    	 Toast toast = Toast.makeText(getApplicationContext(),
    	            "Item " + (position + 1) + ": " + rowItems.get(position),
    	            Toast.LENGTH_SHORT);
    	        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    	        toast.show();
    	        
    	String title = ((TextView) view.findViewById(R.id.title)).getText().toString();
		//String desc = ((TextView) view.findViewById(R.id.desc)).getText().toString();
		String info = ((TextView) view.findViewById(R.id.info)).getText().toString();
		ImageView iv= ((ImageView) view.findViewById(R.id.icon));
		iv.buildDrawingCache();
		
		//Bitmap bitmap = iv.getDrawingCache();
		Bitmap bitmap=BitmapFactory.decodeResource(getResources(), images[position]);
		Bitmap bitmap2=scaleDownBitmap(bitmap, 80, this.getApplicationContext());
		
		String desc= descriptions[position]; 
	
    	Intent in = new Intent(getApplicationContext(), SingleMenuItemActivity.class);
		in.putExtra("title", title);
		in.putExtra("desc", desc);
		in.putExtra("img", bitmap2 );
		in.putExtra("info", info );
		startActivity(in);
		
       
    }
    
    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context){
    	final float densityMultiplier=context.getResources().getDisplayMetrics().density;
    	
    	int h=(int)(newHeight*densityMultiplier);
    	int w=(int)(h*photo.getWidth()/((double)photo.getHeight()));
    	
    	photo=Bitmap.createScaledBitmap(photo, w, h, true);
    	
    	return photo;
    }
    
    @Override
    public void onBackPressed() 
    {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        super.onBackPressed();
    }
}