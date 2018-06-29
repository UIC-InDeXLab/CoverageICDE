package simpleTest;

import java.util.Arrays;
import java.util.Set;

import io.DataSet;
import pattern.Pattern;
import search.HybridSearch;
import search.NaiveSearch;
import utils.Plot;

public class HybridSearchTest {
	public static void main(String[] args) {
		String fileName = "data/airbnb_1million.csv";
		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

		int d = 17;

		int threshold = 1000;

		DataSet dataToCheck = new DataSet(fileName,
				Arrays.copyOfRange(cardinalities, 0, d),
				Arrays.copyOfRange(chosenAttributeIds, 0, d), 1000000);

		// Test 1 with pattern breaker
		HybridSearch ss = new HybridSearch(dataToCheck);

		long t0 = System.currentTimeMillis();
		Set<Pattern> mups = ss.findMaxUncoveredPatternSet(threshold);
		long t1 = System.currentTimeMillis();

		String breakline = String.format("%0" + 50 + "d", 0).replace("0", "-");

		System.out.println(breakline);
		System.out.println("Algo: HybridRandomSearch");
		System.out.println("# of MUPs: " + mups.size());
		System.out.println("Total Time: " + (t1 - t0) + " ms");
		System.out.println("Visited: "
				+ ss.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));
		System.out.println("Hits: " + ss.getNumHits());
		
		System.out.println(mups);


		// Plot
//		Plot pl = new Plot();
//		pl.create2dPlot(ss.getTimeSeries());
//		pl.setVisible(true);

	}

}
