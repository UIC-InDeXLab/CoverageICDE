package pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.RoaringBitmap;

import io.DataSet;

public class PatternSet {
	public Set<Pattern> patternSet;

	public RoaringBitmap[][] patternBitVecForCheckingAncestor;
	public RoaringBitmap[][] patternBitVecForCheckingDescendant;

	public int[] patternBitVecForCheckingAncestorVectorLength;
	public int[] patternBitVecForCheckingDescendantVectorLength;

	public int[] cardinalities;

//	public long time;

	public int maxLevel;
	public int minLevel;

	public PatternSet(int[] cardinalities) {
		this.cardinalities = Arrays.copyOf(cardinalities,
				cardinalities.length);;
		this.patternSet = new HashSet<Pattern>();
		this.patternBitVecForCheckingAncestor = new RoaringBitmap[cardinalities.length
				+ 1][DataSet.sumOfArray(this.cardinalities)
						+ this.cardinalities.length]; // there are
														// this.cardinalities.length
														// many "x"s
		this.patternBitVecForCheckingDescendant = new RoaringBitmap[cardinalities.length
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
				this.patternBitVecForCheckingAncestor[level][attrValue] = new RoaringBitmap();
				this.patternBitVecForCheckingDescendant[level][attrValue] = new RoaringBitmap();
			}
		}

		// Initialize patternBitVecForCheckingAncestorVectorLength and
		// patternBitVecForCheckingDescendantVectorLength
		patternBitVecForCheckingAncestorVectorLength = new int[this.cardinalities.length
				+ 1];
		patternBitVecForCheckingDescendantVectorLength = new int[this.cardinalities.length
				+ 1];
//		time = 0;

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
				int rowId = checkRowIdxInPatternBitVec(attrId, curAttrValue);
				this.patternBitVecForCheckingAncestor[currentLevel][rowId]
						.add(patternId);
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
						.add(patternId);
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

		RoaringBitmap match = new RoaringBitmap();
		match.add((long)0, patternBitVecForCheckingAncestorVectorLength[patternToCheck.level]);

		// Only check attribute where value is 'x' in patternToCheck
		for (int attrId = 0; attrId < patternToCheck.getDimension(); attrId++) {
			char attrValueToCheck = patternToCheck.data[attrId];
			
			if (attrValueToCheck != 'x') {

				RoaringBitmap bitVecForThisValueAtThisAttr = this.patternBitVecForCheckingAncestor[patternToCheck.level][checkRowIdxInPatternBitVec(
						attrId, attrValueToCheck)];
					RoaringBitmap bitVecOfXAtThisAttr = this.patternBitVecForCheckingAncestor[patternToCheck.level][checkRowIdxOfXInPatternBitVec(
							attrId)];
				match.and(RoaringBitmap.or(bitVecForThisValueAtThisAttr, bitVecOfXAtThisAttr));
			}
			else {
				RoaringBitmap bitVecForThisValueAtThisAttr = this.patternBitVecForCheckingAncestor[patternToCheck.level][checkRowIdxInPatternBitVec(
						attrId, attrValueToCheck)];
		
				match.and(bitVecForThisValueAtThisAttr);
			}
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

//		RoaringBitmap match = new RoaringBitmap();
//		match.add((long)0,(long)
//				patternBitVecForCheckingDescendantVectorLength[patternToCheck.level]);
		
		List<RoaringBitmap> toAndBitVecs = new ArrayList<RoaringBitmap>();
		
		
		
		for (int attrId = 0; attrId < patternToCheck.getDimension(); attrId++) {
			char attrValueToCheck = patternToCheck.data[attrId];
			if (attrValueToCheck != 'x') {
				toAndBitVecs.add(
						this.patternBitVecForCheckingDescendant[patternToCheck.level][checkRowIdxInPatternBitVec(
								attrId, attrValueToCheck)]);

			}
		}
		
		RoaringBitmap match = FastAggregation.and(toAndBitVecs.toArray(new RoaringBitmap[toAndBitVecs.size()]));

		return !match.isEmpty();
	}

	public int size() {
		return patternSet.size();
	}

	public static void main(String[] argv) {
		RoaringBitmap r = new RoaringBitmap();
		long s0 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			RoaringBitmap r0 = new RoaringBitmap();
			r0.add((long)0,i);
			r.and(r0);
		}
		System.out.println(System.currentTimeMillis() - s0);
		
		
		BitSet r1 = new BitSet();
		long s1 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			BitSet r0 = new BitSet();
			r0.set(0,i);
			r1.and(r0);
		}
		System.out.println(System.currentTimeMillis() - s1);
	}

}
