package Pattern;

import java.util.ArrayList;
import java.util.List;

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

    public boolean isAncestorOf(Pattern other) {
	int size = getDimension();
	if (size != other.getDimension()) {
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
	char[] rootData = new char[dimension];
	for (int i = 0; i < dimension; i++) {
	    rootData[i] = 'x';
	}
	return new Pattern(rootData);
    }

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
