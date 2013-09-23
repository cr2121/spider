package com.yesmywine.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * 资源采集程序入口
 * 发布前请修改Constants.ROOT_PATH配置
 * @author tao_wang
 */
public class Main {
	
	private static final Logger logger = Logger.getLogger(Main.class);
		
	public static void main(String[] args) throws Exception {
		
		Date beginDate = getInputDate(); // 所搜该日期之后的所有新闻
		Scanner sc = new Scanner(System.in);
		System.out.print("please input start page no. ");
		int startPage = sc.nextInt();
		System.out.print("please input end page no. ");
		int endPage = sc.nextInt();
		sc.close();
		
		File outFoler = new File(Constants.ROOT_PATH + Constants.DATE_FORMAT.format(new Date())+"_"+startPage+"_"+endPage);
		if(outFoler.exists()){
			logger.error("Output folder" + outFoler.getName() + "already exits.remove or rename it.");
			System.exit(4);
		}
		outFoler.mkdir();
		
		List<String> urls = Store.urls;
		for (String url : urls) {
			url = url + "&Pages=";
			int i = 1;
			ParserHtml parserHtml = new ParserHtml();
			List<String> links = parserHtml.extractContentLinkFromSearchPages(url, beginDate, startPage, endPage);
			int total = links.size();
			File outFile = new File(outFoler.getAbsolutePath() + "/result.txt");
			FileOutputStream out = new FileOutputStream(outFile);
			PrintWriter pw = new PrintWriter(out);
			for(String link : links){
				link = "http://newspaper.duxiu.com/" + link;
				logger.info("write file "+i+"/" + total + ".");
				Newspaper newspaper = parserHtml.link2Newspaper(link);
				pw.print((newspaper.toString()));
				i++;
			}
			pw.close();
		}
		logger.info("Complete.");
	}
	
	private static Date getInputDate(){
		Date beginDate = null;
		while(beginDate == null){
			System.out.print("please input start date("+Constants.DATE_FORMAT.format(new Date())+"): ");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				String input = reader.readLine();
				if(input.trim().equals("")){
					return new Date();
				}
				beginDate = Constants.DATE_FORMAT.parse(input);
			} catch (Exception e) {
				System.out.println("date format error (yyyy.MM.dd)。");
				continue;
			}
		}
		return beginDate;
	}
}
