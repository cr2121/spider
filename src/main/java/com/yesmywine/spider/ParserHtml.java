package com.yesmywine.spider;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParserHtml {
	
	private Logger logger = Logger.getLogger(ParserHtml.class);
	private SimpleDateFormat sFormat = new SimpleDateFormat("yyyy.MM.dd");
	
	private boolean done; // 标记解析搜索页面是否完成，超出搜索日期时标记为true
		
	/**
	 * 从搜索页解析出日期大于等于beginDate“阅读全文链接”,只读取第一页
	 * @param html
	 * @param beginDate 开始日期
	 * @return
	 * @throws ParserException 
	 */
	private List<String> extractContentLink(String html, Date beginDate) throws Exception {
		List<String> links = new LinkedList<String>();
		// 找到搜索结果的form
		NodeFilter filter = new NodeClassFilter(FormTag.class);
		Parser parser = new Parser(html);
		
		// 获取到查询结果的form
	    NodeList list = parser.extractAllNodesThatMatch(filter);
	    Node node = list.elementAt(1);
	    
	    // 将form节点转换成String重新解析
	    String formHtml = node.toHtml();
	    parser = new Parser(formHtml);
	    // 获取查询form下面所有的div
	    filter = new NodeClassFilter(Div.class); 
	    list = parser.extractAllNodesThatMatch(filter);
	    
	    // 解析出每个div里所有的link
	    for(int i = 0; i < list.size(); i++){
	    	Div div = (Div) list.elementAt(i);
	    	if(div.getAttribute("id") != null){ // div有id的直接跳过具体看html代码
	    		continue;
	    	}
	    	parser = new Parser(div.toHtml());
		    filter = new NodeClassFilter(LinkTag.class); 
		    NodeList linkList = parser.extractAllNodesThatMatch(filter);
		    // 标题
		    LinkTag titleLink = (LinkTag) linkList.elementAt(1);
		    //日期
		    LinkTag dateLink = (LinkTag) linkList.elementAt(3);
		    long beginTime = 0L;
		    long publishTime = 0L;
		    try {
		    	beginTime = sFormat.parse(sFormat.format(beginDate)).getTime(); // 当天零时零分
				publishTime = sFormat.parse(dateLink.getLinkText()).getTime();
			} catch (ParseException e) {
				logger.info("newspaper: " + titleLink.getLinkText() + " skip.");
				continue;
			}
		    if(publishTime < beginTime){ // 超出指定时间范围内不再解析
		    	logger.info("out of date stop search.");
		    	done = true;
		    	return links;
		    }
		    // 阅读全文链接
		    LinkTag contentLink = (LinkTag) linkList.elementAt(2);
		    links.add(contentLink.getLink());
	    	logger.debug(dateLink.getLinkText() + titleLink.getLinkText());
	    }
	    return links;
	}
	
	/**
	 * 从搜索关键字解析出日期大于等于beginDate“阅读全文链接”,当前页读完继续下一页
	 * @param html
	 * @param beginDate
	 * @return
	 * @throws Exception 
	 */
	public List<String> extractContentLinkFromSearchPages(String url, Date beginDate, int startPage, int endPage) throws Exception{
		List<String> links = new LinkedList<String>();
		int pageNo = startPage; // 当前页
		int pageCount = endPage - startPage + 1; // 最大页数
		int i = 1;
		logger.info("search"+Constants.DATE_FORMAT.format(beginDate)+startPage+"-"+endPage+"record.");
		do {
			if(i == 1){
				logger.info("searching page No.1");
			}else{
				logger.info("Searching Page NO."+i+",total"+pageCount+"pages.");
			}
			// 查询关键字搜索页第一页
			String queryUrl = url+pageNo;
			String html = Http.getHttp().httpClientGet(queryUrl);
			// 处理第一次搜索时读取最大页数
			if(i == 1){
				int maxPageCount = getMaxPageCount(html);
				if(endPage > maxPageCount){
					pageCount = maxPageCount - startPage + 1;
				}
			}
			// 找到搜索结果的form
			List<String> thisPageLinks = extractContentLink(html, beginDate);
			links.addAll(thisPageLinks);
			pageNo++;
			i++;
		} while (pageNo <= pageCount && !done);
		logger.info("Complete search, Writing file.");
		return links;
	}
	
	/**
	 * 获取该搜索结果的最大页码
	 * @param html
	 * @return
	 */
	private int getMaxPageCount(String html){
		Document doc = Jsoup.parse(html);
		Elements list = doc.getElementsByTag("script");
		String script = list.get(list.size()-5).data();
		script = script.substring(0, 40);
		script = script.replaceAll(" ", "");
		script = script.substring(31,script.indexOf(";"));
		return Math.min(200, Integer.valueOf(script));
	}
	
	/**
	 * 把该链接里面的新闻转换为Newspaper对象
	 * @param link
	 * @param outputFile
	 * @throws Exception 
	 */
	public Newspaper link2Newspaper(String link) throws Exception{
		// 获取link里的HTML
		String linkHtml = Http.getHttp().httpClientGet(link);
		Document doc = Jsoup.parse(linkHtml);
		// 包含新闻的div
		Element contentDiv = doc.select("div[class=content]").get(0);
		// 标题
		String title = parserTextFromElement(contentDiv, "h1 b");
		// 副标题
		String subTitle = parserTextFromElement(contentDiv, "h2 b");
		// 来源和日期
		String fromAndPostDate = parserTextFromElement(contentDiv, "div[style]");
		fromAndPostDate = fromAndPostDate.replace("&nbsp;", "");
		
		String postDate = fromAndPostDate.substring(fromAndPostDate.length() - 10);
		String from = fromAndPostDate.replace(postDate, "");
		// 内容
		String content = getContent(contentDiv);
		
		Newspaper newspaper = new Newspaper();
		newspaper.setTitle(title);
		newspaper.setContent(content);
		newspaper.setFrom(from);
		newspaper.setPostDate(postDate);
		newspaper.setSubTitle(subTitle);
		return newspaper;
	}
	
	/**
	 * 从html片段找到符合选择规则元素的文本内容
	 * @param html
	 * @param selector
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String parserTextFromElement(Element html, String selector) throws UnsupportedEncodingException{
		String text = "";
		Elements list = html.select(selector);
		if(list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				text += list.get(i).html();
			}
		}
		return new String(text.getBytes(),"utf8");
	}
	
	private String getContent(Element html){
		String text = "";
		Elements list = html.select("p");
		try {
			text = list.toString();
			text = text.replaceAll("<p>(\\s*)</p>", ""); // 去除空p标签
			text = new String(text.getBytes(),"utf8");
		} catch (UnsupportedEncodingException e) {
		}
		return text;
	}
}
