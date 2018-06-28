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
import dataCollectionNew.DataCollectionBestFirstSearch;
import dataCollectionNew.NaiveDataCollection;
import dataCollectionNew.PatternValueNode;
import io.DataSet;
import pattern.Pattern;
import search.GreedySearch;
import search.HybridSearch;
import search.NaiveSearch;
import search.PatternBreaker;
import search.PatternBreakerOriginal;
import search.PatternCombiner;
import timing.DataCollectionRunnable;
import timing.DataCollectionTimeout;
import utils.FileIOHandle;

public class DataCollectionVariousDimensionTest {
	private static final String DIR_RESULT = "result/";

	private static String genFileName(Cli cmd) {
		String s = "dataCollectionDimensionTest";

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

		String[] algorithms = new String[]{"hybrid", "PatternBreakerOriginal",
				"PatternCombiner"};

		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
		int[] dimensions = new int[]{5, 7, 9, 11, 13, 15, 17, 19, 21};

		List<Map<String, String>> outputTestResultRecords = new ArrayList<Map<String, String>>();
		String outputFileName = genFileName(cmd);
		
		List<String[]> testResults = new ArrayList<String[]>();

		for (int d : dimensions) {
			int threshold = (int) (thresholdRate * n);

			DataSet dataToCheck = new DataSet(fileName,
					Arrays.copyOfRange(cardinalities, 0, d),
					Arrays.copyOfRange(chosenAttributeIds, 0, d), n);

			Map<String, Long> debugInfo = new HashMap<String, Long>();

			String[] tmpResult = new String[3];
			tmpResult[0] = d + "";

			HybridSearch ss = new HybridSearch(dataToCheck);

			long t0 = System.currentTimeMillis();
			Set<Pattern> mups = ss.findMaxUncoveredPatternSet(threshold);
			long t1 = System.currentTimeMillis();

			String breakline = String.format("%0" + 20 + "d", 0).replace("0",
					"-");

			System.out.println(breakline + " Create Mups d = " + d + breakline);
			System.out.println("Algo: HybridRandomSearch");
			System.out.println("# of MUPs: " + mups.size());
			System.out.println("Total Time: " + (t1 - t0) + " ms");
			System.out.println("Visited: "
					+ ss.getDebugInfo().get(NaiveSearch.DEBUG_NODES_VISITED));

			// Create min values covering mups
			System.out.println(breakline
					+ " Create Min Values using GreedySearch " + breakline);

			t0 = System.currentTimeMillis();
			DataCollectionBestFirstSearch search = new DataCollectionBestFirstSearch(
					dataToCheck.cardinalities, mups);
			Queue<PatternValueNode> resultsQueue = new LinkedList<PatternValueNode>();
			try {
				DataCollectionTimeout timeoutBlock = new DataCollectionTimeout(Constants.TIMEOUT);
				DataCollectionRunnable block = new DataCollectionRunnable(resultsQueue) {

					@Override
					public void run() {
						List<PatternValueNode> keyPatterns = search.findMinListOfKeyPatterns();
						if (keyPatterns != null)
							resultsQueue.addAll(keyPatterns);
					}
				};

				timeoutBlock.addBlock(block);

			} catch (Throwable e) {
				System.out.println("TIMEOUT (exceeds " + Constants.TIMEOUT + " seconds). Stopped the test.");
				resultsQueue.clear();			
			} finally {
			}
			t1 = System.currentTimeMillis();
			
			tmpResult[1] = df.format((double)(t1 - t0)/1000);

			System.out.println("num key patterns: " + resultsQueue.size());
			System.out.println("Total Time: " + (t1 - t0) + " ms");

			// Create min values covering mups
			System.out.println(breakline
					+ " Create Min Values using NaiveSearch " + breakline);

			t0 = System.currentTimeMillis();
			NaiveDataCollection naive = new NaiveDataCollection(
					dataToCheck.cardinalities, mups);
			resultsQueue.clear();
			try {
				DataCollectionTimeout timeoutBlock = new DataCollectionTimeout(Constants.TIMEOUT);
				DataCollectionRunnable block = new DataCollectionRunnable(resultsQueue) {

					@Override
					public void run() {
						List<PatternValueNode> keyPatterns = naive.findMinListOfKeyPatterns();
						if (keyPatterns != null)
							resultsQueue.addAll(keyPatterns);
					}
				};

				timeoutBlock.addBlock(block);

			} catch (Throwable e) {
				System.out.println("TIMEOUT (exceeds " + Constants.TIMEOUT + " seconds). Stopped the test.");
				resultsQueue.clear();			
			} finally {
			}
			t1 = System.currentTimeMillis();
			
			tmpResult[2] = df.format((double)(t1 - t0)/1000);

			System.out.println("num key patterns: " + resultsQueue.size());
			System.out.println("Total Time: " + (t1 - t0) + " ms");
			
			testResults.add(tmpResult);
		}

		if (cmd.checkArgument(Cli.CMD_OUTPUT_SHORT)) {
			String msg = "";
			String[] resultItemNamesArray = new String[3];
			resultItemNamesArray[0] = "dimension";
			resultItemNamesArray[1] = "greedy";
			resultItemNamesArray[2] = "naive";


			msg += String.join(",", resultItemNamesArray) + "\n";
			for (String[] resultRecord : testResults) {
				
				msg += String.join(",", resultRecord) + "\n";

			}


			FileIOHandle.writeTextToFile(msg, outputFileName, DIR_RESULT);
		}

	}
}
