package pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.DataSet;

public class PatternSet {
	public Set<Pattern> patternSet;

	public BitSet[][] patternBitVecForCheckingAncestor;
	public BitSet[][] patternBitVecForCheckingDescendant;

	public int[] patternBitVecForCheckingAncestorVectorLength;
	public int[] patternBitVecForCheckingDescendantVectorLength;

	public int[] cardinalities;

	public List<Long>[] patternBitVecs;
	public int numPatterns;

	// public long time;

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
		// time = 0;

		maxLevel = -1;
		minLevel = Integer.MAX_VALUE;

		// Initialize patternBitVecs
		patternBitVecs = new List[DataSet.sumOfArray(this.cardinalities)
				+ this.cardinalities.length];

		for (int i = 0; i < this.patternBitVecs.length; i++) {
			this.patternBitVecs[i] = new ArrayList<Long>();
		}

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
				int rowId = checkRowIdxInPatternBitVec(attrId, curAttrValue);
				this.patternBitVecForCheckingAncestor[currentLevel][rowId]
						.set(patternId);
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
			}
		}

		int rem = this.numPatterns % Long.SIZE;
		if (rem == 0)
			for (int i = 0; i < this.patternBitVecs.length; i++) {
				this.patternBitVecs[i].add(0L);
			}
		int wordId = numPatterns / Long.SIZE; // get floor
		long mask = 1L << (Long.SIZE - rem - 1);
		
//		System.out
//		.println(StringUtils.leftPad(Long.toBinaryString(mask), 64, "0"));

		for (int attrId = 0; attrId < patternToAdd.getDimension(); attrId++) {
			char curAttrValue = patternToAdd.data[attrId];
			int attrValudId = checkRowIdxInPatternBitVec(attrId, curAttrValue);

			long tempVal = this.patternBitVecs[attrValudId].get(wordId);
			tempVal |= mask;
			this.patternBitVecs[attrValudId].set(wordId, tempVal);
		}
		numPatterns++;
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

		// Only check attribute where value is 'x' in patternToCheck
		for (int attrId = 0; attrId < patternToCheck.getDimension(); attrId++) {
			char attrValueToCheck = patternToCheck.data[attrId];

			BitSet bitVecForThisValueAtThisAttr = (BitSet) this.patternBitVecForCheckingAncestor[patternToCheck.level][checkRowIdxInPatternBitVec(
					attrId, attrValueToCheck)].clone();
			if (attrValueToCheck != 'x') {
				BitSet bitVecOfXAtThisAttr = this.patternBitVecForCheckingAncestor[patternToCheck.level][checkRowIdxOfXInPatternBitVec(
						attrId)];
				bitVecForThisValueAtThisAttr.or(bitVecOfXAtThisAttr);
			}

			match.and(bitVecForThisValueAtThisAttr);
			if (match.isEmpty())
				return false;

		}

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

		// BitSet match = new BitSet(
		// patternBitVecForCheckingDescendantVectorLength[patternToCheck.level]);
		// match.set(0,
		// patternBitVecForCheckingDescendantVectorLength[patternToCheck.level]);
		//
		// for (int attrId = 0; attrId < patternToCheck.getDimension();
		// attrId++) {
		// char attrValueToCheck = patternToCheck.data[attrId];
		// if (attrValueToCheck != 'x') {
		// match.and(
		// this.patternBitVecForCheckingDescendant[patternToCheck.level][checkRowIdxInPatternBitVec(
		// attrId, attrValueToCheck)]);
		// if (match.isEmpty())
		// return false;
		// }
		// }
		//
		// return true;
		List<Integer> idxOfBitVecsToAnd = new ArrayList<Integer>();

		for (int attrId = 0; attrId < patternToCheck.getDimension(); attrId++) {
			char attrValueToCheck = patternToCheck.data[attrId];
			if (attrValueToCheck != 'x') {
				idxOfBitVecsToAnd.add(
						checkRowIdxInPatternBitVec(attrId, attrValueToCheck));

			}
		}

		return intersect(idxOfBitVecsToAnd);
	}

	private boolean intersect(List<Integer> attrValIds) {
		if (attrValIds.size() == 0 || this.numPatterns == 0)
			return false;
		int wordId = (this.numPatterns - 1) / Long.SIZE;

		for (int i = 0; i <= wordId; i++) {
			long match = this.patternBitVecs[attrValIds.get(0)].get(i);
			for (int j = 0; j < attrValIds.size(); j++) {
				match &= this.patternBitVecs[attrValIds.get(j)].get(i);
				if (match == 0)
					break;
			}

			if (match != 0)
				return true;
		}
		return false;
	}

	public int size() {
		return patternSet.size();
	}

	public static void main(String[] argv) {
		long t = System.currentTimeMillis();
		System.out.println(Long.toBinaryString(t));

		long l = (long) 1;
		System.out
				.println(StringUtils.leftPad(Long.toBinaryString(l), 64, "0"));
		
		System.out.println(127/64);
	}

}
