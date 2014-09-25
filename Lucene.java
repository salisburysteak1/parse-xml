//CIS 345 - Introduction to XML
//Final Project - Fall 2014
//Aaron Salisbury
//Andrew Corbisier

//TUTORIALS:
// lucenetuts.blogspot.com
// javatechniques.com/blog/lucene-in-memory-text-search-example/
// www.tutorialspoint.com/lucene/lucene_search_operation.htm

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Lucene
{
	private static IndexSearcher indexSearcher;
	
	public static void main(String[] args) throws Exception 
	{
		Scanner userInput = new Scanner( System.in );
		Directory index = createIndex();
		indexSearcher = new IndexSearcher(IndexReader.open(index));
		
		String searchTerm;
		System.out.print("Enter search term: ");
		searchTerm = userInput.next( );
		
		parseIndex(searchTerm);
		
		userInput.close();
	}
  
	private static Directory createIndex() throws IOException
	{
		Directory index = new RAMDirectory();
		
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
		IndexWriter indexWriter = new IndexWriter(index, config);
		
		File dir = new File("XML_Files");
		File[] files = dir.listFiles();

		for (File file : files) 
		{
		    Document document = new Document();
		
		    String path = file.getCanonicalPath();
		    byte[] bytes = path.getBytes();
		    document.add(new Field("path", bytes));
		
		    Reader reader = new FileReader(file);
		    document.add(new Field("contents", reader));
		
		    indexWriter.addDocument(document);
		}
		
		indexWriter.close();
		
		return index;
	}
	
	private static void parseIndex(String queryString) throws IOException, ParseException
	{
		System.out.println("Searching for '" + queryString + "'");
		
		QueryParser queryParser = new QueryParser("contents", new StandardAnalyzer());
		Query query = queryParser.parse(queryString);
		
		int hitsPerPage = 1000;
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    indexSearcher.search(query, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    
	    System.out.println("Found " + hits.length + " hits.");
	}
	
	// Not done, work in progress.
   private void search(String queryString) throws IOException, ParseException
   {
		System.out.println("Searching for '" + queryString + "'");

		long startTime = System.currentTimeMillis();
		
		QueryParser queryParser = new QueryParser("contents", new StandardAnalyzer());
		Query query = queryParser.parse(queryString);
		
		int hitsPerPage = 1000;
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		indexSearcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		int totalHits = hits.length;
		
		long endTime = System.currentTimeMillis();
		
	    System.out.println(totalHits + " documents found. Time :" + (endTime - startTime) +" ms");
		
	    for(ScoreDoc scoreDoc : hits) 
	    {
	    	//Document doc = searcher.getDocument(scoreDoc);
	    	//System.out.println("File: "+ doc.get(LuceneConstants.FILE_PATH));
	    }
   } 
}