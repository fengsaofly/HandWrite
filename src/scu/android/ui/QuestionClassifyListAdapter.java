package scu.android.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.demo.note.R;
/**
 *分类列表
 */
public class QuestionClassifyListAdapter extends BaseAdapter {

	private final String TAG=getClass().getName();
	private Context context;
	private String[] classifies;
	private static TextView selectedGrade;
	private static TextView selectedSubject;
	private static TextView selectedNearby;
	private static TextView selectedStatus;
	
	
	public QuestionClassifyListAdapter(Context context){
		this.context=context;
		classifies=context.getResources().getStringArray(R.array.classifies);
	}
	
	@Override
	public int getCount() {
		return classifies.length;
	}

	@Override
	public Object getItem(int position) {
		return classifies[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class QuestionClassifyListHolder{
		TextView header;
		MGridView body;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		QuestionClassifyListHolder holder=null;
		if(convertView==null){
			holder=new QuestionClassifyListHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.question_classify_list_item,null);
			holder.header=(TextView) convertView.findViewById(R.id.header);
			holder.body=(MGridView)convertView.findViewById(R.id.body);
			convertView.setTag(holder);
		}else{
			holder=(QuestionClassifyListHolder) convertView.getTag();
		}
		holder.header.setText((CharSequence) getItem(position));
		holder.body.setAdapter(new ClassifyAdapter(position));
		return convertView;
	}
	
	class ClassifyAdapter extends BaseAdapter{
		private int type;
		private String[] contents;
		
		public ClassifyAdapter(int position){
			this.type=position;
			switch(position){
			case 0:
				contents=context.getResources().getStringArray(R.array.grades);
				break;
			case 1:
				contents=context.getResources().getStringArray(R.array.subjects);
				break;
			case 2:
				contents=context.getResources().getStringArray(R.array.nearby);
				break;
			case 3:
				contents=context.getResources().getStringArray(R.array.status);
				break;
			}
		}
		
		@Override
		public int getCount() {
			return contents.length;
		}

		@Override
		public Object getItem(int position) {
			return contents[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class ClassifyHolder{
			TextView content;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			ClassifyHolder holder=null;
			if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(R.layout.classify_item, null);
				holder=new ClassifyHolder();
				holder.content=(TextView) convertView.findViewById(R.id.content);
				convertView.setTag(holder);
			}else{
				holder=(ClassifyHolder) convertView.getTag();
			}
			holder.content.setText((CharSequence) getItem(position));
			final int index=position;
	
			holder.content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					((TextView)v).setTextColor(Color.BLUE);		
					String grade=null,subject=null,status=null,nearby=null;
					switch(type){
					case 0:
						grade=contents[index];
						if(selectedGrade!=null)
							selectedGrade.setTextColor(Color.BLACK);
						selectedGrade=(TextView) v;
						break;
					case 1:
						subject=contents[index];
						if(selectedSubject!=null)
							selectedSubject.setTextColor(Color.BLACK);
						selectedSubject=(TextView) v;
						break;
					case 2:
						status=contents[index];
						if(selectedStatus!=null)
							selectedStatus.setTextColor(Color.BLACK);
						selectedStatus=(TextView) v;
						break;
					case 3:
						nearby=contents[index];
						if(selectedNearby!=null)
							selectedNearby.setTextColor(Color.BLACK);
						selectedNearby=(TextView) v;
						break;
					}
					Log.d(TAG,contents[index]);
				}
			});
			return convertView;
		}
	}
}
