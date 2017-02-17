package com.jfox.crawler;

public class Connection{
	String name;
	String connection;
	
	public Connection(String name, String connection){
		this.name = name;
		this.connection = connection;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getConnection(){
		return connection;
	}
	
	public void setConnection(String connection){
		this.connection = connection;
	}
	
	@Override
	public String toString(){
		return "Connection [name=" + name + ", connection=" + connection + "]";
	}
}