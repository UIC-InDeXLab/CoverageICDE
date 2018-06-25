package dc;

import pattern.Pattern;
import search.GreedySearch;

import java.sql.DatabaseMetaData;
import dataCollection.DataCollection;
import io.DataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cli.Cli2;

public class test {
	public static void main(String[] args)
	{
		/*
		System.out.println("test");
		Set<Pattern> MUPs = new HashSet<Pattern>();
		MUPs.add(new Pattern(new char[] {'x','x','x','x','x','x'}));
		//MUPs.add(new Pattern(new char[] {'x','x','x','0'}));
		ArrayList<char[]> wtc = DataCollection.WhatToCollect(MUPs, 1, 6, new int[] {3,2,2,2,2,2,2,2});
		for(char[] c:wtc) System.out.println(c);
		*/
		
		String fileName = "data/airbnb_100000.csv";
		int n = 100000;
		int d = 8;
		int level = 3;
		int threshold = 200;

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
		
		ArrayList<char[]> wtc = DataCollection.WhatToCollect(mups, level, d, cardinalities);
		for(char[] c:wtc) System.out.println(c);
		
	}
}
