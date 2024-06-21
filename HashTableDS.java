import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HashTableDS implements SequenceInterface {

	private static final int INITIAL_CAPACITY = 5;
	private static final int MAX_LOAD_FACTOR = 1;
	private Node[] HT; // separate-chaining hash table
	private int size; // number of entries in the hash table
	private int capacity; // number of hash table buckets

	/** The constructur just initializes all of the field variables to their initial values.
	 */
	public HashTableDS() {
		capacity = INITIAL_CAPACITY;
		size = 0;
		HT = new Node[capacity];
	}

	/** The constructor initializes all of the field variable to their initial values and for
	 * the capacity, it ensures that the capacity is a prime number for better distribution.
	 * @param capacity given capacity or number of hash table buckets
	 */
	public HashTableDS(int capacity) {
		if(isPrime(capacity))
			this.capacity = capacity;
		else
			this.capacity = getNextPrime(capacity);
		size = 0;
		HT = new Node[this.capacity];
	}

	/** Opens and reads the given file and then goes through each word of the file and puts it in 
	 * the sequence in addition to the all the possible pairs of the words which are also put into 
	 * the sequence of the hash table. Through keeping a variable for the previous word, if that word
	 * is not null, then the pair is put into the sequence and each time the previous word is updated
	 * to be the current word.
	 * @param fileName the name of the file to read from
	 */
	@Override
	public boolean load(String fileName) {
		boolean result = false;

		Scanner fileScanner = null;

		try { 
			fileScanner = new Scanner(new File(fileName));
			result = true;

			String prevWord = null;
			while(fileScanner.hasNext()){
				String curr = fileScanner.next();
				put(curr);

				if(prevWord!=null){
					put(prevWord, curr);
				}
				prevWord = curr;
			}

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + e);
		}

		return result;
	}

	/** Based on the parameters, either a word or a pair of words is put into the sequence. A hash key is generated based on the 
	 * word and then that key is used to either create a new node in that hash table index for the word or add to the frequency 
	 * of that word.
	 * @param first the first word to be put into the sequence 
	 * @param second the second word to be put into the sequence and if its not null, the pair that is put into the sequence
	 */
	public void put(String first, String second) {
		String word = first;

		if(size/capacity==MAX_LOAD_FACTOR)
			resize();

		if(second!=null)
			word += second;

		// The hash key is found based on the word and then a temp node is created to point to the correct linked list in the table.
		int val = hash(word);
		Node current = HT[val];

		// If there is no linked list in that index, a head is created for that specific hash key in the table.
		if(HT[val]==null){
			HT[val] = new Node(first, second);
			HT[val].frequency++;
			size++;
		}
		else{ 
			/** Goes through entire linked list to check if the word or pair of words exist and if they do, the frequency is incremented but
			 * otherwise a tail node is created for that word or pair of words.  */  
			while(current!=null){
				if(current.first.equals(first) && 
					((second==null && current.second==null) || (second!=null && current.second!=null && current.second.equals(second)))){
					current.frequency++;
					break;
				}
				else if(current.next==null){
					current.next = new Node(first, second); 
					current.next.frequency++;
					size++;
					break;
				}	
				current = current.next;
			}
		}
	}

	/** The other put() method is called where the second parameter is a null since only a word needs to be put into the sequence.
	 * @param first the word given to be put into the sequence of the hash table
	 */
	public void put(String first) {
		put(first, null);
	}

	/** The method resizes the hash table to the next prime capacity after doubling the capacity and makes sure to rehash all of the 
	 * words into a temporary hash table which is then copied back into the original hash table for an updated table.
	 */
	private void resize() {
		HashTableDS temp = new HashTableDS(getNextPrime(2 * capacity));
		for (int i = 0; i < capacity; i++) {
			Node current = HT[i];
			while (current != null) {
				/** Each word or pair in the nodes of the table are rehashed to be put into the temp hash table. When being put 
				 * into the sequence each word or pair is rehashed based on the frequency that is stored in the node.
				 */
				for(int j=0; j<current.frequency; j++)
					temp.put(current.first, current.second);
				current = current.next;
			}
		}
		HT = temp.HT;
		size = temp.size;
		capacity = temp.capacity;
	}

	/**
	 * Horner's method
	 * 
	 * @param item the String to be hashed
	 * @return the hash value of item
	 */
	private int hash(String item) {
		int hash = 0;
		for (int i = 0; i < item.length(); i++) {
			hash = 256 * hash + item.charAt(i);
			hash = hash % capacity;
		}
		return hash % capacity;
	}

	/** The other getFrequencyOf() is called where the second parameter is a null since we want the find the frequency of a single word.
	 * @param item the word that the frequency of needs to be found
	 * @return int the frequency of the word
	 */
	@Override
	public int getFrequencyOf(String item) {
		return getFrequencyOf(item, null);
	}

	/** The frequency of a single word or a pair of words is found in the sequence through going to that specific hash key for the word
	 * and then going through the linked list to find the correct node which has the frequency.
	 * @param first the first word of the pair
	 * @param second the second word of the pair and can be null based on whether or not the user is looking for a pair or a singular word
	 * @return the frequency of the pair of words
	 */
	@Override
	public int getFrequencyOf(String first, String second) { 
		String word = first;

		if(second!=null)
			word += second;

		// The hash key is found based on the word and then a temp node is created to point to the correct linked list in the table.
		int key = hash(word);
		Node current = HT[key];
		int freq = 0;

		/** Goes through the linked list for the correct hash key in the hash table to find the correct node which has the frequency
		 * that is needed to be returned.
		 */
		while (current != null) {
			if(current.first.equals(first) && 
				((second==null && current.second==null) || (second!=null && current.second!=null && current.second.equals(second)))){
				freq = current.frequency;
				break;
			}
			current = current.next;
		}

		return freq;
	}

	/**
	 * Helper method to display the contents of the hash table. This is useful
	 * for debugging.
	 */
	private void showTable() {
		for (int i = 0; i < capacity; i++) {
			System.out.print(i + ": ");
			Node current = HT[i];
			while (current != null) {
				System.out.print("(" + current.first + ", " + current.second + ", " + current.frequency + ") ");
				current = current.next;
			}
			System.out.println();
		}
	}

	// Returns a prime integer that is >= the given integer.
	private int getNextPrime(int integer) {
		// if even, add 1 to make odd
		if (integer % 2 == 0) {
			integer++;
		} // end if

		// test odd integers
		while (!isPrime(integer)) {
			integer = integer + 2;
		} // end while

		return integer;
	} // end getNextPrime

	// Returns true if the given intege is prime.
	private boolean isPrime(int integer) {
		boolean result;
		boolean done = false;

		// 1 and even numbers are not prime
		if ((integer == 1) || (integer % 2 == 0)) {
			result = false;
		}

		// 2 and 3 are prime
		else if ((integer == 2) || (integer == 3)) {
			result = true;
		}

		else // integer is odd and >= 5
		{
			assert (integer % 2 != 0) && (integer >= 5);

			// a prime is odd and not divisible by every odd integer up to its square root
			result = true; // assume prime
			for (int divisor = 3; !done && (divisor * divisor <= integer); divisor = divisor + 2) {
				if (integer % divisor == 0) {
					result = false; // divisible; not prime
					done = true;
				} // end if
			} // end for
		} // end if

		return result;
	} // end isPrime

	private static class Node {
		private String first;
		private String second;
		private int frequency;
		private Node next;

		private Node(String first, String second) {
			this.first = first;
			this.second = second;
			frequency = 0;
			next = null;
		}
	}

}
