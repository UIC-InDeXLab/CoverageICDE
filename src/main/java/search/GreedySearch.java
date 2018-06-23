package search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import pattern.MupSet;

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
		MupSet mups = new MupSet(curDataSet.cardinalities);
		PriorityQueue<Pattern> patternToCheckQ = new PriorityQueue<Pattern>();

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
			else if ((currentPattern.parentVisitId < mups.lastAddedMupId
					|| currentPattern.parentDominatesMups)
					&& mups.ifIsDominatedBy(currentPattern, false)) {
				ifUncovered = false;
			}
			else if (mups.ifDominates(currentPattern, true))
				continue;
			else {
				updateDebugNodesAddAVisit(currentPattern);
				int coverageValue = this.curDataSet
						.checkCoverage(currentPattern);

				currentPattern.updateCoveragePercentage(
						(double) coverageValue / curDataSet.getNumRecords());
				currentPattern.setCoverage(coverageValue);

				ifUncovered = coverageValue < threshold;

			}

			if (ifUncovered) {
				Pattern mup = bottomUpMupGreedySearch(currentPattern, mups,
						threshold);

				if (mup != null) {
					mups.add(mup);
					addMupMetaData();
				}

			} else {
				Set<Pattern> tmp = curDataSet.getChildrenRule1(currentPattern);
				tmp.removeAll(mups.patternSet);
				patternToCheckQ.addAll(tmp);
			}
		}

		// Update debug info
		updateDebugMUPSSize(mups.size());
		return mups.patternSet;
	}

	/**
	 * We got a node that is uncovered, we randomly do a bottom up search for a
	 * mup node
	 * 
	 * @param uncoveredPattern
	 * @param mups
	 * @param threshold
	 * @return
	 */
	public Pattern bottomUpMupGreedySearch(Pattern uncoveredPattern,
			MupSet mups, int threshold) {
		Map<Integer, Pattern> parents = uncoveredPattern.genParents(this.curDataSet.coveragePercentageOfEachValueInEachAttr);
		boolean ifMup = true;

		Pattern nextPattern = null;

		List<Pattern> listOfParentPatterns = new ArrayList<Pattern>(
				parents.values());
		Collections.sort(listOfParentPatterns);

		for (Pattern parentPattern : listOfParentPatterns) {

			// A mup is the descendant parentPattern. Hence, parentPattern is
			// covered.
			if (mups.ifIsDominatedBy(parentPattern, false))
				continue;
			else {
				updateDebugNodesAddAVisit(parentPattern);
				int coverageValue = this.curDataSet
						.checkCoverage(parentPattern);

				parentPattern.updateCoveragePercentage(
						(double) coverageValue / curDataSet.getNumRecords());
				parentPattern.setCoverage(coverageValue);

				if (coverageValue < threshold) {
					ifMup = false;
					nextPattern = parentPattern;
					break;
				}
			}

		}

		if (ifMup)
			return uncoveredPattern;
		else if (nextPattern != null) {
			return bottomUpMupGreedySearch(nextPattern, mups, threshold);
		}
				
		return null;
	}
}
