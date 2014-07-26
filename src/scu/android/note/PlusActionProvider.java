package scu.android.note;

import android.content.Context;
import android.view.ActionProvider;
import android.view.SubMenu;
import android.view.View;

import com.demo.note.R;

public class PlusActionProvider extends ActionProvider {

	private Context context;
	
	public PlusActionProvider(Context context) {
		super(context);
		this.context=context;
	}

	@Override
	public View onCreateActionView() {
		return null;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();
		subMenu.add(context.getString(R.string.issue_question)).setIcon(R.drawable.issue_question);
		subMenu.add(context.getString(R.string.add_friend)).setIcon(R.drawable.add_friend);
		subMenu.add(context.getString(R.string.add_group)).setIcon(R.drawable.add_group);
		subMenu.add(context.getString(R.string.create_group)).setIcon(R.drawable.add_group);
	}
	@Override
	public boolean hasSubMenu() {
		return true;
	}
}