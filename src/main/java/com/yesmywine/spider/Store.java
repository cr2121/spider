package com.yesmywine.spider;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.log4j.Logger;

public class Store {

	private static final Logger logger = Logger.getLogger(Store.class);
	
	public static LinkedList<String> hosts = new LinkedList<String>();
	public static LinkedList<String> urls = new LinkedList<String>();

	static {
		hosts = readTxtFile(Constants.ROOT_PATH + "hosts.txt");
		Collections.shuffle(hosts); // 打乱host先后顺序，避免单个host发送请求过多
		urls = readTxtFile(Constants.ROOT_PATH + "urls.txt");
	}
	/**
	 * 读取文本文件
	 * @param txtPath  key文件的路径
	 * @return
	 */
	private static LinkedList<String> readTxtFile(String txtPath) {
		LinkedList<String> textContent = new LinkedList<String>();
		try {
			FileInputStream in = new FileInputStream(txtPath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "gb2312"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.charAt(0) == '#' || line.equals("")) {
					continue;
				}
				textContent.add(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error("config file"+txtPath+"not exist.");
			System.exit(1);
		} catch (IOException e) {
			logger.error("config file"+txtPath+"not exist.");
			System.exit(2);
		}
		return textContent;
	}
}
