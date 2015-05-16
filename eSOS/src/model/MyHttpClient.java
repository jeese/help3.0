package model;

import com.lidroid.xutils.HttpUtils;

public class MyHttpClient {
	
	private static final MyHttpClient hc = new MyHttpClient();
	public HttpUtils http;
	
	private MyHttpClient(){
		http = new HttpUtils();
	}
	
	public static MyHttpClient getInstance(){
		return hc;
	}

}
