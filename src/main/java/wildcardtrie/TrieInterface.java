package wildcardtrie;

import java.util.List;

public interface TrieInterface {
	/**
	 * Adds a string to the trie
	 * 
	 * @param str
	 *            string to be added
	 */
	public void add(char[] str);

	/**
	 * Return true if and only if the trie contains the given string
	 * 
	 * @param str
	 *            string that is tested for containment
	 * @return
	 */
	public boolean contains(char[] str);

	/**
	 * Return a list of all the strings in the trie that start with the given
	 * prefix If there are no such strings returns an empty list
	 * 
	 * @param prefix
	 *            the prefix of the strings
	 * @return list of all the strings in the trie that start with the given
	 *         string
	 * 
	 */
	public List<char[]> prefixedWords(char[] prefix);

	/**
	 * Returns all string that match the given pattern where '?' can be any
	 * character 'a'-'z' Example: "bare", "bake", "bale", and "base" all match
	 * ba?e, since ? can be any lower-case English letter
	 * 
	 * @param pattern
	 *            the pattern to match
	 * @return a list of all strings matching the patern
	 */
	public List<char[]> wildcardMatches(char[] pattern);
	
	/**
	 * Check at least one string (pattern) in the trie is dominated by pattern
	 * @param pattern
	 * @return
	 */
	public boolean ifDominatedBy(char[] pattern);
	
	/**
	 * Check at least one string (pattern) in the trie dominates pattern
	 * @param pattern
	 * @return
	 */
	public boolean ifDominates(char[] pattern);

	/**
	 * Removes all words from the trie and resets the size
	 */
	public void clear();

	/**
	 * @return the number of unique words in the trie
	 */
	public int size();

	/**
	 * Removes the given string from the trie
	 * 
	 * @param str
	 *            string to remove
	 */
	public void remove(char[] str);

}
