package scu.android.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
//import csdn.shimiso.eim.model.LoginConfig;

/**
 * Activity����֧����ӿ�.
 * 
 * @author shimiso
 */
public interface IActivitySupport {
	/**
	 * 
	 * ��ȡEimApplication.
	 * 
	 * @author shimiso
	 * @update 2012-7-6 ����9:05:51
	 */
//	public abstract EimApplication getEimApplication();

	/**
	 * 
	 * ��ֹ����.
	 * 
	 * @author shimiso
	 * @update 2012-7-6 ����9:05:51
	 */
	public abstract void stopService();

	/**
	 * 
	 * ��������.
	 * 
	 * @author shimiso
	 * @update 2012-7-6 ����9:05:44
	 */
	public abstract void startService();

	/**
	 * 
	 * У������-���û������͵�������,������true.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 ����9:03:56
	 */
	public abstract boolean validateInternet();

	/**
	 * 
	 * У������-���û������ͷ���true.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 ����9:05:15
	 */
	public abstract boolean hasInternetConnected();

	/**
	 * 
	 * �˳�Ӧ��.
	 * 
	 * @author shimiso
	 * @update 2012-7-6 ����9:06:42
	 */
	public abstract void isExit();

	/**
	 * 
	 * �ж�GPS�Ƿ��Ѿ�����.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 ����9:04:07
	 */
	public abstract boolean hasLocationGPS();

	/**
	 * 
	 * �жϻ�վ�Ƿ��Ѿ�����.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 ����9:07:34
	 */
	public abstract boolean hasLocationNetWork();

	/**
	 * 
	 * ����ڴ濨.
	 * 
	 * @author shimiso
	 * @update 2012-7-6 ����9:07:51
	 */
	public abstract void checkMemoryCard();

	/**
	 * 
	 * ��ʾtoast.
	 * 
	 * @param text
	 *            ����
	 * @param longint
	 *            ������ʾ�೤ʱ��
	 * @author shimiso
	 * @update 2012-7-6 ����9:12:02
	 */
	public abstract void showToast(String text, int longint);

	/**
	 * 
	 * ��ʱ����ʾtoast.
	 * 
	 * @param text
	 * @author shimiso
	 * @update 2012-7-6 ����9:12:46
	 */
	public abstract void showToast(String text);

	/**
	 * 
	 * ��ȡ�����.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 ����9:14:38
	 */
	public abstract ProgressDialog getProgressDialog();

	/**
	 * 
	 * ���ص�ǰActivity������.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 ����9:19:54
	 */
	public abstract Context getContext();

	/**
	 * 
	 * ��ȡ��ǰ��¼�û���SharedPreferences����.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 ����9:23:02
	 */
	public SharedPreferences getLoginUserSharedPre();

	/**
	 * 
	 * �����û�����.
	 * 
	 * @param loginConfig
	 * @author shimiso
	 * @update 2012-7-6 ����9:58:31
	 */
//	public void saveLoginConfig(LoginConfig loginConfig);

	/**
	 * 
	 * ��ȡ�û�����.
	 * 
	 * @param loginConfig
	 * @author shimiso
	 * @update 2012-7-6 ����9:59:49
	 */
//	public LoginConfig getLoginConfig();

	/**
	 * 
	 * �û��Ƿ����ߣ���ǰ�����Ƿ������ɹ���
	 * 
	 * @param loginConfig
	 * @author shimiso
	 * @update 2012-7-6 ����9:59:49
	 */
	public boolean getUserOnlineState();

	/**
	 * �����û�����״̬ true ���� false ������
	 * 
	 * @param isOnline
	 */
	public void setUserOnlineState(boolean isOnline);

	/**
	 * 
	 * ����Notification��method.
	 * 
	 * @param iconId
	 *            ͼ��
	 * @param contentTitle
	 *            ����
	 * @param contentText
	 *            ������
	 * @param activity
	 * @author shimiso
	 * @update 2012-5-14 ����12:01:55
	 */
	public void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from);
}
