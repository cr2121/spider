package com.yesmywine.spider;

import java.text.SimpleDateFormat;

public abstract class Constants {

	public static final String HOME_PAGE = "http://www.duxiu.com/";
	public static final String LOGIN_PAGE = "http://www.duxiu.com/loginhl.jsp";
	public static final String SEARCH_URL = "http://newspaper.duxiu.com/searchNP?Field=1&channel=searchNP&sw=%s&edtype=&searchtype=1&view=0&Pages=%d";
	
	public static final String ROOT_PATH = "c:/spider/"; // 开发环境用绝对路径
//	public static final String ROOT_PATH = "";
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");
}
