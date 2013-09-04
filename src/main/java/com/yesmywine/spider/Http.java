package com.yesmywine.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

public class Http {
	
	private static final Logger logger = Logger.getLogger(Http.class);
	
	private static HttpClient browser = new DefaultHttpClient();
	private HttpGet httpGet;
	private HttpPost httpPost;
	private HttpResponse response;
	
	// 设计为单例模式
	private Http() {
	}
	private static Http http;
	static {
		// 设置请求头
		List<Header> headers = new ArrayList<Header>();  
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.62 Safari/537.36"));  
        //headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
/*        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
        headers.add(new BasicHeader("Cache-Control", "max-age=0"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("accept-language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4"));*/
        browser.getParams().setParameter("http.default-headers", headers);  
        
        http = new Http();
		try {
			http.login();
		} catch (Exception e) {
			logger.error("登陆网站失败，无法继续！");
			System.exit(3);
		}
	}

	public static Http getHttp() {
		return http;
	}
	
	/**
	 * 读取url，获取响应HTML
	 * @param key
	 * @return 响应文本
	 * @throws Exception 
	 */
	public String httpClientGet(String url) throws Exception {
		Thread.sleep(3000);
		String content = null;
		httpGet = new HttpGet(url);
		response = browser.execute(httpGet);
		content = readResponse(response);
		httpGet.abort();
		return content;
	}
	
	/**
	 * 进网站之后不能直接读取，需要进行一个类型登陆的操作
	 * @throws Exception
	 */
	private void login() throws Exception{
		httpPost = new HttpPost(Constants.LOGIN_PAGE);
		StringEntity reqEntity = new StringEntity("send=true&UserName=shr&PassWord=shr");
		reqEntity.setContentType("application/x-www-form-urlencoded");
		httpPost.setEntity(reqEntity);
		response = browser.execute(httpPost);
		response.getStatusLine().getStatusCode();
		httpPost.abort();
	}
	
	/**
	 * 把http响应转换为String
	 * @param response
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private String readResponse(HttpResponse response) throws IllegalStateException, IOException{
		StringBuilder content = new StringBuilder();
		HttpEntity entity = response.getEntity();
		if (entity != null) {
		    InputStream instream = entity.getContent();
		    try {
		    	String s = null;
		        BufferedReader reader = new BufferedReader(new InputStreamReader(instream,"utf8"));
		        while((s=reader.readLine()) != null){
		        	content.append(s);
		        }
		    } finally {
		        instream.close();
		    }
		}
		return content.toString();
	}
}
