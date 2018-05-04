package search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Pattern.Pattern;
import io.DataSet;

public class NaiveSearch {
	// Keywords
	public static final String DEBUG_MUPS_SIZE = "MUPS_SIZE";
	public static final String DEBUG_NODES_VISITED = "NODES_VISITED";
	
	DataSet dataToEvaluate;
	Map<String, Long> debugInfo;
	
	public NaiveSearch(DataSet curData) {
		dataToEvaluate = curData;
		debugInfo = new HashMap<String, Long>();
	}
	
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		Set<Pattern> mups = new HashSet<Pattern>();
		return mups;
	}
	
	/**
	 * Get all debug information
	 * @return
	 */
	public Map<String, Long> getDebugInfo() {
		return debugInfo;
	}
	
	public void updateDebugNodesVisited(long num) {
		debugInfo.put(DEBUG_NODES_VISITED, num);
	}
	
	public void updateDebugMUPSSize(long num) {
		debugInfo.put(DEBUG_MUPS_SIZE, num);
	}
}
