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

public class DataCollectionBestFirstSearchNoRepeat extends NaiveDataCollection {

	public DataCollectionBestFirstSearchNoRepeat(int[] cardinality,
			Set<Pattern> mups) {
		super(cardinality, mups);
	}

	public List<PatternValueNode> findMinListOfKeyPatterns() {
		List<PatternValueNode> minListOfKeyPatterns = new LinkedList<PatternValueNode>();
		PriorityQueue<PatternValueNode> patternTree = new PriorityQueue<PatternValueNode>(
				10000);
		for (char c : getValueRange(0)) {
			patternTree.add(new PatternValueNode(c,
					this.attrValuesMatchedByPatterns[getValueIdx(0, c)]));
		}

		PatternValueNode newNodeFound = findPatternValue(patternTree);
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

			for (PatternValueNode p : patternTree)
				p.updatePatternsToIgnore(patternsToIgnoreBitSet);

			// Get next key pattern
			newNodeFound = findPatternValue(patternTree);

			if (newNodeFound != null)
				minListOfKeyPatterns.add(newNodeFound);
			else
				return minListOfKeyPatterns;

			// Update coveredPatternsBitSet
			coveredPatternsBitSet.or(newNodeFound.matchingPatterns);
		}

		return minListOfKeyPatterns;
	}

	public PatternValueNode findPatternValue(
			PriorityQueue<PatternValueNode> patternTree) {

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

	// public PatternValueNode findPatternValue(BitSet patternsToIgnore) {
	// PriorityQueue<PatternValueNode> patternTree = new
	// PriorityQueue<PatternValueNode>(
	// 10000);
	// for (char c : getValueRange(0)) {
	// BitSet tmpBitSet = (BitSet) patternsToIgnore.clone();
	//
	// tmpBitSet.and(
	// this.attrValuesMatchedByPatterns[getValueIdx(0, c)]);
	// patternTree.add(new PatternValueNode(c, tmpBitSet));
	// }
	// while (!patternTree.isEmpty()) {
	// PatternValueNode curPatternValueNode = patternTree.poll();
	// if (curPatternValueNode.ifComplete())
	// return curPatternValueNode;
	// else {
	// Map<Character, BitSet> charAndBitSet = new HashMap<Character, BitSet>();
	// for (char c : getValueRange(
	// curPatternValueNode.getDimension())) {
	// charAndBitSet.put(c,
	// attrValuesMatchedByPatterns[getValueIdx(
	// curPatternValueNode.getDimension(), c)]);
	// }
	//
	// List<PatternValueNode> listOfPatterns = curPatternValueNode
	// .createChildren(charAndBitSet);
	// if (listOfPatterns != null && listOfPatterns.size() > 0)
	// patternTree.addAll(listOfPatterns);
	// }
	// }
	// return null;
	// }
}
