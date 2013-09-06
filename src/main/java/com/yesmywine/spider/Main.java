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
		System.out.print("请输入开始页码：");
		int startPage = sc.nextInt();
		System.out.print("请输入结束页码：");
		int endPage = sc.nextInt();
		sc.close();
		
		File outFoler = new File(Constants.ROOT_PATH + Constants.DATE_FORMAT.format(new Date())+"_"+startPage+"_"+endPage);
		if(outFoler.exists()){
			logger.error("输出文件夹" + outFoler.getName() + "已经存在，请重命名或者删除。");
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
			pw.print("<?xml version=\"1.0\" encoding=\"utf8\"?>");
			pw.print("<newspapers>");
			for(String link : links){
				link = "http://newspaper.duxiu.com/" + link;
				logger.info("写内容到本地硬盘 "+i+"/" + total + ".");
				Newspaper newspaper = parserHtml.link2Newspaper(link);
				pw.print((newspaper.toString()));
				i++;
			}
			pw.print("</newspapers>");
			pw.close();
		}
		logger.info("任务完毕！");
	}
	
	private static Date getInputDate(){
		Date beginDate = null;
		while(beginDate == null){
			System.out.print("请输入开始时间("+Constants.DATE_FORMAT.format(new Date())+")：");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				String input = reader.readLine();
				if(input.trim().equals("")){
					return new Date();
				}
				beginDate = Constants.DATE_FORMAT.parse(input);
			} catch (Exception e) {
				System.out.println("日期格式错误请重新输入(yyyy.MM.dd)。");
				continue;
			}
		}
		return beginDate;
	}
}
