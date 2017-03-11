package com.jfox.crawler;

public class Driver {
	
	public static void main(String[] args){
		String startUrl = "https://en.wikipedia.org/wiki/John_Lennon";
		Crawler c  = new Crawler(startUrl);
		c.crawl(10000);
		c.saveData();
	}
}
	