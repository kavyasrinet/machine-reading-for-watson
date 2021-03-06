package edu.cmu.lti.oaqa.search;

import java.io.*;
import java.util.Hashtable;

public class WebSearchCache {
	private static String path;
	private Hashtable<String, Object> tableInMemory = new Hashtable<String, Object>();

	private static boolean VERBOSE = false;
	public static void setVerbose( boolean flag ){
		VERBOSE = flag;
	}
	
	public WebSearchCache(String cachePath) {
		this.path=cachePath;
	}

	public MultiMap<String, Result> loadCache(String RetrievalEngine) {
		// If already in memory, then return back
		if (tableInMemory.containsKey(RetrievalEngine))
			return (MultiMap<String, Result>) tableInMemory.get(RetrievalEngine);
		if (tableInMemory.isEmpty()){
			//System.out.println("tableInMemory is still empty");
		}

		String fileName = path + RetrievalEngine + "Cache.txt";
		//System.out.println("Reading " + RetrievalEngine + " Cache File");
		ObjectInputStream inputStream = null;
		MultiMap<String, Result> table = null;

		try {
			inputStream = new ObjectInputStream(new FileInputStream(fileName));
			try {
				table = (MultiMap<String, Result>) inputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("File doesn't exist!");

		} catch (IOException e) {
		} finally {
		}

		if (table != null)
			tableInMemory.put(RetrievalEngine, table);
		if (tableInMemory.isEmpty())
			System.out
			.println("tableInMemory after the put command is still empty");
		return (MultiMap<String, Result>) table;
	}

	public void saveCache(Object table, String RetrievalEngine) {

		if (table == null) {
			System.out.println("Save cache : Table is null");
			return;
		}

		if (tableInMemory.isEmpty()) {
			System.out
			.println("tableInMemory is still empty at the save cache stage");
		}

		tableInMemory.put(RetrievalEngine, table);

		if (VERBOSE) {
			System.out.println("Writing " + RetrievalEngine + " Cache File");
		}
		
		String fileName = path + RetrievalEngine + "Cache.txt";
		if (VERBOSE) {
			System.out.println( "Trying to use cache file: " + fileName );
		}
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(
					new FileOutputStream(fileName));
			outputStream.writeObject(table);
			outputStream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
		}
	}
}
