package lab2;
import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.search.*;

public class stuff {
	
	public static void main(String[] args){
		
		//here we test the method
		
		
		/*
		int[] a = new int[10];
		//System.out.print(a.length);
		int[][] b = new int[10][4];
		b[1][0] = 7;
		System.out.println(b[0].length);
		int[] j = b[1];
		System.out.println(j.length);
		*/
		
		//Store store = new Store();
		
		/*
		IntVar a = new IntVar(store, "a", 1, 5);
		IntVar b = new IntVar(store, "b", 1, 5);
		IntVar c = new IntVar(store, "c", 1, 5);
		IntVar d = new IntVar(store, "c", 1, 5);
		IntVar e = new IntVar(store, "c", 1, 5);
		IntVar[] v = {a, b, c, d, e};
		Constraint ctr = new Subcircuit(v);
		Constraint ctr2 = new XneqC(a,1);
		store.impose(ctr);
		store.impose(ctr2);
		*/
		
		
		/*
		int graph_size = 6;
		int start = 1;
		int n_dests = 1;
		int[] dest = {6};
		int n_edges = 7;
		int[] from = {1,1,2,2,3,4,4};
		int[] to =   {2,3,3,4,5,5,6};
		int[] cost = {4,2,5,10,3,4,11};
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
		
		solver(graph_size, start, n_dests, dest, n_edges, from, to, cost);
		
		/*
		IntVar[] circuit = new IntVar[graph_size];
		for(int i = 0; i < graph_size; i++){
			if(i+1 != start && i+1 != dest[0]){
				circuit[i] = new IntVar(store, "cir" + (i+1), i+1, i+1);
				for(int j = 0; j < n_edges; j++){
					if(from[j] == i+1){
						circuit[i].addDom(to[j],to[j]);
					}
				}
			}else if(i+1 == start){
				for(int j = 0; j < n_edges; j++){
					if(from[j] == start){
						circuit[i] = new IntVar(store, "cir" + (i+1), to[j], to[j]);
						break;
					}
				}
				for(int j = 0; j < n_edges; j++){
					if(from[j] == i+1){
						circuit[i].addDom(to[j],to[j]);
					}
				}
			}else{
				circuit[i] = new IntVar(store, "cir" + (i+1), start, start);
			}
		}
		
		
		
		IntVar[] v = circuit;
		Constraint ctr = new Subcircuit(v);
		store.impose(ctr);
		*/
		
		
		/*
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(store, v, new IndomainMin<IntVar>() );
		boolean result = search.labeling(store, select);
		if( result ){
			System.out.println("Solution: " + v[0]+", "+v[1] +", "+v[2] +", "+v[3] +", "+v[4]);
		}else 
			{System.out.println("***No");}
		*/
		
		
	}
	
	public static void solver(int graphsize, int start, int ndest, int[] dest, int nedges, int[] from, int[] to, int[] cost){
		
		
		int[] fromTemp = new int[nedges*2];
		int[] toTemp = new int[nedges*2];
		int[] costTemp = new int[nedges*2];
		for(int i = 0; i < nedges; i++){
			fromTemp[i] = from[i];
			toTemp[i] = to[i];
			costTemp[i] = cost[i];
		}
		for(int i = 0; i < nedges; i++){
			fromTemp[nedges + i] = to[i];
			toTemp[nedges + i] = from[i];
			costTemp[nedges + i] = cost[i];
		}	
		nedges = nedges*2;
		from = fromTemp;
		to = toTemp;
		cost = costTemp;
		

		int totCost = 0; //maximum cost
		for(int i = 0; i < cost.length; i++){
			totCost = totCost + cost[i];
		}
		Store store = new Store();
		
		//variables go here
		
		IntVar costVar = new IntVar(store, "cost", 0, totCost);
		
		IntVar[] usedEdges = new IntVar[nedges];
		for(int i = 0; i < nedges; i++){
			usedEdges[i] = new IntVar(store, "from" + from[i] + "to" + to[i], 0, 1);
		}
		
		IntVar[][] subGraphs = new IntVar[ndest][graphsize];
		for(int n = 0; n < ndest; n++){
			for(int i = 0; i < graphsize; i++){
				if(i+1 != start && i+1 != dest[n]){
					subGraphs[n][i] = new IntVar(store, "subcir" + n + "el" + (i+1), i+1, i+1);
					for(int j = 0; j < nedges; j++){
						if(from[j] == i+1){
							subGraphs[n][i].addDom(to[j],to[j]);
						}
					}
				}else if(i+1 == start){
					for(int j = 0; j < nedges; j++){
						if(from[j] == start){
							subGraphs[n][i] = new IntVar(store, "subcir" + n + "el" + (i+1), to[j], to[j]);
							break;
						}
					}
					for(int j = 0; j < nedges; j++){
						if(from[j] == i+1){
							subGraphs[n][i].addDom(to[j],to[j]);
						}
					}
				}else{
					subGraphs[n][i] = new IntVar(store, "subcir" + n + "el" + (i+1), start, start);
				}
			}
		}
		
		IntVar[] costs = new IntVar[nedges];
		for(int i = 0; i < nedges; i++){
			costs[i] = new IntVar(store, "edgePrice" + (i+1), 0, cost[i]);
		}
		
		IntVar[] wiwf = new IntVar[graphsize];
		for(int i = 0; i < graphsize; i++){
			wiwf[i] = new IntVar(store, "number" + (i+1), i+1, i+1);
		}
			
		//constraints go here
		
		Constraint sumCtr = new SumInt(costs, "==", costVar);
		
		Constraint[] costCalc = new Constraint[nedges];
		for(int i = 0; i < nedges; i++){
			costCalc[i] = new XmulCeqZ(usedEdges[i],cost[i],costs[i]);
		}
		
		Constraint[] circuitCtrs = new Constraint[ndest];
		for(int i = 0; i < ndest; i++){
			circuitCtrs[i] = new Subcircuit(subGraphs[i]);
		}
		
		Constraint[][][] pathTakenCtrs = new Constraint[ndest][graphsize][nedges];
		for(int n = 0; n < ndest; n++){
			for(int j = 0; j < graphsize; j++){
				for(int i = 0; i < nedges; i++){
					PrimitiveConstraint[] a = {new XeqC(subGraphs[n][j],to[i]), new XeqC(wiwf[j],from[i])};
					PrimitiveConstraint b = new And(a);
					pathTakenCtrs[n][j][i] = new IfThen(b, new XeqC(usedEdges[i],1));
				}
				
			}
		}
		
		//constraints imposed here
		
		store.impose(sumCtr);
		
		for(int i = 0; i < nedges; i++){
			store.impose(costCalc[i]);
		}
		
		for(int i = 0; i < ndest; i++){
			store.impose(circuitCtrs[i]);
		}
		
		for(int n = 0; n < ndest; n++){
			for(int j = 0; j < graphsize; j++){
				for(int i = 0; i < nedges; i++){
					store.impose(pathTakenCtrs[n][j][i]);
				}
			}
		}
		
		
		//search here
		
		Search<IntVar> label =new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(usedEdges, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
		boolean result = label.labeling(store, select, costVar);
		
		if( result ){
			//System.out.println("Solution: " + sum + ", \n" + photo[0] + ", " + photo[1] + ", " + photo[2] + ", " + photo[3] + ", " + photo[4] + ", " + photo[5] + ", " + photo[6] + ", " + photo[7] + ", " + photo[8]);
			System.out.println("Solution: " + costVar);
		}else {
			System.out.println("***No");
		}
		
		
	}

}
