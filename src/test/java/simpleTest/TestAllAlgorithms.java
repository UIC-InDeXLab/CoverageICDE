package simpleTest;

import java.util.Arrays;
import java.util.Set;

import io.DataSet;
import pattern.Pattern;
import search.NaiveSearch;
import search.PatternBreaker;
import search.PatternBreakerOriginal;
import search.PatternCombiner;
import search.HybridSearch;
import search.GreedySearch;

public class TestAllAlgorithms {
	public static void main(String[] args) {

		String breakline = String.format("%0" + 50 + "d", 0).replace("0", "-");

		String fileName = "data/airbnb_100000.csv";
		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

		int d = 15;

		int threshold = 500;

		DataSet dataToCheck = new DataSet(fileName,
				Arrays.copyOfRange(cardinalities, 0, d),
				Arrays.copyOfRange(chosenAttributeIds, 0, d), 100000);

		// Test 1 with pattern breaker
		PatternBreakerOriginal pbo = new PatternBreakerOriginal(dataToCheck);

		long t0 = System.currentTimeMillis();
		Set<Pattern> mups = pbo.findMaxUncoveredPatternSet(threshold);
		long t1 = System.currentTimeMillis();

		System.out.println(breakline);
		System.out.println("Algo: Pattern Breaker Original");
		System.out.println(
				"MUPs: " + pbo.getDebugInfo().get(NaiveSearch.DEBUG_MUPS_SIZE));
		System.out.println("Visited: "
				+ pbo.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));

		System.out.println("Total Time: " + (t1 - t0) + " ms");

		// Test 2 with pattern breaker
		PatternBreaker pb = new PatternBreaker(dataToCheck);

		t0 = System.currentTimeMillis();
		mups = pb.findMaxUncoveredPatternSet(threshold);
		t1 = System.currentTimeMillis();
		System.out.println(breakline);
		System.out.println("Algo: Pattern Breaker");
		System.out.println(
				"MUPs: " + pb.getDebugInfo().get(NaiveSearch.DEBUG_MUPS_SIZE));
		System.out.println("Visited: "
				+ pb.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));
		System.out.println("Total Time: " + (t1 - t0) + " ms");
//
//		// Test 3 with pattern combiner
//		PatternCombiner pc = new PatternCombiner(dataToCheck);
//
//		t0 = System.currentTimeMillis();
//		mups = pc.findMaxUncoveredPatternSet(threshold);
//		t1 = System.currentTimeMillis();
//
//		System.out.println(breakline);
//		System.out.println("Algo: Pattern Combiner");
//		System.out.println(
//				"MUPs: " + pc.getDebugInfo().get(NaiveSearch.DEBUG_MUPS_SIZE));
//		System.out.println("Visited: "
//				+ pc.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));
//		System.out.println("Total Time: " + (t1 - t0) + " ms");

		// Test 4 with greedy search
		GreedySearch ss = new GreedySearch(dataToCheck);

		t0 = System.currentTimeMillis();
		mups = ss.findMaxUncoveredPatternSet(threshold);
		t1 = System.currentTimeMillis();

		System.out.println(breakline);
		System.out.println("Algo: GreedySearch");
		System.out.println(
				"MUPs: " + ss.getDebugInfo().get(NaiveSearch.DEBUG_MUPS_SIZE));
		System.out.println("Visited: "
				+ ss.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));
		System.out.println("Total Time: " + (t1 - t0) + " ms");

		// Test 5 with pattern combiner
		HybridSearch hrs = new HybridSearch(dataToCheck);

		t0 = System.currentTimeMillis();
		mups = hrs.findMaxUncoveredPatternSet(threshold);
		t1 = System.currentTimeMillis();

		System.out.println(breakline);
		System.out.println("Algo: HybridRandomSearch");
		System.out.println("MUPs: "
				+ hrs.getDebugInfo().get(NaiveSearch.DEBUG_MUPS_SIZE));
		System.out.println("Visited: "
				+ hrs.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));
		System.out.println("Total Time: " + (t1 - t0) + " ms");
	}

}
