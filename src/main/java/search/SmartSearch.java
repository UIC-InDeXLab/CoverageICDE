package search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import Pattern.Pattern;
import io.DataSet;

/**
 * PatternBreaker algorithm. Top-down search for MUPS
 *
 */
public class SmartSearch extends NaiveSearch {

	public SmartSearch(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		long numNodesVisited = 0;

		Set<Pattern> mups = new HashSet<Pattern>();

		PriorityQueue<Pattern> patternToCheckQ = new PriorityQueue<Pattern>(
				10000);

		// Add root pattern
		Pattern root = Pattern.getRootPattern(curDataSet.getDimension(),
				curDataSet.coveragePercentageOfEachValueInEachAttr);
		patternToCheckQ.add(root);

		// Top-down mup traveral
		while (!patternToCheckQ.isEmpty()) {
			

			Pattern currentPattern = patternToCheckQ.poll();

			// Check coverage
			numNodesVisited++;
			int coverageValue = this.curDataSet.checkCoverage(currentPattern);

			currentPattern.updateCoveragePercentage(
					(double) coverageValue / curDataSet.getNumRecords());
			// System.out.println(
			// currentPattern + ": " + currentPattern.covereagePercentage);

			if (coverageValue < threshold) {
				Map<Integer, Pattern> parents = currentPattern.genParents();
				boolean ifMup = true;

				for (Pattern parent : parents.values()) {
					if (this.curDataSet.checkCoverage(parent) < threshold) {
						ifMup = false;
						break;
					}

				}

				if (ifMup)
					mups.add(currentPattern);
			} else {
				patternToCheckQ.addAll(
						curDataSet.getChildrenNextLevel(currentPattern));
			}
		}

		// Update debug info
		updateDebugNodesVisited(numNodesVisited);
		updateDebugMUPSSize(mups.size());

		return mups;
	}
}
