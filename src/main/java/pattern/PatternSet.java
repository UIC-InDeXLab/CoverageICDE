package pattern;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import io.DataSet;

public class PatternSet {
	public Set<Pattern> patternSet;
	public BitSet[] patternBitVec;
	public int[] cardinalities;

	public PatternSet(int[] cardinalities) {
		this.cardinalities = Arrays.copyOf(cardinalities,
				cardinalities.length);;
		this.patternSet = new HashSet<Pattern>();
		this.patternBitVec = new BitSet[DataSet.sumOfArray(this.cardinalities)
				+ this.cardinalities.length]; // there are
												// this.cardinalities.length
												// many "x"s
		for (int i = 0; i < this.patternBitVec.length; i++) {
			this.patternBitVec[i] = new BitSet(10);
		}
	}

	public void add(Pattern p) {
		if (this.patternSet.contains(p))
			return;
		int colId = this.patternSet.size();
		this.patternSet.add(p);
		for (int attrId = 0; attrId < p.data.length; attrId++) {
			char curAttrValue = p.data[attrId];			
			int rowId = checkRowIdxInPatternBitVec(attrId, curAttrValue);
			this.patternBitVec[rowId].set(colId);
		}
	}

	/**
	 * Find the rowId in patternBitVec given the value and the attribute id
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
	 * @param p
	 * @return
	 */
	public boolean hasAncestorTo(Pattern p,
			boolean returnTrueIfIdenticalIsFound) {
		if (patternSet.isEmpty())
			return false;

		if (patternSet.contains(p))
			return returnTrueIfIdenticalIsFound;

		BitSet match = new BitSet(size());
		match.set(0, size());

		for (int attrId = 0; attrId < p.data.length; attrId++) {
			char attrValueToCheck = p.data[attrId];
			
			BitSet bitVecForThisValueAtThisAttr = (BitSet) this.patternBitVec[checkRowIdxInPatternBitVec(attrId,
					attrValueToCheck)].clone();

			if (attrValueToCheck != 'x') {
				BitSet bitVecOfXAtThisAttr = this.patternBitVec[checkRowIdxOfXInPatternBitVec(attrId)];
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
	 * @param p
	 * @return
	 */
	public boolean hasDescendantTo(Pattern p,
			boolean returnTrueIfIdenticalIsFound) {
		if (patternSet.isEmpty())
			return false;

		if (patternSet.contains(p))
			return returnTrueIfIdenticalIsFound;

		BitSet match = new BitSet(size());
		match.set(0, size());
		for (int attrId = 0; attrId < p.data.length; attrId++) {
			char attrValueToCheck = p.data[attrId];
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
