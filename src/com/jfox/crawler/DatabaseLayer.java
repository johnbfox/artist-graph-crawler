package com.jfox.crawler;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DatabaseLayer {
	
	public static MongoClient mongo;
	public static MongoDatabase db;
	public static MongoCollection<Document> artistCollection;
	
	public static void init(){
		mongo = new MongoClient( "localhost" , 27017 );
		db = mongo.getDatabase("artists");
		artistCollection = db.getCollection("artistCollection");
	}
	
	public static void saveArtist(Node n){
		Document artist = new Document();
		artist.put("name", n.getArtistName());
		
		ArrayList<Connection> connections = n.getConnections();
		ArrayList<Document> connectionsDbObject = new ArrayList<Document>();
		
		
		for(Connection c : connections){
			Document d = new Document();
			d.put("name", c.getName());
			d.put("connection", c.getConnection());
			
			connectionsDbObject.add(d);
		}
		
		artist.put("connections", connectionsDbObject);
		
		artistCollection.insertOne(artist);
	}
	
	public static void close(){
		mongo.close();
	}

}
