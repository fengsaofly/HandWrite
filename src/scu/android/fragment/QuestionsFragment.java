package scu.android.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import scu.android.activity.ReplyQuestionActivity;
import scu.android.activity.ScanPhotosActivity;
import scu.android.db.QuestionDao;
import scu.android.entity.Question;
import scu.android.ui.MGridView;
import scu.android.util.AppUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

//破题页面
public class QuestionsFragment extends Fragment {

	private ArrayList<Question> questions;
	private QuestionsAdapter questionsAdapter;
	private PullToRefreshListView refreshView;// 下拉刷新组件
	private ProgressBar progress;
	private View view;
	private Button classify;
	private PopupWindow classifyWindow;
	// ///////////////////////////////////////////////
	private ImageLoader loader;
	private DisplayImageOptions options;
	private int photoWidth;

	public static android.support.v4.app.Fragment newInstance() {
		QuestionsFragment questionFragment = new QuestionsFragment();
		return questionFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(com.demo.note.R.layout.fragment_questions,
				container, false);
		refreshView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		refreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity()
						.getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				new GetDataTask().execute();
			}
		});

		refreshView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {

					}
				});

		ListView questionsView = refreshView.getRefreshableView();
		questions = new ArrayList<Question>();
		new InitQuestionsTask().execute();
		progress = (ProgressBar) view.findViewById(R.id.progress);

		// ///////////////////////////
		photoWidth = (AppUtils.getWindowMetrics(getActivity()).widthPixels - 4) / 3;
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.question)
				.showImageForEmptyUri(R.drawable.question).cacheInMemory()
				.cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();

		// //////////////////////////

		questionsAdapter = new QuestionsAdapter(getActivity()
				.getApplicationContext(), questions);
		questionsView.setAdapter(questionsAdapter);

		/*
		 * 跳转到回复页面
		 */
		questionsView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startReply(position - 1);
			}
		});
		/*
		 * 长按问题条目的处理
		 */
		questionsView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				deleteQuestion(questions.get(position - 1));
				return true;
			}
		});

		classify = (Button) view.findViewById(R.id.sel_classify);
		classify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				classifyQuestion();
			}
		});
		return view;
	}

	/*
	 * 下拉刷新获取数据
	 */
	private class GetDataTask extends
			AsyncTask<Long, Void, ArrayList<Question>> {

		@Override
		protected ArrayList<Question> doInBackground(Long... params) {
			/*
			 * 此处添加从服务器获取数据
			 */

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Question> questions) {
			/*
			 * 此处添加下拉刷新数据
			 */
			refreshView.onRefreshComplete();
			super.onPostExecute(questions);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	/*
	 * 首次显示时从网上获取数据
	 */
	private class InitQuestionsTask extends
			AsyncTask<Void, Void, ArrayList<Question>> {

		@Override
		protected ArrayList<Question> doInBackground(Void... params) {
			ArrayList<Question> questions = QuestionDao
					.getQuestions(getActivity().getApplicationContext());
			return questions;
		}

		@Override
		protected void onPostExecute(ArrayList<Question> result) {
			for (Question question : result) {
				questions.add(question);
			}
			questionsAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
			progress.setVisibility(View.INVISIBLE);
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);
		}

	}

	/*
	 * 破题列表适配器
	 */
	private class QuestionsAdapter extends BaseAdapter {

		Context context;
		List<Question> questions;

		public QuestionsAdapter(Context context, List<Question> questions) {
			this.context = context;
			this.questions = questions;
		}

		@Override
		public int getCount() {
			return questions.size();
		}

		@Override
		public Object getItem(int position) {
			return questions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.question_item, null);
			}
			final Question question = (Question) getItem(position);
			ImageView avatar = (ImageView) convertView
					.findViewById(R.id.avatar);
			LayoutParams params = avatar.getLayoutParams();
			params.width = params.height = photoWidth / 3;
			avatar.setLayoutParams(params);
			avatar.setBackgroundResource(R.drawable.avatar);
			((TextView) convertView.findViewById(R.id.nickname))
					.setText("测试用户名");
			((TextView) convertView.findViewById(R.id.location))
					.setText("1.2千米");
			((TextView) convertView.findViewById(R.id.status)).setText(question
					.isStatus() ? "已解决" : "未解决");
			((TextView) convertView.findViewById(R.id.publishTime))
					.setText(AppUtils.timeToNow(question.getPublishTime()));
			TextView title = (TextView) convertView.findViewById(R.id.title);
			MGridView photosView = (MGridView) convertView
					.findViewById(R.id.photosView);
			ArrayList<String> images = question.getImages();
			if (images.size() != 0) {
				photosView.setAdapter(new PhotosAdapter(context, images));
				title.setText(question.getTitle());
			} else {
				title.setVisibility(View.GONE);
				photosView.setVisibility(View.GONE);
				TextView fakeTitle = (TextView) convertView
						.findViewById(R.id.fake_title);
				fakeTitle.setText(question.getTitle());
				fakeTitle.setVisibility(View.VISIBLE);
			}
			ImageButton reply = (ImageButton) convertView
					.findViewById(R.id.reply);
			final int index = position;
			reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startReply(index);
				}
			});
			ImageButton audio = (ImageButton) convertView
					.findViewById(R.id.audio);
			final String sAudio = question.getAudio();
			if (sAudio != null && sAudio.length() > 0) {
				audio.setVisibility(View.VISIBLE);
				audio.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						playAudio(sAudio);
					}
				});
			}
			return convertView;
		}
	}

	/*
	 * 问题图片
	 */
	private class PhotosAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<String> bitmaps;

		public PhotosAdapter(Context context, ArrayList<String> bitmaps) {
			this.context = context;
			this.bitmaps = bitmaps;
		}

		public int getCount() {
			return bitmaps.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.thumbnail_item, null);
			}
			String photo = bitmaps.get(position);
			ImageView thumbnail = ((ImageView) convertView
					.findViewById(R.id.thumbnail));
			android.view.ViewGroup.LayoutParams params = thumbnail
					.getLayoutParams();
			params.width = params.height = photoWidth;
			thumbnail.setLayoutParams(params);// 设置图片大小
			loader.displayImage("file:///" + photo, thumbnail, options);
			final int index = position;
			thumbnail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							ScanPhotosActivity.class);
					intent.putStringArrayListExtra("photos", bitmaps);
					intent.putExtra("index", index + 1);
					startActivity(intent);
				}
			});
			return convertView;
		}

	}

	private class ExpandableListAdapter extends BaseExpandableListAdapter {

		private LayoutInflater inflater;
		private ExpandableListView view;
		private String[] groups;
		private String[][] childs;

		public ExpandableListAdapter(Context context, ExpandableListView view,
				String[] groups, String[][] childs) {
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.view = view;
			this.groups = groups;
			this.childs = childs;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childs[groupPosition][childPosition];
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.question_classify_child_item, null);
			}
			((TextView) convertView.findViewById(R.id.child)).setText(getChild(
					groupPosition, childPosition).toString());
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childs[groupPosition].length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		@Override
		public int getGroupCount() {
			return childs.length;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.question_classify_group_item, null);
			}
			((TextView) convertView.findViewById(R.id.group)).setText(getGroup(
					groupPosition).toString());
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			for (int position = 0; position < groups.length; position++) {
				if (groupPosition != position) {
					view.collapseGroup(position);
				}
			}
			super.onGroupExpanded(groupPosition);
		}

	}

	// 播放问题录音文件
	public void playAudio(String audio) {
		MediaPlayer voiceMp = new MediaPlayer();
		try {
			voiceMp.setDataSource(audio);
			voiceMp.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		voiceMp.setLooping(false);
		voiceMp.start();
	}

	// 回复问题
	public void startReply(int position) {
		Intent intent = new Intent(getActivity(), ReplyQuestionActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("question", questions.get(position));
		intent.putExtras(bundle);
		startActivity(intent);
	}

	// 删除问题
	public void deleteQuestion(final Question question) {
		Dialog alert = new AlertDialog.Builder(getActivity()).setTitle("破题")
				.setMessage("确定删除这个问题?")
				.setPositiveButton("确定", new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!QuestionDao.deleteQuestion(getActivity()
								.getApplicationContext(), question.getQuesId())) {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"删除失败。。.", Toast.LENGTH_SHORT).show();
						} else {
							questions.remove(question);
							questionsAdapter.notifyDataSetChanged();
						}
					}
				}).setNegativeButton("取消", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		alert.show();
	}

	// 显示分类
	public void classifyQuestion() {
		final int height = refreshView.getMeasuredHeight();
		View contentView = getActivity().getLayoutInflater().inflate(
				R.layout.question_classify, null);
		ExpandableListView classifyView = (ExpandableListView) contentView
				.findViewById(R.id.classify_listview);
		final String[][] childs = new String[4][];
		final String[] classifies = getActivity().getResources().getStringArray(
				R.array.classifies);
		childs[0] = getActivity().getResources().getStringArray(R.array.grades);
		childs[1] = getActivity().getResources().getStringArray(
				R.array.subjects);
		childs[2] = getActivity().getResources().getStringArray(R.array.nearby);
		childs[3] = getActivity().getResources().getStringArray(R.array.status);
		classifyView.setAdapter(new ExpandableListAdapter(getActivity()
				.getApplicationContext(), classifyView, classifies, childs));
		classifyWindow = new PopupWindow(contentView,
				AppUtils.getWindowMetrics(getActivity()).widthPixels / 3 * 2,
				height, true);
		classifyWindow.setBackgroundDrawable(new ColorDrawable());
		classifyWindow.showAtLocation(classify, Gravity.RIGHT | Gravity.BOTTOM,
				0, 0);
		contentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (classifyWindow.isShowing()) {
					classifyWindow.dismiss();
				}
				return false;
			}
		});
		classifyView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition,
					int childPosition, long id) {
				Toast.makeText(getActivity(),classifies[groupPosition]+":"+childs[groupPosition][childPosition],Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}
}
