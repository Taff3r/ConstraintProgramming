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
		
		
		System.out.println("done, nor errors at least");
	
	}
}
