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
		return "T\r\n" + title + "\r\nT2\r\n" + subTitle + "\r\nD\r\n" + postDate
				+ "\r\nF\r\n" + from + "\r\nC\r\n" + content +"\r\n";
	}
}
