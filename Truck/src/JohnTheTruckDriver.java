import java.util.Arrays;

import org.jacop.constraints.Constraint;
import org.jacop.constraints.LinearInt;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.Reified;
import org.jacop.constraints.Sum;
import org.jacop.constraints.SumInt;
import org.jacop.constraints.XgtC;
import org.jacop.constraints.XmulCeqZ;
import org.jacop.constraints.XplusYeqC;
import org.jacop.constraints.netflow.NetworkBuilder;
import org.jacop.constraints.netflow.NetworkFlow;
import org.jacop.constraints.netflow.simplex.Arc;
import org.jacop.constraints.netflow.simplex.Node;
import org.jacop.core.BooleanVar;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.floats.constraints.PmulCeqR;
import org.jacop.floats.core.FloatVar;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMax;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

public class JohnTheTruckDriver {
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
		BooleanVar[] usedEdges = new BooleanVar[nEdges];
		Node[] nodes = new Node[graphSize];
		IntVar[] flows = new IntVar[nEdges];

		for (int i = 0; i < nEdges; i++) {
			flows[i] = new IntVar(store, "flow" + i, 0, Integer.MAX_VALUE);
			usedEdges[i] = new BooleanVar(store, ("edge" + i) + ("cost=" + cost[i]));
		}

		nodes[start - 1] = builder.addNode("node" + start + "  (source)", nDests);
		// Add all Nodes but the source node
		for (int i = 0; i < graphSize; i++) {
			if (i + 1 != start) {
				nodes[i] = builder.addNode("node" + (i + 1), 0); // All intermediate nodes get balance of 0 (Not
																	// producing or consuming)
			}
		}
		IntVar networkCost = new IntVar(store, "flowcost", 0, 1000); // Not used in this assignment to determine cost
		builder.setCostVariable(networkCost);
		Arc[] arcs = new Arc[nEdges];
		// Add arcs with corresponding costs to the network
		for (int i = 0; i < nEdges; i++) {
			arcs[i] = builder.addArc(nodes[from[i] - 1], nodes[to[i] - 1], cost[i], flows[i]);
			store.impose(new Reified(new XgtC(flows[i], 0), usedEdges[i]));
		}

		// Use linear int to figure out the cost flow[i] * cost[i]
		// Add a dummy sink
		Node dummySink = builder.addNode("sink", -nDests);
		// Add arcs with cost 0 from the real sink nodes to the dummy sink node
		for (int sink : dest) {
			builder.addArc(nodes[sink - 1], dummySink, 0, 0, Integer.MAX_VALUE);
		}
		// How to use LinearInt instead?
		IntVar costVar = new IntVar(store, "costVar", 0, Integer.MAX_VALUE);
		IntVar[] costs = new IntVar[nEdges];
		for (int i = 0; i < nEdges; i++) {
			costs[i] = new IntVar(store, "cost" + i, 0, 100);
			store.impose(new XmulCeqZ(usedEdges[i], cost[i], costs[i]));
		}
		store.impose(new NetworkFlow(builder));
		store.impose(new SumInt(costs, "==", costVar));
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(usedEdges, null, new IndomainMin<IntVar>());
		boolean res = search.labeling(store, select, costVar);
		System.out.println(costVar);
		System.out.println(Arrays.asList(costs));
	}

}
