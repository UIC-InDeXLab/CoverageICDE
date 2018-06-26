package pattern;

import java.util.ArrayList;
import java.util.Arrays;
import utils.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import io.DataSet;

public class MupSet {
	public Set<Pattern> patternSet;

	public BitSet[][] patternBitVecForCheckingAncestor;
	public BitSet[][] patternBitVecForCheckingDescendant;

	public BitSet[][] patternBitVecForCheckingAncestorOrWithX;
	public BitSet[][] patternBitVecForCheckingDescendaandOrWithX;

	public int[] patternBitVecForCheckingAncestorVectorLength;
	public int[] patternBitVecForCheckingDescendantVectorLength;

	public int[] cardinalities;
	public int[] cardinalitiesSum;

	public int maxLevel;
	public int minLevel;
	
	public int lastAddedMupId;

	public MupSet(int[] cardinalities) {
		this.cardinalities = Arrays.copyOf(cardinalities, cardinalities.length);
		// Initialize cardinalitiesSum
		this.cardinalitiesSum = new int[cardinalities.length];

		for (int i = 0; i < cardinalitiesSum.length; i++) {
			this.cardinalitiesSum[i] = DataSet.sumOfArray(this.cardinalities,
					i + 1);
		}

		this.patternSet = new HashSet<Pattern>();
		this.patternBitVecForCheckingAncestor = new BitSet[cardinalities.length
				+ 1][IntStream.of(this.cardinalities).sum()
						+ this.cardinalities.length]; // there are
														// this.cardinalities.length
														// many "x"s
		this.patternBitVecForCheckingDescendant = new BitSet[cardinalities.length
				+ 1][IntStream.of(this.cardinalities).sum()
						+ this.cardinalities.length]; // there are
														// this.cardinalities.length
														// many "x"s

		this.patternBitVecForCheckingAncestorOrWithX = new BitSet[cardinalities.length
				+ 1][IntStream.of(this.cardinalities).sum()
						+ this.cardinalities.length];
		this.patternBitVecForCheckingDescendaandOrWithX = new BitSet[cardinalities.length
				+ 1][IntStream.of(this.cardinalities).sum()
						+ this.cardinalities.length];

		// Initialize patternBitVecForCheckingAncestor and
		// patternBitVecForCheckingDescendant
		for (int level = 0; level <= cardinalities.length; level++) {
			for (int attrValue = 0; attrValue < IntStream.of(this.cardinalities).sum()
					+ this.cardinalities.length; attrValue++) {
				this.patternBitVecForCheckingAncestor[level][attrValue] = new BitSet(
						10);
				this.patternBitVecForCheckingDescendant[level][attrValue] = new BitSet(
						10);
				this.patternBitVecForCheckingAncestorOrWithX[level][attrValue] = new BitSet(
						10);
				this.patternBitVecForCheckingDescendaandOrWithX[level][attrValue] = new BitSet(
						10);
			}
		}

		// Initialize patternBitVecForCheckingAncestorVectorLength and
		// patternBitVecForCheckingDescendantVectorLength
		patternBitVecForCheckingAncestorVectorLength = new int[this.cardinalities.length
				+ 1];
		patternBitVecForCheckingDescendantVectorLength = new int[this.cardinalities.length
				+ 1];
		// time = 0;

		maxLevel = -1;
		minLevel = Integer.MAX_VALUE;
		
		lastAddedMupId = -1;
	}

	public void add(Pattern patternToAdd) {
		add(patternToAdd, 0);		
	}

	public void add(Pattern patternToAdd, int numLevelsSkipped) {
		if (this.patternSet.contains(patternToAdd))
			return;

		int mupLevelThreshold = cardinalities.length - numLevelsSkipped;

		this.patternSet.add(patternToAdd);

		maxLevel = patternToAdd.level > maxLevel
				? patternToAdd.level
				: maxLevel;
		minLevel = patternToAdd.level < minLevel
				? patternToAdd.level
				: minLevel;

		if (patternToAdd.level <= mupLevelThreshold)
			for (int currentLevel = patternToAdd.level
					+ 1; currentLevel <= cardinalities.length; currentLevel++) {
				int patternId = this.patternBitVecForCheckingAncestorVectorLength[currentLevel]++;
				for (int attrId = 0; attrId < patternToAdd
						.getDimension(); attrId++) {
					char curAttrValue = patternToAdd.data[attrId];
					int attrValueId = checkRowIdxInPatternBitVec(attrId,
							curAttrValue);
					this.patternBitVecForCheckingAncestor[currentLevel][attrValueId]
							.set(patternId);

					// update this.patternBitVecForCheckingAncestorOrWithX
					if (curAttrValue == 'x') {
						for (int i = 1; i <= cardinalities[attrId]; i++) {
							int attrValForThisXRowId = attrValueId - i;
							this.patternBitVecForCheckingAncestorOrWithX[currentLevel][attrValForThisXRowId].set(patternId);
						}

					} else {
						this.patternBitVecForCheckingAncestorOrWithX[currentLevel][attrValueId].set(patternId);
					}
				}
			}

		if (patternToAdd.level >= numLevelsSkipped)
			for (int currentLevel = 0; currentLevel < patternToAdd.level; currentLevel++) {
				int patternId = this.patternBitVecForCheckingDescendantVectorLength[currentLevel]++;
				for (int attrId = 0; attrId < patternToAdd
						.getDimension(); attrId++) {
					char curAttrValue = patternToAdd.data[attrId];
					int attrValueId = checkRowIdxInPatternBitVec(attrId,
							curAttrValue);
					this.patternBitVecForCheckingDescendant[currentLevel][attrValueId]
							.set(patternId);

					// update patternBitVecForCheckingDescendaandOrWithX
					if (curAttrValue == 'x') {
						for (int i = 1; i <= cardinalities[attrId]; i++) {
							int attrValIdForThisX = attrValueId - i;
							this.patternBitVecForCheckingDescendaandOrWithX[currentLevel][attrValIdForThisX].set(patternId);
						}

					} else {
						this.patternBitVecForCheckingDescendaandOrWithX[currentLevel][attrValueId].set(patternId);
					}
				}
			}
	}

	/**
	 * Find the rowId in patternBitVec given the value and the attribute id
	 * 
	 * @param attrId
	 * @param c
	 * @return
	 */
	private int checkRowIdxInPatternBitVec(int attrId, char c) {
		if (c == 'x')
			return cardinalitiesSum[attrId] + attrId;
		if (attrId == 0)
			return c - 48;
		return c - 48 + cardinalitiesSum[attrId - 1] + attrId;
	}

	/**
	 * Find the rowId in patternBitVec of value 'x' given the attribute id
	 * 
	 * @param attrId
	 * @param c
	 * @return
	 */
	private int checkRowIdxOfXInPatternBitVec(int attrId) {
		return cardinalitiesSum[attrId] + attrId;
	}

	/**
	 * If at least one pattern is an ancestor of pattern p
	 * 
	 * @param patternToCheck
	 * @return
	 */
	public boolean ifDominates(Pattern patternToCheck,
			boolean returnTrueIfIdenticalIsFound) {
		if (patternSet.isEmpty())
			return false;

		if (patternSet.contains(patternToCheck))
			return returnTrueIfIdenticalIsFound;

		// If the highest node is still lower than node p, there cannot be any
		// ancestor
		if (patternToCheck.level <= minLevel)
			return false;

		BitSet[] bitSetToCheckArray = new BitSet[patternToCheck.getDimension()];
		int i = 0;
		for (int attrId = 0; attrId < patternToCheck.getDimension(); attrId++) {
			if (patternToCheck.data[attrId] == 'x') {
				bitSetToCheckArray[i++] = this.patternBitVecForCheckingAncestor[patternToCheck.level][checkRowIdxInPatternBitVec(
						attrId, patternToCheck.data[attrId])];
			} else {
				bitSetToCheckArray[i++] = patternBitVecForCheckingAncestorOrWithX[patternToCheck.level][checkRowIdxInPatternBitVec(
						attrId, patternToCheck.data[attrId])];
			}
		}

		return BitSet.intersect(bitSetToCheckArray);
	}

	/**
	 * If at least one pattern is a descendant of pattern p
	 * 
	 * @param patternToCheck
	 * @return
	 */
	public boolean ifIsDominatedBy(Pattern patternToCheck,
			boolean returnTrueIfIdenticalIsFound) {
		if (patternSet.isEmpty()) {
			patternToCheck.selfDominatesMups = false;
			return false;
		}

		if (patternSet.contains(patternToCheck)) {
			patternToCheck.selfDominatesMups = returnTrueIfIdenticalIsFound;
			return returnTrueIfIdenticalIsFound;
		}

		// If the lowest node is still higher than node p, there cannot be any
		// descendant
		if (patternToCheck.level >= maxLevel) {
			patternToCheck.selfDominatesMups = false;
			return false;
		}

		// The number of bitSets to check intersection equals to
		// patternToCheck.level
		BitSet[] bitSetToCheckArray = new BitSet[patternToCheck.level];
		int i = 0;
		for (int attrId = 0; attrId < patternToCheck.getDimension(); attrId++) {
			if (patternToCheck.data[attrId] != 'x') {
				bitSetToCheckArray[i++] = this.patternBitVecForCheckingDescendant[patternToCheck.level][checkRowIdxInPatternBitVec(
						attrId, patternToCheck.data[attrId])];
			}
		}

		patternToCheck.selfDominatesMups = BitSet.intersect(bitSetToCheckArray);
		return patternToCheck.selfDominatesMups;
	}

	public int size() {
		return patternSet.size();
	}

	public static void main(String[] argv) {
	}

}
