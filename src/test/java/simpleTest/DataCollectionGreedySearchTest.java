package simpleTest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import dataCollectionNew.DataCollectionBestFirstSearch;
import dataCollectionNew.DataCollectionGreedySearch;
import dataCollectionNew.PatternValueNode;
import io.DataSet;
import pattern.Pattern;
import search.HybridSearch;
import search.NaiveSearch;
import utils.Plot;

public class DataCollectionGreedySearchTest {
	public static void main(String[] args) {
		String fileName = "data/airbnb_1million.csv";
		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

		int d = 15;

		int threshold = 5000;

		DataSet dataToCheck = new DataSet(fileName,
				Arrays.copyOfRange(cardinalities, 0, d),
				Arrays.copyOfRange(chosenAttributeIds, 0, d), 1000000);

		// Generate mups
		HybridSearch ss = new HybridSearch(dataToCheck);

		long t0 = System.currentTimeMillis();
		Set<Pattern> mups = ss.findMaxUncoveredPatternSet(threshold);
		long t1 = System.currentTimeMillis();

		String breakline = String.format("%0" + 20 + "d", 0).replace("0", "-");

		System.out.println(breakline + " Create Mups " + breakline);
		System.out.println("Algo: HybridRandomSearch");
		System.out.println("# of MUPs: " + mups.size());
		System.out.println("Total Time: " + (t1 - t0) + " ms");
		System.out.println("Visited: "
				+ ss.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));

		// Create min values covering mups
		System.out.println(breakline + " Create Min Values " + breakline);

		t0 = System.currentTimeMillis();
		DataCollectionGreedySearch s = new DataCollectionGreedySearch(
				dataToCheck.cardinalities, mups);
		List<PatternValueNode> keyPatterns = s.findMinListOfKeyPatterns();
		t1 = System.currentTimeMillis();

		System.out.println("num key patterns: " + keyPatterns.size());
		System.out.println(keyPatterns);
		System.out.println("Total Time: " + (t1 - t0) + " ms");

		// Verification
		System.out.println(breakline + " Verification " + breakline);

		t0 = System.currentTimeMillis();
		for (PatternValueNode v : keyPatterns) {
			mups.removeIf(p -> p.covers(p.data, v.getData()));
		}

		t1 = System.currentTimeMillis();
		if (mups.isEmpty())
			System.out.println("Result: success");
		else
			System.out.println("Result: failure");

		System.out.println("Total Time: " + (t1 - t0) + " ms");
	}

}
