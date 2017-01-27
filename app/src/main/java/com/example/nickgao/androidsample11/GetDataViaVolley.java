package com.example.nickgao.androidsample11;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.entity.StringEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GetDataViaVolley {

	private static final String TAG = "ValleyTest";
	private  StringEntity mEntity = null;
	private Listener<String> mListener;
	private Context mContext = null;
	
	public static GetDataViaVolley instance = null;
	
	private GetDataViaVolley(Context context) {
		mContext = context;
	}
	
	public static GetDataViaVolley getInstance(Context context) {
		if(instance == null) {
			instance = new GetDataViaVolley(context);
		}
		return instance;
	}
	
	public  void fetchData() {
		String url = "http://2.novelread.sinaapp.com/framework-sae/index.php";
		
//		String body = "";
//		try {
//			mEntity = new StringEntity(body);
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
		
		
		ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {

			}
		};

//		Listener<String> listener = new Response.Listener<String>() {
//
//			@Override
//			public void onResponse(String message) {
//				Log.d(TAG, "message ="+message);
//			}
//		};

		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		StringRequest request = new StringRequest(Request.Method.GET, url, mListener, errorListener) 
		
		{

		    @Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
		    	 try {
		             String jsonString = new String(response.data, "UTF-8");
		             return Response.success(jsonString,
		                     HttpHeaderParser.parseCacheHeaders(response));
		         } catch (UnsupportedEncodingException e) {
		             return Response.error(new ParseError(e));
		         } catch (Exception je) {
		             return Response.error(new ParseError(je));
		         }
			}
		    
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("key", "value");
				return headers;
			}

			@Override
			public byte[] getPostBody() throws AuthFailureError {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				try {
					mEntity.writeTo(outputStream);
				} catch (IOException e) {
					Log.e(TAG, "IOException @ " + getClass().getSimpleName());
				}
				return outputStream.toByteArray();
			}

			@Override
			public String getPostBodyContentType() {
				  return mEntity.getContentType().getValue();
			}

		}
		;

		requestQueue.add(request);
	}
	
	public void setListener(Listener<String> listener) {
		mListener = listener;
	}
}
