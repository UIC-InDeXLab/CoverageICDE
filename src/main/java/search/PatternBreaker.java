package search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import Pattern.Pattern;
import Pattern.PatternSet;
import io.DataSet;

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
		long numNodesVisited = 0;

		PatternSet mups = new PatternSet(this.curDataSet.cardinalities);

		Queue<Pattern> patternToCheckQ = new LinkedList<Pattern>();

		// Add root pattern
		Pattern root = Pattern.getRootPattern(curDataSet.getDimension());
		patternToCheckQ.add(root);

		// Top-down mup traveral
		while (!patternToCheckQ.isEmpty()) {

			Pattern currentPattern = patternToCheckQ.poll();

			numNodesVisited++;
			// Make sure none of its ancestor is in MUP
			if (mups.containsAncestorOf(currentPattern)) {
				continue;
			}

			// Check coverage
			
			int coverageValue = this.curDataSet.checkCoverage(currentPattern);

			if (coverageValue < threshold)
				mups.add(currentPattern);
			else {
				// Find the right most deterministic cell
				int rightMostDeterministicIdx = currentPattern
						.findRightMostDeterministicIndex();

				// Sequentially create new patterns by replacing each
				// position with all possible values in that position
				for (int i = rightMostDeterministicIdx + 1; i < currentPattern
						.getDimension(); i++) {
					for (char valueToReplace : curDataSet.getValueRange(i)) {
						patternToCheckQ.add(new Pattern(currentPattern.data, i,
								valueToReplace));
					}
				}

			}
		}

		// Update debug info
		updateDebugNodesVisited(numNodesVisited);
		updateDebugMUPSSize(mups.size());

		return mups.patternSet;
	}
}
