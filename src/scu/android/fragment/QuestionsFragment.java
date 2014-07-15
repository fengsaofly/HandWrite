package scu.android.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import scu.android.activity.ReplyQuestionActivity;
import scu.android.application.MyApplication;
import scu.android.db.QuestionDao;
import scu.android.db.ResourceDao;
import scu.android.entity.Question;
import scu.android.entity.Resource;
import scu.android.entity.User;
import scu.android.ui.AudioView;
import scu.android.ui.MGridView;
import scu.android.ui.PhotosAdapter;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 破题主页面：显示题目列表
 * 
 * @author YouMingyang
 * @version 1.0
 */

public class QuestionsFragment extends Fragment implements OnClickListener,
		OnRefreshListener<ListView>, OnItemClickListener,
		OnItemLongClickListener {

	private final String TAG = "QuestionsFragment";
	private static QuestionsFragment questionsFragment;// 单例
	private ArrayList<Question> questions;// 当前问题列表
	private HashMap<Question, User> maps;// 用户问题映射
	private QuestionsAdapter questionsAdapter;// 问题列表适配器
	private PullToRefreshListView refreshView;// 下拉刷新组件
	private boolean isRefreshing;// 刷新中
	private boolean isDownloading;// 下载中
	private View view;
	private View noNetworkConnect;// 无网络连接
	private Button classify;// 分类
	private PopupWindow classifyWindow;// 分类弹窗
	private Context context;
	private User loginUser;// 登录用户
	private BroadcastReceiver receiver;
	// 图片异步加载
	private ImageLoader loader;
	private DisplayImageOptions options;

	public static android.support.v4.app.Fragment newInstance() {
		if (questionsFragment == null) {
			questionsFragment = new QuestionsFragment();
		}
		return questionsFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity().getApplicationContext();
		loginUser = MyApplication.getLoginUser(context);

		view = inflater.inflate(com.demo.note.R.layout.fragment_questions,
				container, false);
		noNetworkConnect = view.findViewById(R.id.no_network_connect);
		noNetworkConnect.setOnClickListener(this);
		refreshView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);

		ListView questionsView = refreshView.getRefreshableView();
		questions = new ArrayList<Question>();
		maps = new HashMap<Question, User>();
		refreshData(0l);
		questionsAdapter = new QuestionsAdapter();
		questionsView.setAdapter(questionsAdapter);
		questionsView.setOnItemClickListener(this);
		questionsView.setOnItemLongClickListener(this);
		refreshView.setMode(Mode.BOTH);
		refreshView.setOnRefreshListener(this);
		classify = (Button) view.findViewById(R.id.sel_classify);
		classify.setOnClickListener(this);

		receiver = new QuestionsBroadcastRecevier();
		context.registerReceiver(receiver,
				new IntentFilter(Constants.QUESTIONS));
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(5))
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageForEmptyUri(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return view;
	}

	/**
	 * 上传或下载之后更新题目列表
	 */
	private class QuestionsBroadcastRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(Constants.QUESTIONS)) {
				final String action = intent.getStringExtra("action");
				if (action.equals("upload")) {
					final String result = intent.getStringExtra("result");
					final long oldId = MyApplication.oldId;
					final long oldResourceId = MyApplication.oldResourceId;
					if (result.equals("success")) {
						final long qId = intent.getLongExtra("qId", 0l);
						final long qResource = intent.getLongExtra(
								"resourceId", 0l);
						// final long createdTime = intent.getLongExtra(
						// "createdTime", System.currentTimeMillis());
						QuestionDao.updateUploadQuestion(context, qId,
								qResource, oldId);
						ResourceDao.updateUploadResource(context, qResource,
								oldResourceId);
						for (Question question : questions) {
							if (question.getqId() == oldId) {
								question.setqState(0);
								break;
							}
						}
					} else if (result.equals("failed")) {
						QuestionDao.updateUploadQuestion(context, oldId);
						for (Question question : questions) {
							if (question.getqId() == oldId) {
								question.setqState(3);
								break;
							}
						}
					}
				} else if (action.equals("download")) {
					@SuppressWarnings("unchecked")
					final ArrayList<Question> mQuestions = (ArrayList<Question>) intent
							.getSerializableExtra("contents");
					if (mQuestions != null && mQuestions.size() > 0) {
						final Question firstQuestion = mQuestions.get(0);
						if (questions.size() > 0
								&& firstQuestion.getCreatedTime().after(
										questions.get(0).getCreatedTime())) {
							questions.addAll(0, mQuestions);
						} else {
							questions.addAll(mQuestions);
						}
						for (Question question : mQuestions) {
							ResourceDao.insertDownloadResource(context,
									question.getResources());
						}
						QuestionDao.insertQuestions(context, mQuestions);
					}
					isDownloading = false;
					refreshView.onRefreshComplete();
					Log.d(TAG, "[onReceive] 刷新完成|size=" + questions.size());

				}
				questionsAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 从手机数据库获取数据，查看更多
	 */
	private class GetDataTask extends
			AsyncTask<Long, Void, ArrayList<Question>> {

		@Override
		protected ArrayList<Question> doInBackground(Long... params) {
			isRefreshing = true;
			final int size = questions.size();
			long startTime = System.currentTimeMillis();
			if (size > 0) {
				final Question lastQuestion = questions.get(size - 1);
				startTime = lastQuestion.getCreatedTime().getTime();
			}
			return QuestionDao.getQuestions(context, startTime, 15);
		}

		@Override
		protected void onPostExecute(ArrayList<Question> result) {
			if (result.size() > 0) {
				questions.addAll(result);
			} else {
				if (!isDownloading) {
					isDownloading = true;
					Log.d(TAG, "[onPostExecute] 刷新中...");
					LinkedList<NameValuePair> params = new LinkedList<NameValuePair>();
					params.add(new BasicNameValuePair("start", "0"));
					MyApplication.downloadAllQuestion(context, params);
				}
			}
			questionsAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
			super.onPostExecute(result);
			isRefreshing = false;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	private class QuestionViewHolder {
		ImageView avatar;
		TextView nickname;
		TextView distance;
		TextView status;
		TextView publishTime;
		TextView title;
		TextView fakeTitle;
		MGridView photosView;
		AudioView audio;
		ImageButton reply;
	}

	/**
	 * 破题列表适配器
	 */
	private class QuestionsAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public QuestionsAdapter() {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			QuestionViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.question_item, null);
				holder = new QuestionViewHolder();
				holder.avatar = (ImageView) convertView
						.findViewById(R.id.avatar);
				holder.nickname = (TextView) convertView
						.findViewById(R.id.nickname);
				holder.distance = (TextView) convertView
						.findViewById(R.id.location);
				holder.status = (TextView) convertView
						.findViewById(R.id.status);
				holder.publishTime = (TextView) convertView
						.findViewById(R.id.publishTime);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.fakeTitle = (TextView) convertView
						.findViewById(R.id.fake_title);
				holder.photosView = (MGridView) convertView
						.findViewById(R.id.photosView);
				holder.audio = (AudioView) convertView.findViewById(R.id.audio);
				holder.reply = (ImageButton) convertView
						.findViewById(R.id.reply);
				convertView.setTag(holder);
			} else {
				holder = (QuestionViewHolder) convertView.getTag();
			}
			final Question mQuestion = (Question) getItem(position);
			User user = loginUser;
			// if (question.getqState() != 2) {
			// user = UserDao.getUserById(context, question.getqUser());
			// } else {
			// user = loginUser;
			// }
			maps.put(mQuestion, user);
			final int width = AppUtils.getDefaultPhotoWidth(getActivity(), 9);
			AppUtils.setViewSize(holder.avatar, width, width);
			loader.displayImage(user.getAvatar(), holder.avatar, options);
			holder.nickname.setText(user.getNickname());
			if (user.getUserId() == loginUser.getUserId())
				holder.distance.setText("附近");
			else
				holder.distance.setText("1.2千米");
			holder.status.setText(mQuestion.getState());
			holder.publishTime.setText(AppUtils.timeToNow(mQuestion
					.getCreatedTime()));
			ArrayList<String> thumbnails = Resource.getSImages(mQuestion
					.getResources());
			ArrayList<String> images = Resource.getLImages(mQuestion
					.getResources());
			if ((thumbnails != null && thumbnails.size() > 0)
					|| (images != null && images.size() > 0)) {
				// photosView.setNumColumns(thumbnails.size() >= 3 ? 3
				// : thumbnails.size());
				PhotosAdapter adapter = new PhotosAdapter(getActivity(),
						thumbnails, images);
				holder.photosView.setAdapter(adapter);
				holder.title.setText(mQuestion.getqTitle());
			} else {
				holder.title.setVisibility(View.GONE);
				holder.photosView.setVisibility(View.GONE);
				holder.fakeTitle.setText(mQuestion.getqTitle());
				holder.fakeTitle.setVisibility(View.VISIBLE);
			}
			final int index = position;
			holder.reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startReply(index);
				}
			});
			String url = Resource.getAudio(mQuestion.getResources());
			if (url != null && url.length() > 0) {
				holder.audio.showDuration(url);
				holder.audio.setVisibility(View.VISIBLE);
			} else {
				// 解决多次出现
				holder.audio.setVisibility(View.GONE);
			}
			return convertView;
		}

	}

	/**
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
		if (question.getqUser() == MyApplication.getLoginUser(context)
				.getUserId()) {
			Dialog alert = new AlertDialog.Builder(getActivity())
					.setTitle("破题").setMessage("确定删除这个问题?")
					.setPositiveButton("确定", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!QuestionDao.deleteQuestion(context,
									question.getqId())) {
								Toast.makeText(
										getActivity().getApplicationContext(),
										"删除失败。。.", Toast.LENGTH_SHORT).show();
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
		case R.id.no_network_connect:
			AppUtils.networkSet(getActivity());
			break;
		}
	}

	public void refreshData(Long param) {
		if (!isRefreshing) {
			new GetDataTask().execute();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Question question = questions.get(position - 1);
		if (question.getqState() != 2)
			startReply(position - 1);
		else
			showToast("正在上传中...", Toast.LENGTH_SHORT);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// final Question question = questions.get(position - 1);
		// System.out.println("longlonsd" + question.getqState());
		// if (question.getqState() != 2)
		deleteQuestion(position - 1);
		return true;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.isHeadherShow()) {
			String label = DateUtils.formatDateTime(getActivity()
					.getApplicationContext(), System.currentTimeMillis(),
					DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			if (!isDownloading) {
				isDownloading = true;
				Log.d(TAG, "[onRefresh] 下拉刷新中...");
				final int size = questions.size();
				LinkedList<NameValuePair> params = new LinkedList<NameValuePair>();
				params.add(new BasicNameValuePair("start", "0"));
				if (size > 0) {
					final long time = questions.get(0).getCreatedTime()
							.getTime();
					params.add(new BasicNameValuePair("floorTime", String
							.valueOf(time / 1000)));
				}
				MyApplication.downloadAllQuestion(context, params);
			}
		} else {
			if (!isDownloading) {
				isDownloading = true;
				questionsAdapter.notifyDataSetChanged();
				Log.d(TAG, "[onRefresh] 上拉刷新中...");
				LinkedList<NameValuePair> params = new LinkedList<NameValuePair>();
				params.add(new BasicNameValuePair("start", "0"));
				final int size = questions.size();
				long topTime = System.currentTimeMillis();
				if (size > 0) {
					topTime = questions.get(size - 1).getCreatedTime()
							.getTime();
				}
				params.add(new BasicNameValuePair("topTime", String
						.valueOf(topTime / 1000)));
				MyApplication.downloadAllQuestion(context, params);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// if (!isDownloading) {
		// isDownloading = true;
		// Log.d(TAG, "[onResume] 刷新中...");
		// MyApplication.downloadAllQuestion(context, 0);
		// }
	}

	public void showToast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

}
