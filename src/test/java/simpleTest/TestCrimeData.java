package simpleTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import io.DataSet;
import pattern.Pattern;
import search.HybridSearch;
import search.NaiveSearch;
import utils.Plot;

public class TestCrimeData {
	public static void main(String[] args) {
		String fileName = "data/RecidivismData_Original-categorized.csv";
		int[] chosenAttributeIds = {26,27,28,29};
		int[] cardinalities = {2,4,4,7};

		int d = 4;

		

		DataSet dataToCheck = new DataSet(fileName,
				Arrays.copyOfRange(cardinalities, 0, d),
				Arrays.copyOfRange(chosenAttributeIds, 0, d), Integer.MAX_VALUE);

//		int threshold = (int)(dataToCheck.getNumRecords() * 0.01);
		int threshold = 30;
		
//		int threshold = 1;
		
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
		
		class Sortbylevel implements Comparator<Pattern>
		{
		    // Used for sorting in ascending order of
		    // roll number
		    public int compare(Pattern a, Pattern b)
		    {
		        return a.level - b.level;
		    }
		}
		
		List<Pattern> mupsList = new ArrayList<Pattern>(mups);
		Collections.sort(mupsList, new Sortbylevel());
		
		System.out.println(mupsList);
		
		String[] titles = new String[]{"level", "color", "age", "race", "marrital status"};
		
		System.out.println(String.join(",", titles));
		
		for (Pattern p : mupsList) {
			String[] msg = new String[5];
			msg[0] = p.getLevel() + "";
			int i = 1;
			for (char s : p.data) {
				msg[i++] = s + "";
			}
			System.out.println(String.join(",", msg));
		}


		// Plot
//		Plot pl = new Plot();
//		pl.create2dPlot(ss.getTimeSeries());
//		pl.setVisible(true);

	}

}
