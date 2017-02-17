package com.jfox.crawler;

import java.util.ArrayList;

public class Node {
	private String artistName;
	private ArrayList<Connection> connections;
	
	public Node(String artistName){
		this.artistName = artistName;
		connections = new ArrayList<Connection>();
	}
	
	@Override
	public String toString() {
		return "Node [artistName=" + artistName + ", connections=" + connections + "]";
	}

	public void setArtistName(String artistName){
		this.artistName = artistName;
	}
	
	public String getArtistName(){
		return artistName;
	}
	
	public void addConnection(String artistName, String connection){
		Connection con = new Connection(artistName, connection);
		connections.add(con);
	}
	
	public ArrayList<Connection> getConnections(){
		return connections;
	}
	
}
