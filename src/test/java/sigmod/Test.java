package sigmod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cli.Cli;
import io.DataSet;
import pattern.Pattern;
import search.GreedySearch;
import search.HybridSearch;
import search.NaiveSearch;
import search.PatternBreaker;
import search.PatternBreakerOriginal;
import search.PatternCombiner;
import utils.FileIOHandle;
import com.google.gson.Gson;

public class Test {
	private static final String DIR_RESULT = "result/";
	
	private static String genFileName(Cli cmd) {
		String s = "test";
		for (String debugType : cmd.commandTypes) {
			if (cmd.getArgument(debugType) != null) {
				s += "_" + debugType + "_" + cmd.getArgument(debugType); 
						
			}
		}
		
		s += ".csv";
		return s;
	}

	public static void main(String[] args) {
		Cli cmd = new Cli(args);
		cmd.parse();

		String fileName = cmd.getArgument(Cli.CMD_FILE_SHORT);
		String algorithm = cmd.getArgument(Cli.CMD_ALGORITHM_SHORT);
		int n = Integer.parseInt(cmd.getArgument(Cli.CMD_NUM_RECORDS_SHORT));
		int d = Integer.parseInt(cmd.getArgument(Cli.CMD_NUM_DIMENSIONS_SHORT));
		int threshold = Integer
				.parseInt(cmd.getArgument(Cli.CMD_THRESHOLD_SHORT));

		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

		DataSet dataToCheck = new DataSet(fileName,
				Arrays.copyOfRange(cardinalities, 0, d),
				Arrays.copyOfRange(chosenAttributeIds, 0, d), n);

		Map<String, Long> debugInfo = new HashMap<String, Long>();
		Set<Pattern> mups = new HashSet<Pattern>();

		long t0 = System.currentTimeMillis();

		if (algorithm.equals("greedy")) {
			GreedySearch search = new GreedySearch(dataToCheck);
			mups = search.findMaxUncoveredPatternSet(threshold);
			debugInfo = search.getDebugInfo();
		} else if (algorithm.equals("hybrid")) {
			HybridSearch search = new HybridSearch(dataToCheck);
			mups = search.findMaxUncoveredPatternSet(threshold);
			debugInfo = search.getDebugInfo();
		} else if (algorithm.equals("PatternBreaker")) {
			PatternBreaker search = new PatternBreaker(dataToCheck);
			mups = search.findMaxUncoveredPatternSet(threshold);
			debugInfo = search.getDebugInfo();
		} else if (algorithm.equals("PatternBreakerOriginal")) {
			PatternBreakerOriginal search = new PatternBreakerOriginal(
					dataToCheck);
			mups = search.findMaxUncoveredPatternSet(threshold);
			debugInfo = search.getDebugInfo();
		} else if (algorithm.equals("PatternCombiner")) {
			PatternCombiner search = new PatternCombiner(dataToCheck);
			mups = search.findMaxUncoveredPatternSet(threshold);
			debugInfo = search.getDebugInfo();
		}
		
		long timespan = System.currentTimeMillis() - t0;

		String breakline = String.format("%0" + 50 + "d", 0).replace("0", "-");
		System.out.println(breakline);
		System.out.println("Algo: " + algorithm);
		System.out.println("# of MUPs: " + mups.size());
		System.out.println(
				"Total Time: " + timespan + " ms");
		System.out.println(
				"Visited: " + debugInfo.get(NaiveSearch.DEBUG_NODES_VISITED));
		
		

		if (cmd.checkArgument(Cli.CMD_OUTPUT_SHORT)) {			
			Map<String, String> testResults = cmd.getArguments();
			testResults.put("TIME", timespan + "");
			for (Map.Entry<String, Long> e : debugInfo.entrySet()) {
				testResults.put(e.getKey(), e.getValue() + "");
			}
			FileIOHandle.writeTextToFile(new Gson().toJson(testResults), genFileName(cmd)
					, DIR_RESULT);
		}

	}
}
