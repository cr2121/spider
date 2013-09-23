package com.yesmywine.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

public class Http {
	public int requestTimes = 0;
	
	private static final Logger logger = Logger.getLogger(Http.class);
	
	private static DefaultHttpClient browser = new DefaultHttpClient();
	private HttpGet httpGet;
	private HttpPost httpPost;
	private HttpResponse response;
	
	// 设计为单例模式
	private Http() {
	}
	private static Http http;
	static {
		// 设置请求头 模拟为Chrome浏览器
		List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.62 Safari/537.36"));
        browser.getParams().setParameter("http.default-headers", headers);
		// 设置代理，隐藏本地ip
		changeProxyHost();
        http = new Http();
		try {
			http.login();
		} catch (Exception e) {
			logger.error("login error, please check proxy server configuration.");
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
		Http.getHttp().requestTimes++;
		if(requestTimes % 15 == 0){
			Thread.sleep(3000);
			browser.getCookieStore().clear();
			http.login();
		}
		String responseText = null;
		boolean success = false; // 表示请求是否成功
		while (true) {
			httpGet = new HttpGet(url);
			response = browser.execute(httpGet);
			int responseCode = response.getStatusLine().getStatusCode();
			switch (responseCode) {
				case 200:
					success = true;
					break;
				case 403:
					logger.warn("please change proxy server.");
					// 设置代理服务器
					httpGet.abort();
					changeProxyHost();
					break;
				default:
					break;
			}
			if(success){
				break;
			}
		}
		responseText = readResponse(response);
		httpGet.abort();
		return responseText;
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
	
	/**
	 *  设置代理服务器，将本地IP彻底隐藏
	 */
	private static void changeProxyHost(){
		if(Store.hosts.size() == 0){
			logger.error("no more proxy servier!");
			System.exit(6);
		}
		String hostAndPortString = Store.hosts.pop();
		String[] hostAndPostArray = hostAndPortString.split(":");
		HttpHost proxy = new HttpHost(hostAndPostArray[0], Integer.valueOf(hostAndPostArray[1]));
		browser.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,	proxy);
		logger.info("current proxy server: " + hostAndPortString);
	}
}
