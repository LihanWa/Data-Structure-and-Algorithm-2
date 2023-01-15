/*************************************************************************
*  An Airline management system that uses a weighted-edge directed graph 
*  implemented using adjacency lists.
*************************************************************************/
import java.util.*;
import java.io.*;

public class AirlineSystem {
  private String [] cityNames = null;
  private Digraph G = null;
  private static Scanner scan = null;
  private static final int INFINITY = Integer.MAX_VALUE;


  /**
  * Test client.
  */
  public static void main(String[] args) throws IOException {
    AirlineSystem airline = new AirlineSystem();
    scan = new Scanner(System.in);
    while(true){
      switch(airline.menu()){
        case 1:
          airline.readGraph();
          break;
        case 2:
          airline.printGraph();
          break;
        case 3:
          airline.maxFlow();
          break;
        case 4:
          scan.close();
          System.exit(0);
          break;
        default:
          System.out.println("Incorrect option.");
      }
    }
  }

  private int menu(){
    System.out.println("*********************************");
    System.out.println("Welcome to FifteenO'One Airlines!");
    System.out.println("1. Read data from a file.");
    System.out.println("2. Display all routes.");
    System.out.println("3. Compute maximum flow between two cities");
    System.out.println("4. Exit.");
    System.out.println("*********************************");
    System.out.print("Please choose a menu option (1-4): ");

    int choice = Integer.parseInt(scan.nextLine());
    return choice;
  }

  private void readGraph() throws IOException {
    System.out.println("Please enter graph filename:");
    String fileName = scan.nextLine();
    Scanner fileScan = new Scanner(new FileInputStream(fileName));
    int v = Integer.parseInt(fileScan.nextLine());
    G = new Digraph(v);

    cityNames = new String[v];
    for(int i=0; i<v; i++){
      cityNames[i] = fileScan.nextLine();
    }

    while(fileScan.hasNext()){
      int from = fileScan.nextInt();
      int to = fileScan.nextInt();
      int weight = fileScan.nextInt();
      FlowEdge e=new FlowEdge(from-1, to-1, weight);
      G.addEdge(e);
      //: Add an edge to act as a placeholder for backwards edge from
      //vertex (to-1) to vertex (from-1)
      G.adj[to-1].add(e);
      if(fileScan.hasNext())
        fileScan.nextLine();
    }
    fileScan.close();
    System.out.println("Data imported successfully.");
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  }

  private void printGraph() {
    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      for (int i = 0; i < G.v; i++) {
        System.out.print(cityNames[i] + ": ");
        for (FlowEdge e : G.adj(i)) {
          if(e.residualCapacityTo(e.other(i)) > 0)
            System.out.print(cityNames[e.w] + "(" + e.capacity + " passengers) ");
        }
        System.out.println();
      }
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();

    }
  }

  private void maxFlow() {
    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      for(int i=0; i<cityNames.length; i++){
        System.out.println(i+1 + ": " + cityNames[i]);
      }
      System.out.print("Please enter source city (1-" + cityNames.length + "): ");
      int source = Integer.parseInt(scan.nextLine());
      System.out.print("Please enter sink city (1-" + cityNames.length + "): ");
      int destination = Integer.parseInt(scan.nextLine());
      source--;
      destination--;
      
      System.out.println("The maximum flow from " + cityNames[source] + 
                         " to " + cityNames[destination] + " is: " + 
                         G.FordFulkerson(source, destination) + " passengers");
     
    }
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  }

  /**
  *  The <tt>Digraph</tt> class represents an directed graph of vertices
  *  named 0 through v-1. It supports the following operations: add an edge to
  *  the graph, iterate over all of edges leaving a vertex.Self-loops are
  *  permitted.
  */
  private class Digraph {
    private final int v;
    private int e;
    private LinkedList<FlowEdge>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private FlowEdge[] edgeTo; // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges in shortest s-v path


    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<FlowEdge>[] temp =
      (LinkedList<FlowEdge>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<FlowEdge>();
    }

    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(FlowEdge edge) {
      int from = edge.v;
      adj[from].add(edge);
      e++;
    }


    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (WeightedDirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<FlowEdge> adj(int v) {
      return adj[v];
    } 

    public void bfs(int source) {
      marked = new boolean[this.v];
      distTo = new int[this.e];
      edgeTo = new FlowEdge[this.v];

      Queue<Integer> q = new LinkedList<Integer>();
      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);

      while (!q.isEmpty()) {
        int v = q.remove();
        for (FlowEdge e : adj(v)) {
          //: check if residual capacity from v to e.w > 0
            if (e.residualCapacityTo(e.other(v))>0&&!marked[e.other(v)]) {
              edgeTo[e.other(v)] = e;
              distTo[e.other(v)] = distTo[v] + 1;
              marked[e.other(v)] = true;
              q.add(e.other(v));
            }
        }
      }
    }

    // return an augmenting path if one exists, otherwise return null
    private boolean hasAugmentingPath(int s, int t) {
      //: start breadth-first search from s and return marked[t]
      bfs(s);
      return marked[t];
    }

     // max flow in flow network G from s to t
     public int FordFulkerson(int s, int t) {
      int value = 0; //max flow value
      // while there exists an augmenting path, use it
      while (hasAugmentingPath(s, t)) {

          // compute bottleneck capacity
          double bottle = Double.POSITIVE_INFINITY;
          for (int v = t; v != s; v = edgeTo[v].other(v)) {
              bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
          }

          System.out.print("Augmenting Path (in reverse): ");
          // augment flow
          for (int v = t; v != s; v = edgeTo[v].other(v)) {
              edgeTo[v].addResidualFlowTo(v, bottle); 
              System.out.print(cityNames[v] + ", ");
          }
          System.out.println(cityNames[s] + " : " + bottle);
          value += bottle;
      }
      return value;
    }


  }

  /**
  *  The <tt>FlowEdge</tt> class represents an edge in a flow network.
  */

  private class FlowEdge {
    private final int v;             // from
    private final int w;             // to 
    private final double capacity;   // capacity
    private double flow;             // flow

    public FlowEdge(int v, int w, double capacity) {
        if (capacity < 0) throw new RuntimeException("Negative edge capacity");
        this.v         = v;
        this.w         = w;  
        this.capacity  = capacity;
        this.flow      = 0;
    }

    private double residualCapacityTo(int vertex) {
        if      (vertex == v) return flow;
        else if (vertex == w) return capacity - flow;
        else throw new RuntimeException("Illegal endpoint");
    }

    private void addResidualFlowTo(int vertex, double delta) {
        if      (vertex == v) flow -= delta;
        else if (vertex == w) flow += delta;
        else throw new RuntimeException("Illegal endpoint");
    }

    private int other(int vertex){
        if (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new RuntimeException("Illegal endpoint");
    }
  }

}
