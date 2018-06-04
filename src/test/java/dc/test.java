package dc;

import pattern.Pattern;
import java.sql.DatabaseMetaData;
import dataCollection.DataCollection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class test {
	public static void main(String[] args)
	{
		System.out.println("test");
		Set<Pattern> MUPs = new HashSet<Pattern>();
		MUPs.add(new Pattern(new char[] {'x','x','1','x'}));
		MUPs.add(new Pattern(new char[] {'x','x','x','0'}));
		ArrayList<char[]> wtc = DataCollection.WhatToCollect(MUPs, 1, 4, new int[] {2,2,2,2});
		for(char[] c:wtc) System.out.println(c);
	}
}
