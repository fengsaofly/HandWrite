package scu.android.ui;

import scu.android.fragment.AccountFragment;
import scu.android.fragment.FindFragment;
import scu.android.fragment.FriendFragment;
import scu.android.fragment.HomeFragment;
import scu.android.note.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabViewPagerAdapter extends FragmentPagerAdapter {

	public TabViewPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case ActionBarActivity.TAB_INDEX_TAB_1:
//			return new Tab1Fragment();
			return HomeFragment.newInstance(); 

		case ActionBarActivity.TAB_INDEX_TAB_2:
			return FindFragment.newInstance(); 

		case ActionBarActivity.TAB_INDEX_TAB_3:
			return FriendFragment.newInstance(); 

		case ActionBarActivity.TAB_INDEX_TAB_4:
//			return new Tab4Fragment();
			return AccountFragment.newInstance(); 
		}

		throw new IllegalStateException("No fragment at position " + arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ActionBarActivity.TAB_COUNT;
	}
}
