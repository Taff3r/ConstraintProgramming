import java.util.Arrays;

import org.jacop.constraints.Alldiff;
import org.jacop.constraints.Distance;
import org.jacop.constraints.Max;
import org.jacop.constraints.Min;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.Reified;
import org.jacop.constraints.Sum;
import org.jacop.constraints.XplusYeqC;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMax;
import org.jacop.search.IndomainMin;
import org.jacop.search.LargestDomain;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

public class PhotoDistance {
	public static void main(String[] args) {
		int n = 15; // Number of people in photo.
		int n_prefs = 20;
		// preferences
		int[][] prefs = { { 1, 3 }, { 1, 5 }, { 2, 5 }, { 2, 8 }, { 2, 9 }, { 3, 4 }, { 3, 5 }, { 4, 1 }, { 4, 15 },
				{ 4, 13 }, { 5, 1 }, { 6, 10 }, { 6, 9 }, { 7, 3 }, { 7, 5 }, { 8, 9 }, { 8, 7 }, { 8, 14 }, { 9, 13 },
				{ 10, 11 } };
		solve(n, n_prefs, prefs);
	}

	static void solve(int n, int numPrefs, int[][] prefs) {
		Store store = new Store();
		IntVar[] arrangement = new IntVar[n]; // Define possible positions of persons
		IntVar[] distances = new IntVar[numPrefs];
		for (int i = 1; i <= n; i++) {
			arrangement[i - 1] = new IntVar(store, "p" + i, 1, n);
		}
		store.impose(new Alldiff(arrangement)); // All positions must be different.

		for (int i = 0; i < numPrefs; i++) {
			IntVar distancei = new IntVar(store, "dist" + i, 1, n - 1);
			distances[i] = distancei;
			PrimitiveConstraint distConstraint = new Distance(arrangement[prefs[i][0] - 1],
					arrangement[prefs[i][1] - 1], distancei);
			store.impose(distConstraint);
		}
		IntVar maxDistance = new IntVar(store, "Max", 1, n);
		store.impose(new Max(distances, maxDistance));
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(arrangement, null, new IndomainMin());
		boolean res = search.labeling(store, select, maxDistance);
		System.out.println(Arrays.asList(maxDistance));

	}
}
