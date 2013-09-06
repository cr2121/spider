package com.yesmywine.spider;

public class Newspaper {

	private String title;
	private String subTitle;
	private String postDate;
	private String content;
	private String from; // 来源

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getPostDate() {
		return postDate;
	}

	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<newspaper>");
		sb.append("<title><![CDATA[").append(title).append("]]></title>");
		sb.append("<subtitle><![CDATA[").append(subTitle).append("]]></subtitle>");
		sb.append("<postdate><![CDATA[").append(postDate).append("]]></postdate>");
		sb.append("<from><![CDATA[").append(from).append("]]></from>");
		sb.append("<content><![CDATA[").append(content).append("]]></content>");
		sb.append("</newspaper>");
		sb.append("\r\n");
		return sb.toString();
	}
}
