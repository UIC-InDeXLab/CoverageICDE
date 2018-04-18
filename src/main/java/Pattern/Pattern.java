package Pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pattern {
	public List<Character> data; // the content of pattern (a sequence of characters)

	public Pattern(List<Character> data) {
		this.data = new ArrayList<Character>(data);
	}
	
	public Pattern(List<Character> data, int idx, Character chrToReplace) {
		this.data = new ArrayList<Character>(data);
		this.data.set(idx, chrToReplace);
	}

	public int getDimention() {
		return data.size();
	}

	public boolean isAncestorOf(Pattern other) {
		int size = getDimention();
		if (size != other.getDimention()) {
			return false;
		}

		boolean ifAncestor = false;
		
		for (Iterator<Character> it1 = data.iterator(), it2 = other.data.iterator(); it1.hasNext() && it2.hasNext();) {
			Character c1 = it1.next();
			Character c2 = it2.next();
			if (c1 != c2) {
				if (c1 != 'x') {
					return false;
				}
				ifAncestor = true;
			}
		}
		
		if (ifAncestor)
			return true;
		else
			return false;
	}
	
	public static Pattern getRootPattern(int dimension) {
		List<Character> rootData = new ArrayList<Character>();
		for (int i = 0; i < dimension; i++) {
			rootData.add('x');
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
