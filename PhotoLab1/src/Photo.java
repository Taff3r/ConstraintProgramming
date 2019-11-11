import java.util.Arrays;

import org.jacop.constraints.Alldiff;
import org.jacop.constraints.Distance;
import org.jacop.core.*;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMax;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

public class Photo {

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
		IntVar[] preferences = new IntVar[n];
	
		// Define all preferences for each person
		for (int i = 1; i <= n; i++) {
			int[] numbers = new int[n];
			int counter = 0; 
			for (int[] p: prefs) {
				if(p[0] == i) {
					numbers[counter] = p[1];
					counter++;
				}
			}
			IntVar pref = new IntVar(store, "pref" + i);
			for(int number : numbers) {
				if(number != 0) {
					pref.addDom(number, number);
				}
			}
			preferences[i-1] = pref;
		}
	 
		IntVar[] positions = new IntVar[n];  // Define possible positions of persons
		for (int i = 1; i <= n; i++) {
			positions[i-1] = new IntVar(store, "p" + i, 1, 9);
		}
		store.impose(new Alldiff(positions)); // All positions must be different.
		
		
		// Add constraints, preferences == neighbor == distance of 1 at most.
		IntVar dist = new IntVar(store, "distance", 1, 1);
		for(int i = 0; i < n; i++) {
			IntVar preference = preferences[i];
			if(!preference.dom().isEmpty()) {
				int[] domain = preference.dom().toIntArray();
				for(int pref : domain) {
					store.impose(new Distance(positions[i], positions[pref-1], dist));
				}
			}
		}
		
		System.out.println("CONSISTENT? : " + store.consistency()); // Is consistent?
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(positions, null, new IndomainMin());
		search.setPrintInfo(true);
		boolean result = search.labeling(store, select);
		System.out.println(result);
		System.out.println(Arrays.asList(positions));

	}

}
