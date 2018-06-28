package vldb;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
import timing.MupSearchRunnable;
import timing.MupSearchTimeout;
import utils.FileIOHandle;

public class DimensionTestWithLevelLimit {
	private static final String DIR_RESULT = "result/";

	private static String genFileName(Cli cmd) {
		String s = "dimensionTest";

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

		double thresholdRate = Double
				.parseDouble(cmd.getArgument(Cli.CMD_THRESHOLD_SHORT));
		int n = Integer.parseInt(cmd.getArgument(Cli.CMD_NUM_RECORDS_SHORT));

		String[] algorithms = new String[]{"hybrid"};

		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
		int[] dimensions = new int[]{15, 17, 19, 21, 23, 25};
		int[] maxLevels = new int[]{3, 5, 7, 9};

		List<Map<String, String>> outputTestResultRecords = new ArrayList<Map<String, String>>();
		String outputFileName = genFileName(cmd);

		List<String[]> testResultList = new LinkedList<String[]>();

		for (int d : dimensions) {
			
			String[] resultRecord = new String[maxLevels.length + 1];
			resultRecord[0] = d + "";
			int resultRecordIdx = 1;
			
			for (int maxLevel  : maxLevels) {
				int threshold = (int) (thresholdRate * n);

				DataSet dataToCheck = new DataSet(fileName,
						Arrays.copyOfRange(cardinalities, 0, d),
						Arrays.copyOfRange(chosenAttributeIds, 0, d), n);

				Map<String, Long> debugInfo = new HashMap<String, Long>();

				for (String algorithm : algorithms) {
					long t0 = System.currentTimeMillis();

					Queue<Pattern> resultsQueue = new LinkedList<Pattern>();
					if (algorithm.equals("hybrid")) {
						HybridSearch search = new HybridSearch(dataToCheck);

						try {
							MupSearchTimeout timeoutBlock = new MupSearchTimeout(
									Constants.TIMEOUT);
							MupSearchRunnable block = new MupSearchRunnable(
									resultsQueue) {

								@Override
								public void run() {
									Set<Pattern> mups = search
											.findMaxUncoveredPatternSet(
													threshold, maxLevel);
									if (mups != null)
										resultsQueue.addAll(mups);
								}
							};

							timeoutBlock.addBlock(block);

						} catch (Throwable e) {
							System.out.println(
									"TIMEOUT (exceeds " + Constants.TIMEOUT
											+ " seconds). Stopped the test.");
							resultsQueue.clear();
						} finally {
						}

						debugInfo = search.getDebugInfo();
					}

					long timespan = System.currentTimeMillis() - t0;

					resultRecord[resultRecordIdx++] = timespan + "";

					String breakline = String.format("%0" + 25 + "d", 0)
							.replace("0", "-");
					System.out.println(breakline + " d = " + d + ", maxLevel = " + maxLevel + " " + breakline);
					System.out.println("Algo: " + algorithm);
					System.out.println("# of MUPs: " + resultsQueue.size());
					System.out.println("Total Time: " + timespan + " ms");
					System.out.println("Visited: "
							+ debugInfo.get(NaiveSearch.DEBUG_NODES_VISITED));

					Map<String, String> testResults = cmd.getArguments();
					testResults.put("TIME",
							df.format((double) timespan / 1000) + "");
					for (Map.Entry<String, Long> e : debugInfo.entrySet()) {
						testResults.put(e.getKey(), e.getValue() + "");
					}

					testResults.put("algorithm", algorithm);
					testResults.put("dimension", d + "");

					outputTestResultRecords.add(testResults);
				}

			}

			testResultList.add(resultRecord);
		}

		if (cmd.checkArgument(Cli.CMD_OUTPUT_SHORT)) {
			String msg = "";
			String[] resultItemNamesArray = new String[maxLevels.length
					+ 1];
			resultItemNamesArray[0] = "dimension";
			for (int i = 0; i < maxLevels.length; i++)
				resultItemNamesArray[i + 1] = maxLevels[i] + "";

			msg += String.join(",", resultItemNamesArray) + "\n";
			for (String[] testResultRecord : testResultList) {
				msg += String.join(",", testResultRecord) + "\n";

			}

			FileIOHandle.writeTextToFile(msg, outputFileName, DIR_RESULT);
		}

	}
}
