package search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import Pattern.Pattern;
import io.DataSet;

/**
 * PatternBreaker algorithm. Top-down search for MUPS
 *
 */
public class PatternBreaker extends NaiveSearch {

	public PatternBreaker(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {

		Set<Pattern> mups = new HashSet<Pattern>();

		Queue<Pattern> patternToCheckQ = new LinkedList<Pattern>();

		// Add root pattern
		Pattern root = Pattern.getRootPattern(dataToEvaluate.getDimension());
		patternToCheckQ.add(root);

		// Top-down mup traveral
		while (!patternToCheckQ.isEmpty()) {
			Pattern currentPattern = patternToCheckQ.poll();
			// Make sure none of its ancestor is in MUP
			boolean noMupAncestor = true;
			for (Pattern mup : mups) {
				if (mup.isAncestorOf(currentPattern)) {
					noMupAncestor = false;
					break;
				}
			}
			if (!noMupAncestor)
				continue;

			// Check coverage
			int coverageValue = this.dataToEvaluate
					.checkCoverage(currentPattern);

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
					for (char valueToReplace : dataToEvaluate
							.getValueRange(i)) {
						patternToCheckQ.add(new Pattern(currentPattern.data, i,
								valueToReplace));
					}
				}

			}
		}

		return mups;
	}
}
