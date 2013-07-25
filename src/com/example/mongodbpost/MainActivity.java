package com.example.mongodbpost;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity {

	Button send, get, send_volley, get_volley;
	EditText name_field, email_field, regid_field;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getApplicationContext();
		setContentView(R.layout.activity_main);
		initWidgets();
		setListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initWidgets() {
		send = (Button) findViewById(R.id.send);
		get = (Button) findViewById(R.id.get);
		send_volley = (Button) findViewById(R.id.send_volley);
		get_volley = (Button) findViewById(R.id.get_volley);
		name_field = (EditText) findViewById(R.id.name_field);
		email_field = (EditText) findViewById(R.id.email_field);
		regid_field = (EditText) findViewById(R.id.regid_field);

	}

	private void setListeners() {
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String response = null;
					String name = name_field.getText().toString();
					String email = email_field.getText().toString();
					String regid = regid_field.getText().toString();
					
					URL url = null;
					url = new URL("http://10.0.2.2:8080/add_user");

					HttpURLConnection http = null;
					http = (HttpURLConnection) url.openConnection();
					http.setDoOutput(true);
					http.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					http.setRequestMethod("POST");
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("name", name));
					params.add(new BasicNameValuePair("email", email));
					params.add(new BasicNameValuePair("regid", regid));
					UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params);
					http.connect();
					OutputStream os = null;
					try {
						os = http.getOutputStream();
						formEntity.writeTo(os);
						
					}catch(Exception e){
						e.printStackTrace();
					}

					String line = "";
					InputStreamReader isr = new InputStreamReader(http
							.getInputStream());
					BufferedReader reader = new BufferedReader(isr);
					StringBuilder sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					// Response from server after login process will be stored
					// in response variable.
					response = sb.toString();
					// You can perform UI operations here
					Toast.makeText(v.getContext(),
							"Message from Server: \n" + response,
							Toast.LENGTH_SHORT).show();
					isr.close();
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		get.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String response = null;

					URL url = null;
					url = new URL("http://10.0.2.2:8080/");

					HttpURLConnection http = null;
					http = (HttpURLConnection) url.openConnection();
					http.setDoOutput(true);
					http.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					http.setRequestMethod("GET");

					// request = new OutputStreamWriter(http.getOutputStream());
					// request.write("");
					// request.flush();
					// request.close();
					String line = "";
					InputStreamReader isr = new InputStreamReader(http
							.getInputStream());
					BufferedReader reader = new BufferedReader(isr);
					StringBuilder sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					// Response from server after login process will be stored
					// in response variable.
					response = sb.toString();
					// You can perform UI operations here
					Toast.makeText(v.getContext(),
							"Message from Server: \n" + response,
							Toast.LENGTH_SHORT).show();
					isr.close();
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		send_volley.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RequestQueue queue = Volley.newRequestQueue(context);

				StringRequest myReq = new StringRequest(Request.Method.POST,
						"http://10.0.2.2:8080/add_user",
						new Response.Listener<String>() {

							@Override
							public void onResponse(String response) {
								// TODO Auto-generated method stub
								Toast.makeText(context,
										"Response => " + response.toString(),
										Toast.LENGTH_SHORT).show();
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								Toast.makeText(context, "Response Error: ",
										Toast.LENGTH_SHORT).show();

							}
						}) {

					protected Map<String, String> getParams()
							throws com.android.volley.AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						String name = name_field.getText().toString();
						String email = email_field.getText().toString();
						String regid = regid_field.getText().toString();
						params.put("name", name);
						params.put("email", email);
						params.put("regid", regid);
						return params;
					};
				};
				queue.add(myReq);
			}
		});

		get_volley.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RequestQueue queue = Volley.newRequestQueue(context);
				String url = "http://10.0.2.2:8080/";

				StringRequest jsObjRequest = new StringRequest(
						Request.Method.GET, url,
						new Response.Listener<String>() {

							@Override
							public void onResponse(String response) {
								// TODO Auto-generated method stub
								Toast.makeText(context,
										"Response => " + response.toString(),
										Toast.LENGTH_SHORT).show();
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								Toast.makeText(context, "Response Error: ",
										Toast.LENGTH_SHORT).show();

							}
						});
				queue.add(jsObjRequest);
				// Toast.makeText(context, "Queue started!",
				// Toast.LENGTH_SHORT).show();
			}
		});

	}

}
