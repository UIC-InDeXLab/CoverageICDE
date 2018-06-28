package dataCollectionNew;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.math3.util.CombinatoricsUtils;

import io.DataSet;
import pattern.Pattern;

public class NaiveDataCollection {
	BitSet[] attrValuesMatchedByPatterns;
	int numberOfPatterns;
	int dimensions;
	public int[] cardinalities;
	public int[] cardinalitiesSum;
	
	public List<Pattern> mupsList;

	public NaiveDataCollection(int[] cardinality, Set<Pattern> mups) {
		this.numberOfPatterns = mups.size();
		
		if (mups.isEmpty()) {
			mupsList = null;
			return;
		}
		
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
		
		this.mupsList = new LinkedList<Pattern>(mups);

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
		
		if (mupsList == null)
			return minListOfKeyPatterns;

		// Get all patterns
		List<char[]> keyPatternList = new LinkedList<char[]>();
		for (char c : getValueRange(0)) {
			keyPatternList.add(new char[] {c});
		}

		for (int attrId = 1; attrId < this.dimensions; attrId++) {
			if (Thread.currentThread().isInterrupted())
				return null;
			
			List<char[]> tempPatternList = new LinkedList<char[]>(
					keyPatternList);
			keyPatternList.clear();

			for (char c : getValueRange(attrId)) {
				for (char[] curPatternValue : tempPatternList) {
					char[] newPatternValue = new char[curPatternValue.length + 1];
					System.arraycopy(curPatternValue, 0, newPatternValue, 0, curPatternValue.length);
					newPatternValue[newPatternValue.length - 1] = c;
					keyPatternList.add(newPatternValue);
				}
			}
		}

		boolean[] coverage = new boolean[this.numberOfPatterns];
		
		// Hitting set
		while (!ifAllTrue(coverage)) {
			// find the pattern covers the most uncovered patterns
			char[] patternValueFound = findMaxCoverPatternValue(coverage, keyPatternList);
			
			if (Thread.currentThread().isInterrupted())
				return null;
			
			// Update coverage
			for (int i = 0; i < this.numberOfPatterns; i++)
				if (!coverage[i] && Pattern.covers(mupsList.get(i).data,patternValueFound))
					coverage[i] = true;
			
			// Remove 
			keyPatternList.remove(patternValueFound);
			
			// Add to minListOfKeyPatterns
			minListOfKeyPatterns.add(new PatternValueNode(patternValueFound));
		}

		return minListOfKeyPatterns;
	}
	
	private char[] findMaxCoverPatternValue(boolean[] currentCoverage, List<char[]> curKeyPatternList) {
		char[] patternToReturn = null;
		int coverageOfPatternToReturn = 0;
		for (char[] currentPattern : curKeyPatternList) {
			if (Thread.currentThread().isInterrupted())
				return null;
			
			int curCoverage = 0;
			for (int i = 0; i < this.numberOfPatterns; i++)
				if (!currentCoverage[i] && Pattern.covers(mupsList.get(i).data,currentPattern))
					curCoverage++;
				
			if (curCoverage > coverageOfPatternToReturn) {
				coverageOfPatternToReturn = curCoverage;
				patternToReturn = currentPattern;
			}
					
		}
		
		return patternToReturn;
	}
	
	/**
	 * Get all descendant patterns of a set of patterns at level l
	 * @param mups
	 * @param level
	 * @return
	 */
	public static Set<Pattern> getAllDescendentPatternsAtLevel(Set<Pattern> mups, int level, DataSet dataSet) {
		Set<Pattern> descendantPatterns = new HashSet<Pattern>();
		
		for (Pattern mup : mups) {
			descendantPatterns.addAll(getAllDescendentPatternsAtLevel(mup, level, dataSet));
		}		
		
		return descendantPatterns;
	}
	
	/**
	 * Get all descendant patterns of a single pattern at level l
	 * @param mup
	 * @param level
	 * @return
	 */
	public static Set<Pattern> getAllDescendentPatternsAtLevel(Pattern mup, int level, DataSet dataSet) {
		
		Set<Pattern> descendantPatterns = new HashSet<Pattern>();
		
		if (mup.level > level) 
			return descendantPatterns;
		else if (mup.level == level) {
			descendantPatterns.add(mup);
			return descendantPatterns;
		}
		else {
			char[] rawPattern = mup.data;
			int[] xIndexes = new int[mup.getDimension() - mup.level];
			
			int tmpIdx = 0;
			
			for (int i = 0; i < mup.getDimension(); i++) {
				if (mup.getValue(i) == 'x')
					xIndexes[tmpIdx++] = i;
			}

			Iterator<int[]> indexesSetChosenToChange = CombinatoricsUtils.combinationsIterator(xIndexes.length, level - mup.level);
			
			while (indexesSetChosenToChange.hasNext()) {
				int[] indexesChosenToChange = indexesSetChosenToChange.next();
				
				List<char[]> intermediatePatternDataList = new LinkedList<char[]>();
				intermediatePatternDataList.add(rawPattern);
				
				for (int chosenIdx : indexesChosenToChange) {
					List<char[]> tmpPatternDataList = new LinkedList<char[]>(intermediatePatternDataList);
					intermediatePatternDataList.clear();
					
					for (char c : dataSet.getValueRange(chosenIdx)) {
						for (char[] patternData : tmpPatternDataList) {
							char[] newPatternData = patternData.clone();
							newPatternData[chosenIdx] = c;
							intermediatePatternDataList.add(newPatternData);
						}
					}
				}
				
				// We get all new patterns
				for (char[] newPatternData : intermediatePatternDataList) {
					descendantPatterns.add(new Pattern(newPatternData));
				}
				
			}
		}		
		
		return descendantPatterns;
	}
	
	/**
	 * Check if a binary array is all true
	 * @param binaryArray
	 * @return
	 */
	private boolean ifAllTrue(boolean[] binaryArray) {
		return IntStream.range(0, binaryArray.length).allMatch(i -> binaryArray[i]);
	}

	public char[] getValueRange(int columnId) {
		char[] range = new char[this.cardinalities[columnId]];

		for (int i = 0; i < this.cardinalities[columnId]; i++)
			range[i] = (char) (i + 48);

		return range;
	}
}
