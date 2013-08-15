package com.example.mongodbpost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainService extends Service {
	static final int GET = 0;
	static final int POST = 1;
	static final int GET_SUCCESS = 2;
	static final int GET_FAIL = 3;
	static final int POST_SUCCESS = 4;
	static final int POST_FAIL = 5;
	private static MainService instance = null;

	static final int REG_CLIENT = 100;
	static final int UNREG_CLIENT = 101;
	static Context context;
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();

	public static boolean isServiceRunning() {
		return instance != null;
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REG_CLIENT:
				mClients.add(msg.replyTo);
				Log.d("MONGODB", "Client added!!!");
				break;
			case UNREG_CLIENT:
				mClients.remove(msg.replyTo);
				Log.d("MONGODB", "Client removed!!!");
				break;
			case GET:
				Toast.makeText(context, "Tesutooo!", Toast.LENGTH_SHORT).show();
				volleyGet();
				break;
			case POST:
				String name = ((Entry) msg.obj).name;
				String email = ((Entry) msg.obj).email;
				String regid = ((Entry) msg.obj).regid;
				volleyPost(name, email, regid);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	final Messenger mMessenger = new Messenger(new IncomingHandler());

	@Override
	public void onCreate() {
		instance = this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		context = getApplicationContext();
		Toast.makeText(getApplicationContext(), "binding...",
				Toast.LENGTH_SHORT).show();
		return mMessenger.getBinder();
	}

	private void volleyGet() {
		RequestQueue queue = Volley.newRequestQueue(context);
		String url = "http://10.0.2.2:8080/";

		StringRequest jsObjRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						responseToClientGet(response);
						Log.d("MONGODB", response);
						// TODO Auto-generated method stub
						// Toast.makeText(context,
						// "Response => " + response.toString(),
						// Toast.LENGTH_SHORT).show();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// Toast.makeText(context, "Response Error: ",
						// Toast.LENGTH_SHORT).show();

					}
				});
		queue.add(jsObjRequest);
		Log.d("MONGODB", "Added request");

	}

	private void volleyPost(final String name, final String email,
			final String regid) {
		RequestQueue queue = Volley.newRequestQueue(context);
		StringRequest myReq = new StringRequest(Request.Method.POST,
				"http://10.0.2.2:8080/add_user",
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						// Toast.makeText(context,
						// "Response => " + response.toString(),
						// Toast.LENGTH_SHORT).show();
						responseToClientPost(response);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "Response Error: ",
								Toast.LENGTH_SHORT).show();

					}
				}) {

			@Override
			protected Map<String, String> getParams()
					throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				// String name = name_field.getText().toString();
				// String email = email_field.getText().toString();
				// String regid = regid_field.getText().toString();
				params.put("name", name);
				params.put("email", email);
				params.put("regid", regid);
				return params;
			};
		};
		queue.add(myReq);
	}

	private void responseToClientGet(Object response) {
		Log.d("MONGODB", "-- mClients size is " + mClients.size());
		for (int i = 0; i < mClients.size(); i++) {
			try {
				Message msg = Message.obtain(null, GET_SUCCESS);
				msg.obj = response;
				Log.d("MONGODB", "-- Trying to send to messenger!!!");
				mClients.get(i).send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mClients.remove(i);
			}
		}

	}

	private void responseToClientPost(Object response) {
		Log.d("MONGODB", "-- mClients size is " + mClients.size());
		for (int i = 0; i < mClients.size(); i++) {
			try {
				Message msg = Message.obtain(null, POST_SUCCESS);
				msg.obj = response;
				Log.d("MONGODB", "-- Trying to send to messenger!!!");
				mClients.get(i).send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mClients.remove(i);
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		instance = null;
		Log.d("MONGODB", "SHIT MAN IM DESTROYED");
	}

	// public boolean isMyServiceRunning() {
	// ActivityManager manager =
	// (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
	// for (RunningServiceInfo service : manager
	// .getRunningServices(Integer.MAX_VALUE)) {
	// if (MainService.class.getName().equals(
	// service.service.getClassName())) {
	// return true;
	// }
	// }
	// return false;
	// }

}
