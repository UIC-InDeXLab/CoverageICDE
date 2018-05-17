package search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.DataSet;
import pattern.Pattern;

public class NaiveSearch {
	// Keywords
	public static final String DEBUG_MUPS_SIZE = "MUPS_SIZE";
	public static final String DEBUG_NODES_VISITED = "NODES_VISITED";

	DataSet curDataSet;
	Map<String, Long> debugInfo;

	public NaiveSearch(DataSet curData) {
		curDataSet = curData;
		debugInfo = new HashMap<String, Long>();
	}

	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		Set<Pattern> mups = new HashSet<Pattern>();
		return mups;
	}

	/**
	 * Get all debug information
	 * 
	 * @return
	 */
	public Map<String, Long> getDebugInfo() {
		return debugInfo;
	}

	public void updateDebugNodesVisited(long num) {
		debugInfo.put(DEBUG_NODES_VISITED, num);
	}

	public void updateDebugNodesAddAVisit() {
		debugInfo.put(DEBUG_NODES_VISITED,
				debugInfo.getOrDefault(DEBUG_NODES_VISITED, (long) 0) + 1);
	}

	public void updateDebugMUPSSize(long num) {
		debugInfo.put(DEBUG_MUPS_SIZE, num);
	}
}
