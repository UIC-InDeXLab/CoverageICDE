package dataCollectionNew;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pattern.Pattern;

public class PatternValueNode implements Comparable<PatternValueNode> {
	char[] data;
	BitSet matchingPatterns;
	int numCoveredPatterns;

	static int maxDimensions;

	public PatternValueNode(char c, BitSet newMatchingPatterns) {
		this.data = new char[]{c};

		this.matchingPatterns = (BitSet) newMatchingPatterns.clone();
		this.numCoveredPatterns = this.matchingPatterns.cardinality();
	}

	public PatternValueNode(PatternValueNode curPattern, char c,
			BitSet newMatchingPatterns) {
		this.data = new char[curPattern.getDimension() + 1];
		System.arraycopy(curPattern.data, 0, this.data, 0,
				curPattern.getDimension());
		this.data[this.data.length - 1] = c;

		this.matchingPatterns = (BitSet) curPattern.matchingPatterns.clone();
		this.matchingPatterns.and(newMatchingPatterns);

		this.numCoveredPatterns = this.matchingPatterns.cardinality();
	}
	
	public PatternValueNode(char[] patternData) {
		this.data = patternData;
	}

	public int getDimension() {
		return data.length;
	}

	public boolean ifComplete() {
		return getDimension() >= maxDimensions;
	}

	public List<PatternValueNode> createChildren(
			char[] characterArray, BitSet[] coverageArray) {
		if (!ifComplete()) {
			List<PatternValueNode> childrenList = new LinkedList<PatternValueNode>();
			int len = characterArray.length;
			
			for (int i = 0; i < len; i++) {
				PatternValueNode newNode = new PatternValueNode(this,
						characterArray[i], coverageArray[i]);
				if (newNode.numCoveredPatterns > 0)
					childrenList.add(newNode);
			}
			return childrenList;
		} else
			return null;
	}

//	public int compareTo(PatternValueNode other) {
//		if (Math.pow(this.numCoveredPatterns, this.getDimension()) == Math
//				.pow(other.numCoveredPatterns, other.getDimension())) {
//			return 0;
//		} else if (Math.pow(this.numCoveredPatterns, this.getDimension()) > Math
//				.pow(other.numCoveredPatterns, other.getDimension())) {
//			return -1;
//		} else
//			return 1;
//	}

	 public int compareTo(PatternValueNode other) {
	 if (this.numCoveredPatterns == other.numCoveredPatterns) {
	 return 0;
	 } else if (this.numCoveredPatterns > other.numCoveredPatterns ) {
	 return -1;
	 } else
	 return 1;
	 }

	public char[] getData() {
		return this.data;
	}

	public void updatePatternsToIgnore(BitSet patternsToIgnore) {
		this.matchingPatterns.and(patternsToIgnore);
		this.numCoveredPatterns = this.matchingPatterns.cardinality();
	}

	@Override
	public String toString() {
		String msg = "";
		for (char a : data)
			msg += a;

		return msg;
	}
}
