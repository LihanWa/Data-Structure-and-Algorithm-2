import javax.swing.ImageIcon;

import java.util.*;
import java.io.*;

public class AirlineSystem implements AirlineInterface {
  private String[] cityNames = null;
  private Graph G = null;
  private static Scanner scan = null;
  private static final int INFINITY = Integer.MAX_VALUE;

  /**
   * reads the city names and the routes from a file
   * 
   * @param fileName the String file name
   * @return true if routes loaded successfully and false otherwise
   */
  public boolean loadRoutes(String fileName) {

    Scanner fileScan = null;// scanner
    try {
      fileScan = new Scanner(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      System.out.print("filenotfound");
      return false;
    } // file valid
    int v = Integer.parseInt(fileScan.nextLine());// total vertex
    G = new Graph(v);

    cityNames = new String[v];// citynames array
    for (int i = 0; i < v; i++) {
      cityNames[i] = fileScan.nextLine();
    } // cities

    while (fileScan.hasNext()) {// load each edge
      int from = fileScan.nextInt();
      int to = fileScan.nextInt();
      int dist = fileScan.nextInt();
      double price = fileScan.nextDouble();
      G.addEdge(new WeightedDirectedEdge(from - 1, to - 1, dist, price));
      G.addEdge(new WeightedDirectedEdge(to - 1, from - 1, dist, price));
      if (fileScan.hasNext())
        fileScan.nextLine();
    }

    return true;
  }

  /**
   * returns the set of city names in the Airline system
   * 
   * @return a (possibly empty) Set<String> of city names
   */
  public Set<String> retrieveCityNames() {
    Set<String> a = new HashSet<String>();// make a set
    for (int i = 0; i < cityNames.length; i++) {// put each city in set
      a.add(cityNames[i]);
    }

    return a;
  }

  /**
   * returns the set of direct routes out of a given city
   * 
   * @param city the String city name
   * @return a (possibly empty) Set<Route> of Route objects representing the
   *         direct routes out of city
   * @throws CityNotFoundException if the citsy is not found in the Airline
   *                               system
   */
  public Set<Route> retrieveDirectRoutesFrom(String city) throws CityNotFoundException {
    for (int i = 0; i < cityNames.length; i++) {
      if (city.equals(cityNames[i])) {
        Set<Route> a = new HashSet<Route>();
        for (WeightedDirectedEdge w : G.adj(i)) {
          a.add(new Route(cityNames[w.from()], cityNames[w.to()], w.dist(), w.price()));
        }
        return a;
      }
    }
    throw new CityNotFoundException(city);
  }

  /**
   * finds fewest-stops path(s) between two cities
   * 
   * @param source      the String source city name
   * @param destination the String destination city name
   * @return a (possibly empty) Set<ArrayList<String>> of fewest-stops paths.
   *         Each path is an ArrayList<String> of city names that includes the
   *         source
   *         and destination city names.
   * @throws CityNotFoundException if any of the two cities are not found in the
   *                               Airline system
   */
  public Set<ArrayList<String>> fewestStopsItinerary(String source, String destination) throws CityNotFoundException {
    boolean findSource = false;
    boolean findDes = false;
    int s = -1;
    int d = -1;
    for (int i = 0; i < cityNames.length; i++) {// look for source and destinaion, and their index
      if (source.equals(cityNames[i])) {
        findSource = true;
        s = i;
      }
      if (destination.equals(cityNames[i])) {
        findDes = true;
        d = i;
      }
    }
    if (!findSource)
      throw new CityNotFoundException(source);// if didn't find them, throw exe
    if (!findDes)
      throw new CityNotFoundException(destination);

    G.bfs(s);
    Set<ArrayList<String>> e = new HashSet<ArrayList<String>>();
    if (!G.marked[d]) {
      return e;// if destination isn't marked, can't get there;
    } else {

      ArrayList<String> a = new ArrayList<String>();// make a arraylist
      int curr = d;

      Stack<Integer> t = new Stack<Integer>();// create a stack load from destination to the one before source
      while (curr != s) {
        t.push(curr);
        curr = G.edgeTo[curr];
      }
      a.add(cityNames[s]);// add cityname to arraylist in order

      while (!t.empty()) {
        a.add(cityNames[t.pop()]);

      }
      e.add(a);
      return e;
    }

  }

  /**
   * finds shortest distance path(s) between two cities
   * 
   * @param source      the String source city name
   * @param destination the String destination city name
   * @return a (possibly empty) Set<ArrayList<Route>> of shortest-distance
   *         paths. Each path is an ArrayList<Route> of Route objects that
   *         includes a
   *         Route out of the source and a Route into the destination.
   * @throws CityNotFoundException if any of the two cities are not found in the
   *                               Airline system
   */
  public Set<ArrayList<Route>> shortestDistanceItinerary(String source, String destination)
      throws CityNotFoundException {
    boolean findSource = false;
    boolean findDes = false;
    int s = -1;
    int d = -1;
    for (int i = 0; i < cityNames.length; i++) {// look for source and destinaion, and their index
      if (source.equals(cityNames[i])) {
        findSource = true;
        s = i;
      }
      if (destination.equals(cityNames[i])) {
        findDes = true;
        d = i;
      }
    }
    if (!findSource)
      throw new CityNotFoundException(source);// if didn't find them, throw exe
    if (!findDes)
      throw new CityNotFoundException(destination);

    G.dijkstras(s, d, 1);
    Set<ArrayList<Route>> e = new HashSet<ArrayList<Route>>();
    if (!G.marked[d]) {
      return e;// if destination isn't marked, can't get there;
    } else {
      Stack<Route> path = new Stack<>();// push all paths to the stack
      for (int x = d; x != s; x = G.edgeTo[x]) {
        // push route in stack in order
        path.push(new Route(cityNames[G.edgeTo[x]], cityNames[x], G.milesTo[x], G.priceTo[x]));
      }

      ArrayList<Route> a = new ArrayList<>();// make an arraylist to load route
      while (!path.empty()) {
        a.add(path.pop());// add the current route to arraylist
      }
      e.add(a);// add arraylist to set
      return e;

    }
  }

  /**
   * finds cheapest path(s) between two cities
   * 
   * @param source      the String source city name
   * @param destination the String destination city name
   * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
   *         paths. Each path is an ArrayList<Route> of Route objects that
   *         includes a
   *         Route out of the source and a Route into the destination.
   * @throws CityNotFoundException if any of the two cities are not found in the
   *                               Airline system
   */
  public Set<ArrayList<Route>> cheapestItinerary(String source, String destination) throws CityNotFoundException {
    boolean findSource = false;
    boolean findDes = false;
    int s = -1;
    int d = -1;
    // look for source and destinaion, and their index
    for (int i = 0; i < cityNames.length; i++) {
      if (source.equals(cityNames[i])) {
        findSource = true;
        s = i;
      }
      if (destination.equals(cityNames[i])) {
        findDes = true;
        d = i;
      }
    }
    if (!findSource)
      throw new CityNotFoundException(source);// if didn't find them, throw exeception
    if (!findDes)
      throw new CityNotFoundException(destination);

    G.dijkstras(s, d, 2);
    Set<ArrayList<Route>> e = new HashSet<ArrayList<Route>>();
    if (!G.marked[d]) {
      return e;// if destination isn't marked, can't get there;
    } else {
      Stack<Route> path = new Stack<>();// push all paths to the stack
      for (int x = d; x != s; x = G.edgeTo[x]) {
        path.push(new Route(cityNames[G.edgeTo[x]], cityNames[x], G.milesTo[x], G.priceTo[x]));// push route in stack in
                                                                                               // order
      }

      ArrayList<Route> a = new ArrayList<>();// make an arraylist to load route
      while (!path.empty()) {
        a.add(path.pop());// add the current route to arraylist
      }
      e.add(a);// add arraylist to set
      return e;

    }
  }

  /**
   * finds cheapest path(s) between two cities going through a third city
   * 
   * @param source      the String source city name
   * @param transit     the String transit city name
   * @param destination the String destination city name
   * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
   *         paths. Each path is an ArrayList<Route> of city names that includes
   *         a Route out of source, into and out of transit, and into destination.
   * @throws CityNotFoundException if any of the three cities are not found in
   *                               the Airline system
   */
  public Set<ArrayList<Route>> cheapestItinerary(String source,
      String transit, String destination) throws CityNotFoundException {
    boolean findSource = false;
    boolean findDes = false;
    boolean findT = false;
    int s = -1;
    int d = -1;
    int t = -1;
    // look for source and destinaion, and their index
    for (int i = 0; i < cityNames.length; i++) {
      if (source.equals(cityNames[i])) {
        findSource = true;
        s = i;
      }
      if (transit.equals(cityNames[i])) {
        findT = true;
        t = i;
      }
      if (destination.equals(cityNames[i])) {
        findDes = true;
        d = i;
      }
    }
    if (!findSource)
      throw new CityNotFoundException(source);// if didn't find them, throw exe
    if (!findDes)
      throw new CityNotFoundException(destination);
    if (!findT)
      throw new CityNotFoundException(transit);
    Stack<Route> path = new Stack<>();// push all paths to the stack
    G.dijkstras(t, d, 2);
    Set<ArrayList<Route>> e = new HashSet<ArrayList<Route>>();
    // from transit to destination
    if (!G.marked[d]) {
      return e;// if destination isn't marked, can't get there;
    } else {
      // push route in stack in order
      for (int x = d; x != t; x = G.edgeTo[x]) {
        path.push(new Route(cityNames[G.edgeTo[x]], cityNames[x], G.milesTo[x], G.priceTo[x]));
      }
    }
    // from source to transit
    G.dijkstras(s, t, 2);
    // if transit isn't marked, can't get there;
    if (!G.marked[t]) {
      return e;

    } else {
      // push route in stack in order
      System.out.print(t);
      System.out.print(s);
      for (int m = t; m != s; m = G.edgeTo[m]) {
        path.push(new Route(cityNames[G.edgeTo[m]], cityNames[m], G.milesTo[m], G.priceTo[m]));
      }
    }

    ArrayList<Route> a = new ArrayList<>();// make an arraylist to load route
    while (!path.empty()) {

      a.add(path.pop());// add the current route to arraylist
    }
    e.add(a);// add arraylist to set

    return e;

  }

  /**
   * finds one Minimum Spanning Tree (MST) for each connected component of
   * the graph
   * 
   * @return a (possibly empty) Set<Set<Route>> of MSTs. Each MST is a Set<Route>
   *         of Route objects representing the MST edges.
   */
  public Set<Set<Route>> getMSTs() {
    boolean[] marked = new boolean[G.v];// if the vertex has been added to the gragh
    int[] parent = new int[G.v];// from which vertex
    int[] min = new int[G.v];// the min distance to connect
    double[] price = new double[G.v];
    int nMarked = 0;
    for (int i = 0; i < G.v; i++) {// initialize
      marked[i] = false;
      parent[i] = -1;
      min[i] = INFINITY;
    }
    int current = 0;
    boolean find = false;
    Set<Set<Route>> b = new HashSet<>();
    Set<Route> a = new HashSet<>();
    do {
      marked[0] = true;
      min[0] = 0;
      // start to try to find a path
      // at most v vertex can be connected

      while (nMarked < G.v) {

        // update because of the new marked vertex
        for (WeightedDirectedEdge w : G.adj(current)) {
          if (!marked[w.to()] && w.dist() < min[w.to()]) {
            min[w.to()] = w.dist();
            price[w.to()] = w.price();
            parent[w.to()] = current;
          }
        }
        // traverse all vertex to find min distance; if doesn't find it, break
        int minCur = INFINITY;
        int minP = INFINITY;// price
        double p = -1;
        current = -1;
        for (int i = 0; i < G.v; i++) {
          // if already marked, leave it
          if (marked[i])
            continue;
          else {
            // if it's smaller than minCur, update
            if (minCur > min[i]) {
              current = i;
              minCur = min[i];
              p = price[i];
            }
          }
        }
        // if doesn't find one
        if (current == -1)
          break;
        else {
          // mark and load route
          marked[current] = true;

          a.add(new Route(cityNames[parent[current]], cityNames[current], minCur, p));
        }
      }

      // if there is vertex that isn't marked, then go to next loop
      for (int i = 0; i < G.v; i++) {
        if (marked[i] = false) {
          current = i;
          find = true;
        }
      }
    } while (find);
    b.add(a);
    return b;
  }

  /**
   * finds all itineraries starting out of a source city and within a given
   * price
   * 
   * @param city   the String city name
   * @param budget the double budget amount in dollars
   * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
   *         less than or equal to the budget. Each path is an ArrayList<Route> of
   *         Route
   *         objects starting with a Route object out of the source city.
   */
  public Set<ArrayList<Route>> tripsWithin(String city, double budget)
      throws CityNotFoundException {
    boolean findCity = false;
    int c = -1;
    boolean[] marked = new boolean[G.v];
    // look for city and find the index to give to the private method bellow
    for (int i = 0; i < cityNames.length; i++) {

      if (city.equals(cityNames[i])) {
        findCity = true;
        c = i;
      }
    }
    if (!findCity) {
      throw new CityNotFoundException(city);// if didn't find them, throw exe
    } else {

      for (int i = 0; i < G.v; i++) {// initialize marked
        marked[i] = false;

      }
      marked[c] = true;// the source is marked
      ArrayList<Route> a = new ArrayList<>();
      Set<ArrayList<Route>> b = new HashSet<>();
      WeightedDirectedEdge d = null;
      return tripsWithin(c, budget, marked, a, b, d);
    }

  }

  private Set<ArrayList<Route>> tripsWithin(int city, double budget, boolean[] marked, ArrayList<Route> a,
      Set<ArrayList<Route>> b, WeightedDirectedEdge d) {
    int next = -1;
    // add an edge from the previous recusion
    if (d != null){
      //once arraylist changes, add it to set
      a.add(new Route(cityNames[d.from()], cityNames[d.to()], d.dist(), d.price()));
      b.add(a);
    }
    for (WeightedDirectedEdge w : G.adj(city)) {
      if (!marked[w.to()] && (budget - w.price()) >= 0) {
        //create a new arraylist berfore going to next city 
        ArrayList<Route> temp = null;
        temp = new ArrayList<>(a);
        
        next = w.to();// next city
        marked[w.to()] = true;
        tripsWithin(next, budget - w.price(), marked, temp, b, w);
        marked[w.to()] = false;//
      }
    }
    // if doesn't find an edge, put this path to set
   
    return b;
  }

  /**
   * finds all itineraries within a given price regardless of the
   * starting city
   * 
   * @param budget the double budget amount in dollars
   * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
   *         less than or equal to the budget. Each path is an ArrayList<Route> of
   *         Route
   *         objects.
   */
  public Set<ArrayList<Route>> tripsWithin(double budget) {
    Set<ArrayList<Route>> result = new HashSet<>();
    Set<ArrayList<Route>> a=null;
    for(int i=0;i<G.v;i++){
      try{
        a=tripsWithin(cityNames[i], budget);
      }catch(CityNotFoundException e){
        ;
      }
      for(ArrayList<Route> j:a){
        result.add(j);
      }

    }
    return result;
  }

  private class Graph {
    private final int v;
    private int e;
    private LinkedList<WeightedDirectedEdge>[] adj;
    private boolean[] marked; // marked[v] = is there an s-v path
    private int[] edgeTo; // edgeTo[v] = previous edge on shortest s-v path
    private double[] tPriceTo; // this is the total price from the source
    private int[] distTo;
    private double[] priceTo;// price of edges shortest s-v path
    private int[] milesTo;// miles of edges shortest s-v path

    /**
     * Create an empty digraph with v vertices.
     */
    public Graph(int v) {
      if (v < 0)
        throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<WeightedDirectedEdge>[] temp = (LinkedList<WeightedDirectedEdge>[]) new LinkedList[v];// linkedlist
                                                                                                       // array
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<WeightedDirectedEdge>();// for each element there is a linked list
    }

    /**
     * Add the edge e to this digraph.
     */
    public void addEdge(WeightedDirectedEdge edge) {
      int from = edge.from();
      adj[from].add(edge);// add to linkedlist
      e++;
    }

    /**
     * Return the edges leaving vertex v as an Iterable.
     * To iterate over the edges leaving vertex v, use foreach notation:
     * <tt>for (WeightedDirectedEdge e : graph.adj(v))</tt>.
     */
    public Iterable<WeightedDirectedEdge> adj(int v) {
      return adj[v];// a linkedlist
    }

    public void bfs(int source) {
      marked = new boolean[this.v];// mark vertex if meet it
      distTo = new int[this.e];// the distance from source to the specific vertex
      edgeTo = new int[this.v];// comes from which vertex

      Queue<Integer> q = new LinkedList<Integer>();// a queue using linkedlist,
      for (int i = 0; i < v; i++) {// initialize
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);

      while (!q.isEmpty()) {
        int v = q.remove();
        for (WeightedDirectedEdge w : adj(v)) {// each edge connected with vertex v
          if (!marked[w.to()]) {// go to the vertex which haven't been marked
            edgeTo[w.to()] = v;// another vertex comes from vertex v
            distTo[w.to()] = distTo[v] + 1; // another vertex distance from source is
            marked[w.to()] = true;// another vertex has been marked
            q.add(w.to());// load it to vertex
          }
        }
      }
    }

    public void dijkstras(int source, int destination, int c) {// case==1: miles; case==2: price
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];
      tPriceTo = new double[this.v];
      priceTo = new double[this.v];
      milesTo = new int[this.v];
      for (int i = 0; i < v; i++) {// initialize
        distTo[i] = INFINITY;
        marked[i] = false;
        tPriceTo[i] = INFINITY;
      }
      distTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;
      tPriceTo[source] = 0;

      int current = source;
      while (nMarked < this.v) {// the longest case
        for (WeightedDirectedEdge w : adj(current)) {// for every edge connected to current
          if (c == 1 && distTo[current] + w.dist() < distTo[w.to()]) {// update the distance

            distTo[w.to()] = distTo[current] + w.dist();// from source to the vertex
            edgeTo[w.to()] = current;// update parent
            priceTo[w.to()] = w.price();
            milesTo[w.to()] = w.dist();
          }
          if (c == 2 && tPriceTo[current] + w.price() < tPriceTo[w.to()]) {// update the price
            tPriceTo[w.to()] = tPriceTo[current] + w.price();// from source
            edgeTo[w.to()] = current;// update parent
            priceTo[w.to()] = w.price();
            milesTo[w.to()] = w.dist();
          }
        }
        // Find the vertex with minimim path distance
        // This can be done more effiently using a priority queue!
        double min = INFINITY;

        current = -1;
        if (c == 1) {
          for (int i = 0; i < distTo.length; i++) {// for all vertex
            if (marked[i])// if marked doesn't consider
              continue;
            if (distTo[i] < min) {// find the one with minimum distance (no one <Inf, then break later)
              min = distTo[i];
              current = i;
            }
          }
        }
        if (c == 2) {
          for (int i = 0; i < tPriceTo.length; i++) {// for all vertex
            if (marked[i])// if marked doesn't consider
              continue;
            if (tPriceTo[i] < min) {// find the one with minimum distance (no one <Inf, then break later)
              min = tPriceTo[i];
              current = i;
            }
          }
        }

        // Update marked[] and nMarked. Check for disconnected graph.
        if (current != -1)
          marked[current] = true;
        else
          break;

        nMarked++;
      }
    }
  }

  private class WeightedDirectedEdge {
    private final int v;
    private final int w;
    private int distance;
    private double price;

    /**
     * Create a directed edge from v to w with given weight.
     */
    public WeightedDirectedEdge(int v, int w, int dist, double price) {
      this.v = v;
      this.distance = dist;
      this.price = price;
      this.w = w;
    }

    public int from() {
      return v;
    }

    public int to() {
      return w;
    }

    public int dist() {
      return distance;
    }

    public double price() {
      return price;
    }
  }

}
