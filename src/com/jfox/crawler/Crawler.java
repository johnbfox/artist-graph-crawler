package com.jfox.crawler;

import java.util.Queue;
import java.util.Set;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	private final String wikiBase = "https://en.wikipedia.org";
	private final String[] wikiHomes = {"https://www.wikipedia.org/", "https://en.wikipedia.org/wiki/Main_Page"};
	private QueueArtist curArtist;
	private Map<String, Node> artistGraph;
	
	private Queue<QueueArtist> linkQueue;
	private ArrayList<QueueArtist> completed;
	private Map<String, String> visited;
	
	private boolean isCrawling = true;
	
	public Crawler( String baseUrl){
		linkQueue = new LinkedList<QueueArtist>();
		completed = new ArrayList<QueueArtist>();
		visited = new HashMap<String, String>();
		
		QueueArtist firstArtist = new QueueArtist();
		firstArtist.setToURL(baseUrl);
		linkQueue.add(firstArtist);
	}
	
	public void crawl(int iterations){
		int i = 0;
		isCrawling = i < iterations;
		while((curArtist = linkQueue.poll()) != null || isCrawling){
			try{
				if(visited.get(curArtist.getToURL()) == null){
					step();
					i++;
					isCrawling = (i < iterations);
				}
			}catch(NullPointerException e){
				break;
			}
		}
		
		System.out.println("****** COMPLETED ARTISTS *********");
		System.out.println(completed);
		System.out.println();
		
		
		complete();
		reconcileData();
	}
	
	
	/**
	 * 	Primary logic for stepping through wikipedia for data
	 * 
	 */
	private void step(){
		try{
			if(isHomePage(curArtist.getToURL())){
				return;
			}			
			waitOne();
			Document doc  = Jsoup.connect(curArtist.getToURL()).get();
			Elements infoTable = doc.select(".infobox");
			String pageTitle = doc.title().replace(" - Wikipedia", "");		
			Elements rows = infoTable.select("tr");
			
			PageType pageType = PageType.UNKNOWN;
			
			
			List<String> links = extractLinks(rows, pageType);
			ArrayList<QueueArtist> newArtists = new ArrayList<QueueArtist>();
			
			for(String link : links){
				QueueArtist qa = new QueueArtist();
				
				if(pageType == PageType.BAND){
					
					qa.setFromURL(curArtist.getFromURL());
					qa.setArtistName(curArtist.getArtistName());
					qa.setConnection(pageTitle);
					qa.setToURL(link);
					
					if (isCrawling){
						linkQueue.add(qa);
					}
					
				}else{
					
					qa.setArtistName(pageTitle);
					qa.setFromURL(curArtist.getToURL());
					qa.setToURL(link);
					
					newArtists.add(qa);
				}
				
			}
			
			
			if(isCrawling){
				for(QueueArtist an : newArtists){
					linkQueue.add(an);
				}
			}
			
			
			if(pageType == PageType.ARTIST){
				processArtist(curArtist, pageTitle);
			}
			
			System.out.println();
		}catch(Exception e){
			
		}
	}
	
	private void complete(){
		artistGraph = new HashMap<String, Node>();
		for(QueueArtist an : completed){
			Node n;
			
			if(( n = artistGraph.get(an.getArtistName()) ) == null){
				n = new Node(an.getArtistName());
				n.addConnection(an.getToArtist(), an.getConnection());
				
				artistGraph.put(n.getArtistName(), n);
			}else{
				n.addConnection(an.getToArtist(), an.getConnection());
			}
		}

	}
	
	public void reconcileData(){
		Set<String> artistNames = artistGraph.keySet();
		for(String name : artistNames){
			Node n = artistGraph.get(name);
			
			for(Connection c : n.getConnections()){
				String toArtist = c.getName();
				
				Node n2 = artistGraph.get(toArtist);
				
				
				if(n2 != null){
					boolean hasArtist = false;
					
					for(Connection c2 : n2.getConnections()){
						if(c2.getName().equals(name)){
							hasArtist = true;
						}
					}
					
					if(!hasArtist){
						n2.addConnection(name, c.getConnection());
					}
				}
			}
		}
		System.out.println("***************RECONCILED DATA *************");
		System.out.println(artistGraph);
		System.out.println();
	}
	
	public void saveData(){
		Neo4jLayer.init();
		Set<String> names = artistGraph.keySet();
		
		for(String name : names){
			Neo4jLayer.saveArtist(artistGraph.get(name));
		}
		
		System.out.println(names.size() + " artists saved to db.");
		
		Neo4jLayer.close();
	}
	
	private List<String> extractLinks(Elements rows, PageType pageType){
		List<String> memberLinks = new ArrayList<String>();
		List<String> associatedActsLinks = new ArrayList<String>();
		
		for(Element row : rows){
			String headerContent = row.select("th").text().toLowerCase();
			Elements links = row.select("a");
			
			for (Element link : links){
				String tmpFullUrl = wikiBase + link.attr("href");
				
				if(headerContent.equals("members") || headerContent.equals("former members") || headerContent.equals("past members")){
					memberLinks.add(tmpFullUrl);
				}else if (headerContent.equals("associated acts")){
					associatedActsLinks.add(tmpFullUrl);
				}
			}
		}
		
		if(memberLinks.size() > 0){
			pageType = PageType.BAND;
			return memberLinks;
		}else if(associatedActsLinks.size() > 0){
			pageType = PageType.ARTIST;
			return associatedActsLinks;
		}else{
			pageType = PageType.UNKNOWN;
			return new ArrayList<String>();
		}
	}
	
	private void processArtist(QueueArtist qa, String artistName){
		visited.put(curArtist.getToURL(), artistName);
		
		curArtist.setToArtist(artistName);
		if(curArtist.getConnection() == null){
			curArtist.setConnection("Direct");
		}
		System.out.println(curArtist);

		if(curArtist.isComplete() && curArtist.isValidNode()){
			completed.add(curArtist);
		}
	}
	
	private void waitOne(){
		long time0, time1;
		time0 = System.currentTimeMillis();
		do{
			time1 = System.currentTimeMillis();
		}
		while ((time1-time0) < 1000);
	}
	
	private boolean isHomePage(String url){
		for(String s : wikiHomes){
			if(s.equals(url)){
				return true;
			}
		}
		
		return false;
	}
	
	enum PageType{
		BAND, ARTIST, UNKNOWN
	}
}
