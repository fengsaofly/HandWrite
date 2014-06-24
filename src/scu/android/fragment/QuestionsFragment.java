package scu.android.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scu.android.activity.ReplyQuestionActivity;
import scu.android.application.MyApplication;
import scu.android.db.QuestionDao;
import scu.android.db.ReplyDao;
import scu.android.db.UserDao;
import scu.android.entity.Question;
import scu.android.entity.User;
import scu.android.ui.MGridView;
import scu.android.ui.PhotosAdapter;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 破题主页面
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class QuestionsFragment extends Fragment implements OnClickListener,
		OnRefreshListener<ListView>, OnLastItemVisibleListener {

	private ArrayList<Question> questions;
	private HashMap<Question, User> maps;
	private QuestionsAdapter questionsAdapter;
	private PullToRefreshListView refreshView;// 下拉刷新组件
	private boolean isRefreshing;
	private boolean isAllDownload;
	private View view;
	private View noNetworkConnect;
	private ProgressDialog progressDialog;
	private Button classify;
	private TextView disMore;
	private PopupWindow classifyWindow;
	private Context context;

	private ImageLoader loader;
	private DisplayImageOptions options;

	public static android.support.v4.app.Fragment newInstance() {
		QuestionsFragment questionFragment = new QuestionsFragment();
		return questionFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity().getApplicationContext();

		view = inflater.inflate(com.demo.note.R.layout.fragment_questions,
				container, false);
		noNetworkConnect = view.findViewById(R.id.no_network_connect);
		noNetworkConnect.setOnClickListener(this);
		refreshView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		ListView questionsView = refreshView.getRefreshableView();
		questions = new ArrayList<Question>();
		maps = new HashMap<Question, User>();
		isAllDownload = false;
		progressDialog = getProgressDialog("正在加载破题列表");

		refreshData(0L);
		questionsAdapter = new QuestionsAdapter(context, questions);
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
				deleteQuestion(position - 1);
				return true;
			}
		});
		// refreshView.setMode(Mode.BOTH);
		refreshView.setOnRefreshListener(this);
		refreshView.setOnLastItemVisibleListener(this);
		classify = (Button) view.findViewById(R.id.sel_classify);
		classify.setOnClickListener(this);
		disMore = (TextView) view.findViewById(R.id.dis_more);
		disMore.setOnClickListener(this);

		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(5))
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageForEmptyUri(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		MyApplication.getLoginUser(context);
		return view;
	}

	/*
	 * 下拉刷新获取数据
	 */
	private class GetDataTask extends
			AsyncTask<Long, Void, ArrayList<Question>> {
		int resultCode = -1;;// 错误类型

		/**
		 * params[0],数据来源:0,net;1,local
		 * parems[1],目的:0,首次获取数据;1,(获取当前列表表首问题之后的数据);2:加载更多(获取当前列表表尾问题之前的数据);
		 */
		@Override
		protected ArrayList<Question> doInBackground(Long... params) {
			isRefreshing = true;
			long srcType = params[0];
			long object = params[1];
			ArrayList<Question> mLocalQuestions = null;
			switch ((int) srcType) {
			case Constants.SRC_NET:
				long localQuesNum = QuestionDao.getQuesNum(context);
				long localLens = 10L;
				switch ((int) object) {
				case Constants.DATA_INIT:
					if (localQuesNum > 0) {
						if (localQuesNum <= localLens) {
							localLens = localQuesNum;
							isAllDownload = true;
						}
						mLocalQuestions = QuestionDao.getQuestions(context,
								(long) questions.size(), localLens);
					} else {
						resultCode = -1;
					}
					break;
				case Constants.DATA_AFTER:
//					resultCode = -1;
					MyApplication.downloadAllQuestion(null);
					break;

				}
				break;
			case Constants.SRC_LOCAL:
				localQuesNum = QuestionDao.getQuesNum(context);
				localLens = 10L;
				mLocalQuestions = null;
				resultCode = -1;
				switch ((int) object) {
				case Constants.DATA_INIT:
					if (localQuesNum > 0) {
						if (localQuesNum <= localLens) {
							localLens = localQuesNum;
							isAllDownload = true;
						}
						mLocalQuestions = QuestionDao.getQuestions(context,
								(long) questions.size(), localLens);
					} else {
						resultCode = Constants.ERR_LOCAL_NO_DATA;
					}
					break;
				case Constants.DATA_AFTER:
					resultCode = Constants.ERR_LOCAL_NO_NEW_DATA;
					break;
				case Constants.DATA_BEFORE:
					long lave = localQuesNum - (questions.size() + localLens);
					if (lave <= 0) {
						localLens = localQuesNum - questions.size();
						isAllDownload = true;
					}
					mLocalQuestions = QuestionDao.getQuestions(context,
							(long) questions.size(), localLens);
					break;
				}
				break;
			}
			return mLocalQuestions;
		}

		@Override
		protected void onPostExecute(ArrayList<Question> result) {
			progressDialog.dismiss();
			if (result != null) {
				questions.addAll(result);
				onLastItemVisible();
			} else {
				switch (resultCode) {
				case -1:
					showToast("暂无数据...", Toast.LENGTH_SHORT);
					break;
				case Constants.ERR_LOCAL_NO_DATA:
					showToast("本地无数据,打开网络连接加载数据...", Toast.LENGTH_SHORT);
					break;
				case Constants.ERR_LOCAL_NO_NEW_DATA:
					showToast("请打开网络连接,加载新数据...", Toast.LENGTH_SHORT);
					break;
				}
			}
			questionsAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
			isRefreshing = false;
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
			final User user = UserDao
					.getUserById(context, question.getUserId());
			maps.put(question, user);
			ImageView avatar = (ImageView) convertView
					.findViewById(R.id.avatar);
			final int width = AppUtils.getDefaultPhotoWidth(getActivity(), 9);
			AppUtils.setViewSize(avatar, width, width);
			loader.displayImage(user.getAvatar(), avatar, options);
			((TextView) convertView.findViewById(R.id.nickname)).setText(user
					.getNickname());
			final TextView distance = (TextView) convertView
					.findViewById(R.id.location);
			if (user.getUserId() == MyApplication.getLoginUser(context)
					.getUserId())
				distance.setText("附近");
			else
				distance.setText("1.2千米");
			// final View
			// uploadStatus=convertView.findViewById(R.id.upload_status);
			// uploadStatus.setVisibility(View.GONE);
			((TextView) convertView.findViewById(R.id.status)).setText(question
					.isStatus() ? "已解决" : "未解决");
			((TextView) convertView.findViewById(R.id.publishTime))
					.setText(AppUtils.timeToNow(question.getPublishTime()));
			TextView title = (TextView) convertView.findViewById(R.id.title);
			MGridView photosView = (MGridView) convertView
					.findViewById(R.id.photosView);
			ArrayList<String> images = question.getImages();
			if (images.size() != 0) {
				final String imgSaveDir = "";
				photosView.setAdapter(new PhotosAdapter(getActivity(), images,
						imgSaveDir));
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
	 * 分类列表
	 */
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
		final Question question = (Question) questions.get(position);
		final User user = maps.get(question);
		bundle.putSerializable("question", question);
		bundle.putSerializable("user", user);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	// 删除问题
	public void deleteQuestion(final int index) {
		final Question question = (Question) questions.get(index);
		if (question.getUserId() == MyApplication.getLoginUser(context)
				.getUserId()) {
			Dialog alert = new AlertDialog.Builder(getActivity())
					.setTitle("破题").setMessage("确定删除这个问题?")
					.setPositiveButton("确定", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!QuestionDao.deleteQuestion(context,
									question.getQuesId(), 0)) {
								Toast.makeText(
										getActivity().getApplicationContext(),
										"删除失败。。.", Toast.LENGTH_SHORT).show();
								System.out.println(ReplyDao
										.getTotalReplyNum(context));
							} else {
								questions.remove(index);
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
	}

	// 显示分类
	public void disQuestionClassify() {
		final int height = refreshView.getMeasuredHeight();
		View contentView = getActivity().getLayoutInflater().inflate(
				R.layout.question_classify, null);
		ExpandableListView classifyView = (ExpandableListView) contentView
				.findViewById(R.id.classify_listview);
		final String[][] childs = new String[4][];
		final String[] classifies = getActivity().getResources()
				.getStringArray(R.array.classifies);
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
			public boolean onChildClick(ExpandableListView parent, View view,
					int groupPosition, int childPosition, long id) {
				Toast.makeText(
						getActivity(),
						classifies[groupPosition] + ":"
								+ childs[groupPosition][childPosition],
						Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.sel_classify:
			disQuestionClassify();
			break;
		case R.id.dis_more:
			refreshData(2L);
			break;
		case R.id.no_network_connect:
			AppUtils.networkSet(getActivity());
			break;
		}
	}

	public void refreshData(Long param) {
		Long[] params = new Long[2];
		params[1] = param;
		progressDialog.show();

		if (AppUtils.isNetworkConnect(getActivity())) {
			noNetworkConnect.setVisibility(View.GONE);
			params[0] = 0L;
			new GetDataTask().execute(params);
		} else {
			noNetworkConnect.setVisibility(View.VISIBLE);
			params[0] = 1L;
			if (questions.size() == 0)
				params[1] = 0L;
			new GetDataTask().execute(params);
		}
	}

	public void showToast(String text, int delay) {
		Toast.makeText(context, text, delay).show();
	}

	public ProgressDialog getProgressDialog(String message) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(message);
		return dialog;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity()
				.getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		if (!isRefreshing) {
			refreshData(1L);
		}
	}

	@Override
	public void onLastItemVisible() {
		if (isAllDownload) {
			disMore.setVisibility(View.GONE);
			Toast.makeText(context, "数据加载完毕...", Toast.LENGTH_SHORT).show();
		} else {
			disMore.setVisibility(View.VISIBLE);
		}
	}
}
