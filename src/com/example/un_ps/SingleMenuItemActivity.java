package com.example.un_ps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleMenuItemActivity  extends Activity {
	
	// JSON node keys
	private static final String TAG_TITLE = "title";
	private static final String TAG_DESC = "desc";
	private static final String TAG_IMG = "img";
	private static final String TAG_INFO = "info";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item);
        
        // getting intent data
        Intent in = getIntent();
        
        // Get JSON values from previous intent
        String title = in.getStringExtra(TAG_TITLE);
        String info = in.getStringExtra(TAG_INFO);
        String desc = in.getStringExtra(TAG_DESC);
        Bitmap bitmap = (Bitmap) in.getParcelableExtra(TAG_IMG);
        
        // Displaying all values on the screen
        TextView lbltitle = (TextView) findViewById(R.id.title);
        TextView lbldesc = (TextView) findViewById(R.id.desc);
        TextView lblinfo = (TextView) findViewById(R.id.info);
        ImageView lblimg = (ImageView) findViewById(R.id.icon);
        
        lbltitle.setText(title);
        lblinfo.setText(info);
        lbldesc.setText(desc);
        lblimg.setImageBitmap(bitmap);
    }
}

