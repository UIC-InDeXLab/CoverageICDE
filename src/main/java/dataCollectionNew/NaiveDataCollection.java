package dataCollectionNew;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;

import pattern.Pattern;

public class NaiveDataCollection {
	BitSet[] attrValuesMatchedByPatterns;
	int numberOfPatterns;
	int dimensions;
	public int[] cardinalities;
	public int[] cardinalitiesSum;

	public NaiveDataCollection(int[] cardinality, Set<Pattern> mups) {
		this.numberOfPatterns = mups.size();
		this.dimensions = mups.iterator().next().getDimension();
		PatternValueNode.maxDimensions = this.dimensions;
		this.cardinalities = Arrays.copyOf(cardinality, cardinality.length);

		// Update cardinalitiesSum
		this.cardinalitiesSum = new int[this.dimensions];

		int sum = 0;
		for (int i = 0; i < cardinalitiesSum.length; i++) {
			this.cardinalitiesSum[i] = sum;
			sum += cardinality[i];
		}

		// update attrValuesMatchedByPatterns
		attrValuesMatchedByPatterns = new BitSet[IntStream.of(cardinality)
				.sum()];

		for (int i = 0; i < IntStream.of(cardinality).sum(); i++)
			attrValuesMatchedByPatterns[i] = new BitSet();

		int patternId = 0;
		for (Pattern mup : mups) {
			for (int attrId = 0; attrId < this.dimensions; attrId++) {
				char value = mup.getValue(attrId);
				if (value != 'x') {
					this.attrValuesMatchedByPatterns[getValueIdx(attrId, value)]
							.set(patternId);
				} else {
					int baseIdx = getValueIdx(attrId, '0');
					for (int i = 0; i < cardinality[attrId]; i++) {
						this.attrValuesMatchedByPatterns[baseIdx + i]
								.set(patternId);
					}
				}
			}
			patternId++;
		}

	}

	/**
	 * Given attribute and attribute value, compute index in
	 * attrValuesMatchedByPatterns
	 * 
	 * @param attrId
	 * @param c
	 * @return
	 */
	int getValueIdx(int attrId, char c) {
		if (c != 'x')
			return this.cardinalitiesSum[attrId] + c - '0';
		return -1;
	}

	public List<PatternValueNode> findMinListOfKeyPatterns() {
		List<PatternValueNode> minListOfKeyPatterns = new LinkedList<PatternValueNode>();

		// Get all patterns
		List<PatternValueNode> keyPatternList = new LinkedList<PatternValueNode>();
		for (char c : getValueRange(0)) {
			keyPatternList.add(new PatternValueNode(c,
					this.attrValuesMatchedByPatterns[getValueIdx(0, c)]));
		}

		for (int attrId = 1; attrId < this.dimensions; attrId++) {
			List<PatternValueNode> tempPatternList = new LinkedList<PatternValueNode>(
					keyPatternList);
			keyPatternList.clear();

			for (char c : getValueRange(attrId)) {
				for (PatternValueNode p : tempPatternList)
					keyPatternList.add(new PatternValueNode(p, c,
							this.attrValuesMatchedByPatterns[getValueIdx(attrId,
									c)]));
			}
		}

		BitSet patternCoverage = new BitSet();

		// Hitting set
		while (patternCoverage.nextClearBit(0) < this.numberOfPatterns) {
			// Get pattern with max coverage
			PatternValueNode keyPatternValueWithMaxCoverage = Collections.min(keyPatternList);
			keyPatternList.remove(keyPatternValueWithMaxCoverage);
						
			// update patternCoverage
			patternCoverage.or(keyPatternValueWithMaxCoverage.matchingPatterns);
			
			// Get patterns to ingore
			BitSet patternsToIgnore = (BitSet) patternCoverage.clone();
			patternsToIgnore.flip(0, this.numberOfPatterns);
			
			// Update coverage of the rest of the key patterns
			for (PatternValueNode p : keyPatternList)
				p.updatePatternsToIgnore(patternsToIgnore);			
			
			// Add to minListOfKeyPatterns
			minListOfKeyPatterns.add(keyPatternValueWithMaxCoverage);
		}

		return minListOfKeyPatterns;
	}

	public char[] getValueRange(int columnId) {
		char[] range = new char[this.cardinalities[columnId]];

		for (int i = 0; i < this.cardinalities[columnId]; i++)
			range[i] = (char) (i + 48);

		return range;
	}
}
