package search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import Pattern.Pattern;
import io.DataSet;

/**
 * PatternBreaker algorithm. Top-down search for MUPS
 *
 */
public class PatternBreakerOriginal extends NaiveSearch {

	public PatternBreakerOriginal(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {

		Set<Pattern> mups = new HashSet<Pattern>();

		// Queue<Pattern> patternToCheckQ = new LinkedList<Pattern>();

		// Add root pattern
		Pattern root = Pattern.getRootPattern(dataToEvaluate.getDimension());

		Set<Pattern> curPatternSet = new HashSet<Pattern>();
		curPatternSet.add(root);

		Set<Pattern> prevPatternSet = new HashSet<Pattern>();
		Set<Pattern> nextPatternSet = new HashSet<Pattern>();

		// Top-down mup traveral
		while (!curPatternSet.isEmpty()) {
			Set<Pattern> patternsToRemove = new HashSet<Pattern>();
			for (Pattern currentPattern : curPatternSet) {
				Map<Integer, Pattern> parentsOfCurPattern = currentPattern
						.genParents();

				boolean ifPossiblyMup = true;
				for (Pattern parentPattern : parentsOfCurPattern.values()) {
					if (mups.contains(parentPattern)
							|| !prevPatternSet.contains(parentPattern)) {
						ifPossiblyMup = false;
						break;
					}
				}

				if (!ifPossiblyMup) {
					patternsToRemove.add(currentPattern);
					continue;
				}

				if (this.dataToEvaluate
						.checkCoverage(currentPattern) < threshold) {
					mups.add(currentPattern);
				} else {
					// Find the right most deterministic cell
					int rightMostDeterministicIdx = currentPattern
							.findRightMostDeterministicIndex();

					// Sequentially create new patterns by replacing each
					// position with all possible values in that position
					for (int i = rightMostDeterministicIdx
							+ 1; i < currentPattern.getDimension(); i++) {
						for (char valueToReplace : dataToEvaluate
								.getValueRange(i)) {
							nextPatternSet.add(new Pattern(currentPattern.data,
									i, valueToReplace));
						}
					}
				}
			}

			curPatternSet.removeAll(patternsToRemove);
			prevPatternSet = new HashSet<Pattern>(curPatternSet);
			curPatternSet = new HashSet<Pattern>(nextPatternSet);
			nextPatternSet = new HashSet<Pattern>();
		}

		return mups;
	}
}
