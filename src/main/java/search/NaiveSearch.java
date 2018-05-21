package search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import io.DataSet;
import pattern.Pattern;

public class NaiveSearch {
	// Keywords
	public static final String DEBUG_MUPS_SIZE = "MUPS_SIZE";
	public static final String DEBUG_NODES_VISITED = "NODES_VISITED";

	DataSet curDataSet;
	Map<String, Long> debugInfo;
	
	int threshold;
	
	// Debug info
	long numNodesVisited;
	long mupsSize;
	long initialTime;
	List<Long> timeSeries;
	
	Set<Pattern> nodesVisited;
	int numOfHits;

	public NaiveSearch(DataSet curData) {
		this.curDataSet = curData;
		this.numNodesVisited = 0;
		this.mupsSize = 0;
		this.initialTime = System.currentTimeMillis();
		this.timeSeries = new LinkedList<Long>();
		this.nodesVisited = new HashSet<Pattern>();
		this.numOfHits = 0;
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
		debugInfo.put(DEBUG_NODES_VISITED, numNodesVisited);
		debugInfo.put(DEBUG_MUPS_SIZE, mupsSize);
		return debugInfo;
	}

	public void updateDebugAddMupDiscoveryTimeline() {
		timeSeries.add(System.currentTimeMillis() - initialTime);
	}
	
	public long[] getTimeSeries() {
		Long[] tmp = timeSeries.toArray(new Long[timeSeries.size()]);
		return ArrayUtils.toPrimitive(tmp);
	}
	
	public int getNumHits() {
		return numOfHits;
	}

	public void updateDebugNodesAddAVisit(Pattern p) {
//		System.out.println(p);
		numNodesVisited++;
		if (nodesVisited.contains(p))
			numOfHits++;
		else
			nodesVisited.add(p);			
	}

	public void updateDebugMUPSSize(long num) {
		mupsSize += num;
	}
}
