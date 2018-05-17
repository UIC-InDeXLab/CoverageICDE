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
public class SmartSearch extends NaiveSearch {

	public SmartSearch(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {
		long numNodesVisited = 0;

		Set<Pattern> mups = new HashSet<Pattern>();
		
		PatternSet coveredPatternSet = new PatternSet(curDataSet.cardinalities);
		PatternSet uncoveredPatternSet = new PatternSet(curDataSet.cardinalities);

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
			else if (coveredPatternSet.hasDescendantTo(currentPattern))
				ifUncovered = false;
			else if (uncoveredPatternSet.hasAncestorTo(currentPattern))
				ifUncovered = true;
			else {
				numNodesVisited++;
				int coverageValue = this.curDataSet.checkCoverage(currentPattern);

				currentPattern.updateCoveragePercentage(
						(double) coverageValue / curDataSet.getNumRecords());
				currentPattern.setCoverage(coverageValue);
				
				ifUncovered = coverageValue < threshold;
				
				if (ifUncovered)
					uncoveredPatternSet.add(currentPattern);
				else
					coveredPatternSet.add(currentPattern);
				
			}

			if (ifUncovered) {
				Map<Integer, Pattern> parents = currentPattern.genParents();
				boolean ifMup = true;

				for (Pattern parentPattern : parents.values()) {
					if (parentPattern.covereage >= 0)
						ifUncovered = parentPattern.covereage < threshold;
					else if (coveredPatternSet.hasDescendantTo(parentPattern))
						ifUncovered = false;
					else if (uncoveredPatternSet.hasAncestorTo(parentPattern)) {
						ifUncovered = true;
						ifMup = false;
						break;
					}
					else {
						numNodesVisited++;
						int coverageValue = this.curDataSet.checkCoverage(parentPattern);

						parentPattern.updateCoveragePercentage(
								(double) coverageValue / curDataSet.getNumRecords());
						parentPattern.setCoverage(coverageValue);
						
						ifUncovered = coverageValue < threshold;
						
						if (ifUncovered) {
							uncoveredPatternSet.add(parentPattern);
							ifMup = false;
							break;
						}
						else
							coveredPatternSet.add(parentPattern);
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
	
	public boolean checkIfPatternIsAParentOfAny(Set<Pattern> patternSet, Pattern patternToCheck) {
		for (Pattern myPattern : patternSet) {
			if (patternToCheck.isAncestorOf(myPattern))
				return true;
		}
		
		return false;
	}
	
	public boolean checkIfPatternIsAChildOfAny(Set<Pattern> patternSet, Pattern patternToCheck) {
		for (Pattern myPattern : patternSet) {
			if (myPattern.isAncestorOf(patternToCheck))
				return true;
		}
		
		return false;
	}
}
