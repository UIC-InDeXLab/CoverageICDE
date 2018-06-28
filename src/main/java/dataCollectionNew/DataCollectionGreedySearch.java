package dataCollectionNew;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;

import pattern.Pattern;

public class DataCollectionGreedySearch extends NaiveDataCollection {

	public DataCollectionGreedySearch(int[] cardinality, Set<Pattern> mups) {
		super(cardinality, mups);
	}

	public List<PatternValueNode> findMinListOfKeyPatterns() {
		List<PatternValueNode> minListOfKeyPatterns = new LinkedList<PatternValueNode>();
		
		if (this.mupsList == null)
			return minListOfKeyPatterns;

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
			BitSet patternsNeedsToBeCovered = (BitSet) coveredPatternsBitSet
					.clone();
			patternsNeedsToBeCovered.flip(0, this.numberOfPatterns);

			// Get next key pattern
			newNodeFound = findPatternValue(patternsNeedsToBeCovered);
			
			if (Thread.currentThread().isInterrupted())
				return null;

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
		Stack<PatternValueNode> patternStack = new Stack<PatternValueNode>();
		// Put root node into the stack
		for (char c : getValueRange(0)) {
			patternStack.add(new PatternValueNode(c,
					this.attrValuesMatchedByPatterns[getValueIdx(0, c)]));
		}

		PatternValueNode patternWithMaxCoverage = null;

		int filter = 0;

		while (!patternStack.isEmpty()) {

			PatternValueNode curPatternValueNode = patternStack.pop();

			// If the current node has an coverage lower than filter, prune it
			if (curPatternValueNode.numCoveredPatterns <= filter)
				continue;
			// Find a node that might has the max coverage, we save it and
			// update the filter
			else if (curPatternValueNode.ifComplete()) {
				patternWithMaxCoverage = curPatternValueNode;
				filter = curPatternValueNode.numCoveredPatterns;
			}
			// expand the node
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
				if (listOfPatterns != null && listOfPatterns.size() > 0) {

					// Make them in reverse order, so that the one with high
					// coverage is on top of stack
					Collections.sort(listOfPatterns, (a, b) -> b.compareTo(a));

					patternStack.addAll(listOfPatterns);
				}
			}
		}
		return patternWithMaxCoverage;
	}

	public PatternValueNode findPatternValue(BitSet patternsToBeCovered) {
		Stack<PatternValueNode> patternStack = new Stack<PatternValueNode>();
		for (char c : getValueRange(0)) {
			BitSet tmpBitSet = (BitSet) patternsToBeCovered.clone();

			tmpBitSet.and(this.attrValuesMatchedByPatterns[getValueIdx(0, c)]);
			patternStack.add(new PatternValueNode(c, tmpBitSet));
		}
		PatternValueNode patternWithMaxCoverage = null;

		int filter = 0;

		while (!patternStack.isEmpty()) {			
			if (Thread.currentThread().isInterrupted())
				return null;
			
			PatternValueNode curPatternValueNode = patternStack.pop();

			// If the current node has an coverage lower than filter, prune it
			if (curPatternValueNode.numCoveredPatterns <= filter)
				continue;
			// Find a node that might has the max coverage, we save it and
			// update the filter
			else if (curPatternValueNode.ifComplete()) {
				patternWithMaxCoverage = curPatternValueNode;
				filter = curPatternValueNode.numCoveredPatterns;
			}
			// expand the node
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
				if (listOfPatterns != null && listOfPatterns.size() > 0) {

					// Make them in reverse order, so that the one with high
					// coverage is on top of stack
					Collections.sort(listOfPatterns, Collections.reverseOrder());

					patternStack.addAll(listOfPatterns);
				}
			}
		}
		return patternWithMaxCoverage;
	}
}
