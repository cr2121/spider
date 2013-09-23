package com.yesmywine.spider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpTest {
	
	Logger logger = Logger.getLogger(HttpTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 测试请求一个搜索页面
	 * @throws Exception
	 */
	@Test
	public void testHttpClientGet() throws Exception {

	}
	
	/**
	 * htmlparser测试
	 */
	@Test
	public void httpGet() throws Exception {
		Http.getHttp().httpClientGet("http://newspaper.duxiu.com/searchNP?Field=1&channel=searchNP&sw=%C4%D0%D7%B0&edtype=&searchtype=1&view=0");
	}
	
	@Test
	public void link2NewspaperTest() throws Exception {
		OutputStream out = new FileOutputStream("c:/data.txt");
		ParserHtml parserHtml = new ParserHtml();
		Newspaper newspaper = parserHtml.link2Newspaper("http://newspaper.duxiu.com/readbz.jsp?dxid=100003542347&ustext=1&npid=114416511&qwid=-1&d=C0980B42464CB9B866A7053FA8748D38&sw=+%E6%B1%BD%E8%BD%A6&ecode=utf-8");
		PrintWriter pw = new PrintWriter(out);
		pw.append(newspaper.toString());
		pw.close();
	}
	
	@Test
	public void PrintWriterTest() throws FileNotFoundException {
		Newspaper newspaper = new Newspaper();
		newspaper.setTitle("title");
		newspaper.setSubTitle("subtitle");
		newspaper.setFrom("from");
		newspaper.setPostDate("date");
		newspaper.setContent("content");
		
		FileOutputStream out = new FileOutputStream("c:/123.txt");
		PrintWriter printWriter = new PrintWriter(out);
		
		printWriter.print(newspaper.toString());
		printWriter.print(newspaper.toString());
		printWriter.print(newspaper.toString());
		printWriter.print(newspaper.toString());
		printWriter.close();
	}
	
	@Test
	public void getInputDateTest() throws Exception {
		ParserHtml parserHtml = new ParserHtml();
		Newspaper newspaper = parserHtml.link2Newspaper("http://newspaper.duxiu.com/readbz.jsp?dxid=100003713119&ustext=1&npid=114572961&qwid=-1&d=29EC860FD3812DC4F413B5B1BADC62D1&sw=+%E6%B1%BD%E8%BD%A6&ecode=utf-8");
	}

}
