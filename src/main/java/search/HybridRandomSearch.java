package search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import io.DataSet;
import pattern.Pattern;
import pattern.PatternSet;

/**
 * PatternBreaker algorithm. Top-down search for MUPS
 *
 */
public class HybridRandomSearch extends NaiveSearch {

	public HybridRandomSearch(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		long numNodesVisited = 0;

		PatternSet mups = new PatternSet(curDataSet.cardinalities);
		Stack<Pattern> patternToCheckStack = new Stack<Pattern>();
		// Add root pattern
		Pattern root = Pattern.getRootPattern(curDataSet.getDimension(),
				curDataSet.coveragePercentageOfEachValueInEachAttr);
		patternToCheckStack.add(root);

		while (!patternToCheckStack.isEmpty()) {

			Pattern currentPattern = patternToCheckStack.pop();

			// Check coverage
			boolean ifUncovered;

			if (mups.hasDescendantTo(currentPattern, false))
				ifUncovered = false;
			else if (mups.hasAncestorTo(currentPattern, true))
				ifUncovered = true;
			else {
				updateDebugNodesAddAVisit();

				int coverageValue = this.curDataSet
						.checkCoverage(currentPattern);

				ifUncovered = coverageValue < threshold;

			}

			if (ifUncovered) {
				Pattern mup = bottomUpMupRandomSearch(currentPattern, mups,
						threshold);

				if (mup != null)
					mups.add(mup);
			} else {
				List<Pattern> listOfChildPatterns = new ArrayList<Pattern>(
						curDataSet.getChildrenNextLevel(currentPattern));
				Collections.shuffle(listOfChildPatterns);
				patternToCheckStack.addAll(listOfChildPatterns);
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
	public Pattern bottomUpMupRandomSearch(Pattern uncoveredPattern,
			PatternSet mups, int threshold) {
		Map<Integer, Pattern> parents = uncoveredPattern.genParents();
		boolean ifMup = true;

		Pattern nextPattern = null;

		List<Pattern> listOfParentPatterns = new ArrayList<Pattern>(
				parents.values());
		Collections.shuffle(listOfParentPatterns);

		for (Pattern parentPattern : listOfParentPatterns) {
			if (mups.hasDescendantTo(parentPattern, false))
				continue;
			else if (mups.hasAncestorTo(parentPattern, true)) {
				ifMup = false;
				nextPattern = parentPattern;
				break;
			} else {
				updateDebugNodesAddAVisit();
				int coverageValue = this.curDataSet
						.checkCoverage(parentPattern);

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
			return bottomUpMupRandomSearch(nextPattern, mups, threshold);
		}

		return null;
	}
}
