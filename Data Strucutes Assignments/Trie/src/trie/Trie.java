
package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/		
		TrieNode root = new TrieNode(null,null,null);
		if(allWords.length <= 0) {
			return root;
		}
		
		short end = (short)(allWords[0].length()-1);
		root.firstChild = new TrieNode(new Indexes(0,(short)(0),end),null,null);
		
		TrieNode ptr = root.firstChild;
		TrieNode prev = root.firstChild;
		int wordIndex = 0;
		int startIndex = 0;
		int endIndex = 0;
		int sim = 0;
		
		for(int i = 1; i < allWords.length; i++) {	
			String currWord = allWords[i];		
			while(ptr!=null) {
				wordIndex = ptr.substr.wordIndex;
				startIndex = ptr.substr.startIndex;
				endIndex = ptr.substr.endIndex;

				sim = Similar(allWords[wordIndex].substring(startIndex, endIndex+1), currWord.substring(startIndex));
				
				if(sim == -1) {
					prev = ptr;
					ptr = ptr.sibling;
				} else {
					sim+=startIndex;
					if(sim == endIndex) { 
						prev = ptr;
						ptr = ptr.firstChild;
					}
					else if (sim < endIndex){ 
						prev = ptr;
						Split(sim,prev,i,currWord);
						break;
					}
				}
			}		
			if(ptr == null) {
				TrieNode sib = new TrieNode(new Indexes(i,(short)(0),(short)(currWord.length()-1)),null,null);
				prev.sibling = sib;
			} 
			ptr = root.firstChild;
			prev = root.firstChild;
			sim = -1;
			startIndex = -1;
			endIndex = -1;
			wordIndex = -1;
		}
		
		return root;
	}
	private static int Similar(String treeWord, String newWord) {
		int count = 0;
		int i = 0;

		while(count < treeWord.length() && count < newWord.length() && treeWord.charAt(i) == newWord.charAt(i)) { 
			count++;
			i++;
		}
		count--;
		return count;
		
	}
	private static void Split(int sim, TrieNode prev, int i, String currWord){
		Indexes currIndex = prev.substr; 
		TrieNode currFirstChild = prev.firstChild; 

		Indexes currWordNewIndex = new Indexes(currIndex.wordIndex, (short)(sim+1), currIndex.endIndex);
		currIndex.endIndex = (short)sim; 
		prev.firstChild = new TrieNode(currWordNewIndex, null, null);
		prev.firstChild.firstChild = currFirstChild;
		prev.firstChild.sibling = new TrieNode(new Indexes((short)i, (short)(sim+1), (short)(currWord.length()-1)), null, null);
		
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/
		if(root == null) 
			return null;
		
		ArrayList<TrieNode> list = new ArrayList<TrieNode>();
		TrieNode ptr = root;
		
		int count = 0;
		while(ptr != null) {
			if(ptr.substr == null) 
				ptr = ptr.firstChild;
			
			String word = allWords[ptr.substr.wordIndex];
			String str = word.substring(0,ptr.substr.endIndex+1);
			
			if(word.startsWith(prefix) || prefix.startsWith(str)) {
				if(ptr.firstChild != null) {
					list.addAll(completionList(ptr.firstChild, allWords, prefix));
					ptr = ptr.sibling;
					count++;
				} else { 
					list.add(ptr);
					ptr = ptr.sibling;
					count++;
				}
			} else {
				ptr = ptr.sibling;
			}
			
		}
		if(count > 0) {			
			return list;
		}else 
			return null;
	}
	
	private static boolean isPrefix(String word, String prefix) {
		if(word.substring(0,prefix.length()).equals(prefix)) 
			return true;
		return false;
		
	}
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }








