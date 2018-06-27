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

public class DataCollectionBestFirstSearch extends NaiveDataCollection {

	public DataCollectionBestFirstSearch(int[] cardinality,
			Set<Pattern> mups) {
		super(cardinality, mups);
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
			if (Thread.currentThread().isInterrupted())
				return null;
			
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

				char[] characterArray = getValueRange(
						curPatternValueNode.getDimension());
				BitSet[] coverageArray = new BitSet[characterArray.length];
				for (int i = 0; i < characterArray.length; i++) {
					coverageArray[i] = attrValuesMatchedByPatterns[getValueIdx(
							curPatternValueNode.getDimension(),
							characterArray[i])];
				}

				List<PatternValueNode> listOfPatterns = curPatternValueNode
						.createChildren(characterArray, coverageArray);
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

			tmpBitSet.and(this.attrValuesMatchedByPatterns[getValueIdx(0, c)]);
			patternTree.add(new PatternValueNode(c, tmpBitSet));
		}
		while (!patternTree.isEmpty()) {			
			PatternValueNode curPatternValueNode = patternTree.poll();
			if (curPatternValueNode.ifComplete())
				return curPatternValueNode;
			else {
				char[] characterArray = getValueRange(
						curPatternValueNode.getDimension());
				BitSet[] coverageArray = new BitSet[characterArray.length];
				for (int i = 0; i < characterArray.length; i++) {
					coverageArray[i] = attrValuesMatchedByPatterns[getValueIdx(
							curPatternValueNode.getDimension(),
							characterArray[i])];
				}

				List<PatternValueNode> listOfPatterns = curPatternValueNode
						.createChildren(characterArray, coverageArray);
				if (listOfPatterns != null && listOfPatterns.size() > 0)
					patternTree.addAll(listOfPatterns);
			}
		}
		return null;
	}
}
