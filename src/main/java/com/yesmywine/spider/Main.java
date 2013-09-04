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
 * 
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
		
		File outFoler = new File(/*"c:/" + */Constants.DATE_FORMAT.format(new Date())+"_"+startPage+"_"+endPage); // !!!!!!!!!!!!!开发时请用绝对路径
		if(outFoler.exists()){
			logger.error("输出文件夹" + outFoler.getName() + "已经存在，请重命名或者删除。");
			System.exit(4);
		}
		outFoler.mkdir();
		
		List<String> urls = new Store().readKeys(/*"c:/" + */"urls.txt"); // !!!!!!!!!!!!!开发时请用绝对路径
		for (String url : urls) {
			url = url + "&Pages=";
			int i = 1;
			int j = 1;
			logger.info("正在解析第"+i+"个搜索页面");
			ParserHtml parserHtml = new ParserHtml();
			List<String> links = parserHtml.extractContentLinkFromSearchPages(url, beginDate, startPage, endPage);
			int total = links.size();
			File outFile = new File(outFoler.getAbsolutePath() + "/"+i+"_1.txt");
			FileOutputStream out = new FileOutputStream(outFile);
			PrintWriter pw = new PrintWriter(out);
			for(String link : links){
				link = "http://newspaper.duxiu.com/" + link;
				logger.info("写内容到本地硬盘 "+i+"/" + total + ".");
				Newspaper newspaper = parserHtml.link2Newspaper(link);
				pw.print((newspaper.toString()));
				if(i%100 == 0){// 一个文件写一百条
					j++;
					pw.close();
					out = new FileOutputStream(outFoler.getAbsolutePath() + "/" + i + "_" + j+ ".txt");
					pw = new PrintWriter(out);
				}
				i++;
				if(i > total){
					pw.close();
				}
			}
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
