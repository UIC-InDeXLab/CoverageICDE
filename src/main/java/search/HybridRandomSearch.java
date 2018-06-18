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
import java.util.Random;
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

		PatternSet mups = new PatternSet(curDataSet.cardinalities);
		Stack<Pattern> patternToCheckStack = new Stack<Pattern>();
		// Add root pattern
		Pattern root = Pattern.getRootPattern(curDataSet.getDimension(),
				curDataSet.coveragePercentageOfEachValueInEachAttr);
		patternToCheckStack.push(root);

		while (!patternToCheckStack.isEmpty()) {

			Pattern currentPattern = patternToCheckStack.pop();

			// Check coverage
			boolean ifUncovered;

			if (mups.ifIsDominatedBy(currentPattern, false))
				ifUncovered = false;
			// We arrive at a region that is below and covered by a discovered mup. We abandon this search.
			else if (mups.ifDominates(currentPattern, true))
				continue;
			else {
				updateDebugNodesAddAVisit(currentPattern);

				int coverageValue = this.curDataSet
						.checkCoverage(currentPattern);

				ifUncovered = coverageValue < threshold;

			}

			if (ifUncovered) {
				Pattern mup = bottomUpMupRandomSearch(currentPattern, mups,
						threshold);

				if (mup != null) {
					mups.add(mup);
					updateDebugAddMupDiscoveryTimeline();

//					// We take the pattern in the bottom and put it on the top
//					Pattern chosenPattern = new Pattern(patternToCheckStack.get(0).data);
//					patternToCheckStack.remove(0);
//					patternToCheckStack.push(chosenPattern);
				}
				
			} else {
				Set<Pattern> tmp = curDataSet.getChildrenNextLevel(currentPattern);
				tmp.removeAll(mups.patternSet);
				List<Pattern> listOfChildPatterns = new ArrayList<Pattern>(
						tmp);
//				Collections.shuffle(listOfChildPatterns);
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
//		Collections.shuffle(listOfParentPatterns);

		for (Pattern parentPattern : listOfParentPatterns) {
			// A mup is the descendant of the parentPattern. Hence, parentPattern is covered. 
			if (mups.ifIsDominatedBy(parentPattern, false))
				continue;
			else {
				updateDebugNodesAddAVisit(parentPattern);
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
