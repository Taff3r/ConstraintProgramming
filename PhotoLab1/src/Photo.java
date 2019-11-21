import java.util.Arrays;

import org.jacop.constraints.Alldiff;
import org.jacop.constraints.Constraint;
import org.jacop.constraints.Count;
import org.jacop.constraints.Distance;
import org.jacop.constraints.IfThenElse;
import org.jacop.constraints.Linear;
import org.jacop.constraints.LinearInt;
import org.jacop.constraints.Or;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.Reified;
import org.jacop.constraints.Sum;
import org.jacop.constraints.XeqC;
import org.jacop.constraints.XltY;
import org.jacop.constraints.XmulCeqZ;
import org.jacop.constraints.XmulYeqC;
import org.jacop.constraints.XplusCeqZ;
import org.jacop.constraints.XplusYeqC;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.floats.constraints.PminusQeqR;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.MostConstrainedDynamic;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

public class Photo {
	public static void main(String[] args) {
		int n = 15; // 15 // Number of people in photo.
		int n_prefs = 20; // 17;
		// preferences
		int[][] prefs = { { 1, 3 }, { 1, 5 }, { 2, 5 }, { 2, 8 }, { 2, 9 }, { 3, 4 }, { 3, 5 }, { 4, 1 }, { 4, 15 },
				{ 4, 13 }, { 5, 1 }, { 6, 10 }, { 6, 9 }, { 7, 3 }, { 7, 5 }, { 8, 9 }, { 8, 7 }, { 8, 14 }, { 9, 13 },
				{ 10, 11 } };
		solve(n, n_prefs, prefs);
	}

	static void solve(int n, int numPrefs, int[][] prefs) {
		Store store = new Store();
		IntVar[] arrangement = new IntVar[n]; // Define possible positions of persons
		IntVar[] satisfied = new IntVar[numPrefs];
		for (int i = 1; i <= n; i++) {
			arrangement[i - 1] = new IntVar(store, "p" + i, 1, n);
		}
		store.impose(new Alldiff(arrangement)); // All positions must be different.
		for (int i = 1; i <= numPrefs; i++) {
			IntVar sat = new IntVar(store, "sat" + 1, 0, 1);
			satisfied[i - 1] = sat;
		}
		IntVar distance = new IntVar(store, "distance", 1, 1);
		for (int i = 0; i < numPrefs; i++) {
			// Use XplusYeqC instead in combination with Reified, and Abs/Distance.
			PrimitiveConstraint distConstraint = new Distance(arrangement[prefs[i][0] - 1],
					arrangement[prefs[i][1] - 1], distance);
			PrimitiveConstraint reif = new Reified(distConstraint, satisfied[i]);
			store.impose(reif);
		}
		//store.impose(new XltY(arrangement[0], arrangement[1]));
		IntVar cost = new IntVar(store, "cost", 0, numPrefs);
		IntVar nCost = new IntVar(store, "nCost", -numPrefs, 0);
		store.impose(new Count(satisfied, cost, 1));
		store.impose(new XplusYeqC(cost, nCost, 0));
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(arrangement, new MostConstrainedDynamic(), new IndomainMin());
		boolean res = search.labeling(store, select, nCost);
		System.out.println("Cost: " + cost.value());
	}

	static int sum(IntVar[] vars) {
		int sum = 0;
		for (IntVar v : vars) {
			sum = v.value();
		}
		return sum;
	}
}