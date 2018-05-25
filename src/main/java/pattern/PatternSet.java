package pattern;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import io.DataSet;

public class PatternSet {
	public Set<Pattern> patternSet;

	public BitSet[][] patternBitVecForCheckingAncestor;
	public BitSet[][] patternBitVecForCheckingDescendant;

	public int[][] numOfTrueBitsForCheckingAncestor;
	public int[][] numOfTrueBitsForCheckingDescendant;

	public int[] patternBitVecForCheckingAncestorVectorLength;
	public int[] patternBitVecForCheckingDescendantVectorLength;

	public int[] cardinalities;

	public long time;

	public int maxLevel;
	public int minLevel;

	public PatternSet(int[] cardinalities) {
		this.cardinalities = Arrays.copyOf(cardinalities,
				cardinalities.length);;
		this.patternSet = new HashSet<Pattern>();
		this.patternBitVecForCheckingAncestor = new BitSet[cardinalities.length
				+ 1][DataSet.sumOfArray(this.cardinalities)
						+ this.cardinalities.length]; // there are
														// this.cardinalities.length
														// many "x"s
		this.patternBitVecForCheckingDescendant = new BitSet[cardinalities.length
				+ 1][DataSet.sumOfArray(this.cardinalities)
						+ this.cardinalities.length]; // there are
														// this.cardinalities.length
														// many "x"s
		
		this.numOfTrueBitsForCheckingAncestor = new int[cardinalities.length
		                                   				+ 1][DataSet.sumOfArray(this.cardinalities)
		                             						+ this.cardinalities.length];
		this.numOfTrueBitsForCheckingDescendant = new int[cardinalities.length
			                                   				+ 1][DataSet.sumOfArray(this.cardinalities)
				                             						+ this.cardinalities.length];
		// Initialize patternBitVecForCheckingAncestor and
		// patternBitVecForCheckingDescendant
		for (int level = 0; level <= cardinalities.length; level++) {
			for (int attrValue = 0; attrValue < DataSet
					.sumOfArray(this.cardinalities)
					+ this.cardinalities.length; attrValue++) {
				this.patternBitVecForCheckingAncestor[level][attrValue] = new BitSet(
						10);
				this.patternBitVecForCheckingDescendant[level][attrValue] = new BitSet(
						10);
			}
		}

		// Initialize patternBitVecForCheckingAncestorVectorLength and
		// patternBitVecForCheckingDescendantVectorLength
		patternBitVecForCheckingAncestorVectorLength = new int[this.cardinalities.length
				+ 1];
		patternBitVecForCheckingDescendantVectorLength = new int[this.cardinalities.length
				+ 1];
		time = 0;

		maxLevel = -1;
		minLevel = Integer.MAX_VALUE;
	}

	public void add(Pattern patternToAdd) {
		if (this.patternSet.contains(patternToAdd))
			return;

		this.patternSet.add(patternToAdd);

		maxLevel = patternToAdd.level > maxLevel
				? patternToAdd.level
				: maxLevel;
		minLevel = patternToAdd.level < minLevel
				? patternToAdd.level
				: minLevel;
		for (int currentLevel = patternToAdd.level
				+ 1; currentLevel <= cardinalities.length; currentLevel++) {
			int patternId = this.patternBitVecForCheckingAncestorVectorLength[currentLevel]++;
			for (int attrId = 0; attrId < patternToAdd
					.getDimension(); attrId++) {
				char curAttrValue = patternToAdd.data[attrId];
				int attrValueId = checkRowIdxInPatternBitVec(attrId, curAttrValue);
				this.patternBitVecForCheckingAncestor[currentLevel][attrValueId]
						.set(patternId);
				this.numOfTrueBitsForCheckingAncestor[currentLevel][attrValueId]++;
			}
		}

		for (int currentLevel = 0; currentLevel < patternToAdd.level; currentLevel++) {
			int patternId = this.patternBitVecForCheckingDescendantVectorLength[currentLevel]++;
			for (int attrId = 0; attrId < patternToAdd
					.getDimension(); attrId++) {
				char curAttrValue = patternToAdd.data[attrId];
				int attrValudId = checkRowIdxInPatternBitVec(attrId,
						curAttrValue);
				this.patternBitVecForCheckingDescendant[currentLevel][attrValudId]
						.set(patternId);
				this.numOfTrueBitsForCheckingDescendant[currentLevel][attrValudId]++;
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
			return DataSet.sumOfArray(this.cardinalities, attrId + 1) + attrId;
		return c - 48 + DataSet.sumOfArray(this.cardinalities, attrId) + attrId;
	}

	/**
	 * Find the rowId in patternBitVec of value 'x' given the attribute id
	 * 
	 * @param attrId
	 * @param c
	 * @return
	 */
	private int checkRowIdxOfXInPatternBitVec(int attrId) {
		return DataSet.sumOfArray(this.cardinalities, attrId + 1) + attrId;
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

		BitSet match = new BitSet(
				patternBitVecForCheckingAncestorVectorLength[patternToCheck.level]);
		match.set(0,
				patternBitVecForCheckingAncestorVectorLength[patternToCheck.level]);
		
		// count, ifX, attrValueIdx, attrXIdx
		int[][] occurences = new int[patternToCheck.getDimension()][4];

		// Only check attribute where value is 'x' in patternToCheck
		for (int attrId = 0; attrId < patternToCheck.getDimension(); attrId++) {
			char attrValueToCheck = patternToCheck.data[attrId];
//			occurences[attrId][0] = attrId;
			occurences[attrId][0] = numOfTrueBitsForCheckingAncestor[patternToCheck.level][checkRowIdxInPatternBitVec(
					attrId, attrValueToCheck)];
			occurences[attrId][2] = checkRowIdxInPatternBitVec(attrId, attrValueToCheck);
			if (attrValueToCheck != 'x') {
				occurences[attrId][0] += numOfTrueBitsForCheckingAncestor[patternToCheck.level][checkRowIdxOfXInPatternBitVec(
						attrId)];
				occurences[attrId][1] = 1;
				occurences[attrId][3] = checkRowIdxOfXInPatternBitVec(attrId);
			}
			else {
				occurences[attrId][1] = 0;
			}
		}
		
		Arrays.sort(occurences, Comparator.comparingInt(arr -> arr[0]));
		
		for (int[] s : occurences) {
			
			int count = s[0];
			int ifCharacterIsX = s[1];
			
//			System.out.println(count);

			BitSet bitVecForThisValueAtThisAttr = (BitSet) this.patternBitVecForCheckingAncestor[patternToCheck.level][s[2]].clone();
			if (ifCharacterIsX == 1) {
				BitSet bitVecOfXAtThisAttr = this.patternBitVecForCheckingAncestor[patternToCheck.level][s[3]];
				bitVecForThisValueAtThisAttr.or(bitVecOfXAtThisAttr);
			}

			match.and(bitVecForThisValueAtThisAttr);
			if (match.isEmpty()) {
//				System.out.println();
				return false;
			}

		}
//		System.out.println();

		return true;
	}

	/**
	 * If at least one pattern is a descendant of pattern p
	 * 
	 * @param patternToCheck
	 * @return
	 */
	public boolean ifIsDominatedBy(Pattern patternToCheck,
			boolean returnTrueIfIdenticalIsFound) {
		if (patternSet.isEmpty())
			return false;

		if (patternSet.contains(patternToCheck))
			return returnTrueIfIdenticalIsFound;

		// If the lowest node is still higher than node p, there cannot be any
		// descendant
		if (patternToCheck.level >= maxLevel)
			return false;

		BitSet match = new BitSet(
				patternBitVecForCheckingDescendantVectorLength[patternToCheck.level]);
		match.set(0,
				patternBitVecForCheckingDescendantVectorLength[patternToCheck.level]);

		for (int attrId = 0; attrId < patternToCheck.getDimension(); attrId++) {
			char attrValueToCheck = patternToCheck.data[attrId];
			if (attrValueToCheck != 'x') {
				match.and(
						this.patternBitVecForCheckingDescendant[patternToCheck.level][checkRowIdxInPatternBitVec(
								attrId, attrValueToCheck)]);
				if (match.isEmpty())
					return false;
			}
		}

		return !match.isEmpty();
	}

	public int size() {
		return patternSet.size();
	}

	public static void main(String[] argv) {
	}

}
