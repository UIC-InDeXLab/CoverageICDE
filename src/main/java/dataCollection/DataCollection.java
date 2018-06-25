package dataCollection;
import pattern.Pattern;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collection;


public class DataCollection {
	public static ArrayList<char[]> WhatToCollect(Set<Pattern> Mups,int k, int dimensions, int[] cardinalities)
	{
		System.out.println("starting to find the level k patterns");
		ArrayList<Pattern> universe = new ArrayList<Pattern>(levelkPatterns(Mups, k, dimensions,cardinalities));
		System.out.println("starting the freq itemset");
		FreqItemSet FIS = new FreqItemSet();
		ArrayList<PatternHit> PC = FIS.FrequentItemset(universe,dimensions);
		System.out.println("starting the hitting set");
		return HittingSet.Hittingset(universe.size(),PC);
	}
	private static Set<Pattern> levelkPatterns(Set<Pattern> Mups,int k, int dimensions,int[] cardinalities)
	{
		Set<Pattern> patterns = new HashSet<Pattern>();
		for(Pattern P: Mups)
			patterns.addAll(_atLevel(P, k, dimensions,cardinalities));
		return patterns;			
	}
	private static Set<Pattern> _atLevel(Pattern P, int level, int dimensions,int[] cardinalities)
	{
		ArrayList<Integer> Xindex = new ArrayList<Integer>();
		Set<Pattern> patterns = new HashSet<Pattern>();
		char[] pattern = P.data;
		for(int i=0;i<dimensions;i++) if(pattern[i]=='x') Xindex.add(i);
		int xlevel = dimensions-Xindex.size();
		if(xlevel>level) 
			return patterns;
		if(xlevel==level) {
			patterns.add(P);
			return patterns;
		}
		int k = level-xlevel; // the goal is to generate combinations of (Xindex.size() choose k)
		int[] comb = new int[k];
		int[] c = new int[Xindex.size()];
		for(int i=0;i<k;i++) c[i]=1;
		for(int i=k;i<Xindex.size();i++) c[i]=0;
		for(boolean stat=true; stat;stat = getnextComb(c,Xindex.size()))
		{			
			for(int i=0,j=0;i<Xindex.size();i++) if(c[i]==1) comb[j++] = Xindex.get(i);
			patterns.addAll(Arrays.asList(_patternsfor(P,comb,dimensions,cardinalities)));
		}
		return patterns;
	}
	private static Pattern[] _patternsfor(Pattern P, int[] comb,int dimensions,int[] cardinalities)
	{
		int setsize = 1;
		for(int i:comb) setsize*=cardinalities[i];
		Pattern[] patterns = new Pattern[setsize];
		char[] current = P.data.clone();
		for(int i:comb) current[i]='0';
		int j=0;
		for(boolean stat=true; stat;stat = getnextPattern(current,comb,dimensions,cardinalities))
		{
			patterns[j++] = new Pattern(current);
		}
		return patterns;
	}
	private static boolean getnextComb(int[] c,int dimensions)
	{
		// 1- find the first zero from the right
		int padding=0;
		for(;padding<dimensions && c[dimensions-1-padding]==1;padding++) {
			c[dimensions-1-padding]=0; // is this correct?
		}
		if(padding == dimensions) return false;
		
		//2- find the first 1 after that
		int k = dimensions-1-padding;
		for (; k>=0 && c[k]!=1;k--);
		if(k<0) return false;
		//3- replace it with 0 and add the padding after that
		c[k++]=0;
		for(int i=0;i<=padding;i++)
			c[k+i]=1;
		return true;
	}

	private static boolean getnextPattern(char[] current, int[] comb,int dimensions,int[] cardinalities)
	{
		int c=0,j,k = comb.length;
		j = k-1;
		while(j>=0)
		{
			c = Integer.parseInt(String.valueOf(current[comb[j]]));
			if(c<cardinalities[comb[j]]-1) break;
			current[comb[j]]='0';
			j--;
		}
		if(j<0) return false;
		current[comb[j]] = (char)(48+c+1);
		return true;
	}
}


