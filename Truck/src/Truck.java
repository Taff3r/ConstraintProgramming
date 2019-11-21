import java.util.Arrays;

import org.jacop.constraints.Circuit;
import org.jacop.constraints.Constraint;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.Reified;
import org.jacop.constraints.XgtC;
import org.jacop.constraints.netflow.NetworkBuilder;
import org.jacop.constraints.netflow.NetworkFlow;
import org.jacop.constraints.netflow.simplex.Arc;
import org.jacop.constraints.netflow.simplex.Node;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.floats.constraints.PgtC;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

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
		IntVar[] usedEdges = new IntVar[nEdges * 2];
		// Create all nodes and add them to the network builder
		// Add source node

		Node[] nodes = new Node[graphSize];
		IntVar[] flows = new IntVar[nEdges * 2];
		Node source = builder.addNode("node" + start + "  (source)", nDests); // Balance == Flow ?
		nodes[start - 1] = source;
		// Add all Nodes but the source node
		for (int i = 0; i < graphSize; i++) {
			if (i + 1 != start) {
				nodes[i] = builder.addNode("node" + (i + 1), 0);
			}
		}

		IntVar costVariable = new IntVar(store, "cost", 0, Integer.MAX_VALUE); // Whats this for?
		builder.setCostVariable(costVariable);
		Arc[] arcs = new Arc[nEdges * 2];
		// Add arcs with corresponding costs to the network
		for (int i = 0; i < nEdges - 1; i += 2) {
			IntVar flow = new IntVar(store, "flow" + i, 0, Integer.MAX_VALUE);
			arcs[i] = builder.addArc(nodes[from[i] - 1], nodes[to[i] - 1], cost[i], flow);
			PrimitiveConstraint usedEdge = new XgtC(flow, 0);
			PrimitiveConstraint reif = new Reified(usedEdge, usedEdges[i]);
			store.impose(reif);
			IntVar flow2 = new IntVar(store, "flow" + (i + 1), 0, Integer.MAX_VALUE);
			arcs[i + 1] = builder.addArc(nodes[to[i] - 1], nodes[from[i] - 1], cost[i], flow2);
			PrimitiveConstraint ue = new XgtC(flow2, 0);
			PrimitiveConstraint rif = new Reified(ue, usedEdges[i+1]);
			store.impose(rif);
		}
		// Use linear int to figure out the cost flow[i] * cost[i]
		// Add a dummy sink
		Node dummySink = builder.addNode("sink", -nDests);

		// Add arcs with cost 0 from the real sink nodes to the dummy sink node
		for (int sink : dest) {
			builder.addArc(nodes[sink - 1], dummySink, -nDests);
		}
		store.impose(new NetworkFlow(builder));

		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(usedEdges, null, new IndomainMin());
		boolean res = search.labeling(store,select);
	}

}
