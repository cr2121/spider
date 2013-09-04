package com.yesmywine.spider;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class Store {

	Logger logger = Logger.getLogger(Store.class);

	/**
	 * 从文本文件中读取查询关键字，每个关键字用换行隔开
	 * 
	 * @param txtPath
	 *            key文件的路径
	 * @return
	 */
	public List<String> readKeys(String txtPath) {
		List<String> keys = new LinkedList<String>();
		try {
			FileInputStream in = new FileInputStream(txtPath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "gb2312"));
			String key = null;
			while ((key = reader.readLine()) != null) {
				key = key.trim();
				if (key.equals("")) {
					continue;
				}
				if(key.charAt(0) == '#'){
					continue;
				}
				keys.add(key);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error("关键字文件keys.txt不存在！");
			System.exit(1);
		} catch (IOException e) {
			logger.error("关键字文件读取错误！");
			System.exit(2);
		}
		return keys;
	}
}
