package scu.android.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import scu.android.activity.QuestionDetailsActivity;
import scu.android.application.MyApplication;
import scu.android.dao.Question;
import scu.android.dao.Resource;
import scu.android.dao.User;
import scu.android.db.DBTools;
import scu.android.ui.CircularImage;
import scu.android.ui.MAudioView;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 破题主页面：显示题目列表
 * 
 * @author YouMingyang
 * @version 1.0
 */

public class QuestionsFragment extends Fragment implements OnClickListener, OnRefreshListener<ListView>, OnItemClickListener, OnItemLongClickListener {

	private final String TAG = getClass().getName();

	private ArrayList<Question> questions;// 当前问题列表
	private HashMap<Question, User> maps;// 用户问题映射
	private QuestionsAdapter questionsAdapter;// 问题列表适配器
	private PullToRefreshListView refreshView;// 下拉刷新组件
	private boolean isRefreshing;// 刷新中
	private boolean isDownloading;// 下载中
	private View view;
	private View noNetworkConnect;// 无网络连接
	private Context context;
	private User loginUser;// 登录用户
	private boolean isNetworkConnected;
	private BroadcastReceiver receiver;
	// 图片异步加载
	private ImageLoader loader;
	private DisplayImageOptions options;

	public static android.support.v4.app.Fragment newInstance() {
		return new QuestionsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity().getApplicationContext();
		loginUser = MyApplication.getCurrentUser(context);

		view = inflater.inflate(com.demo.note.R.layout.fragment_questions, container, false);
		noNetworkConnect = view.findViewById(R.id.no_network_connect);
		noNetworkConnect.setOnClickListener(this);
		refreshView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);

		ListView questionsView = refreshView.getRefreshableView();
		questions = new ArrayList<Question>();
		maps = new HashMap<Question, User>();
		questionsAdapter = new QuestionsAdapter();
		questionsView.setAdapter(questionsAdapter);
		questionsView.setOnItemClickListener(this);
		questionsView.setOnItemLongClickListener(this);
		refreshView.setMode(Mode.BOTH);
		refreshView.setEmptyView(view.findViewById(R.id.empty_view));
		refreshView.setOnRefreshListener(this);

		receiver = new QuestionsBroadcastRecevier();
		context.registerReceiver(receiver, new IntentFilter(Constants.QUESTIONS));
		initImageLoader();
		return view;
	}

	public void initImageLoader() {
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 上传或下载之后更新题目列表
	 */
	private class QuestionsBroadcastRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.QUESTIONS)) {
				final String action = intent.getStringExtra("action");
				if (action.equals("upload") && MyApplication.isUploading) {
					final String result = intent.getStringExtra("result");
					if (result.equals("success")) {
						showToast("上传成功");
						long q_id = intent.getLongExtra("q_id", 0);
						long created_time = intent.getLongExtra("created_time", 0);
						long q_resource = intent.getLongExtra("q_resource", 0);
						updateQuestion(q_id, created_time, q_resource);
					} else if (result.equals("failed")) {
						showToast("上传失败");
						updateQuestion(0, 0, 0);
					}
					MyApplication.isUploading = false;
				} else if (action.equals("download") && isDownloading) {
					@SuppressWarnings("unchecked")
					ArrayList<Question> mQuestions = (ArrayList<Question>) intent.getSerializableExtra("questions");
					if (mQuestions != null && mQuestions.size() > 0) {
						final Question firstQuestion = mQuestions.get(0);
						if (questions.size() > 0 && firstQuestion.getCreated_time() > questions.get(0).getCreated_time()) {
							questions.addAll(0, mQuestions);
						} else {
							questions.addAll(mQuestions);
						}
						showToast("下载完毕");
					} else {
						showToast("已获取最新数据");
					}
					questionsAdapter.notifyDataSetChanged();
					isDownloading = false;
				} else if (action.equals("special")) {
					if (isDownloading) {
						showToast("正在下载中,稍后再试...");
					} else {
						loadData(Constants.DOWN);
					}
				}
			}
		}
	}

	/**
	 * 获取数据，查看更多
	 */
	private class GetDataTask extends AsyncTask<Integer, Void, List<Question>> {

		@Override
		protected List<Question> doInBackground(Integer... params) {
			isRefreshing = true;
			final int action = params[0];
			final DBTools mDBTools = DBTools.getInstance(context);
			List<Question> mQuestions = new ArrayList<Question>();
			final int size = questions.size();
			if (size > 0) {// 获取更多数据
				long floorTime = questions.get(0).getCreated_time();
				long topTime = questions.get(size - 1).getCreated_time();
				switch (action) {
				case Constants.DOWN:// 下拉
					mQuestions = mDBTools.loadQuestions(floorTime);
					if (mQuestions.size() == 0) {
						if (isNetworkConnected) {
							isDownloading = true;
							LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
							mParams.add(new BasicNameValuePair("start", "0"));
							mParams.add(new BasicNameValuePair("floorTime", String.valueOf(floorTime)));
							MyApplication.downloadQuestion(context, mParams);
						}
					}
					break;
				case Constants.UP:// 上拉
					mQuestions = mDBTools.loadQuestions(topTime, 5);// 本地数据
					if (mQuestions.size() == 0) {
						if (isNetworkConnected) {
							isDownloading = true;
							LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
							mParams.add(new BasicNameValuePair("start", "0"));
							mParams.add(new BasicNameValuePair("topTime", String.valueOf(topTime)));
							MyApplication.downloadQuestion(context, mParams);
						}
					}
					break;
				}
			} else {// 首次加载数据
				mQuestions = DBTools.getInstance(context).loadQuestions(5);
				if (mQuestions.size() == 0) {
					if (isNetworkConnected) {
						isDownloading = true;
						LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
						mParams.add(new BasicNameValuePair("start", "0"));
						MyApplication.downloadQuestion(context, mParams);
					}
				}
			}
			return mQuestions;
		}

		@Override
		protected void onPostExecute(List<Question> result) {
			if (AppUtils.isNetworkConnect(getActivity())) {
				noNetworkConnect.setVisibility(View.GONE);
			} else {
				noNetworkConnect.setVisibility(View.VISIBLE);
			}
			if (result.size() > 0) {
				final Question firstQuestion = result.get(0);
				if (questions.size() > 0 && firstQuestion.getCreated_time() > questions.get(0).getCreated_time()) {
					questions.addAll(0, result);
				} else {
					questions.addAll(result);
				}
			} else {
				if (!isDownloading)
					showToast("没有加载到数据");
			}
			isRefreshing = false;
			questionsAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
		}
	}

	private class QuestionViewHolder {
		CircularImage avatar;
		TextView distance;
		TextView nickname;
		TextView publishTime;
		TextView postAlertInfo;
		ImageView img;
		TextView desc;
		MAudioView audio;
		TextView comment;

		// TextView fakeTitle;
	}

	/**
	 * 破题列表适配器
	 */
	private class QuestionsAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public QuestionsAdapter() {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			// if (convertView == null) {
			convertView = inflater.inflate(R.layout.question_item, null);
			holder = new QuestionViewHolder();
			holder.avatar = (CircularImage) convertView.findViewById(R.id.avatar_val);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.publishTime = (TextView) convertView.findViewById(R.id.publish_time);
			holder.postAlertInfo = (TextView) convertView.findViewById(R.id.post_alert_info);
			holder.img = (ImageView) convertView.findViewById(R.id.find_img);
			holder.desc = (TextView) convertView.findViewById(R.id.find_desc);
			holder.audio = (MAudioView) convertView.findViewById(R.id.audio);
			holder.comment = (TextView) convertView.findViewById(R.id.find_comment);
			// holder.fakeTitle = (TextView)
			// convertView.findViewById(R.id.fake_title);
			convertView.setTag(holder);
			// } else {
			// holder = (QuestionViewHolder) convertView.getTag();
			// }
			final Question mQuestion = (Question) getItem(position);
			DBTools mDBTools = DBTools.getInstance(context);
			User user = loginUser;
			maps.put(mQuestion, user);
			loader.displayImage(user.getUser_avatar(), holder.avatar, options);
			holder.distance.setText("附近");
			holder.nickname.setText(user.getUser_nickname());
			holder.publishTime.setText(AppUtils.timeToNow(mQuestion.getCreated_time()));
			holder.postAlertInfo.setText(DBTools.getState(mQuestion.getQ_state()));
			holder.desc.setText(mQuestion.getQ_text_content());
			List<Resource> mResources = mDBTools.loadResources(mQuestion.getQ_resource());
			// List<String> thumbnails = DBTools.getSImages(mResources);
			// List<String> images = DBTools.getLImages(mResources);
			String thumbnail = DBTools.getSImage(mResources);
			String img = DBTools.getLImage(mResources);
			if (thumbnail != null) {
				// photosView.setNumColumns(thumbnails.size() >= 3 ? 3
				// : thumbnails.size());
				// PhotosAdapter adapter = new
				// PhotosAdapter(getActivity(),thumbnails, images);
				// holder.photosView.setAdapter(adapter);
				AppUtils.disImg(context, loader, options, holder.img, thumbnail, img);
			} else {
				holder.img.setVisibility(View.GONE);
				// holder.photosView.setVisibility(View.GONE);
				// holder.fakeTitle.setText(mQuestion.getQ_title());
				// holder.fakeTitle.setVisibility(View.VISIBLE);
			}
			// final int index = position;
			// holder.reply.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// startReply(index);
			// }
			// });
			String url = DBTools.getAudio(mResources);
			if (url != null && url.length() > 0) {
				holder.audio.setAudioUrl(url);
				holder.audio.setVisibility(View.VISIBLE);
			} else {
				holder.audio.setVisibility(View.GONE);
			}
			holder.comment.setText("0");
			return convertView;
		}

	}

	// 回复问题
	public void startReply(int position) {
//		Intent intent = new Intent(getActivity(), ReplyQuestionActivity.class);
		Intent intent = new Intent(getActivity(), QuestionDetailsActivity.class);
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
		Dialog alert = new AlertDialog.Builder(getActivity()).setTitle("破题").setMessage("确定删除这个问题?").setPositiveButton("确定", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DBTools.getInstance(context).deleteQuestion(question);
				questions.remove(index);
				questionsAdapter.notifyDataSetChanged();
			}
		}).setNegativeButton("取消", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create();
		alert.show();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.no_network_connect:
			AppUtils.networkSet(getActivity());
			break;
		}
	}

	public void loadData(int action) {
		isNetworkConnected = AppUtils.isNetworkConnect(context);
		if (!isRefreshing && !isDownloading) {
			new GetDataTask().execute(action);
		} else {
			showToast("刷新中...");
			refreshView.onRefreshComplete();
		}
	}

	public void showToast(String text, int delay) {
		Toast.makeText(context, text, delay).show();
	}

	public void showToast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public ProgressDialog getProgressDialog(String message) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(message);
		return dialog;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Question question = questions.get(position - 1);
		if (question.getQ_state() != 2)
			startReply(position - 1);
		else
			showToast("正在上传中...", Toast.LENGTH_SHORT);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		deleteQuestion(position - 1);
		return true;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.isHeadherShow()) {
			String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
					| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			if (!MyApplication.isUploading) {
				loadData(Constants.DOWN);
			} else {
				showToast("玩命上传中,稍后刷新...");
				this.refreshView.onRefreshComplete();
			}
		} else {
			loadData(Constants.UP);
		}
	}

	public void updateQuestion(long q_id, long created_time, long q_resource) {
		if (q_id != 0) {
			for (Question mQuestion : questions) {
				if (mQuestion.getQ_state() == 2) {
					mQuestion.setQ_state((short) 0);
					mQuestion.setQ_id(q_id);
					mQuestion.setCreated_time(created_time);
					mQuestion.setQ_resource(q_resource);
					break;
				}
			}
		} else {
			for (Question mQuestion : questions) {
				if (mQuestion.getQ_state() == 2) {
					mQuestion.setQ_state((short) 3);
					break;
				}
			}
		}
		questionsAdapter.notifyDataSetChanged();
	}

	// public void updateLog(){
	// SharedPreferences
	// questions=getActivity().getSharedPreferences("note",getActivity().MODE_PRIVATE);
	// Editor editor=questions.edit();
	// editor.putLong("time",System.currentTimeMillis());
	// editor.commit();
	// }
	//
	// public boolean canUpdate(){
	// SharedPreferences
	// questions=getActivity().getSharedPreferences("note",getActivity().MODE_PRIVATE);
	// long now=System.currentTimeMillis();
	// long lastTime=questions.getLong("time",now);
	// if(now-lastTime>5000){
	// return true;
	// }
	// return false;
	// }

	@Override
	public void onResume() {
		if ((questions == null || questions.size() == 0)) {
			loadData(Constants.DOWN);
		} else {
			questionsAdapter.notifyDataSetChanged();
			refreshView.invalidate();
		}
		Log.d(TAG, "OnResume");
		super.onResume();
	}

}
