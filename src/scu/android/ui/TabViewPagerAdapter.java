package scu.android.ui;

import scu.android.fragment.FindFragment;
import scu.android.fragment.FriendFragment;
import scu.android.fragment.HomeFragment;
import scu.android.fragment.QuestionsFragment;
import scu.android.note.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabViewPagerAdapter extends FragmentPagerAdapter {

	public TabViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		switch (arg0) {
		case ActionBarActivity.TAB_INDEX_TAB_1:
			return HomeFragment.newInstance();
		case ActionBarActivity.TAB_INDEX_TAB_2:
			return QuestionsFragment.newInstance();
		case ActionBarActivity.TAB_INDEX_TAB_3:
			return FriendFragment.newInstance();
		case ActionBarActivity.TAB_INDEX_TAB_4:
			// return AccountFragment.newInstance();
			/**
			 * @author YouMingyang 06/18: 修改为发现页面
			 */
			return FindFragment.newInstance();
		}

		throw new IllegalStateException("No fragment at position " + arg0);
	}

	@Override
	public int getCount() {
		return ActionBarActivity.TAB_COUNT;
	}
}
