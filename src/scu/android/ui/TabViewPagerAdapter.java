package scu.android.ui;

import scu.android.fragment.FindFragment;
import scu.android.fragment.FriendFragment;
import scu.android.fragment.HomeFragment;
import scu.android.fragment.QuestionsFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabViewPagerAdapter extends FragmentStatePagerAdapter {
	public final String[] titles = { "消息", "破题", "通讯录", "发现" };

	public TabViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return HomeFragment.newInstance();
		case 1:
			return QuestionsFragment.newInstance();
		case 2:
			return FriendFragment.newInstance();
		case 3:
			return FindFragment.newInstance();
		default:
			return null;
		}
		
	}

}
