package dataCollectionNew;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;

import pattern.Pattern;

public class KeyPatternValuesSearch {
	BitSet[] attrValuesMatchedByPatterns;
	int numberOfPatterns;
	int dimensions;
	public int[] cardinalities;
	public int[] cardinalitiesSum;

	public KeyPatternValuesSearch(int[] cardinality, Set<Pattern> mups) {
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
	private int getValueIdx(int attrId, char c) {
		if (c != 'x')
			return this.cardinalitiesSum[attrId] + c - '0';
		return -1;
	}

	public List<PatternValueNode> findMinListOfKeyPatterns() {
		List<PatternValueNode> minListOfKeyPatterns = new LinkedList<PatternValueNode>();
		PatternValueNode newNodeFound = findPatternValue();
		if (newNodeFound == null)
			return minListOfKeyPatterns;
		else
			minListOfKeyPatterns.add(newNodeFound);
		BitSet coveredPatternsBitSet = (BitSet) newNodeFound.matchingPatterns
				.clone();

		// There are still patterns to cover
		while (coveredPatternsBitSet.nextClearBit(0) < this.numberOfPatterns) {
			// Get patternToIgnore BitSet
			BitSet patternsToIgnoreBitSet = (BitSet) coveredPatternsBitSet
					.clone();
			patternsToIgnoreBitSet.flip(0, this.numberOfPatterns);

			// Get next key pattern
			newNodeFound = findPatternValue(patternsToIgnoreBitSet);

			if (newNodeFound != null)
				minListOfKeyPatterns.add(newNodeFound);
			else
				return minListOfKeyPatterns;

			// Update coveredPatternsBitSet
			coveredPatternsBitSet.or(newNodeFound.matchingPatterns);
		}
		
		return minListOfKeyPatterns;
	}

	public PatternValueNode findPatternValue() {
		PriorityQueue<PatternValueNode> patternTree = new PriorityQueue<PatternValueNode>(
				10000);
		for (char c : getValueRange(0)) {
			patternTree.add(new PatternValueNode(c,
					this.attrValuesMatchedByPatterns[getValueIdx(0, c)]));
		}
		while (!patternTree.isEmpty()) {
			PatternValueNode curPatternValueNode = patternTree.poll();
			if (curPatternValueNode.ifComplete())
				return curPatternValueNode;
			else {
				Map<Character, BitSet> charAndBitSet = new HashMap<Character, BitSet>();
				for (char c : getValueRange(
						curPatternValueNode.getDimension())) {
					charAndBitSet.put(c,
							attrValuesMatchedByPatterns[getValueIdx(
									curPatternValueNode.getDimension(), c)]);
				}

				List<PatternValueNode> listOfPatterns = curPatternValueNode
						.createChildren(charAndBitSet);
				if (listOfPatterns != null && listOfPatterns.size() > 0)
					patternTree.addAll(listOfPatterns);
			}
		}
		return null;
	}

	public PatternValueNode findPatternValue(BitSet patternsToIgnore) {
		PriorityQueue<PatternValueNode> patternTree = new PriorityQueue<PatternValueNode>(
				10000);
		for (char c : getValueRange(0)) {
			BitSet tmpBitSet = (BitSet) patternsToIgnore.clone();
			
			tmpBitSet.and(
					this.attrValuesMatchedByPatterns[getValueIdx(0, c)]);
			patternTree.add(new PatternValueNode(c, tmpBitSet));
		}
		while (!patternTree.isEmpty()) {
			PatternValueNode curPatternValueNode = patternTree.poll();
			if (curPatternValueNode.ifComplete())
				return curPatternValueNode;
			else {
				Map<Character, BitSet> charAndBitSet = new HashMap<Character, BitSet>();
				for (char c : getValueRange(
						curPatternValueNode.getDimension())) {
					charAndBitSet.put(c,
							attrValuesMatchedByPatterns[getValueIdx(
									curPatternValueNode.getDimension(), c)]);
				}

				List<PatternValueNode> listOfPatterns = curPatternValueNode
						.createChildren(charAndBitSet);
				if (listOfPatterns != null && listOfPatterns.size() > 0)
					patternTree.addAll(listOfPatterns);
			}
		}
		return null;
	}

	public char[] getValueRange(int columnId) {
		char[] range = new char[this.cardinalities[columnId]];

		for (int i = 0; i < this.cardinalities[columnId]; i++)
			range[i] = (char) (i + 48);

		return range;
	}
}
