package dataCollection;

import java.util.ArrayList;
import java.util.Arrays;
import pattern.Pattern;
import dataCollection.PatternHit;
import java.util.HashMap;
import java.util.Map;

public class FreqItemSet {
	private HashMap<Integer, ArrayList<Integer>> cmap;
	public ArrayList<PatternHit > FrequentItemset(ArrayList<Pattern> items, int dimensions)
	{
		ArrayList<PatternHit > output = new ArrayList<PatternHit>();
		ArrayList<PatternHit > current = new ArrayList<PatternHit>();
		for(int i=0;i<items.size();i++)
		{
			current.add(
					new PatternHit(items.get(i).data,
					new ArrayList<Integer>(Arrays.asList(i))
					));
			//System.out.println(items.get(i).data);
			//current.get(i).patternsIndices[0]=i;
		}
		ArrayList<PatternHit > nextset = new ArrayList<PatternHit>();
		do {
			boolean[] maximals= new boolean[current.size()];
			Arrays.fill(maximals, true);
			nextset = genNext(current, maximals, dimensions); // the frequent item-sets in the next level
			for(int i=0;i<current.size();i++) if(maximals[i]) 
				{
					output.add(current.get(i));
				}
			current = nextset;
			System.out.println(current.size());
		}while(current.size()>0);
		//System.out.println("Done with freq itemset");
		return output;
	}
	
	private ArrayList<PatternHit > genNext(ArrayList<PatternHit > current, boolean[] maximals, int dimensions)
	{ // it combines the frequent item-sets at a specific level to get the ones in the next level
		int i,j,ip,jp,k,si,sj,size2,hashcode;
		//int[] tmp;
		if(cmap==null)
		{
			cmap = new HashMap<>();
			ArrayList<Integer> tmp2 = new ArrayList<Integer>();
			for(i=0;i<current.size();i++) tmp2.add(i);
			cmap.put(0, tmp2);
		}
		HashMap<Integer, ArrayList<Integer>> nmap = new HashMap<>(); // the next level map
		ArrayList<PatternHit> output = new ArrayList<PatternHit>(); // the freq-item-sets in the next level
		for(Integer key:cmap.keySet())
		{
			ArrayList<Integer> val = cmap.get(key);
			for (i=0;i<val.size()-1;i++)
			{
				ip = val.get(i);
				size2 = current.get(ip).patternsIndices.size();
				for(j=i+1;j<val.size();j++)
				{
					jp = val.get(j);
					char[] intersect = getIntersect(current.get(ip).vcomb, current.get(jp).vcomb, dimensions);
					if(intersect[0]=='n') continue;// if the intersection is not empty
					maximals[ip] = false; maximals[jp]=false;	
					si = current.get(ip).patternsIndices.get(size2-1);
					sj = current.get(jp).patternsIndices.get(size2-1);
					ArrayList<Integer> ps = new ArrayList<Integer>();
					//int[] ps = new int[size2+1];
					if(si<sj)
					{
						ps = (ArrayList<Integer>)current.get(ip).patternsIndices.clone();
						hashcode = ps.hashCode();
						ps.add(sj);
						//tmp = (int[])current.get(i).patternsIndices.clone();
						//for(k=0;k<size2;k++) ps[k]=tmp[k];
						//ps[k] = sj;
					}
					else
					{
						ps = (ArrayList<Integer>)current.get(jp).patternsIndices.clone();
						hashcode = ps.hashCode();
						ps.add(si);
						//tmp = (int[])current.get(j).patternsIndices.clone();
						//for(k=0;k<size2;k++) ps[k]=tmp[k];
						//ps[k] = si;
					}
					output.add(new PatternHit(intersect, ps));
					//hashcode = tmp.hashCode();
					if(nmap.containsKey(hashcode)) nmap.get(hashcode).add(output.size()-1); // the index current output
					else nmap.put(hashcode, new ArrayList<Integer>(Arrays.asList(output.size()-1)));
				}
			}
		}
		
		/*
		ArrayList<PatternHit> output = new ArrayList<PatternHit>(); // the freq-item-sets in the next level
		for(int i=0;i<size;i++) maximals[i]=true;
		for(int i=0;i<size-1;i++)
			for(int j=i+1;j<size;j++)
			{
				size2 = current.get(i).patternsIndices.size();
				if(size2==1 || current.get(i).patternsIndices.subList(0, size2-1).equals(current.get(j).patternsIndices.subList(0, size2-1)))
				{
					char[] intersect = getIntersect(current.get(i).vcomb, current.get(j).vcomb, dimensions);
					if(intersect[0]=='n') continue;// if the intersection is not empty
					maximals[i] = false; maximals[j]=false;		
					si = current.get(i).patternsIndices.get(size2-1);
					sj = current.get(j).patternsIndices.get(size2-1);
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
		*/
		cmap.clear();
		//for(PatternHit o:output) {System.out.println(o.patternsIndices); System.out.println(o.vcomb);}
		cmap = nmap;
		return output;
	}
	private char[] getIntersect(char[] a, char[] b,int dimensions)
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
