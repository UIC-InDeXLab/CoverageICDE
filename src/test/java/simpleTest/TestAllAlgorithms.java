package simpleTest;

import java.util.Arrays;
import java.util.Set;

import Pattern.Pattern;
import io.DataSet;
import search.PatternBreaker;
import search.PatternBreakerOriginal;
import search.PatternCombiner;

public class TestAllAlgorithms {
	public static void main(String[] args) {

		String breakline = String.format("%0" + 50 + "d", 0).replace("0", "-");

		String fileName = "data/airbnb_1000.csv";
		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

		int d = 10;

		int threshold = 200;

		DataSet dataToCheck = new DataSet(fileName,
				Arrays.copyOfRange(cardinalities, 0, d),
				Arrays.copyOfRange(chosenAttributeIds, 0, d));

		// Test 1 with pattern breaker
		PatternBreaker pb = new PatternBreaker(dataToCheck);

		long t0 = System.currentTimeMillis();
		Set<Pattern> mups = pb.findMaxUncoveredPatternSet(threshold);
		long t1 = System.currentTimeMillis();
		System.out.println(breakline);
		System.out.println("Algo: Pattern Breaker");
		System.out.println("MUPs: " + mups);
		System.out.println("# of MUPs: " + mups.size());
		System.out.println("Total Time: " + (t1 - t0) + " ms");

		// Test 2 with pattern breaker
		PatternBreakerOriginal pbo = new PatternBreakerOriginal(dataToCheck);

		t0 = System.currentTimeMillis();
		mups = pbo.findMaxUncoveredPatternSet(threshold);
		t1 = System.currentTimeMillis();

		System.out.println(breakline);
		System.out.println("Algo: Pattern Breaker Original");
		System.out.println("MUPs: " + mups);
		System.out.println("# of MUPs: " + mups.size());
		System.out.println("Total Time: " + (t1 - t0) + " ms");

		// Test 3 with pattern combiner
		PatternCombiner pc = new PatternCombiner(dataToCheck);

		t0 = System.currentTimeMillis();
		mups = pc.findMaxUncoveredPatternSet(threshold);
		t1 = System.currentTimeMillis();

		System.out.println(breakline);
		System.out.println("Algo: Pattern Combiner");
		System.out.println("MUPs: " + mups);

		System.out.println("# of MUPs: " + mups.size());
		System.out.println("Total Time: " + (t1 - t0) + " ms");

		// for (Pattern p : mups)
		// System.out.println(p.data);
	}

}
