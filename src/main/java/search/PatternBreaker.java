package search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import io.DataSet;
import pattern.Pattern;
import pattern.MupSet;

/**
 * PatternBreaker algorithm. Top-down search for MUPS
 *
 */
public class PatternBreaker extends NaiveSearch {

	public PatternBreaker(DataSet curData) {
		super(curData);
	}

	/**
	 * Check if this pattern is covered by any mup
	 * 
	 * @param patternToCheck
	 * @param mups
	 * @return
	 */
	public boolean ifCoveredByMups(Pattern patternToCheck, Set<Pattern> mups) {
		// Make sure none of its ancestor is in MUP
		for (Pattern mup : mups) {
			if (mup.isAncestorOf(patternToCheck)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {

		MupSet mups = new MupSet(this.curDataSet.cardinalities);

		Queue<Pattern> patternToCheckQ = new LinkedList<Pattern>();

		// Add root pattern
		Pattern root = Pattern.getRootPattern(curDataSet.getDimension());
		patternToCheckQ.add(root);

		// Top-down mup traveral
		while (!patternToCheckQ.isEmpty()) {

			Pattern currentPattern = patternToCheckQ.poll();

			// Make sure none of its ancestor is in MUP
			if (mups.ifDominates(currentPattern, true)) {
				continue;
			}

			// Check coverage
			updateDebugNodesAddAVisit(currentPattern);
			int coverageValue = this.curDataSet.checkCoverage(currentPattern);

			if (coverageValue < threshold) {
				mups.add(currentPattern);
				addMupMetaData();
			}
			else {
				patternToCheckQ.addAll(curDataSet.getChildrenRule1(currentPattern));
			}
		}

		// Update debug info
		updateDebugMUPSSize(mups.size());

		return mups.patternSet;
	}
}
