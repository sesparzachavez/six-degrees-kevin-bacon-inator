import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.json.simple.parser.JSONParser;

import java.util.Scanner;
import java.util.NoSuchElementException;
import org.json.simple.parser.ParseException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Hashtable;


public class bacon {
	/*
	 * Queue Class 
	 */
	public static class ArrayQueue<T> {

		Object[] arr;
		int front;
		int back;


		public ArrayQueue(){
			arr = (T[]) new Object [10];
			front = 0;
			back = 0;
		}

		public T dequeue() throws Exception{
			if (empty()){
				throw new Exception();
			}
			T new_arr = (T) arr[front];
			//return (T) arr[(front++)%arr.length];//this is nottttt working 
			front = (front + 1) % arr.length;
			return new_arr;

		}

		public void enqueue(T item) {

			if ((back + 1) % arr.length == front){
				grow_array();
			}
			arr[back++] = item;
			back = back % arr.length;
		}

		public boolean empty(){
			if (front == back){
				return true;
			}
			return false;
		}
		
		public void grow_array() {
			Object[] new_arr = new Object[arr.length * 2];
			for (int i = 0; i < arr.length; i++){
				//new_arr[i] = dequeue();
				new_arr[i] = (T) arr[(front + i) % arr.length];	
			}
			front = 0;
			back = arr.length - 1;
			arr = new_arr;
			
		}
	}
/*
 * Graph Class 
 */
	public static class Graph {

		public Hashtable<String, ArrayList<String>> graphInator;


		public Graph(){
			graphInator = new Hashtable<String, ArrayList<String>>();
		}


		public void addVertex(String vertex){
			ArrayList<String> list = new ArrayList<String>();
			if(!graphInator.containsKey(vertex))
				graphInator.put(vertex, list);
		}

/**
 * make and edge between vertices 
 * @param s source vertex
 * @param t target vertex 
 */
		public void addEdge(String s, String t){
			ArrayList<String> list = graphInator.get(s);
			if (!list.contains(t)){
				list.add(t);
			}
			graphInator.replace(s, list);
		}
		
//System.out.println(team1.keySet().toArray()[0]);
//keySet() returns a set, so you convert the set to an array.
//The problem, of course, is that a set doesn't promise to keep your order. If you only have one item in your HashMap, you're good, b
//key set returns a set view of the keys contained in the map
//stack overflow to the rescue 
		
		public ArrayList<String> getVertices(){
			ArrayList<String> hashKeys = new ArrayList<String>(graphInator.keySet());
			return hashKeys;
		}

		public ArrayList<String> incident(String vertex){
			return graphInator.get(vertex);
		}
			
	}
	
	/**
	 * finds shortest bath using breadth first search 
	 * @param graph of actors
	 * @param a - actor 1
	 * @param b - actor 2
	 * @return shortest path from actor a to actor b 
	 */

	private static ArrayList<String> bfsInator(Graph graph, String a, String b) {
		
		ArrayQueue<String> queue = new ArrayQueue<String>();
		ArrayList<String> shortestPath = new ArrayList<String>();

		
		Hashtable<String, Boolean> visited = new Hashtable<String, Boolean>();
		Hashtable<String, String> prevVisited = new Hashtable<String, String>();

		String curr = a;
		queue.enqueue(curr);
		
		/*.put
		 * Parameters:
			key key with which the specified value is to be associated
			value value to be associated with the specified key
			Returns:
			the previous value associated with key, or null if there was no mapping for key. 
			(A null return can also indicate that the map previously associated null with key, 
			if the implementation supports null values.)
		 */
		
		visited.put(curr, true);

		while (!queue.empty()) {
			try{
				curr = queue.dequeue();
			}catch (Exception e){
				e.printStackTrace();
			}
			curr = queue.dequeue();
			
			if (!curr.equals(b)) {

				for (String adj : graph.incident(curr)) {

					if (visited.get(adj) == null) {

						queue.enqueue(adj);
						visited.put(adj, true);
						prevVisited.put(adj, curr);
					}
				}
			}
			else {
				break;
			}
		}
		
		//edge case 
		if (!curr.equals(b)) {

			System.out.println("No path exists between Actor 1 and Actor 2.");
			return null;
		}
		
		String str = b;
		while (str != null) {

			shortestPath.add(str);
			str = prevVisited.get(str);
		}

		return shortestPath;
		
	}
	
	/**
	 * 
	 * @param args movie file 
	 */
	
    public static void main(String[] args) {
    	
    	Graph graph = new Graph();
    	
    	try {
        	Reader reader = new FileReader(args[0]);
        	CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        	JSONParser jsonParser = new JSONParser();

            
                
            boolean flag = true;
            for (CSVRecord csvRecord : csvParser) {
                	
            	if (!flag) {
            		
            		try {
            			//String title = csvRecord.get(1);
            			String castJSON = csvRecord.get(2);
            			// [] = array
    					// { } = "object" / "dictionary" / "hash table" -- key "name": value
            			Object object = jsonParser.parse(castJSON);
            			JSONArray jsonArray = (JSONArray)object;

            			//for every movie, make a graph for every actor 
            			//connect actors with cast for movie 
            			//the result is a graph of every actor connected to all they have worked with 
            			for (int i = 0; i < jsonArray.size(); i++) {
                				
            				Object first = jsonArray.get(i);
            				JSONObject jsonObject = (JSONObject)first;
            				String name = (String)jsonObject.get("name");
            				graph.addVertex(name);
                				
            				for (int j = 0; j < jsonArray.size(); j++) {
                					
            					Object second = jsonArray.get(j);
            					JSONObject jsonObject2 = (JSONObject)second;
            					String name2 = (String)jsonObject2.get("name");
            					graph.addEdge(name, name2);
            					
            					
            				}
            				
            			}
            			

            		}
    				catch(ParseException e){
    					 //e.printStackTrace();
    				}
             }
             flag = false;
            }
         } catch(Exception e) {
        	 System.out.println("File " + args[0] + "is invalid or is in the wrong format.");
         }
    	
		System.out.println("########################### WELCOME TO FIND ACTOR-PATH-INATOR ###############################");
		
		ArrayList<String> vertices = graph.getVertices();
		
		Scanner scan = new Scanner(System.in);
		
		System.out.print("Actor 1 name: ");
		String actor1 = scan.nextLine();
		actor1 = actor1.toLowerCase();
		
		boolean actorExists = false;
		for (String vertex : vertices) {
			if (actor1.equalsIgnoreCase(vertex)) {
					actorExists = true;
					actor1 = vertex;
			}
		}
		if(!actorExists) {
 			System.out.println("No such actor.");
 			return;
 		}
		
		System.out.print("Actor 2 name: ");
		String actor2 = scan.nextLine();
		actor2 = actor2.toLowerCase();
		
		actorExists = false;
		for (String vertex : vertices) {
			
				if (actor2.equalsIgnoreCase(vertex)) {
					actorExists = true;
					actor2 = vertex;
				}
		}
     	
 		if(!actorExists) {
 			System.out.println("No such actor.");
 			return;
 		}


 		ArrayList<String> shortestPath = bfsInator(graph, actor1, actor2);
 		

 		System.out.println("############################ RESULTS FROM ACTOR-PATH-INATOR ################################");
 		
 		System.out.println("Path between " + actor1 + " to " + actor2 + ":");
 		
 		int x = shortestPath.size()-1;
 		
 		for (int i = shortestPath.size() - 1; i >= 0; i--) {
 			if(i == x) {
 				System.out.print(shortestPath.get(i));
 			}
 			System.out.print(" --> ");
 			System.out.print(shortestPath.get(i));
 		}
     		
    }

}
