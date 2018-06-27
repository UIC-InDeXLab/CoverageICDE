package search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
	public List<long[]> numOfMupsDiscoveredAndTimeStamp;
	public int mupsCounter;

	Set<Pattern> nodesVisited;
	int numOfHits;

	// Sequence id generater
	AtomicInteger seq;

	public NaiveSearch(DataSet curData) {
		this.curDataSet = curData;
		this.numNodesVisited = 0;
		this.mupsSize = 0;
		this.initialTime = System.currentTimeMillis();
		this.nodesVisited = new HashSet<Pattern>();
		this.numOfHits = 0;

		this.seq = new AtomicInteger();

		this.mupsCounter = 0;
		this.numOfMupsDiscoveredAndTimeStamp = new LinkedList<long[]>();
		this.numOfMupsDiscoveredAndTimeStamp.add(new long[]{0, 0});
	}

	public void addMupMetaData() {
		// Update metadaata
		mupsCounter++;
		if (mupsCounter % 1000 == 0) {
			this.numOfMupsDiscoveredAndTimeStamp.add(new long[]{mupsCounter,
					System.currentTimeMillis() - this.initialTime});
		}
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

	public int getNumHits() {
		return numOfHits;
	}

	public void updateDebugNodesAddAVisit(Pattern p) {
		// System.out.println(p);
		numNodesVisited++;
		// if (nodesVisited.contains(p))
		// numOfHits++;
		// else
		// nodesVisited.add(p);
	}

	public void updateDebugMUPSSize(long num) {
		mupsSize += num;
	}
}
