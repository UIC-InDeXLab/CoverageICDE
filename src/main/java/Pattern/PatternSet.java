package Pattern;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PatternSet {
	public Set<Pattern> patternSet;
	public Map<Integer, Map<Character, BitSet>> attributeValueHash;
	public int[] cardinalities;

	public PatternSet(int[] cardinalities) {
		this.cardinalities = Arrays.copyOf(cardinalities,
				cardinalities.length);;
		this.patternSet = new HashSet<Pattern>();
		this.attributeValueHash = new HashMap<Integer, Map<Character, BitSet>>(
				cardinalities.length);
		for (int attrId = 0; attrId < cardinalities.length; attrId++) {
			Map<Character, BitSet> valueHash = attributeValueHash.getOrDefault(
					attrId,
					new HashMap<Character, BitSet>(cardinalities[attrId] + 1));
			for (int value = 0; value < cardinalities[attrId]; value++) {
				valueHash.put((char) (value + 48), null);
			}
			valueHash.put('x', null);
			attributeValueHash.put(attrId, valueHash);
		}
	}

	public void add(Pattern p) {
		if (this.patternSet.contains(p))
			return;

		int originalSize = size();

		this.patternSet.add(p);
		for (int attrId = 0; attrId < p.data.length; attrId++) {
			Map<Character, BitSet> valueHash = attributeValueHash.get(attrId);
			for (int value = 0; value < cardinalities[attrId]; value++) {
				char curAttrValue = (char) (value + 48);
				BitSet b = valueHash.get(curAttrValue);
				if (b == null) {
					b = new BitSet(1);
				}

				if (curAttrValue == p.data[attrId])
					b.set(originalSize);
				else
					b.set(originalSize, false);
				valueHash.put(curAttrValue, b);
			}

			// 'x'
			char curAttrValue = 'x';
			BitSet b = valueHash.get(curAttrValue);
			if (b == null) {
				b = new BitSet(1);
			}

			if (curAttrValue == p.data[attrId])
				b.set(originalSize);
			else
				b.set(originalSize, false);
			valueHash.put(curAttrValue, b);

			attributeValueHash.put(attrId, valueHash);
		}
	}

	/**
	 * If at least one pattern is an ancestor of pattern p
	 * 
	 * @param p
	 * @return
	 */
	public boolean hasAncestorTo(Pattern p) {
		if (patternSet.isEmpty())
			return false;

		BitSet match = new BitSet(size());
		match.set(0, size());

		for (int colId = 0; colId < p.data.length; colId++) {
			char attrValueToCheck = p.data[colId];

			// patterns contains attrValueToCheck at colId
			BitSet b1 = (BitSet) attributeValueHash.get(colId)
					.get(attrValueToCheck).clone();

			// patterns contains 'x' at colId
			if (attrValueToCheck != 'x') {
				BitSet b2 = attributeValueHash.get(colId).get('x');
				b1.or(b2);
			}

			// get matching patterns
			match.and(b1);

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
	public boolean hasDescendantTo(Pattern p) {
		if (patternSet.isEmpty())
			return false;

		BitSet match = new BitSet(size());
		match.set(0, size());
		for (int colId = 0; colId < p.data.length; colId++) {
			char attrValueToCheck = p.data[colId];

			// If attrValueToCheck in pattern p is 'x', we skip it because all
			// patterns will be descendant of p in this attribute
			if (attrValueToCheck == 'x')
				continue;

			// patterns contains attrValueToCheck at colId
			BitSet b1 = attributeValueHash.get(colId).get(attrValueToCheck);

			// get matching patterns
			match.and(b1);

			if (match.isEmpty())
				return false;
		}

		return !match.isEmpty();
	}

	public int size() {
		return patternSet.size();
	}

}
