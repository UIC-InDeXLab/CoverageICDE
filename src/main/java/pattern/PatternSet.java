package pattern;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.DataSet;

public class PatternSet {
	public Set<Pattern> patternSet;

	public BitSet[] patternBitVec;
	public int[] cardinalities;
	
	public BitSet[] initialMatchBitVecForCheckingAncestor;
	public BitSet[] initialMatchBitVecForCheckingDescendant;

	public long time;

	public List<Integer> levelList;
	public int maxLevel;
	public int minLevel;

	public PatternSet(int[] cardinalities) {
		this.cardinalities = Arrays.copyOf(cardinalities,
				cardinalities.length);;
		this.patternSet = new HashSet<Pattern>();
		this.levelList = new LinkedList<Integer>();
		this.patternBitVec = new BitSet[DataSet.sumOfArray(this.cardinalities)
				+ this.cardinalities.length]; // there are
												// this.cardinalities.length
												// many "x"s
		for (int i = 0; i < this.patternBitVec.length; i++) {
			this.patternBitVec[i] = new BitSet(10);
		}

		time = 0;

		maxLevel = -1;
		minLevel = Integer.MAX_VALUE;
		
		
		// 
		initialMatchBitVecForCheckingAncestor = new BitSet[cardinalities.length + 1];
		initialMatchBitVecForCheckingDescendant = new BitSet[cardinalities.length + 1];
		for (int level = 0; level <= this.cardinalities.length; level++) {
			initialMatchBitVecForCheckingAncestor[level] = new BitSet(1);
			initialMatchBitVecForCheckingDescendant[level] = new BitSet(1);
		}
	}

	public void add(Pattern patternToAdd) {
		if (this.patternSet.contains(patternToAdd))
			return;
		int colId = this.patternSet.size();
		this.patternSet.add(patternToAdd);
		this.levelList.add(patternToAdd.level);

		maxLevel = patternToAdd.level > maxLevel ? patternToAdd.level : maxLevel;
		minLevel = patternToAdd.level < minLevel ? patternToAdd.level : minLevel;

		for (int attrId = 0; attrId < patternToAdd.data.length; attrId++) {
			char curAttrValue = patternToAdd.data[attrId];
			int rowId = checkRowIdxInPatternBitVec(attrId, curAttrValue);
			this.patternBitVec[rowId].set(colId);
		}
		
		// Update initialMatchBitVec
		for (int futurePatternToCheckLevel = 0; futurePatternToCheckLevel <= this.cardinalities.length; futurePatternToCheckLevel++) {
			if (futurePatternToCheckLevel >= patternToAdd.level) {
				initialMatchBitVecForCheckingAncestor[futurePatternToCheckLevel].set(colId);
			}
			if (futurePatternToCheckLevel <= patternToAdd.level) {
				initialMatchBitVecForCheckingDescendant[futurePatternToCheckLevel].set(colId);
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
	public boolean hasAncestorTo(Pattern patternToCheck,
			boolean returnTrueIfIdenticalIsFound) {
		if (patternSet.isEmpty())
			return false;

		if (patternSet.contains(patternToCheck))
			return returnTrueIfIdenticalIsFound;

		// If the highest node is still lower than node p, there cannot be any
		// ancestor
		if (patternToCheck.level > minLevel)
			return false;

//		BitSet match = new BitSet(size());
//		match.set(0, size());
		BitSet match = (BitSet)initialMatchBitVecForCheckingAncestor[patternToCheck.level].clone();

		for (int attrId = 0; attrId < patternToCheck.data.length; attrId++) {

			char attrValueToCheck = patternToCheck.data[attrId];

			BitSet bitVecForThisValueAtThisAttr = (BitSet) this.patternBitVec[checkRowIdxInPatternBitVec(
					attrId, attrValueToCheck)].clone();

			if (attrValueToCheck != 'x') {
				BitSet bitVecOfXAtThisAttr = this.patternBitVec[checkRowIdxOfXInPatternBitVec(
						attrId)];
				bitVecForThisValueAtThisAttr.or(bitVecOfXAtThisAttr);

			}

			match.and(bitVecForThisValueAtThisAttr);
			if (match.isEmpty())
				return false;
		}

		return !match.isEmpty();
	}

	/**
	 * If at least one pattern is a descendant of pattern p
	 * 
	 * @param patternToCheck
	 * @return
	 */
	public boolean hasDescendantTo(Pattern patternToCheck,
			boolean returnTrueIfIdenticalIsFound) {
		if (patternSet.isEmpty())
			return false;

		if (patternSet.contains(patternToCheck))
			return returnTrueIfIdenticalIsFound;

		// If the lowest node is still higher than node p, there cannot be any
		// descendant
		if (patternToCheck.level < maxLevel)
			return false;
		
		BitSet match = (BitSet)initialMatchBitVecForCheckingDescendant[patternToCheck.level].clone();
		
		for (int attrId = 0; attrId < patternToCheck.data.length; attrId++) {
			char attrValueToCheck = patternToCheck.data[attrId];
			if (attrValueToCheck != 'x') {
				match.and(this.patternBitVec[checkRowIdxInPatternBitVec(attrId,
						attrValueToCheck)]);
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
