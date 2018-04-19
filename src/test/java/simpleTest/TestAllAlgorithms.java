package simpleTest;

import io.DataSet;
import search.PatternBreaker;

public class TestAllAlgorithms {
	public static void main(String[] args) {
		String fileName = "";
		DataSet dataToCheck = new DataSet(fileName);
		PatternBreaker pb = new PatternBreaker(dataToCheck);
		pb.findMaxUncoveredPatternSet(0.2);
	}

}
