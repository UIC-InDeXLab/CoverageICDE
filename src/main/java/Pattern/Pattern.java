package Pattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Pattern implements Comparable<Pattern> {
	public char[] data; // the content of pattern (a sequence of characters)

	public Pattern(char[] data) {
		this.data = Arrays.copyOf(data, data.length);
	}

	public Pattern(char[] data, int idx, char chrToReplace) {
		this.data = data.clone();
		this.data[idx] = chrToReplace;
	}

	public int getDimension() {
		return data.length;
	}

	/**
	 * Check if the current pattern is the ancestor of "other"
	 * 
	 * @param other
	 * @return
	 */
	public boolean isAncestorOf(Pattern other) {
		int size = getDimension();
		if (size != other.getDimension()) {
			return false;
		}
		
		for (int i = 0; i < size; i++) {
			char thisChar = data[i];
			char otherChar = other.data[i];
			if (thisChar == 'x' || thisChar == otherChar)
				continue;
			else
				return false;
		}

		return true;
	}

	/**
	 * Get the root pattern 'xxx...x'
	 * 
	 * @param dimension
	 * @return
	 */
	public static Pattern getRootPattern(int dimension) {
		char[] rootData = new char[dimension];
		for (int i = 0; i < dimension; i++) {
			rootData[i] = 'x';
		}
		return new Pattern(rootData);
	}

	/**
	 * From right to left, find the index on the first deterministic character
	 * (non 'x') (i.e., not 'x')
	 * 
	 * @return
	 */
	public int findRightMostDeterministicIndex() {
		int idx = -1;
		for (idx = getDimension() - 1; idx >= 0; idx--) {
			if (data[idx] != 'x')
				return idx;
		}
		return idx;
	}

	/**
	 * From right to left, find the index on the first non-deterministic
	 * character ('x') (i.e., not 'x')
	 * 
	 * @return
	 */
	public int findRightMostNonDeterministicIndex() {
		int idx = -1;
		for (idx = getDimension() - 1; idx >= 0; idx--) {
			if (data[idx] == 'x')
				return idx;
		}
		return idx;
	}

	/**
	 * Get parent patterns by replacing each non 'x' with an x
	 * 
	 * @return
	 */
	public Map<Integer, Pattern> genParents() {
		Map<Integer, Pattern> replacedPositionToParentPatternMap = new HashMap<Integer, Pattern>();
		for (int i = 0; i < getDimension(); i++) {
			if (data[i] != 'x') {
				replacedPositionToParentPatternMap.put(i,
						new Pattern(data, i, 'x'));
			}
		}

		return replacedPositionToParentPatternMap;
	}

	/**
	 * Get parent patterns by replacing each non 'x' with an x
	 * 
	 * @return
	 */
	public Map<Integer, Pattern> genParentsBasedOnRule2() {
		Map<Integer, Pattern> replacedPositionToParentPatternMap = new HashMap<Integer, Pattern>();
		for (int i = 0; i < getDimension(); i++) {
			if (data[i] == '0') {
				replacedPositionToParentPatternMap.put(i,
						new Pattern(data, i, 'x'));
			}
		}

		return replacedPositionToParentPatternMap;
	}

	@Override
	public boolean equals(Object o) {
		// System.out.println("equal called");
		if (o == this)
			return true; // If objects equal, is OK
		if (o instanceof Pattern) {
			Pattern other = (Pattern) o;

			if (Arrays.equals(this.data, other.data)) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(data));
	}

	@Override
	public String toString() {
		String msg = "";
		for (char a : data)
			msg += a;

		return msg;
	}

	public int compareTo(Pattern other) {
		return this.data.toString().compareTo(other.data.toString());
	}

	public static void main(String[] args) {
		Pattern p1 = new Pattern(new char[]{'1', 'x'});
		Pattern p2 = new Pattern(new char[]{'1', 'x'});
		Map<Pattern, Integer> coverageHashThisLevel = new HashMap<Pattern, Integer>();
		coverageHashThisLevel.put(p1, 0);

		System.out.println(p1.equals(p2));
		System.out.println(coverageHashThisLevel.get(p1));
		System.out.println(coverageHashThisLevel.get(p2));
		System.out.println(coverageHashThisLevel);
	}

}
