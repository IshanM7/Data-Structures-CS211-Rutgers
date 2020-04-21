package lse;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		BufferedReader txt = new BufferedReader(new FileReader(docFile));
		HashMap<String,Occurrence> keywordList = new HashMap<String,Occurrence>();
		try {
			String line = "";
			int count = 0;
			while((line = txt.readLine()) != null) {
				//System.out.println(line+"\n");
				
				StringTokenizer tokenizer = new StringTokenizer(line, " ");
				while(tokenizer.hasMoreTokens()) {
					String x = tokenizer.nextToken();
					//System.out.println("Token:\t\t"+ x + "\t\tLength:\t"+x.length());
					
					String word = getKeyword(x);
					if(word != null) {
						if(keywordList.containsKey(word)) {
							Occurrence old = keywordList.get(word);
							old.frequency++;
							keywordList.replace(word,old); 
							//System.out.println("SAME KEYWORD: "+keywordList.get(word));
						} else {						
							keywordList.put(word,new Occurrence(docFile,1));
						}
					}									
				}
				count++;
			}
				
				
				
				
		System.out.println("\nHashMap:\n"+keywordList);
			
		} catch (IOException e) {
			throw new NoSuchElementException();
		}
   
				
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return keywordList;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		
		HashMap<String, Occurrence> kw = kws;
		for(Entry<String, Occurrence> word : kw.entrySet()) {
			String keyword = word.getKey();
			Occurrence occ = word.getValue();
			
			ArrayList<Occurrence> OccurrenceList = keywordsIndex.get(keyword);
			
			if(OccurrenceList == null) {
				OccurrenceList = new ArrayList<Occurrence>();
				OccurrenceList.add(occ);
				keywordsIndex.put(keyword, OccurrenceList);
			} else {
				OccurrenceList.add(occ);
				insertLastOccurrence(OccurrenceList);
			}
		}
		
		//System.out.println("keywordsList: "+keywordsIndex);
		
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		word = word.toLowerCase();
		int len = word.length();
		
		if(word.length() == 1) {
			char x = word.charAt(0);
			if((isPunc(x))) {
				return null;
			}
		}
		String punc = "";
		for(int i = word.length()-1; i >= 0; i--) {
			if(isPunc(word.charAt(i))) {
				punc+=word.charAt(i);
				word = word.substring(0,word.length()-1);
			} else {
				break;
			}
		}
		
		for(int i = 0; i < word.length(); i++) {
			if(!(Character.isLetter(word.charAt(i)))) {
				return null;
			}
		}
		if(noiseWords.contains(word)) {
			return null;
		}
		
		return word;
	    	
    	
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
	}
	
	private boolean isPunc(char t){
		if(t == '.' || t == ',' || t == '?' || t == ':' || t == ';' || t == '!') {
			return true;
		}
		else 
			return false;
	}
	
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> indexes = new ArrayList<>();
        //
        int frequency = occs.get(occs.size()-1).frequency;
        //
        int left = 0;
        int right = occs.size() - 2;
        //
        int midpoint;
        int insertIndex;
        while (true) {                                              
            midpoint = (right + left) / 2;
            indexes.add(midpoint);
            //
            Occurrence occ = occs.get(midpoint);
            //
            if (occ.frequency == frequency) {       
                insertIndex = midpoint;
                break;
            }
            else if (occ.frequency < frequency) {   
                right = midpoint - 1;               
                if (left > right) {
                    insertIndex = midpoint;         
                    break;
                }
            }
            else {                                  
                left = midpoint + 1;                
                if (left > right) {
                    insertIndex = midpoint + 1;     
                    break;
                }
            }
        }
		
        if (insertIndex != occs.size() - 1) {
            Occurrence temp = occs.get(occs.size()-1);              
            occs.remove(occs.size()-1);                             
            occs.add(insertIndex, temp);                          
        }
        //System.out.println(indexes);
        return indexes;

	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String>list=new ArrayList<String>();
		ArrayList<Occurrence>occ1=new ArrayList<Occurrence>();
		ArrayList<Occurrence>occ2=new ArrayList<Occurrence>();
		ArrayList<Occurrence>comb=new ArrayList<Occurrence>();
		if(keywordsIndex.containsKey(kw1))
			occ1=keywordsIndex.get(kw1);
		if(keywordsIndex.containsKey(kw2))
			occ2=keywordsIndex.get(kw2);
		comb.addAll(occ1);
		comb.addAll(occ2);
		if(!(occ1.isEmpty()) && !(occ2.isEmpty())) 
		{
			for(int i=0;i<comb.size()-1;i++)
			{
				for(int j=i+1;j<comb.size();j++)
				{
					if(comb.get(i).frequency<comb.get(j).frequency)
					{
						Occurrence temp = comb.get(i);
						comb.set(i,comb.get(j));
						comb.set(j,temp);
					}
				}
			}
			for(int i=0;i<comb.size()-1;i++)
			{
				for(int j=i+1;j<comb.size();j++)
				{
					if(comb.get(i).document==comb.get(j).document) 
						comb.remove(j);
				}
			}
		}
		while(comb.size()>5)
			comb.remove(comb.size()-1);
		//System.out.println(comb);
		for(Occurrence occur:comb)
			list.add(occur.document);
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return list;
	
	}
	
	
	/*public static void main(String[] args) throws FileNotFoundException {
		String docsFile = "docs.txt";
        String noiseWords = "noisewords.txt";

        LittleSearchEngine searchEngine = new LittleSearchEngine();

        //System.out.println(searchEngine.keywordsIndex);
        searchEngine.makeIndex(docsFile, noiseWords);

        String kw1 = "deep";
        String kw2 = "world";

        System.out.println("List: "+searchEngine.top5search(kw1, kw2));
		
		
	}*/
}
