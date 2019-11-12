import org.jacop.constraints.Alldiff;
import org.jacop.constraints.Distance;
import org.jacop.constraints.Min;
import org.jacop.constraints.Sum;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.LargestDomain;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

public class PhotoDistance {
	public static void main(String[] args) {
		int n = 9; // Number of people in photo.
		int n_prefs = 17;
		// preferences
		int[][] prefs = { { 1, 3 }, { 1, 5 }, { 1, 8 }, { 2, 5 }, { 2, 9 }, { 3, 4 }, { 3, 5 }, { 4, 1 }, { 4, 5 },
				{ 5, 6 }, { 5, 1 }, { 6, 1 }, { 6, 9 }, { 7, 3 }, { 7, 8 }, { 8, 9 }, { 8, 7 } };
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
		for (int i = 1; i <= numPrefs; i++) {
			IntVar sat = new IntVar(store, "dist" + 1, 1, n - 1);
			distances[i - 1] = sat;
		}
		
		// Constrain
		IntVar span = new IntVar(store, "span", 1, n - 1);
		for (int i = 0; i < numPrefs; i++) {
			distances[i].putConstraint(new Distance(arrangement[prefs[i][0] - 1], arrangement[prefs[i][1] - 1], span));
		}
		IntVar cost = new IntVar(store, "cost", 0, Integer.MAX_VALUE);
		store.impose(new Sum(distances, cost));
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(arrangement, new LargestDomain<IntVar>(), new IndomainMin());
		boolean res = search.labeling(store, select, cost);

	}
}
