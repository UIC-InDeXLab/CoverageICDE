package dataCollection;

import java.util.ArrayList;
import java.util.Arrays;
import pattern.Pattern;
import dataCollection.PatternHit;

public class FreqItemSet {
	public static ArrayList<PatternHit > FrequentItemset(ArrayList<Pattern> items, int dimensions)
	{
		ArrayList<PatternHit > output = new ArrayList<PatternHit>();
		ArrayList<PatternHit > current = new ArrayList<PatternHit>();
		for(int i=0;i<items.size();i++)
			current.add(
					new PatternHit(items.get(i).data,
					new ArrayList<Integer>(Arrays.asList(i))
					));
		ArrayList<PatternHit > nextset = new ArrayList<PatternHit>();
		do {
			boolean[] maximals= new boolean[current.size()];
			nextset = genNext(current, maximals, dimensions);
			for(int i=0;i<current.size();i++)
				if(maximals[i]) output.add(current.get(i));
			current = nextset;
		}while(current.size()>0);
		return output;
	}
	private static ArrayList<PatternHit > genNext(ArrayList<PatternHit > current,boolean[] maximals, int dimensions)
	{
		int size = current.size();
		ArrayList<PatternHit> output = new ArrayList<PatternHit>();
		for(int i=0;i<size;i++) maximals[i]=true;
		for(int i=0;i<size-1;i++)
			for(int j=i+1;j<size;j++)
				if(current.get(i).patternsIndices.size()==1 || current.get(i).patternsIndices.subList(0, size-3) == current.get(j).patternsIndices.subList(0, size-3))
				{
					char[] intersect = getIntersect(current.get(i).vcomb, current.get(j).vcomb, dimensions);
					if(intersect[0]=='n') continue;// if the intersection is not empty
					maximals[i] = maximals[j]=false;
					ArrayList<Integer> ps = new ArrayList<Integer>();
					int pts = current.get(i).patternsIndices.size()-1;
					int si = current.get(i).patternsIndices.get(pts);
					int sj = current.get(j).patternsIndices.get(pts);
					if(si<sj)
					{
						ps = current.get(i).patternsIndices;
						ps.add(sj);
					}
					else
					{
						ps = current.get(j).patternsIndices;
						ps.add(si);
					}
					output.add(new PatternHit(intersect, ps));
				}
		return output;
	}
	private static char[] getIntersect(char[] a, char[] b,int dimensions)
	{
		char[] output = a.clone();
		for(int i=0;i<dimensions;i++)
		{
			if(b[i]=='x') continue;
			if(output[i]=='x') output[i] = b[i];
			else if (output[i]!=b[i]) // the intersection is empty
			{
				output[0]='n'; // stands for not valid
				break;
			}
		}
		return output;
	}
}
