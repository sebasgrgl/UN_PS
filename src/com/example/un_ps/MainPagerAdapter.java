package com.example.un_ps;

import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> mFragments;
	private String[] titles = new String[] {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes"};
	private int mCount = titles.length;

	
	public MainPagerAdapter(FragmentManager fm, List<Fragment> f) {
		super(fm);
		mFragments = f;
	}

	@Override
	public Fragment getItem(int position) {
		return mFragments.get(position);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
	
	@Override
	public int getItemPosition(Object object) {//when you call notifyDataSetChanged(), the view pager will remove all views and reload them all. As so the reload effect is obtained.
	    return POSITION_NONE;
	}
}
