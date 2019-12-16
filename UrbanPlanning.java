package lab5;
import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.search.*;

public class UrbanPlanning {
	
	public static void main(String[] args){
		
		int n = 5;
		int n_commercial = 13;
		int n_residential = 12;
		int[] point_distribution ={-5,-4,-3,3,4,5};
		UrbanPlanner(n, n_commercial, n_residential, point_distribution);
		
		
		/*
		int n = 5;
		int n_commercial = 7;
		int n_residential = 18;
		int[] point_distribution = {-5,-4,-3,3, 4, 5};
		UrbanPlanner(n, n_commercial, n_residential, point_distribution);
		*/
		/*
		int n = 7;
		int n_commercial = 20;
		int n_residential = 29;
		int[] point_distribution ={-7,-6,-5,-4,4, 5, 6, 7};
		UrbanPlanner(n, n_commercial, n_residential, point_distribution);
		*/
	}
	
	
	public static void UrbanPlanner(int n, int n_commercial, int n_residential, int[] point_distribution){
		Store store = new Store();
		
		IntVar[][] grid = new IntVar[n][n]; //a one means residential
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				grid[i][j] = new IntVar(store, "row" +i+"column"+j, 0, 1);
			}
		}
	
	
		IntVar[] rowSums = new IntVar[n]; //this let's us count the reidents in each row
		for(int i = 0; i < n; i++){
			rowSums[i] = new IntVar(store, "row" +i+"sum", 0, n);
		}
		
		IntVar[] colSums = new IntVar[n]; //this let's us count the reidents in each col
		for(int i = 0; i < n; i++){
			colSums[i] = new IntVar(store, "col" +i+"sum", 0, n);
		}
		
		IntVar[] colPoints = new IntVar[n]; //the points of each column calculated from colSums
		for(int i = 0; i < n; i++){
			colPoints[i] = new IntVar(store, "col"+i+"points", -100,100);
		}
		
		IntVar[] rowPoints = new IntVar[n]; //the points of each row calculated from rowSums
		for(int i = 0; i < n; i++){
			rowPoints[i] = new IntVar(store, "row" +i+"points", -100,100);
		}
		
		IntVar numRes = new IntVar(store, "numRes", n_residential, n_residential);
		
		IntVar colCost = new IntVar(store, "ColCost", -1000, 1000);
		
		IntVar rowCost = new IntVar(store, "rowCost", -1000, 1000);
		
		IntVar cost = new IntVar(store, "cost", -1000, 1000);
		
		IntVar negCost = new IntVar(store, "negCost", -1000, 1000);
		
		
		// Constraints
		
		//constraint for rowSums
		for(int i = 0; i < n; i++){
			Constraint c = new SumInt(grid[i], "==", rowSums[i]);
			store.impose(c);
		}
		
		//constraint for colSums
		for(int i = 0; i < n; i++){
			IntVar[] col = new IntVar[n];
			for(int j = 0; j < n; j++){
				col[j] = grid[i][j];
			}
			Constraint c = new SumInt(col, "==", colSums[i]);
			store.impose(c);
		}
		
		//enforce number of residential lots
		store.impose(new SumInt(rowSums, "==", numRes));
		
		//calculate rowPoints
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n+1; j++){
				IntVar b =  new IntVar(store, "row b " + i + " " + j, 0, 1);
				store.impose(new Reified(new XeqC(rowSums[i], j),b));
				store.impose(new Reified(new XeqC(rowPoints[i], point_distribution[j]),b));
			}
		}
		
		//calculate colPoints
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n+1; j++){
				IntVar b =  new IntVar(store, "col b " + i + " " + j, 0, 1);
				store.impose(new Reified(new XeqC(colSums[i], j),b));
				store.impose(new Reified(new XeqC(colPoints[i], point_distribution[j]),b));
			}
		}
		
		//cost calculated
		store.impose(new SumInt(rowPoints, "==", rowCost));
		store.impose(new SumInt(colPoints, "==", colCost));
		IntVar[] v = {colCost, rowCost};  
		store.impose(new SumInt(v, "==", cost));
		
		store.impose(new XmulCeqZ(cost, -1, negCost));
		
		//search
		
		System.out.println("done, no errors at least");
		
		System.out.println(store.consistency());
		
		IntVar[] gridList = new IntVar[n*n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j <n; j++){
				gridList[i*n + j] = grid[i][j];
			}
		}
		
		
		//???????????????????????????????
		/*
		IntVar[] gridListHalf1 = new IntVar[(n*n)/2];
		IntVar[] gridListHalf2 = new IntVar[(n*n)/2];
		IntVar zero = new IntVar(store, "zero", 0,0);
		
		for(int i = 0; i < (n*n)/2; i++){
			if(i%n != 0){
				gridListHalf1[i] = gridList[i];
			}else{
				gridListHalf1[i] = zero;
			}
		}
		for(int i = 0; i < (n*n)/2; i++){
			if(i%n != 0){
				gridListHalf2[i] = gridList[((n*n)/2) + i + ((n*n)%2)];
			}else{
				gridListHalf2[i] = zero;
			}
		}
		
		IntVar glh1Cost = new IntVar(store, "glh1", -1000, 1000);
		IntVar glh2Cost = new IntVar(store, "glh2", -1000, 1000);
		
		store.impose(new SumInt(gridListHalf1, "==", glh1Cost));
		store.impose(new SumInt(gridListHalf2, "==", glh2Cost));
		store.impose(new XgteqY(glh1Cost,glh2Cost));
		*/
		//???????????????????
		
		store.imposeDecomposition(new Lex(grid, true));
		IntVar[][] gridT = new IntVar[n][n]; 
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				gridT [i][j] = grid[j][i]; 
			}
		}
		store.imposeDecomposition(new Lex(gridT, true));
		/*
		// diag 1 = /
		IntVar[] leftOfDiag1 = new IntVar[((n-1)*n)/2];
		int a = 0;
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n - 1 -i ;j++){
				leftOfDiag1[a] = grid[i][j]; 
				a++;
			}
		}
		
		a = 0;
		IntVar[] rightOfDiag1 = new IntVar[((n-1)*n)/2];
		for(int i = 0; i < n; i++){
			for(int j = n - 1 ; j > n - 1 - i;j--){
				rightOfDiag1[a] = grid[i][j];
				a++;
			}
		}
		
		IntVar lod1Cost = new IntVar(store, "lod1", -1000, 1000);
		IntVar rod1Cost = new IntVar(store, "rod2", -1000, 1000);
		store.impose(new SumInt(leftOfDiag1, "==", lod1Cost));
		store.impose(new SumInt(rightOfDiag1, "==", rod1Cost));
		store.impose(new XgteqY(lod1Cost,rod1Cost));
		*/
		
		
		Search<IntVar> label =new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(gridList, new MostConstrainedDynamic<IntVar>(), new IndomainMin<IntVar>());
		boolean result = label.labeling(store, select, negCost);
		
		for(int i = 0; i < n; i++){
			for(int j = 0; j <n; j++){
				System.out.println("Solution: " + grid[i][j]);
			}
		}
		
		
		
	}
}
