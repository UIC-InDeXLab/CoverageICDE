package search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import io.DataSet;
import pattern.Pattern;
import pattern.PatternSet;

/**
 * PatternBreaker algorithm. Top-down search for MUPS
 *
 */
public class GreedySearch extends NaiveSearch {

	public GreedySearch(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		long numNodesVisited = 0;

		PatternSet mups = new PatternSet(curDataSet.cardinalities);
		PriorityQueue<Pattern> patternToCheckQ = new PriorityQueue<Pattern>(
				10000);

		// Add root pattern
		Pattern root = Pattern.getRootPattern(curDataSet.getDimension(),
				curDataSet.coveragePercentageOfEachValueInEachAttr);
		patternToCheckQ.add(root);

		while (!patternToCheckQ.isEmpty()) {

			Pattern currentPattern = patternToCheckQ.poll();

			// Check coverage
			boolean ifUncovered;

			if (currentPattern.covereage >= 0)
				ifUncovered = currentPattern.covereage < threshold;
			else if (mups.hasDescendantTo(currentPattern, false))
				ifUncovered = false;
			else if (mups.hasAncestorTo(currentPattern, true))
				ifUncovered = true;
			else {
				numNodesVisited++;

				int coverageValue = this.curDataSet
						.checkCoverage(currentPattern);

				currentPattern.updateCoveragePercentage(
						(double) coverageValue / curDataSet.getNumRecords());
				currentPattern.setCoverage(coverageValue);

				ifUncovered = coverageValue < threshold;

			}

			if (ifUncovered) {
				Map<Integer, Pattern> parents = currentPattern.genParents();
				boolean ifMup = true;

				for (Pattern parentPattern : parents.values()) {
					if (parentPattern.covereage >= 0) {
						if (parentPattern.covereage < threshold) {
							ifMup = false;
							break;
						}
					} else if (mups.hasDescendantTo(parentPattern, false))
						continue;
					else if (mups.hasAncestorTo(parentPattern, true)) {
						ifUncovered = true;
						ifMup = false;
						break;
					} else {
						numNodesVisited++;

						int coverageValue = this.curDataSet
								.checkCoverage(parentPattern);

						parentPattern
								.updateCoveragePercentage((double) coverageValue
										/ curDataSet.getNumRecords());
						parentPattern.setCoverage(coverageValue);

						ifUncovered = coverageValue < threshold;
						if (ifUncovered) {
							ifMup = false;
							break;
						}
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

		return mups.patternSet;
	}
}
