import java.util.Arrays;

import org.jacop.constraints.Circuit;
import org.jacop.constraints.Constraint;
import org.jacop.constraints.netflow.NetworkBuilder;
import org.jacop.constraints.netflow.NetworkFlow;
import org.jacop.constraints.netflow.simplex.Arc;
import org.jacop.constraints.netflow.simplex.Node;
import org.jacop.core.IntVar;
import org.jacop.core.Store;

public class Truck {
	public static void main(String[] args) {
		int graph_size = 6;
		int start = 1;
		int n_dests = 1;
		int[] dest = { 6 };
		int n_edges = 7;
		int[] from = { 1, 1, 2, 2, 3, 4, 4 };
		int[] to = { 2, 3, 3, 4, 5, 5, 6 };
		int[] cost = { 4, 2, 5, 10, 3, 4, 11 };

		solve(graph_size, start, n_dests, n_edges, from, to, cost, dest);
	}

	static void solve(int graphSize, int start, int nDests, int nEdges, int[] from, int[] to, int[] cost, int[] dest) {
		Store store = new Store();
		NetworkBuilder builder = new NetworkBuilder();
		// Create all nodes and add them to the network builder
		// Add source node

		Node[] nodes = new Node[graphSize];

		Node source = builder.addNode("node" + start + "  (source)"); // Balance == Flow ?
		nodes[start - 1] = source;
		// Add all Nodes but the source node
		for (int i = 0; i < graphSize; i++) {
			if (i + 1 != start) {
				nodes[i] = builder.addNode("node" + (i + 1));
			}
		}
		
		IntVar costVariable = new IntVar(store, "cost", 0 , 10000);
		Arc[] arcs = new Arc[nEdges];
		// Add arcs with corresponding costs to the network
		for (int i = 0; i < nEdges - 1; i++) {
			arcs[i] = builder.addArc(nodes[from[i] - 1], nodes[to[i] - 1], cost[i]);
		}
		// Add a dummy sink
		Node dummySink = builder.addNode("sink", 0);
		
		// Add arcs with cost 0 from the real sink nodes to the dummy sink node 
		for(int sink: dest) {
			builder.addArc(nodes[sink - 1], dummySink, 0);
		}
		
		System.out.println(Arrays.asList(nodes));
		System.out.println(Arrays.asList(arcs));
		Constraint network = builder.build();
		System.out.println(network);
	}

}
