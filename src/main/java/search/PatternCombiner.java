package search;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Pattern.Pattern;
import io.DataSet;

/**
 * PatternBreaker algorithm. Bottom-up search for MUPS
 *
 */
public class PatternCombiner extends NaiveSearch {

	public PatternCombiner(DataSet curData) {
		super(curData);
	}

	@Override
	public Set<Pattern> findMaxUncoveredPatternSet(int threshold) {

		Set<Pattern> mups = new HashSet<Pattern>();
		Map<Pattern, Integer> coverageHashThisLevel = new HashMap<Pattern, Integer>();

		// Initialize the bottom level
		List<Pattern> patternList = dataToEvaluate.enumAllValues();
		for (Pattern p : patternList) {
			coverageHashThisLevel.put(p, 0);
		}

		// Count the pattern coverage from the bottom level.
		for (int i = 0; i < dataToEvaluate.getNumRecords(); i++) {
			Pattern curRowPattern = new Pattern(dataToEvaluate.getRow(i));
			int count = coverageHashThisLevel.getOrDefault(curRowPattern, 0);
			coverageHashThisLevel.put(curRowPattern, count + 1);
		}

		int curLevel = dataToEvaluate.getDimension();
		while (curLevel >= 0) {
			Map<Pattern, Integer> coverageHashNextLevel = new HashMap<Pattern, Integer>();

			// Calculate the coverages of the parent patterns and put them in
			// coverageHashNextLevel
			for (Map.Entry<Pattern, Integer> e1 : coverageHashThisLevel
					.entrySet()) {
				Pattern curPattern = e1.getKey();
				Map<Integer, Pattern> replacedPositionToParentPatternMap = curPattern
						.genParentsBasedOnRule2();

				for (Map.Entry<Integer, Pattern> e2 : replacedPositionToParentPatternMap
						.entrySet()) {
					int coverageOfParentPattern = 0;

					Pattern parentPattern = e2.getValue();
					int idx = e2.getKey();

					for (char charToReplace : dataToEvaluate
							.getValueRange(idx)) {
						Pattern childOfParentPattern = new Pattern(
								parentPattern.data, idx, charToReplace);

						if (coverageHashThisLevel
								.get(childOfParentPattern) != null)
							coverageOfParentPattern += coverageHashThisLevel
									.get(childOfParentPattern);
						else
							coverageOfParentPattern = threshold;
					}

					if (coverageOfParentPattern < threshold)
						coverageHashNextLevel.put(parentPattern,
								coverageOfParentPattern);

				}

			}

			// Check if each uncovered pattern at the current level does not
			// have a parent pattern in the coverageHashNextLevel (a uncovered
			// parent pattern). If so, put it in mups.
			for (Map.Entry<Pattern, Integer> e : coverageHashThisLevel
					.entrySet()) {
				if (e.getValue() < threshold) {
					Pattern curPattern = e.getKey();
					Map<Integer, Pattern> allParentPatterns = curPattern
							.genParents();

					if (Collections.disjoint(coverageHashNextLevel.keySet(),
							allParentPatterns.values())) {
						mups.add(curPattern);
					}
				}

			}

			if (coverageHashNextLevel.isEmpty())
				break;

			coverageHashThisLevel = coverageHashNextLevel;
			curLevel--;

		}

		return mups;
	}

}