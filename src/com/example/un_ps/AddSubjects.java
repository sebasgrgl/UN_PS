package com.example.un_ps;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AddSubjects extends Activity {
	
	private static ListView lv;
	private static String[] from = {"SName","SCode","STeacher"};
    private static int[] to = {R.id.SNameL,R.id.SCodeL,R.id.STeacherL};
	private static InternalDB dm;
    private static ArrayList<HashMap<String, Object>> results;
    private static Dialog addDialog;
    private static Button addBT;
    private static Button cancelBT;
    private static Button colorBT;
    private static TextView SName;
    private static TextView SCode;
	private static TextView STeacher;
	private static Context context;
	private static String currentColor;
	private static String colorVet[];
	private static String action = "insert";
	private static String SNameS;
    private static AlertDialog.Builder builder;
    private static AlertDialog alert;

    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.timetable_addsubjects);
	    
	    lv = (ListView) findViewById(R.id.Slist);
	    dm = new InternalDB(this);
	    update();
	    addDialog = new Dialog(this);
	    addDialog.setCancelable(false);
	    addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    addDialog.setContentView(R.layout.timetable_adddialog);
	    SName = (TextView)addDialog.findViewById(R.id.SName);
	    SCode = (TextView)addDialog.findViewById(R.id.SCode);
	    STeacher = (TextView)addDialog.findViewById(R.id.STeacher);
	    colorBT = (Button) addDialog.findViewById(R.id.colorBT);
	    context = this;
	    currentColor = "#0000ff";
	    colorBT.setOnClickListener(new OnClickListener() {
	    	
			public void onClick(View v) {
				
				ColorPickerDialog cpd = new ColorPickerDialog(context,
						new ColorPickerDialog.OnColorChangedListener() {
					
					public void colorChanged(int color) {
						currentColor = Integer.toHexString(color);
						currentColor = currentColor.substring(2, 8);
						currentColor = "#"+currentColor;
						colorBT.setBackgroundColor(color);
					}
				}, Color.parseColor(currentColor));
				cpd.show();
			}
	    	
	    });
	    cancelBT = (Button) addDialog.findViewById(R.id.cancelBT);
	    cancelBT.setOnClickListener(new OnClickListener() {
	    	
			public void onClick(View v) {
				addDialog.cancel();
				SName.setText(null);
				SCode.setText(null);
				STeacher.setText(null);
			}
	    	
	    });
	    addBT = (Button) addDialog.findViewById(R.id.addBT);
	    addBT.setOnClickListener(new OnClickListener() {
	    			
			public void onClick(View v) {
				if(action == "insert") {
					dm.open();
					dm.insertIntoSubjects(SName.getText().toString(),SCode.getText().toString(), STeacher.getText().toString(), currentColor);
					dm.close();
				}else if(action == "update"){
					dm.open();
	        		dm.updateSubjects(SName.getText().toString(),SCode.getText().toString(), STeacher.getText().toString(), currentColor);
	        		dm.close();
				}
				update();
				addDialog.cancel();
				SName.setText(null);
				SCode.setText(null);
				STeacher.setText(null);
			}
	    	
	    });
	    
	    registerForContextMenu(lv);
	    
    	builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this subject? (All lessons contains this " +
        		"subject will be deleted)")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dm.open();
                	   dm.deleteSubjects(SNameS);
                	   dm.close();
                       update();
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        alert = builder.create();
	}
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.add(0, 1, 0, "Edit");
    	menu.add(0, 2, 0, "Delete");
    	menu.setHeaderTitle("Options");	
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	SNameS = results.get(info.position).get("SName").toString();
    	String SCodeS = results.get(info.position).get("SCode").toString();
    	String STeacherS = results.get(info.position).get("STeacher").toString();
    	currentColor = results.get(info.position).get("SColor").toString();
    	
    	switch (item.getItemId()) {
        	case 1:
        		addDialog.show();
				SName.setText(SNameS);
				SCode.setText(SCodeS);
				STeacher.setText(STeacherS);
				colorBT.setBackgroundColor(Color.parseColor(currentColor));
				action = "update";
				SName.setEnabled(false);//set to true
        		SName.setText(SNameS);
        		SCode.setText(SCodeS);
				STeacher.setText(STeacherS);
        		break;
        	case 2:
        		alert.show();
        		break;
    	}
    	update();
    	return true;
    }
	
	public void addSubject(View v) {
		SName.setEnabled(true);
		addDialog.show();
		action = "insert";
		SName.requestFocus();
	}
	
	public class myAdapter extends SimpleAdapter{
        
		String[] colors;
        public myAdapter(Context context, List<? extends Map<String, ?>> data,
        	int resource, String[] from, int[] to, String[] col) {
            	super(context, data, resource, from, to);
                colors = col;
        	}
            @Override
			public View getView(int position, View convertView, ViewGroup parent ) {
            	View view = super.getView(position, convertView, parent);
            	View label = view.findViewById(R.id.colorLabel);
                label.setBackgroundColor(Color.parseColor(colors[position]));
                return view;
            }
    }
	
	private void update() {
		dm.open();
		results = dm.selectSubjects();
		dm.close();
		colorVet = new String[results.size()];
	    for(int i=0;i<results.size();i++) {
	    	HashMap<String, Object> color = 
	    		new HashMap<String, Object>();
	    	color = results.get(i);
	    	colorVet[i] = (String) color.get("SColor");
	    }
	    myAdapter mA = new myAdapter(this, results, R.layout.timetable_paperlist, from, to, colorVet);
		lv.setAdapter(mA);
	}
	
	@Override
	public void onBackPressed () {
		finish();
	}

}
