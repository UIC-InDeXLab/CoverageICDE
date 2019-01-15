package pattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Pattern implements Comparable<Pattern> {
	public char[] data; // the content of pattern (a sequence of characters)
	public double covereagePercentage;
	public double covereage;
	public int level;
	
	public Pattern parentPattern = null;
	public boolean selfDominatesMups = false;
	public boolean parentDominatesMups = false;
	
	public int visitId;
	public int parentVisitId;

	public Pattern(char[] data) {
		this.data = Arrays.copyOf(data, data.length);
		this.covereage = -1;
		this.level = getLevel();
	}

	public Pattern(char[] data,
			List<double[]> coveragePercentageOfEachValueInEachAttr) {
		this.data = Arrays.copyOf(data, data.length);
		this.covereagePercentage = predictCoveragePercentage(
				coveragePercentageOfEachValueInEachAttr);
		this.covereage = -1;
		this.level = getLevel();
	}

	public Pattern(char[] data, int idx, char chrToReplace) {
		this.data = data.clone();
		this.data[idx] = chrToReplace;
		this.covereage = -1;
		this.level = getLevel();
	}

	public Pattern(char[] dataContent, int idx, char chrToReplace,
			List<double[]> coveragePercentageOfEachValueInEachAttr,
			double curPercentage) {
		this.data = dataContent.clone();
		this.data[idx] = chrToReplace;
		if (chrToReplace != 'x')
			this.covereagePercentage = curPercentage
					* coveragePercentageOfEachValueInEachAttr.get(idx)[Integer
							.parseInt(chrToReplace + "")];
		else {
			this.covereagePercentage = curPercentage
					/ coveragePercentageOfEachValueInEachAttr.get(idx)[Integer
							.parseInt(dataContent[idx] + "")];
		}
		this.covereage = -1;
		this.level = getLevel();
	}

	/**
	 * Get number of elements in a pattern
	 * @return
	 */
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
	 * Get the root pattern 'xxx...x' with coverage percentage info
	 * 
	 * @param dimension
	 * @return
	 */
	public static Pattern getRootPattern(int dimension,
			List<double[]> coveragePercentageOfEachValueInEachAttr) {
		char[] rootData = new char[dimension];
		for (int i = 0; i < dimension; i++) {
			rootData[i] = 'x';
		}
		return new Pattern(rootData, coveragePercentageOfEachValueInEachAttr);
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
	 * Count occurences of a certain character in an array.
	 * 
	 * @param data
	 * @param chr
	 * @return
	 */
	private int countOccurence(char[] data, char chr) {
		int count = 0;
		for (char chrToCheck : data) {
			if (chr == chrToCheck)
				count++;
		}
		return count;
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
	public Map<Integer, Pattern> genParents(List<double[]> coveragePercentageOfEachValueInEachAttr) {
		Map<Integer, Pattern> replacedPositionToParentPatternMap = new HashMap<Integer, Pattern>();
		for (int i = 0; i < getDimension(); i++) {
			if (data[i] != 'x') {
				replacedPositionToParentPatternMap.put(i,
						new Pattern(data, i, 'x',
								coveragePercentageOfEachValueInEachAttr,
								this.covereagePercentage));
			}
		}

		return replacedPositionToParentPatternMap;
	}
	
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

	public int getLevel() {
		return data.length - countOccurence(this.data, 'x');
	}
	
	/**
	 * Compute the percentage of each value in each attribute
	 * 
	 * @param coveragePercentageOfEachValueInEachAttr
	 * @return
	 */
	private double predictCoveragePercentage(
			List<double[]> coveragePercentageOfEachValueInEachAttr) {
		double percentage = 1;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 'x') {
				percentage *= coveragePercentageOfEachValueInEachAttr
						.get(i)[Integer.parseInt(data[i] + "")];
			}
		}

		return percentage;
	}

	public int compareTo(Pattern other) {
		if (this.covereagePercentage == other.covereagePercentage) {
			return 0;
		} else if (this.covereagePercentage < other.covereagePercentage) {
			return -1;
		} else
			return 1;
	}

	/**
	 * Update the coverage percentage after we checked the dataset
	 * 
	 * @param newPercentage
	 */
	public void updateCoveragePercentage(double newPercentage) {
		this.covereagePercentage = newPercentage;
	}

	public void setCoverage(int coverageValue) {
		this.covereage = coverageValue;
	}
	
	/**
	 * Get data value
	 * @param idx
	 * @return
	 */
	public char getValue(int idx) {
		return this.data[idx];
	}
	
	
	/**
	 * Check of pattern a covers pattern b
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean covers(Pattern a, Pattern b) {
		int len = a.getDimension();
		
		for (int i = 0; i < len; i++)
			if (a.getValue(i) != 'x' && a.getValue(i) != b.getValue(i))
				return false;
		
		return true;
	}
	
	public static boolean covers(char[] a, char[] b) {
		int len = a.length;
		
		for (int i = 0; i < len; i++)
			if (a[i] != 'x' && a[i] != b[i])
				return false;
		
		return true;
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
