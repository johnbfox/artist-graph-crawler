package com.jfox.crawler;

public interface DatabaseLayer {
	
	public void init();
	public void saveArtist(Node n);
	public void close();

}
