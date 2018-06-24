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
			nextset = genNext(current, maximals, dimensions); // the frequent item-sets in the next level
			//System.out.print("maximals:");
			for(int i=0;i<current.size();i++) if(maximals[i]) 
				{
					output.add(current.get(i));
					//System.out.print(i);System.out.print(", ");
				}
			//System.out.println();
			current = nextset;
			/*for(PatternHit h:current)
			{
				for(int i:h.patternsIndices)
					{System.out.print(i);System.out.print(", ");}
				System.out.println();
			}*/
		}while(current.size()>0);
		return output;
	}
	private static ArrayList<PatternHit > genNext(ArrayList<PatternHit > current,boolean[] maximals, int dimensions)
	{ // it combines the frequent item-sets at a specific level to get the ones in the next level
		int size = current.size(); // the number of freq-item-sets at the current level
		ArrayList<PatternHit> output = new ArrayList<PatternHit>(); // the freq-item-sets in the next level
		for(int i=0;i<size;i++) maximals[i]=true;
		for(int i=0;i<size-1;i++)
			for(int j=i+1;j<size;j++)
			{
				int size2 = current.get(i).patternsIndices.size();
				/*if(size2>1) {// delete this whole "if" block later
					for(int k:current.get(i).patternsIndices.subList(0, size2-1)) System.out.print(k); 
					for(int k:current.get(j).patternsIndices.subList(0, size2-1)) System.out.print(k); 
				}*/
				if(size2==1 || current.get(i).patternsIndices.subList(0, size2-1).equals(current.get(j).patternsIndices.subList(0, size2-1)))
				{
					char[] intersect = getIntersect(current.get(i).vcomb, current.get(j).vcomb, dimensions);
					if(intersect[0]=='n') continue;// if the intersection is not empty
					maximals[i] = false; maximals[j]=false;		
					int si = current.get(i).patternsIndices.get(size2-1);
					int sj = current.get(j).patternsIndices.get(size2-1);
					ArrayList<Integer> ps = new ArrayList<Integer>();
					if(si<sj)
					{
						ps = (ArrayList<Integer>)current.get(i).patternsIndices.clone();
						ps.add(sj);
					}
					else
					{
						ps = (ArrayList<Integer>)current.get(j).patternsIndices.clone();
						ps.add(si);
					}
					output.add(new PatternHit(intersect, ps));
				}
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
