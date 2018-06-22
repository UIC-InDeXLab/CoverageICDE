package vldb;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

public class DataSizeTest {
	private static final String DIR_RESULT = "result/";

	private static String genFileName(Cli cmd) {
		String s = "sizeTest";

		for (String debugType : cmd.commandTypes) {
			if (cmd.getArgument(debugType) != null && !debugType.equals("a")) {
				s += "_" + debugType + "_" + cmd.getArgument(debugType);

			}
		}

		SimpleDateFormat formatter = new SimpleDateFormat("_MM_dd_HH_mm");
		Date date = new Date();
		s += formatter.format(date);

		s += ".csv";
		return s;
	}

	public static void main(String[] args) {
		Cli cmd = new Cli(args);
		cmd.parse();
		
		DecimalFormat df = new DecimalFormat("#.###");

		String fileName = cmd.getArgument(Cli.CMD_FILE_SHORT);
		int d = Integer.parseInt(cmd.getArgument(Cli.CMD_NUM_DIMENSIONS_SHORT));
		double thresholdRate = Double
				.parseDouble(cmd.getArgument(Cli.CMD_THRESHOLD_SHORT));

		String[] algorithms = new String[]{"hybrid", "PatternBreakerOriginal",
				"PatternCombiner"};

		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

		int[] dataSizes = new int[]{1000, 10000, 100000, 1000000};

		List<Map<String, String>> outputTestResultRecords = new ArrayList<Map<String, String>>();
		String outputFileName = genFileName(cmd);

		for (int n : dataSizes) {
			int threshold = (int) (thresholdRate * n);

			DataSet dataToCheck = new DataSet(fileName,
					Arrays.copyOfRange(cardinalities, 0, d),
					Arrays.copyOfRange(chosenAttributeIds, 0, d), n);

			Map<String, Long> debugInfo = new HashMap<String, Long>();
			Set<Pattern> mups = new HashSet<Pattern>();

			for (String algorithm : algorithms) {
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

				String breakline = String.format("%0" + 50 + "d", 0)
						.replace("0", "-");
				System.out.println(breakline);
				System.out.println("Algo: " + algorithm);
				System.out.println("# of MUPs: " + mups.size());
				System.out.println("Total Time: " + timespan + " ms");
				System.out.println("Visited: "
						+ debugInfo.get(NaiveSearch.DEBUG_NODES_VISITED));

				Map<String, String> testResults = cmd.getArguments();
				testResults.put("TIME", df.format((double)timespan/1000) + "");
				for (Map.Entry<String, Long> e : debugInfo.entrySet()) {
					testResults.put(e.getKey(), e.getValue() + "");
				}

				testResults.put("algorithm", algorithm);
				testResults.put("size", n + "");

				outputTestResultRecords.add(testResults);

			}
		}

		if (cmd.checkArgument(Cli.CMD_OUTPUT_SHORT)) {
			String msg = "";
			String[] resultItemNamesArray = new String[algorithms.length + 1];
			resultItemNamesArray[0] = "n";
			for (int i = 0; i < algorithms.length; i++)
				resultItemNamesArray[i + 1] = algorithms[i];

			msg += String.join(",", resultItemNamesArray) + "\n";
			for (int n : dataSizes) {
				String[] tmpMsg = new String[algorithms.length + 1];
				tmpMsg[0] = n + "";

				int i = 1;

				for (String algorithm : algorithms) {
					for (Map<String, String> resultEntry : outputTestResultRecords) {
						if (resultEntry.get("size").equals(n + "") && resultEntry
								.get("algorithm").equals(algorithm))
							tmpMsg[i++] = resultEntry.get("TIME");

					}
				}

				msg += String.join(",", tmpMsg) + "\n";

			}

			FileIOHandle.writeTextToFile(msg, outputFileName, DIR_RESULT);
		}

	}
}