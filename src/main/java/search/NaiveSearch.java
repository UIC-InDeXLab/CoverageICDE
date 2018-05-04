package search;

import java.util.HashSet;
import java.util.Set;

import Pattern.Pattern;
import io.DataSet;

public class NaiveSearch {
	DataSet dataToEvaluate;
	
	public NaiveSearch(DataSet curData) {
		dataToEvaluate = curData;
	}
	
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		Set<Pattern> mups = new HashSet<Pattern>();
		return mups;
	}
}
