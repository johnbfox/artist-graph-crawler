package com.jfox.crawler;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class Neo4jLayer {
	
	private static org.neo4j.driver.v1.Driver driver;
	
	public static void init(){
		driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "L@gn@f2016" ) );
	}
	
	public static void saveArtist(Node n){
		try (Session session = driver.session()){
			try ( Transaction tx = session.beginTransaction()){
				if(!hasArtist(tx, n.getArtistName())){
					createNewArtist(tx, n.getArtistName());
				}
				List<Connection> connections = n.getConnections();
				for(Connection c : connections){
					if(!hasArtist(tx, c.getName())){
						createNewArtist(tx, c.getName());
					}
					createLink(tx, n.getArtistName(), c.getName());
				}
				tx.success();
				tx.close();
			}
		}
	}
	
	public static void close(){
		driver.close();
	}
	
	public static void createNewArtist(Transaction tx, String name){
		if(name.toLowerCase() != "Wikipedia, the free encyclopedia".toLowerCase()){
			tx.run("CREATE (a:Artist {name:{name}})", parameters("name", name));
		}
	}
	
	public static void createLink(Transaction tx, String name1, String name2){
		if(name1.toLowerCase() != "Wikipedia, the free encyclopedia".toLowerCase() && name2 != "Wikipedia, the free encyclopedia".toLowerCase()){
			tx.run("MATCH (a1:Artist {name:{name1}}), (a2:Artist {name:{name2}}) CREATE (a1)-[:LINK]->(a2)",
					parameters("name1",name1, "name2", name2));
		}
	}
	
	public static boolean hasArtist(Transaction tx, String name){
		StatementResult sr = tx.run("MATCH (a1:Artist) WHERE a1.name={name} RETURN a1", parameters("name", name));
		return (sr.hasNext());
	}
}
