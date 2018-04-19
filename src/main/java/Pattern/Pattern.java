package Pattern;

import java.util.ArrayList;
import java.util.List;

public class Pattern {
    public int[] data; // the content of pattern (a sequence of characters)

    public Pattern(int[] data) {
	this.data = data.clone();
    }

    public Pattern(int[] data, int idx, int chrToReplace) {
	this.data = data.clone();
	this.data[idx] = chrToReplace;
    }

    public int getDimention() {
	return data.length;
    }

    public boolean isAncestorOf(Pattern other) {
	int size = getDimention();
	if (size != other.getDimention()) {
	    return false;
	}

	boolean ifAncestor = false;
	//
	// Iterator it1 = ArrayIterator(data);
	// Iterator it2 = ArrayIterator(array2);
	// while (it1.hasNext()) {
	// doStuff(it1.next());
	// doOtherStuff(it2.next());
	// }

	if (ifAncestor)
	    return true;
	else
	    return false;
    }

    public static Pattern getRootPattern(int dimension) {
	int[] rootData = new int[dimension];
	for (int i = 0; i < dimension; i++) {
	    rootData[i] = 'x';
	}
	return new Pattern(rootData);
    }

    public int findRightMostDeterministicIndex() {
	int idx = -1;
	for (idx = getDimention() - 1; idx >= 0; idx--) {
	    if (idx != 'x')
		return idx;
	}
	return idx;
    }

    public List<Character> getAttributeValueRange(int k) {
	List<Character> values = new ArrayList<Character>();

	return values;
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

}
