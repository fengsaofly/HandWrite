package scu.android.fragment;

import scu.android.activity.GroupListActivity;
import scu.android.activity.NearByPeopleActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.demo.note.R;

public class FindFragment extends Fragment implements OnClickListener {

	public static FindFragment newInstance() {
		FindFragment detail = new FindFragment();

		return detail;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_find, container, false);
		// pd = new ProgressDialog(getActivity());
		// pd.setTitle("正在加载好友动态");
		// pd.setMessage("请稍后");
		// pd.setCancelable(false);
		//
		//
		// find_add_btn = (ImageButton)view.findViewById(R.id.find_add_btn);
		// find_listview = (ListView)view.findViewById(R.id.find_listview);
		// data = new ArrayList<Map<String,Object>>();
		// for(int i=0;i<1;i++){
		// Map<String,Object> map = new HashMap<String, Object>();
		// map.put("find_user", "肖逸飞");
		// map.put("find_content", "勿忘初心，方得始衷，如果可以 ，我愿意从头再来。。。。");
		// map.put("find_icon",
		// getActivity().getResources().getDrawable(R.drawable.home_bottom_onehead));
		// map.put("find_publish_time", TimeRender.getDate());
		// map.put("find_upload_img_flag", 0);
		// // if(i%2==0){
		// // map.put("find_upload_img_flag", 1);
		// // map.put("find_upload_img", null);
		// // }
		// //
		// // else{
		// // map.put("find_upload_img_flag", 0);
		// // }
		//
		// data.add(map);
		// }
		// adapter = new MyFindAdapter(getActivity(), getActivity(), data, 0);
		// find_listview.setAdapter(adapter);
		// find_add_btn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// startActivity(new Intent(getActivity(),MyFindActivity.class));
		// }
		// });
		// handler.sendEmptyMessage(3); //加载好友动态
		//
		//
		/**
		 * @author YouMingyang
		 */
		final View nearbyPeople = view.findViewById(R.id.nearby_people);
		nearbyPeople.setOnClickListener(this);
		final View groupsList = view.findViewById(R.id.groups_list);
		groupsList.setOnClickListener(this);
		return view;
	}

	// Handler handler = new Handler(){
	// public void handleMessage(Message msg){
	// switch(msg.what){
	// case 0: //从服务器获取文本信息成功，如果有图片则取异步下载
	// System.out.println("从服务器获取文本信息成功");
	// if(pd.isShowing())
	// pd.dismiss();
	// // data.clear();
	// data = (List<Map<String, Object>>) msg.obj;
	//
	// System.out.println("data.size():  "+data.size());
	// // adapter.notifyDataSetChanged();
	// List<Integer> positions = new ArrayList<Integer>();
	// List<String> urls = new ArrayList<String>();
	// for(int i=0;i<data.size();i++){
	// if((Integer)data.get(i).get("find_upload_img_flag")==1){ //表示该动态有图片
	// Map<String,Object> map = new HashMap<String, Object>();
	// map.put("find_user", data.get(i).get("find_user"));
	// map.put("find_content", data.get(i).get("find_content"));
	// map.put("find_upload_img_flag", 1);
	// map.put("find_upload_img",
	// drawableToBitmap((Drawable)data.get(i).get("find_icon")));
	// map.put("find_icon", (Drawable)data.get(i).get("find_icon"));
	// map.put("find_publish_time", data.get(i).get("find_publish_time"));
	// positions.add(i);
	// urls.add((String)data.get(i).get("find_upload_img"));
	// data.set(i, map);
	// }
	// }
	// adapter = new MyFindAdapter(getActivity(), getActivity(), data, 0);
	// find_listview.setAdapter(adapter);
	// adapter.notifyDataSetChanged();
	// if(positions.size()!=0){
	// for(String url:urls){
	// System.out.println("downloadUrl: "+url);
	// }
	// DownLoadFindBitmaps dfb = new DownLoadFindBitmaps(getActivity(),
	// getActivity(), handler, urls, positions);
	// dfb.execute();
	// }
	//
	// break;
	// case 1:
	// if(pd.isShowing())
	// pd.dismiss();
	// Toast.makeText(getActivity(), "加载失败", 3).show();
	// break;
	// case 2: //下载好友动态图片成功
	// int position = msg.arg1;
	// System.out.println("podsiiton:  "+position);
	// Bitmap bitmap = (Bitmap)msg.obj;
	//
	// Map<String,Object> map = new HashMap<String, Object>();
	// map.put("find_user", data.get(position).get("find_user"));
	// map.put("find_content", data.get(position).get("find_content"));
	// map.put("find_upload_img_flag", 1);
	// map.put("find_upload_img", bitmap);
	// map.put("find_icon", (Drawable)data.get(position).get("find_icon"));
	// map.put("find_publish_time",
	// data.get(position).get("find_publish_time"));
	// data.set(position, map);
	// // adapter = new MyFindAdapter(getActivity(), getActivity(), data, 0);
	// // find_listview.setAdapter(adapter);
	// adapter.notifyDataSetChanged();
	// break;
	// case 3:
	// GetContanctsFindsThread gcft = new
	// GetContanctsFindsThread(getActivity(),getActivity(),handler);
	// //从服务器获取好友动态信息
	//
	// gcft.start();
	// pd.show();
	// break;
	// default:
	// break;
	// }
	// super.handleMessage(msg);
	// }
	//
	// };

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(

		drawable.getIntrinsicWidth(),

		drawable.getIntrinsicHeight(),

		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

		: Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		// canvas.setBitmap(bitmap);

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		drawable.draw(canvas);

		return bitmap;

	}

	/**
	 * @author YouMingyang
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.nearby_people:
			startActivity(NearByPeopleActivity.class);
			break;
		case R.id.groups_list:
			startActivity(GroupListActivity.class);
			break;
		}
	}

	/**
	 * @author YouMingyang
	 */
	public void startActivity(Class<?> cls) {
		Intent intent = new Intent(getActivity(), cls);
		getActivity().startActivity(intent);
	}

}