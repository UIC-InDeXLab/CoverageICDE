package simpleTest;

import search.PatternBreaker;

public class TestAllAlgorithms {
	public static void main(String[] args) {
		PatternBreaker pb = new PatternBreaker();
		pb.findMaxUncoveredPatternSet(0.2);
	}

}
