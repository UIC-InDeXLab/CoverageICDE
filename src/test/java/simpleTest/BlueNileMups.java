package simpleTest;

import java.util.Arrays;
import java.util.Set;

import io.DataSet;
import pattern.Pattern;
import search.HybridSearch;
import search.NaiveSearch;
import utils.Plot;

public class BlueNileMups {
	public static void main(String[] args) {
		String fileName = "data/BlueNile_categorical.csv";
		int[] chosenAttributeIds = {1, 2, 3, 4, 5, 6, 7};
		int[] cardinalities = {10, 7, 4, 8, 3, 3, 5};
		int d = 7;

		double thresholdRate = 0.00001;

		DataSet dataToCheck = new DataSet(fileName,
				Arrays.copyOfRange(cardinalities, 0, d),
				Arrays.copyOfRange(chosenAttributeIds, 0, d), 1000000);
		
		int threshold = (int) (thresholdRate * dataToCheck.getNumRecords());

		// Test 1 with pattern breaker
		HybridSearch ss = new HybridSearch(dataToCheck);

		long t0 = System.currentTimeMillis();
		Set<Pattern> mups = ss.findMaxUncoveredPatternSet(threshold);
		long t1 = System.currentTimeMillis();

		String breakline = String.format("%0" + 50 + "d", 0).replace("0", "-");
//
//		System.out.println(breakline);
//		System.out.println("Algo: HybridRandomSearch");
//		System.out.println("# of MUPs: " + mups.size());
//		System.out.println("Total Time: " + (t1 - t0) + " ms");
//		System.out.println("Visited: "
//				+ ss.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));
//		System.out.println("Hits: " + ss.getNumHits());
//		
//		System.out.println(mups);


		// Plot
//		Plot pl = new Plot();
//		pl.create2dPlot(ss.getTimeSeries());
//		pl.setVisible(true);

		
		long[] levelInfo = new long[mups.size()];
		
		int[] count = new int[d+1];
		
		int i = 0;
		for (Pattern p : mups) {
			levelInfo[i++] = p.getLevel();
			count[p.getLevel()]++;
		}
		
		for (int j = 0; j < d; j++) {
			System.out.println(j + "," + count[j]);
		}

		Plot pl2 = new Plot();
		pl2.createBarchart(levelInfo, d);
		pl2.setVisible(true);
	}

}
