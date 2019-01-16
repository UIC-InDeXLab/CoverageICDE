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
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import io.DataSet;
import pattern.Pattern;
import pattern.MupSet;
import utils.BitSet;

/**
 * Search for mups using the frequent item set algorithm
 *
 */
public class AprioriSearch extends NaiveSearch {

	public AprioriSearch(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		return findMaxUncoveredPatternSet(threshold, Integer.MAX_VALUE);
	}

	public Set<Pattern> findMaxUncoveredPatternSet(int threshold,
			int maxLevel) {

		Set<Pattern> uncoveredPatternSet = new HashSet<Pattern>();

		Pattern root = Pattern.getRootPattern(curDataSet.getDimension(),
				curDataSet.coveragePercentageOfEachValueInEachAttr);

		/*
		 * Build one-element itemset set (covered)
		 */
		Set<Pattern> oneElementSet = new HashSet<Pattern>();
		for (Pattern oneElementItemset : curDataSet.getAllChildren(root)) {
			updateDebugNodesAddAVisit(oneElementItemset);
			if (curDataSet.checkCoverage(oneElementItemset) < threshold)
				uncoveredPatternSet.add(oneElementItemset);
			else
				oneElementSet.add(oneElementItemset);
		}

		/*
		 * Repeatedly build k+1-element itemsets. If any new itemset become
		 * uncovered, put it in the uncoveredPatternSet.
		 */
		Set<Pattern> kElementSet = new HashSet<Pattern>(oneElementSet);
		while (!kElementSet.isEmpty()) {
			Set<Pattern> kPlusOneElementSet = new HashSet<Pattern>();
			for (Pattern curItemset : kElementSet) {	
				// Check if the current itemset exceeds the maxLevel
				if (curItemset.level > maxLevel)
					continue;
				
				// Create (k+1) element itemsets
				for (Pattern oneElementItemset : oneElementSet) {
					Pattern newItemset = merge(curItemset, oneElementItemset);
					if (newItemset != null) {
						updateDebugNodesAddAVisit(newItemset);
						int support = curDataSet.checkCoverage(newItemset);
						
						if (!newItemset.getValidity())
							continue;
						if ( support < threshold) {
							// not perfect
							uncoveredPatternSet.add(newItemset);
						} else {
							kPlusOneElementSet.add(newItemset);
						}
					}
				}
			}
			kElementSet.clear();
			kElementSet.addAll(kPlusOneElementSet);
		}

		/*
		 * Find mups from uncoveredPatternSet
		 */
		Set<Pattern> mups = new HashSet<Pattern>();
		for (Pattern uncoveredPattern : uncoveredPatternSet) {
			// Check if all its parent patterns are covered.
			boolean ifMup = Collections.disjoint(uncoveredPatternSet,
					uncoveredPattern.genParents().values());

			if (ifMup)
				mups.add(uncoveredPattern);
		}

		updateDebugMUPSSize(mups.size());

		return mups;
	}

	/**
	 * Merge two patterns. Return null if two elements have different values on
	 * the same position and neither is 'x', or one is a parent of the other.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public Pattern merge(Pattern p1, Pattern p2) {
		boolean ifValid = true;
		
		if (p1.getDimension() != p2.getDimension()) {
			return null;
		}
		char[] mergedData = new char[p1.getDimension()];

		for (int idx = 0; idx < p1.getDimension(); idx++)
			if (p1.data[idx] == 'x')
				mergedData[idx] = p2.data[idx];
			else if (p2.data[idx] == 'x')
				mergedData[idx] = p1.data[idx];
			else if (p1.data[idx] != p2.data[idx]) {
				ifValid = false;
				mergedData[idx] = 'x';
			}
			else
				mergedData[idx] = p1.data[idx];
		if (Arrays.equals(mergedData, p1.data)
				|| Arrays.equals(mergedData, p2.data) ||  p1.data == p2.data)
			ifValid = false;
		
		// System.out.println(p1 + " + " + p2 + " = " + mergedData);
		return new Pattern(mergedData, ifValid);
	}
}
