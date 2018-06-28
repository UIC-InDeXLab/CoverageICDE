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
import pattern.MupSet;
import utils.BitSet;

/**
 * PatternBreaker algorithm. Top-down search for MUPS
 *
 */
public class HybridSearch extends NaiveSearch {

	public HybridSearch(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		return findMaxUncoveredPatternSet(threshold, Integer.MAX_VALUE);
	}
	
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold, int maxLevel) {

		MupSet mups = new MupSet(curDataSet.cardinalities);
		Stack<Pattern> patternToCheckStack = new Stack<Pattern>();
		// Add root pattern
		Pattern root = Pattern.getRootPattern(curDataSet.getDimension(),
				curDataSet.coveragePercentageOfEachValueInEachAttr);

		patternToCheckStack.push(root);

		while (!patternToCheckStack.isEmpty()) {
			if (Thread.currentThread().isInterrupted())
				return null;

			Pattern currentPattern = patternToCheckStack.pop();
			
			currentPattern.visitId = seq.getAndIncrement();

			// Check coverage
			boolean ifUncovered = true;
			
			if (currentPattern.level > maxLevel)
				continue;			
			
			// If pattern dominates mups, we know it is not covered.
			if ((currentPattern.parentVisitId < mups.lastAddedMupId
					|| currentPattern.parentDominatesMups)
					&& mups.ifIsDominatedBy(currentPattern, false)) {
				ifUncovered = false;
			}
			// We arrive at a region that is below and dominated by
			// mup. We abandon this search.
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
					addMupMetaData();
				}

			} else {
				Set<Pattern> tmp = curDataSet
						.getChildrenRule1(currentPattern);
				tmp.removeAll(mups.patternSet);
				patternToCheckStack.addAll(tmp);
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
			MupSet mups, int threshold) {

		uncoveredPattern.visitId = seq.getAndIncrement();

		Map<Integer, Pattern> parents = uncoveredPattern.genParents();
		boolean ifMup = true;

		Pattern nextPattern = null;

		List<Pattern> listOfParentPatterns = new ArrayList<Pattern>(
				parents.values());

		for (Pattern parentPattern : listOfParentPatterns) {
			// A mup is the descendant of the parentPattern. Hence,
			// parentPattern is covered.
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

		if (ifMup) {
			mups.lastAddedMupId = uncoveredPattern.visitId;
			return uncoveredPattern;
		} else if (nextPattern != null) {
			return bottomUpMupRandomSearch(nextPattern, mups, threshold);
		}

		return null;
	}
}
