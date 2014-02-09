package com.example.un_ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.viewpagerindicator.TitlePageIndicator;

public class TimetableActivity extends SherlockFragmentActivity{
	private static Intent intent;
	private static AlertDialog.Builder builder;
	private static AlertDialog.Builder builder2;
	private static AlertDialog alert;
	private static AlertDialog alert2;
	private static InternalDB data;
	 private static String action = "insert";
	 private static Dialog addDialog;
		private static ListView currentLW;

		private static ArrayList<HashMap<String, Object>> results;
	    private static SimpleAdapter simpleAdapter;
		private static String[] from = {"SName","SCode","STeacher","HType","HClass","HStart","HEnd"};
	    private static int[] to = {R.id.SName,R.id.SCode,R.id.STeacher,R.id.HType,R.id.HClass,R.id.HStart,R.id.HEnd};
	    static int currentDay;

	    private static EditText HClass;
	    private static EditText HType;
	    private static String[] arraydays;
	    private static ArrayAdapter<String> Sarrayadapter;
	    private static ArrayAdapter<String> arrayadapter;
	    private static String selectedDay;
	    private static TimePickerDialog fromDialog;
	    private static TimePickerDialog toDialog;
	    private static TextView start;
	    private static TextView end;
	    private static String[] arraySubjects;
	    private static Spinner SName;
	    private static Spinner HDay;
	    private static String selectedSubject;
	    private static String colorVet[];

	    private static String OLDSName;
	    private static String OLDHType;
	    private static String OLDHClass;
	    private static int OLDHDay;
	    private static String OLDHStart;
	    private static String OLDHEnd;

	private ViewPager mPager;
	private TitlePageIndicator mIndicator;
	private MainPagerAdapter mAdapter;
	private List<Fragment> mFragments;
	
	private static final String MONDAYFRAGMENT = MondayFragment.class.getName();
	private static final String TUESDAYFRAGMENT = TuesdayFragment.class.getName();
	private static final String WEDNESDAYFRAGMENT = WednesdayFragment.class.getName();
	private static final String THURSDAYFRAGMENT = ThursdayFragment.class.getName();
	private static final String FRIDAYFRAGMENT = FridayFragment.class.getName();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable_main);
		ActionBar Bar = getSupportActionBar();
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6600")));
		getSupportActionBar().setIcon(R.drawable.logo_48);
	    getSupportActionBar().setTitle("Mi horario");
		Bar.setLogo(R.drawable.logo_48);
		//Bar.setTitle("");
		boolean enabled = true;
		Bar.setHomeButtonEnabled(enabled);
		
        //File database=getApplicationContext().getDatabasePath("massey.db");
        // if (database.exists()) {
        	 data = new InternalDB(this);
        // }
        	 
         
		// add fragments
		mFragments = new ArrayList<Fragment>();
		mFragments.add(Fragment.instantiate(this, MONDAYFRAGMENT));
		mFragments.add(Fragment.instantiate(this, TUESDAYFRAGMENT));
		mFragments.add(Fragment.instantiate(this, WEDNESDAYFRAGMENT));
		mFragments.add(Fragment.instantiate(this, THURSDAYFRAGMENT));
		mFragments.add(Fragment.instantiate(this, FRIDAYFRAGMENT));
		
		// adapter
		mAdapter = new MainPagerAdapter(getSupportFragmentManager(), mFragments);
		
		// pager
		mPager = (ViewPager) findViewById(R.id.view_pager);
		mPager.setAdapter(mAdapter);
		
		// indicator
		mIndicator = (TitlePageIndicator) findViewById(R.id.title_indicator);
		mIndicator.setViewPager(mPager);
		
		
		intent = new Intent(getApplicationContext(), AddSubjects.class);//intent = new Intent(getApplicationContext(), AddSubjects.class);
	    
    	builder = new AlertDialog.Builder(this);
        builder.setMessage("Está seguro que quiere eliminar todas las lecciones?")
               .setCancelable(false)
               .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   data.open();
                	   data.deleteAllHours();
                	   data.close();
                      // update();
                	   mAdapter.notifyDataSetChanged();
                	   mAdapter.finishUpdate(mPager);
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        alert = builder.create();
        
    	builder2 = new AlertDialog.Builder(this);
        builder2.setMessage("¿Está seguro que desea borrar esta lección?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   data.open();
                	   data.deleteHour(OLDSName, OLDHDay, OLDHType, OLDHClass, OLDHStart, OLDHEnd);
                	   data.close();
                	   //update();
                	   mAdapter.notifyDataSetChanged();
                	   mAdapter.finishUpdate(mPager);
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        alert2 = builder2.create(); 
        
        //added
        
    	
    	addDialog = new Dialog(this);
	    addDialog.setCancelable(false);
	    action = "insert";
	    addDialog.setTitle("Añadir Clase");
	    addDialog.setContentView(R.layout.timetable_addhour);
	    
	    HType = (EditText) addDialog.findViewById(R.id.typeEdit);
	    HClass = (EditText) addDialog.findViewById(R.id.classroomEdit);
	    arraydays = new String []{"Monday","Tuesday","Wednesday","Thursday","Friday"};
        HDay = (Spinner) addDialog.findViewById(R.id.day);
        arrayadapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item, arraydays);
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        HDay.setAdapter(arrayadapter);
        HDay.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {                    
                 selectedDay = arraydays[position];               
            }

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
        });
        
        fromDialog = new TimePickerDialog(this, fromTimeSetListener, 12, 0, true);
        toDialog = new TimePickerDialog(this, toTimeSetListener, 12, 0, true);
        
        SName = (Spinner) addDialog.findViewById(R.id.SName);
        SName.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {                    
                 selectedSubject = arraySubjects[position];               
            }

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
        });
        
        Button buttonFrom = (Button) addDialog.findViewById(R.id.buttonFrom);
        buttonFrom.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				fromDialog.show();
			}
		});
        
        Button buttonTo = (Button) addDialog.findViewById(R.id.buttonTo);
        buttonTo.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				toDialog.show();
			}
		}); 
	    
        start = (TextView) addDialog.findViewById(R.id.start);
	    end = (TextView) addDialog.findViewById(R.id.end);
	    
	    Button cancel = (Button) addDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
                       
        	public void onClick(View v) {
        		addDialog.cancel();
        		fromDialog.updateTime(12, 0);
        		toDialog.updateTime(12, 0);
        		start.setText("--:--");
        		end.setText("--:--");
        		HType.setText(null);
        		HClass.setText(null);
            }
        	
        });
        
        Button ok = (Button) addDialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
                       
        	public void onClick(View v) {                              
        		int HDay = SDay2IDay(selectedDay);
        		if(action.equals("insert")){
        			data.open();
        			data.insertIntoHours(selectedSubject, HDay, HType.getText().toString(), HClass.getText().toString(), start.getText().toString(), end.getText().toString());
        			data.close();}
        		else if(action.equals("edit")) {
        			data.open();
        			data.updateHours(OLDSName, OLDHType, OLDHClass, OLDHDay, OLDHStart, OLDHEnd, selectedSubject, HDay, HType.getText().toString(), HClass.getText().toString(), start.getText().toString(), end.getText().toString());
        			data.close();}
        		addDialog.cancel();
        		fromDialog.updateTime(12, 0);
        		toDialog.updateTime(12, 0);
        		start.setText("--:--");
        		end.setText("--:--");
        		HType.setText(null);
        		HClass.setText(null);
        		mAdapter.notifyDataSetChanged();
        		//update();
        		mAdapter.finishUpdate(mPager);
            }	
        });
        
	}
	

	//remove
	protected void update() {
    	data.open();
    	results = data.selectAllFromDay(currentDay);
    	data.close();
		colorVet = new String[results.size()];
	    for(int i=0;i<results.size();i++) {
	    	HashMap<String, Object> color = new HashMap<String, Object>();
	    	color = results.get(i);
	    	colorVet[i] = (String) color.get("SColor");
	    }
	    simpleAdapter = new myAdapter(this, results, R.layout.timetable_daylist, from, to, colorVet);
	    currentLW.setAdapter(simpleAdapter); 
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
	
	  private int SDay2IDay(String selectedDay) {
	    	
	    	if(selectedDay.equals("Tuesday"))
	    		return 1;
	    	else if(selectedDay.equals("Wednesday"))
	    		return 2;
	    	else if(selectedDay.equals("Thursday"))
	    		return 3;
	    	else if(selectedDay.equals("Friday"))
	    		return 4;
	    	
	    	return 0;
	    }
	
    private TimePickerDialog.OnTimeSetListener fromTimeSetListener =
        	new TimePickerDialog.OnTimeSetListener() {

    			public void onTimeSet(TimePicker view, int hour,
    					int minute) {
    				
    				StringBuilder sb = new StringBuilder();
    				
    				if(hour<10)
    					sb.append("0");
    				sb.append(hour+":");
    				if(minute<10)
    					sb.append("0");
    				sb.append(minute);
    				
    				start.setText(sb);
    			}

        };
        
        private TimePickerDialog.OnTimeSetListener toTimeSetListener =
        	new TimePickerDialog.OnTimeSetListener() {

    			public void onTimeSet(TimePicker view, int hour,
    					int minute) {
    				
    				StringBuilder sb = new StringBuilder();
    				
    				if(hour<10)
    					sb.append("0");
    				sb.append(hour+":");
    				if(minute<10)
    					sb.append("0");
    				sb.append(minute);
    				
    				end.setText(sb);
    			}
        };
 
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  boolean result = super.onCreateOptionsMenu(menu);
	        menu.add(0, 0, 0, "Administrar Actividades").setIcon(android.R.drawable.ic_menu_manage).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        menu.add(1, 1, 1, "Añadir Clase").setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        menu.add(1, 2, 2, "Borrar todo").setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        return result;
		
	}
	//startActivity(intent);
	 //(com.actionbarsherlock.view.MenuItem item)
	@Override
		public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	        switch (item.getItemId()) {
	            case 0:
	            	startActivityForResult(intent, 0);
	                break;
	            case 1:
	            	action = "insert";
	            	data.open();
	            	ArrayList<HashMap<String, Object>> arrayS = data.selectSubjects();
	            	data.close();
	            	arraySubjects = new String [arrayS.size()];
	                Iterator<HashMap<String, Object>> it = arrayS.iterator();
	                int i = 0;
	                while(it.hasNext()) {
	                	HashMap<String, Object> hm = it.next();
	                	arraySubjects[i] = hm.get("SName").toString();
	                	i++;
	                }
	                Sarrayadapter = new ArrayAdapter<String>(this,
	                		android.R.layout.simple_spinner_item, arraySubjects);
	                Sarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	                SName.setAdapter(Sarrayadapter);
	                addDialog.setTitle("Añadir Clase");
	            	addDialog.show();
	            	
	            	break;
	            case 2:
	            	alert.show();
	            	break;
	            
	        }
	        return false;
	    }
		
	
	    @Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    	super.onCreateContextMenu(menu, v, menuInfo);
	    	menu.add(0, 1, 0, "Editar");
	    	menu.add(0, 2, 0, "Borrar");
	    	menu.setHeaderTitle("Opciones");	
	    }
	    
	    public  boolean onContextItemSelected(android.view.MenuItem item) {
	    	data.open();
	    	int day = currentDay = mPager.getCurrentItem();
	    	System.out.println(day);
	    	results = data.selectAllFromDay(day);
	    	data.close();
	    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    	String course = results.get(info.position).get("SName").toString();
	    	String type = results.get(info.position).get("HType").toString();
	    	String classroom = results.get(info.position).get("HClass").toString();
	    	String startH = results.get(info.position).get("HStart").toString();
	    	String endH = results.get(info.position).get("HEnd").toString();
	    	
	    	
	    	switch (item.getItemId()) {
	        	case 1:
	        		action = "edit";
	        		data.open();
	            	ArrayList<HashMap<String, Object>> arrayS = data.selectSubjects();
	            	data.close();
	            	arraySubjects = new String [arrayS.size()];
	                Iterator<HashMap<String, Object>> it = arrayS.iterator();
	                int i = 0;
	                while(it.hasNext()) {
	                	HashMap<String, Object> hm = it.next();
	                	arraySubjects[i] = hm.get("SName").toString();
	                	i++;
	                }
	                Sarrayadapter = new ArrayAdapter<String>(this,
	                		android.R.layout.simple_spinner_item, arraySubjects);
	                Sarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	                SName.setAdapter(Sarrayadapter);
	        		SName.setSelection(Sarrayadapter.getPosition(course));
	        		String dayS = IDay2SDay(day);
	        		HDay.setSelection(arrayadapter.getPosition(dayS));
	            	CharSequence fhour = startH.subSequence(0, 2);
	            	CharSequence fminutes = startH.subSequence(3, 5);
	            	CharSequence thour = endH.subSequence(0, 2);
	            	CharSequence tminutes = endH.subSequence(3, 5);
	            	int fhourInt;
	            	int fminutesInt;
	            	try{
	                	fhourInt = Integer.parseInt((String)fhour);
	                	fminutesInt = Integer.parseInt((String)fminutes);
	            	} catch(Exception e) {
	            		fhourInt = 12;
	                	fminutesInt = 0;
	            	}
	            	int thourInt;
	            	int tminutesInt;
	            	try{
	                	thourInt = Integer.parseInt((String)thour);
	                	tminutesInt = Integer.parseInt((String)tminutes);
	            	} catch(Exception e) {
	            		thourInt = 12;
	                	tminutesInt = 0;
	            	}
	        		fromDialog.updateTime(fhourInt, fminutesInt);
	        		toDialog.updateTime(thourInt, tminutesInt);
	        		start.setText(startH);
	        		end.setText(endH);
	        		HType.setText(type);
	        		HClass.setText(classroom);
	        		addDialog.setTitle("Editar Clase");
	        		OLDSName = course;
	        		OLDHType = type;
	        		OLDHClass = classroom;
	        		OLDHDay = day;
	        		OLDHStart = startH;
	        		OLDHEnd = endH;
	        		addDialog.show();
	        		break;
	        	case 2:
	        		OLDSName = course;
	        		OLDHType = type;
	        		OLDHClass = classroom;
	        		OLDHDay = day;
	        		OLDHStart = startH;
	        		OLDHEnd = endH;
	        		alert2.show();
	        		break;
	    	}
	    	
	    	
	    	mAdapter.notifyDataSetChanged();
	    	mAdapter.finishUpdate(mPager);
	    	//update();
	    	return true;
	    }
	    
	    private String IDay2SDay(int selectedDay) {
	    	
	    	if(selectedDay == 1)
	    		return "Tuesday";
	    	else if(selectedDay == 2)
	    		return "Wednesday";
	    	else if(selectedDay == 3)
	    		return "Thursday";
	    	else if(selectedDay == 4)
	    		return "Friday";
	    	
	    	return "Monday";
	    }
	    
	    
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			mAdapter.notifyDataSetChanged();
			
			//update();
		}
	    
	   
	    
	
}