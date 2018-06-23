package sigmod;

import pattern.Pattern;
import java.sql.DatabaseMetaData;
import dataCollection.DataCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import cli.Cli2;
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

public class TestDC {
	private static final String DIR_RESULT = "result/";
	
	private static String genFileName(Cli2 cmd) {
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
		Cli2 cmd = new Cli2(args);
		cmd.parse();

		String fileName = cmd.getArgument(Cli2.CMD_FILE_SHORT);
		int n = Integer.parseInt(cmd.getArgument(Cli2.CMD_NUM_RECORDS_SHORT));
		int d = Integer.parseInt(cmd.getArgument(Cli2.CMD_NUM_DIMENSIONS_SHORT));
		int l = Integer.parseInt(cmd.getArgument(Cli2.CMD_LEVEL_SHORT));
		int threshold = Integer
				.parseInt(cmd.getArgument(Cli2.CMD_THRESHOLD_SHORT));

		int[] chosenAttributeIds = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		int[] cardinalities = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

		DataSet dataToCheck = new DataSet(fileName,
				Arrays.copyOfRange(cardinalities, 0, d),
				Arrays.copyOfRange(chosenAttributeIds, 0, d), n);

		Map<String, Long> debugInfo = new HashMap<String, Long>();
		Set<Pattern> mups = new HashSet<Pattern>();
		
		GreedySearch search = new GreedySearch(dataToCheck);
		mups = search.findMaxUncoveredPatternSet(threshold);
		System.out.println(mups.size());
		long t0 = System.currentTimeMillis();
		ArrayList<char[]> wtc = DataCollection.WhatToCollect(mups, l, d, cardinalities);
		long timespan = System.currentTimeMillis() - t0;

		String breakline = String.format("%0" + 50 + "d", 0).replace("0", "-");
		System.out.println(breakline);
		System.out.println(n+','+d+','+threshold+','+l+','+mups.size()+','+timespan+','+wtc.size());
		
/*
		if (cmd.checkArgument(Cli.CMD_OUTPUT_SHORT)) {			
			Map<String, String> testResults = cmd.getArguments();
			testResults.put("n", n + "");
			testResults.put("d", d + "");
			testResults.put("level", l + "");
			testResults.put("threshold", threshold + "");
			testResults.put("|mup|", mups.size() + "");
			testResults.put("TIME", timespan + "");
			testResults.put("|wtc|", wtc.size() + "");
			testResults.put("TIME", timespan + "");
			
			FileIOHandle.appendTextToFile(new Gson().toJson(testResults), genFileName(cmd)
					, DIR_RESULT);
		}
*/
	}
}
