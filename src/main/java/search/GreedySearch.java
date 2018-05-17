package search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import Pattern.Pattern;
import Pattern.PatternSet;
import io.DataSet;

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
			else if (mups.hasDescendantTo(currentPattern))
				ifUncovered = false;
			else if (mups.hasAncestorTo(currentPattern))
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
					}
					else if (mups.hasDescendantTo(parentPattern))
						continue;
					else if (mups.hasAncestorTo(parentPattern)) {
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

	public boolean checkIfPatternIsAParentOfAny(Set<Pattern> patternSet,
			Pattern patternToCheck) {
		for (Pattern myPattern : patternSet) {
			if (patternToCheck.isAncestorOf(myPattern))
				return true;
		}

		return false;
	}

	public boolean checkIfPatternIsAChildOfAny(Set<Pattern> patternSet,
			Pattern patternToCheck) {
		for (Pattern myPattern : patternSet) {
			if (myPattern.isAncestorOf(patternToCheck))
				return true;
		}

		return false;
	}
}
