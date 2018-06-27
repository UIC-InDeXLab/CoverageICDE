package search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import io.DataSet;
import pattern.Pattern;

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

		// Add root pattern
		Pattern root = Pattern.getRootPattern(curDataSet.getDimension());

		Set<Pattern> curPatternSet = new HashSet<Pattern>();
		curPatternSet.add(root);

		Set<Pattern> prevPatternSet = new HashSet<Pattern>();
		Set<Pattern> nextPatternSet = new HashSet<Pattern>();

		// Top-down mup traveral
		while (!curPatternSet.isEmpty()) {
			if (Thread.currentThread().isInterrupted())
				return null;
			
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

				updateDebugNodesAddAVisit(currentPattern);
				if (this.curDataSet
						.checkCoverage(currentPattern) < threshold) {
					mups.add(currentPattern);
					addMupMetaData();
				} else {
					nextPatternSet.addAll(curDataSet.getChildrenRule1(currentPattern));
				}
			}

			curPatternSet.removeAll(patternsToRemove);
			prevPatternSet = new HashSet<Pattern>(curPatternSet);
			curPatternSet = new HashSet<Pattern>(nextPatternSet);
			nextPatternSet = new HashSet<Pattern>();
		}

		// Update debug info
		updateDebugMUPSSize(mups.size());

		return mups;
	}
}
