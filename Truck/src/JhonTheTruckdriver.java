package lab2;
import java.util.Arrays;

import org.jacop.constraints.Assignment;
import org.jacop.constraints.Constraint;
import org.jacop.constraints.IfThenElse;
import org.jacop.constraints.LinearInt;
import org.jacop.constraints.Or;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.Reified;
import org.jacop.constraints.Sum;
import org.jacop.constraints.SumInt;
import org.jacop.constraints.XeqC;
import org.jacop.constraints.XeqY;
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
import org.jacop.search.SmallestDomain;

public class JhonTheTruckdriver {
	public static void main(String[] args) {
		/*
		int graph_size = 6;
		int start = 1;
		int n_dests = 1;
		int[] dest = { 6 };
		int n_edges = 7;
		int[] from = { 1, 1, 2, 2, 3, 4, 4 };
		int[] to = { 2, 3, 3, 4, 5, 5, 6 };
		int[] cost = { 4, 2, 5, 10, 3, 4, 11 };
		*/
	/*	
		int graph_size = 6;
		int start = 1;
		int n_dests = 2;
		int[] dest = {5,6};
		int n_edges = 7;
		int[] from = {1,1,2,2,3,4,4};
		int[] to =   {2,3,3,4,5,5,6};
		int[] cost = {4,2,5,10,3,4,11};
		*/
		int graph_size = 6;
		int start = 1;
		int n_dests = 2;
		int[] dest = {5,6};
		int n_edges = 9;
		int[] from = {1,1,1,2,2,3,3,3,4};
		int[] to = {2,3,4,3,5,4,5,6,6};
		int[] cost = {6,1,5,5,3,5,6,4,2};

		solve(graph_size, start, n_dests, n_edges, from, to, cost, dest);
	}

	static void solve(int graphSize, int start, int nDests, int nEdges, int[] from, int[] to, int[] cost, int[] dest) {
		
		int[] fromTemp = new int[nEdges*2];
		int[] toTemp = new int[nEdges*2];
		int[] costTemp = new int[nEdges*2];
		for(int i = 0; i < nEdges; i++){
			fromTemp[i] = from[i];
			toTemp[i] = to[i];
			costTemp[i] = cost[i];
		}
		for(int i = 0; i < nEdges; i++){
			fromTemp[nEdges + i] = to[i];
			toTemp[nEdges + i] = from[i];
			costTemp[nEdges + i] = cost[i];
		}	
		nEdges = nEdges*2;
		from = fromTemp;
		to = toTemp;
		cost = costTemp;
		
		
		Store store = new Store();
		NetworkBuilder builder = new NetworkBuilder();
		IntVar[] usedEdges = new IntVar[nEdges];
		Node[] nodes = new Node[graphSize];
		IntVar[] flows = new IntVar[nEdges];

		for (int i = 0; i < nEdges; i++) {
			flows[i] = new IntVar(store, "flow" + i, 0, 1000);
			usedEdges[i] = new IntVar(store, ("edge" + from[i] + "to" + to[i]) + ("cost" + cost[i]), 0, 1); // An edge can either be used or
																						// not (boolean)
		}

		nodes[start - 1] = builder.addNode("node" + start + "  (source)", nDests); // Balance = nDest (Production from
																					// this node)
		// Add all Nodes but the source node
		for (int i = 0; i < graphSize; i++) {
			if (i + 1 != start) {
				nodes[i] = builder.addNode("node" + (i + 1), 0); // All intermediate nodes get balance of 0 (Not
																	// producing or consuming)
			}
		}
		Arc[] arcsForward = new Arc[nEdges];
		// Add arcs with corresponding costs to the network
		for (int i = 0; i < nEdges; i++) {
			arcsForward[i] = builder.addArc(nodes[from[i] - 1], nodes[to[i] - 1], cost[i], flows[i]);
			PrimitiveConstraint ifX = new XgtC(flows[i], 0);
			PrimitiveConstraint thenX = new XeqC(usedEdges[i], 1);
			PrimitiveConstraint elseX = new XeqC(usedEdges[i], 0);
			store.impose(new IfThenElse(ifX, thenX, elseX));
		}

		// Make bidirectional
//		Arc[] arcsBackward = new Arc[nEdges];
//		for (int i = 0; i < nEdges; i++) {
//			arcsBackward[i] = builder.addArc(nodes[to[i] - 1], nodes[from[i] - 1], cost[i], flows[i]);
//			store.impose(new Reified(new XeqC(flows[i], 1), usedEdges[i]));
//		}
//		
		// Use linear int to figure out the cost flow[i] * cost[i]
		// Add a dummy sink
		Node dummySink = builder.addNode("sink", -nDests);
		// Add arcs with cost 0 from the real sink nodes to the dummy sink node
		for (int sink : dest) {
			builder.addArc(nodes[sink - 1], dummySink, 0, 0, 1); // Add arcs to the sink. Does not have to be
																		// bidirectional (?)
		}
		// How to use LinearInt instead?
		IntVar costVar = new IntVar(store, "costVar", 0, 100); // Our cost variable
		builder.setCostVariable(new IntVar(store, "random", 0, 1000)); // Maybe not?
		IntVar[] costs = new IntVar[nEdges]; // All costs
		for (int i = 0; i < nEdges; i++) {
			costs[i] = new IntVar(store, "cost" + i, 0, 1000);
			store.impose(new XmulCeqZ(usedEdges[i], cost[i], costs[i])); // Cost[i] = Edge_used(0|1) * cost of arc(a
																			// weight)
		}
		store.impose(new NetworkFlow(builder));
		store.impose(new Sum(costs, costVar)); // Define the value of costVar to be the sum of all costs.
		//System.out.println(store.toString());
		
//		Search<IntVar> search = new DepthFirstSearch<IntVar>();
	//	SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(usedEdges, new SmallestDomain<IntVar>(),
	//			new IndomainMin<IntVar>());
	//	System.out.println(store.toString());
//		search.labeling(store, select, costVar);
	//	System.out.println(Arrays.asList(usedEdges));
	//	System.out.println(Arrays.asList(flows));
		
//		System.out.println(Arrays.asList(arcsForward));
//		System.out.println(costVar);
		
		Search<IntVar> label =new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(usedEdges, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
		boolean result = label.labeling(store, select, costVar);
		
		if(result){
			System.out.println("Solution: " + costVar);
//			for(int i = 0; i < n; i++){
	//			System.out.print(photo[i] + ", ");
		//	}
		}else{
			System.out.println("***No");
		}
	}

}
