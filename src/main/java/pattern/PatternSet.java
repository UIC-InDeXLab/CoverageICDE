package pattern;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

		this.patternSet.add(p);
		for (int attrId = 0; attrId < p.data.length; attrId++) {
			char curAttrValue = p.data[attrId];
			int colId = this.patternSet.size();
			int rowId = checkRowIdxInPatternBitVec(attrId, curAttrValue);
			this.patternBitVec[rowId].set(colId);
		}
	}

	private int checkRowIdxInPatternBitVec(int attrId, char c) {
		if (c == 'x')
			return DataSet.sumOfArray(this.cardinalities, attrId + 1) + attrId;
		return c - 48 + DataSet.sumOfArray(this.cardinalities, attrId) + attrId;
	}

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

			if (attrValueToCheck == 'x') {
				match.and(this.patternBitVec[checkRowIdxInPatternBitVec(attrId,
						attrValueToCheck)]);
				if (match.isEmpty())
					return false;
			} else {
				BitSet a = this.patternBitVec[checkRowIdxInPatternBitVec(attrId,
						attrValueToCheck)];
				BitSet b = (BitSet)this.patternBitVec[checkRowIdxOfXInPatternBitVec(attrId)].clone();
				b.or(a);
				match.and(b);
				if (match.isEmpty())
					return false;
			}

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
