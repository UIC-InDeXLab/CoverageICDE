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
	
	// Debug info
	long nodesVisited;
	long mupsSize;

	public NaiveSearch(DataSet curData) {
		curDataSet = curData;
		nodesVisited = 0;
		mupsSize = 0;
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
		debugInfo = new HashMap<String, Long>();
		debugInfo.put(DEBUG_NODES_VISITED, nodesVisited);
		debugInfo.put(DEBUG_MUPS_SIZE, mupsSize);
		return debugInfo;
	}

	public void updateDebugNodesVisited(long num) {
		nodesVisited += num;
	}

	public void updateDebugNodesAddAVisit() {
		nodesVisited++;
	}

	public void updateDebugMUPSSize(long num) {
		mupsSize += num;
	}
}
