package dataCollection;
import java.util.ArrayList;
import java.util.Set;

import pattern.Pattern;

public class HittingSet {
	public static ArrayList<char[]> Hittingset(int numberofPatterns,ArrayList<PatternHit> PHs)
	{
		ArrayList<Integer> universe = new ArrayList<Integer>();
		for(int i=0;i<numberofPatterns;i++) universe.add(i);
		ArrayList<char[]> output = new ArrayList<char[]>();
		while(universe.size()>0 && PHs.size()>0) // while all sets are not hit
		{
			int next = maxHit(PHs);
			output.add(PHs.get(next).vcomb);
			ArrayList<Integer> ptrns = PHs.get(next).patternsIndices;
			PHs.remove(next);
			universe.removeAll(ptrns);
			for(PatternHit ph:PHs)
			{
				ph.patternsIndices.removeAll(ptrns);
				if(ph.patternsIndices.size()==0)
					PHs.remove(ph);
			}			
		}
		return output;
	}
	private static int maxHit(ArrayList<PatternHit> PHs)
	{
		int maxi = -1, max=-1;
		for(int i=0;i<PHs.size();i++)
		{
			int size = PHs.get(i).patternsIndices.size();
			if(size>max)
			{
				max = size; maxi = i;
			}
		}
		return maxi;
	}
}
