package Pattern;

import java.util.Iterator;
import org.apache.commons.collections.iterators.ArrayIterator;

public class Pattern {
    public char[] data; // the content of pattern (a sequence of characters)

    public Pattern(char[] data) {
	this.data = data.clone();
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
     * @param other
     * @return
     */
    public boolean isAncestorOf(Pattern other) {
	int size = getDimension();
	if (size != other.getDimension()) {
	    return false;
	}

	Iterator<Character> thisIt = new ArrayIterator(data);
	Iterator<Character> otherIt = new ArrayIterator(other.data);
	while (thisIt.hasNext()) {
	    char thisChar = thisIt.next();
	    char otherChar = otherIt.next();
	    if (thisChar == 'x' || thisChar == otherChar)
		continue;
	    else
		return false;
	}

	return true;
    }

    /**
     * Get the root pattern 'xxx...x'
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
     * From right to left, find the index on the first deterministic character (i.e., not 'x')
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

    @Override
    public boolean equals(Object obj) {
	boolean flag = false;
	Pattern emp = (Pattern) obj;
	if (emp.data.equals(this.data))
	    flag = true;
	return flag;
    }

    @Override
    public int hashCode() {
	return data.hashCode();
    }

    @Override
    public String toString() {
	String msg = "";
	for (char a : data)
	    msg += a;

	return msg;
    }

}
