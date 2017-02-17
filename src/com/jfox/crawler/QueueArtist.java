package com.jfox.crawler;

public class QueueArtist {
	private String fromURL, toURL, artistName, toArtist, connection;
	
	public QueueArtist(){
		
	}

	public String getFromURL() {
		return fromURL;
	}

	public void setFromURL(String fromURL) {
		this.fromURL = fromURL;
	}

	public String getToURL() {
		return toURL;
	}

	public void setToURL(String toURL) {
		this.toURL = toURL;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}
	
	public String getToArtist(){
		return toArtist;
	}
	
	public void setToArtist(String toArtist){
		this.toArtist = toArtist;
	}
	
	public boolean isValidNode(){
		if(artistName != null && !artistName.equals(toArtist)){
			return true;
		}
		
		return false;
	}
	
	public boolean isComplete(){
		return (fromURL != null && toURL != null && artistName != null && toArtist != null && connection!=null);
	}
	
	@Override
	public String toString() {
		return "ArtistNode [fromURL=" + fromURL + ", toURL=" + toURL + ", artistName=" + artistName + ", toArtist="
				+ toArtist + ", connection=" + connection + "]";
	}

}
