import java.util.*;
import java.lang.*;
import java.io.*;

class Main {
	static int numberOfNodes;
	static int[][] matrix;
	static int days;
	static int curDay;
	static int netCost = 0;
	static int zonesLeft;
	static boolean isPossible = false;
	static Airport currentPort;
	static Airport departure;
	static Airport arrival;
	static List<Flight> flights = new ArrayList<Flight>();
	static Map<Flight, List<Integer>> badF = new HashMap<Flight, List<Integer>>();
	static List<Airport> airports = new ArrayList<Airport>();
	static List<String> zonesVisited = new ArrayList<String>();
	static Map<String, List<String>> map = new HashMap<String, List<String>>();
	static Map<String, Integer> zoneMap = new HashMap<String, Integer>();
	static String[] zones;
	static String[] ports;
	static int n;
	static int space;
	static int z = 1;
	static String line = "";
	static String startPort = "";
	static String startingZone = "";
	static Stack<Flight> stack;
	static BufferedReader bufferReader = null;
	static String FILENAME = "C:\\Users\\Kyle\\Desktop\\algs\\TestFile.txt";
	static List<Long> milliTimes = new ArrayList<Long>();

	public static void main(String[] args) throws java.lang.Exception 
	{
		
		for (int m = 4; m <= 10; m++)
		{
			for (int n = 1; n <= 5; n++) {
			FILENAME = "C:\\Users\\Kkdj2\\Desktop\\algs\\" + m + "_Nodes_" + n + "_Ports.txt";
			long time = System.nanoTime();

			bufferReader = new BufferedReader(new FileReader(FILENAME));
			int i = 0;
			Scanner scan = new Scanner(new File(FILENAME));
			while (scan.hasNextLine()) 
			{
				boolean found = false;
				line = scan.nextLine();
				if (i == 0)
				{
					{
						String[] s = line.split("\\s+");
						zonesLeft = n = Integer.parseInt(s[0]);
						startPort = s[1];			
						System.out.println(n + " " + startPort);
						zones = new String[n];
						ports = new String[n];
						i++;
					}
				} else if (i < 2 * n) {
					if (i % 2 == 1) {
						zones[i / 2] = line;
						zoneMap.put(zones[i/2], 0);
					} else {
						ports[(i - 1) / 2] = line;
					}
					if (i == 2 * n - 1) {
						ports[n - 1] = scan.nextLine();
						addMap();
					}
					i++;
				} else {
					String[] splited = line.split("\\s+");
					for (int k = 0; k < airports.size(); k++)
					{
						Airport a = airports.get(k);
						if (a.getName().equals(splited[0]))
						{
							departure = a;
						}
						if (a.getName().equals(splited[1]))
						{
							arrival = a;
						}
						if (a.getName().equals(startPort))
						{
							currentPort = a;
						}
					}
					int day = Integer.parseInt(splited[2]);
					int cost = Integer.parseInt(splited[3]);
					// System.out.println(departure.getName() + "," + arrival.getName() + "," + ":" + day + ":" + cost);
					flights.add(new Flight(departure, arrival, day, cost));
				}
			}
			for (int k = 0; k < flights.size(); k++) {
				Flight f = flights.get(k);
				// System.out.println(f.getDep().getName() + "," + f.getArr().getName() + "-" + f.getCost());
			}
			scan.close();
			curDay = 1;
			startingZone = currentPort.getZone();
			zoneMap.replace(startingZone, 1);
			stack = new Stack<Flight>();
			while(!findFlight());
			System.out.println("Found a route:");
			for (Flight f : stack) {
				System.out.println(f.getDep().getName() + "->" + f.getArr().getName() + " (" + f.getArr().getZone() + ")");
			}
			milliTimes.add((System.nanoTime()-time)*1000000);
			time = System.nanoTime();
			System.out.println(FILENAME);
			System.out.println("Total Cost: " + netCost);
			System.out.println("Nodes: " + flights.size() + ";  Time taken: " + (System.nanoTime()-time) + " Nanoseconds" + "; " + (System.nanoTime()-time)/1000000000 + " seconds");
		}
		for (long f : milliTimes)
		{
			System.out.println(f);
		}
		}

	}

	private static boolean findFlight() 
	{
		int min = Integer.MAX_VALUE;
		Flight result = null;
		if (zonesLeft == 1)
		{
			for (int i = 0; i < flights.size(); i++)
			{
				Flight f = flights.get(i);
				boolean dontLoop = false;
				for (Map.Entry<Flight, List<Integer>> entry : badF.entrySet()) 
				{
					for (Integer k : entry.getValue()) 
					{
						if (entry.getKey().equals(f))
						{
							if (f.getDay() == k)
							{
								dontLoop = true;
							}
						}
					}
				}
				if (!dontLoop && f.getDep().getName().equals(currentPort.getName()) && (f.getDay() == curDay || f.getDay() == 0))
				{
					if (f.getArr().getZone().equals(startingZone))
					{
						if (f.getCost() <= min)
						{
							min = flights.get(i).getCost();
							result = flights.get(i);
						}
					}
				}
			}
		}
		else
		{
			for (int i = 0; i < flights.size(); i++)
			{
				Flight f = flights.get(i);
				boolean dontLoop = false;
				for (Map.Entry<Flight, List<Integer>> entry : badF.entrySet()) 
				{
					for (Integer k : entry.getValue()) 
					{
						if (entry.getKey().equals(f))
						{
							if (curDay == k)
							{
								dontLoop = true;
							}
						}
					}
					// System.out.println();
				}
				if (!dontLoop && f.getDep().getName().equals(currentPort.getName()) && (f.getDay() == curDay || f.getDay() == 0))
				{
					if (!zoneVisited(f.getArr().getZone()))
					{
						if (f.getCost() < min)
						{
							min = flights.get(i).getCost();
							//System.out.println(f.getDep().getName() + "," + f.getArr().getName() + "-" + f.getCost());
							result = flights.get(i);
						}
					}
				}
			}
		}
		if (result == null)
		{
			Flight f = stack.pop();
			zoneMap.replace(f.getArr().getZone(), 0);
			currentPort = f.getDep();
			zonesLeft++;
			curDay--;
			netCost -= f.getCost();
			// System.out.println("backtracking");
			if (f.getDay() == 0)
			{
				badF.put(f, new ArrayList<Integer>());
				badF.get(f).add(curDay);
			}
			else
			{
				flights.remove(f);
			}
		}
		else {
			zoneMap.replace(result.getArr().getZone(), 1);
			stack.push(result);
			currentPort = result.getArr();
			netCost += result.getCost();
			System.out.println(result.getDep().getName() + "," + result.getArr().getName() + ":" + result.getCost() + "  " + zonesLeft);
			curDay++;
			zonesLeft--;
			if (zonesLeft == 0) 
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean zoneVisited(String s)
	{
		return zoneMap.get(s) != 0;
	}
	
	public static List<String> parse(String str) {
		List<String> airports = new ArrayList<String>();
		String[] strArr = str.split("\\s+");
		for (int i = 0; i < strArr.length; i++) airports.add(strArr[i]);
		return airports;
	}

	public static void addMap() {
		for (int i = 0; i < n; i++) {
			map.put(zones[i], parse(ports[i]));
			for (int k = 0; k < parse(ports[i]).size(); k++) {
				// System.out.println("Id:" + parse(ports[i]).get(k));
			}
		}
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			System.out.print("Zone: " + entry.getKey() + "; Ports: ");
			for (String str : entry.getValue()) {
				System.out.print(str + " ");
				airports.add(new Airport(str, entry.getKey()));
			}
			System.out.println();
		}
	}

	public static class Airport
	{
		String name;
		String zone;
		
		public Airport(String n, String z)
		{
			name = n;
			zone = z;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getZone()
		{
			return zone;
		}
	}
	public static class Flight {
		Airport arrival;
		Airport departure;
		int day;
		int cost;

		public Flight(Airport dep, Airport arr, int d, int c) {
			arrival = arr;
			departure = dep;
			day = d;
			cost = c;
		}

		public int getDay() {
			return day;
		}

		public int getCost() {
			return cost;
		}

		public Airport getArr() {
			return arrival;
		}

		public Airport getDep() {
			return departure;
		}
	}
}