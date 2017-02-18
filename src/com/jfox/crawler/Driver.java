package com.jfox.crawler;

public class Driver {
	
	public static void main(String[] args){
		String startUrl = "https://en.wikipedia.org/wiki/Trey_Anastasio";
		Crawler c  = new Crawler(startUrl);
		c.crawl(200);
		c.saveData();
	}
}
	