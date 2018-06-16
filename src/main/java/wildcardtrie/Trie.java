package wildcardtrie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;

public class Trie implements TrieInterface {
	/**
	 * starting node of the trie
	 */
	private final Node root;
	/**
	 * the number of unique words in the trie
	 */
	private int size;
	/**
	 * The wild card or "don't care' character
	 */
	public static final char WILDCARD = 'x';

	/**
	 * Constructs a new empty trie
	 */
	public Trie() {
		root = new Node();
		size = 0;
	}

	/* construct the trie from a Collection of Strings */
	public Trie(Collection<char[]> col) {
		this();
		for (char[] s : col) {
			this.add(s);
		}
	}

	/* construct the trie from an array of Strings */
	public Trie(char[][] arr) {
		this();
		for (char[] s : arr) {
			this.add(s);
		}
	}

	/* adds a word to the trie */
	@Override
	public void add(char[] str) {
		Node curr = root;
		for (int i = 0; i < str.length; i++) {
			if (curr.children.get(str[i]) == null) {
				curr.children.put(str[i], new Node());
			}
			curr = curr.children.get(str[i]);
		}
		if (curr.isEnd == false) {
			size++;
		}
		curr.isEnd = true;
	}

	/*
	 * crawls to the ending node of a word and returns it or returns null if the
	 * word is not found in the trie
	 */
	private Node getNode(char[] str) {
		Node node = root;
		for (int i = 0; i < str.length; i++) {
			Node child = node.children.get(str[i]);
			if (child == null) {
				return null;
			}
			node = child;
		}
		return node;
	}

	/* returns true if there are any words in the trie with this prefix */
	public boolean hasPrefix(char[] str) {
		return getNode(str) != null;
	}

	/* returns a list of all words matching the string with wildcards */
	@Override
	public List<char[]> wildcardMatches(char[] str) {
		List<char[]> wildcardMatches = new ArrayList<>();
		wildcardTraverse(str, new StringBuilder(), root, 0, wildcardMatches);
		return wildcardMatches;
	}

	/*
	 * traverses the trie and adds all words matching the string with wildcards
	 * * to list
	 */
	private void wildcardTraverse(char[] pattern, StringBuilder prefix, Node root, int len,
			List<char[]> wildcardMatches) {
		if (root == null) {
			return;
		}
		if (len == pattern.length) {
			if (root.isEnd) {
				wildcardMatches.add(prefix.toString().toCharArray());
			}
			return;
		}
		if (pattern[len] == WILDCARD) {
			for (Entry<Character, Node> e : root.children.entrySet()) {
				prefix.append(e.getKey());
				wildcardTraverse(pattern, prefix, e.getValue(), len + 1, wildcardMatches);
				prefix.deleteCharAt(prefix.length() - 1);
			}
		} else {
			prefix.append(pattern[len]);
			wildcardTraverse(pattern, prefix, root.children.get(pattern[len]), len + 1,
					wildcardMatches);
			prefix.deleteCharAt(prefix.length() - 1);
		}
	}
	
	/*
	 * Check if a string pattern can dominate any string (patterns) in the trie.
	 * 
	 */
	private boolean ifWildcardDominatedBy(char[] pattern, Node root, int len) {
//		if (root == null) {
//			return false;
//		}
//		if (len == pattern.length) {
//			if (root.isEnd) {
//				return true;
//			}		
//		}
//		if (pattern[len] == WILDCARD) {
//			for (Entry<Character, Node> e : root.children.entrySet()) {
//				boolean result = ifWildcardDominatedBy(pattern, e.getValue(), len + 1);
//				if (result)
//					return true;
//			}
//		} else {
//			boolean result = ifWildcardDominatedBy(pattern, root.children.get(pattern[len]), len + 1);
//			if (result)
//				return true;
//		}
		Stack<Node> nodeStack = new Stack<Node>();
		Stack<Integer> lenStack = new Stack<Integer>();

		nodeStack.push(root);
		lenStack.push(len);
		
		while(!nodeStack.isEmpty()) {
			Node curNode = nodeStack.pop();
			int curLen = lenStack.pop();
			
			if (curNode == null) {
				continue;
			}
			if (curLen == pattern.length) {
				if (curNode.isEnd) {
					return true;
				}
			}
			if (pattern[curLen] == WILDCARD) {
				for (Entry<Character, Node> e : curNode.children.entrySet()) {
					if (e.getValue() != null) {
						nodeStack.push(e.getValue());
						lenStack.push(curLen + 1);
					}
				}

			} else {
				Node newNode = curNode.children.get(pattern[curLen]);
				if (newNode != null) {
					nodeStack.push(newNode);
					lenStack.push(curLen + 1);
				}

			}
		}
		
		return false;
	}
	
	@Override
	public boolean ifDominatedBy(char[] str) {
		return ifWildcardDominatedBy(str, root, 0);
	}
	
	/*
	 * Check if a string pattern is dominated by any string (patterns) in the trie.
	 * * to list
	 */
	private boolean ifWildcardDominates(char[] pattern, Node root, int len) {
//		if (root == null) {
//			return false;
//		}
//		if (len == pattern.length) {
//			if (root.isEnd) {
//				return true;
//			}		
//		}
//		if (pattern[len] == WILDCARD) {
//			boolean result = ifWildcardDominates(pattern, root.children.get(pattern[len]), len + 1);
//			if (result)
//				return true;
//		} else {
//			// Check 
//			char[] charToCheckArray = new char[]{'x', pattern[len]};
//			for (char charToCheck : charToCheckArray) {
//				boolean result = ifWildcardDominates(pattern, root.children.get(charToCheck), len + 1);
//				if (result)
//					return true;
//			}
//
//		}
//		
//		return false;	
		Stack<Node> nodeStack = new Stack<Node>();
		Stack<Integer> lenStack = new Stack<Integer>();

		nodeStack.push(root);
		lenStack.push(len);
		
		while(!nodeStack.isEmpty()) {
			Node curNode = nodeStack.pop();
			int curLen = lenStack.pop();
			
			if (curNode == null) {
				continue;
			}
			if (curLen == pattern.length) {
				if (curNode.isEnd) {
					return true;
				}
			}
			if (pattern[curLen] == WILDCARD) {
				Node newNode = curNode.children.get(pattern[curLen]);
				if (newNode !=null) {
					nodeStack.push(curNode.children.get(pattern[curLen]));
					lenStack.push(curLen + 1);
				}

			} else {
				// Check
				char[] charToCheckArray = new char[]{'x', pattern[curLen]};
				for (char charToCheck : charToCheckArray) {
					Node newNode = curNode.children.get(charToCheck);
					if (newNode != null) {
						nodeStack.push(curNode.children.get(charToCheck));
						lenStack.push(curLen + 1);
					}
				}

			}
		}
		return false;
	}
	
	
	
	@Override
	public boolean ifDominates(char[] str) {
		return ifWildcardDominates(str, root, 0);
	}

	/* returns whether the trie contains a given word */
	@Override
	public boolean contains(char[] str) {
		Node node = getNode(str);
		return node != null && node.isEnd;
	}

	/* returns all words in the trie that start with this prefix */
	@Override
	public List<char[]> prefixedWords(char[] str) {
		Node curr = getNode(str);
		List<char[]> prefixedWords = new ArrayList<>();
		DFS(curr, str, prefixedWords);
		return prefixedWords;
	}

	/* traverses the trie depth first and adds all words to list */
	private static void DFS(Node root, char[] prefix, List<char[]> list) {
		if (root == null) {
			return;
		}
		if (root.isEnd) {
			list.add(prefix);
		}
		for (Entry<Character, Node> e : root.children.entrySet()) {
			char[] merge = Arrays.copyOf(prefix, prefix.length + 1);
			merge[merge.length - 1] = e.getKey();
			DFS(e.getValue(), merge, list);
		}
	}


	/* a utility method to remove a word from the trie. */
	private boolean removeUtil(Node node, char[] str, int level) {
		if (node != null) {
			if (level == str.length) {
				if (node.isEnd) {
					node.isEnd = false;
					if (node.children.isEmpty()) {
						return true;
					}
					return false;
				}
			} else {
				if (removeUtil(node.children.get(str[level]), str, level + 1)) {
					node.children.remove(str[level]);
					return (!node.isEnd && node.children.isEmpty());
				}
			}
		}
		return false;
	}

	/* remove all elements from the trie */
	@Override
	public void clear() {
		root.children.clear();
		size = 0;
	}

	/* remove an element from the trie */
	@Override
	public void remove(char[] str) {
		if (this.contains(str)) {
			removeUtil(root, str, 0);
			size--;
		}
	}

	@Override
	public int size() {
		return size;
	}
	
//	private static Set<String> getAll(Trie trie) {
//		return new HashSet<>(trie.prefixedWords(''));
//	}
	
	public static void main(String[] argv) {
//		Trie trie = new Trie();
//		int d = 15;
//		int n = 40000;
//		String sigma = "01";
//		String sigmaWildcard = "01x";
//		
//		char[][] patternList = new char[n][d];
//		
//		for (int i = 0; i < n; i++) {
//			StringBuilder sb = new StringBuilder();
//			Random rnd = new Random();
//			while (sb.length() < d) { // length of the random string.
//	            int index = (int) (rnd.nextFloat() * sigma.length());
//	            sb.append(sigma.charAt(index));
//	        }
//			patternList[i] = sb.toString().toCharArray();
//		}
//		
//		for (int i = 0; i < n; i++) {
//			trie.add(patternList[i]);
//		}
//		
//		for (int i = 0; i < 1000000; i++) {
//			StringBuilder sb = new StringBuilder();
//			Random rnd = new Random();
//			while (sb.length() < d) { // length of the random string.
//	            int index = (int) (rnd.nextFloat() * sigma.length());
//	            sb.append(sigma.charAt(index));
//	        }
//			trie.ifDominates(sb.toString().toCharArray());
//		}
//		
		
		Trie trie = new Trie();
		trie.add("01x0".toCharArray());
		trie.add("x100".toCharArray());
		trie.add("1x01".toCharArray());
		trie.add("10x0".toCharArray());
//		System.out.println(trie.wildcardIsCoveredBy("xx11"));
		System.out.println(trie.ifDominates("01xx".toCharArray()));
	}

}