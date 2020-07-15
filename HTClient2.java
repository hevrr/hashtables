import edu.princeton.cs.algs4.StdOut;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class HTClient2 {

    /* location of files */
    private static final String DICTIONARY_LOCATION = "src/1000WordDictionary.txt";
    private static final String PRIMES_LOCATION = "src/MultiplierPrimes.txt";

    /* words, prime numbers (for testing sizes), and hashtable */
    private static LinkedList<String> words = new LinkedList<>();
    private static LinkedList<Integer> primes = new LinkedList<>();
    private static HashTable<String, String> hashTable;

    private static final int SIZE = 1499;

    /* runner */
    public static void main(String[] args) {
        System.out.println("- Relates hashtable function to collisions and memory");
        System.out.println("- My hash function: for each character of String, set hash = (hash * multiplier + ASCII) % tableSize");
        System.out.println("- For this test, I modify the multiplier from a list of prime numbers");
        loadDictionary();
        loadPrimes();
        testSizing();
    }

    /* testing for different prime number sizing */
    private static void testSizing() {
        /* formatting */
        StdOut.printf("%-20s%-20s%-20s%-20s", "Multiplier used", "Collisions (put)", "Collisions (hit)", "Collisions (miss)");
        StdOut.println();

        /* goes through all the prime numbers and tests various parameters */
        for (int i = 0; i < primes.size(); i++) {
            /* gets the multiplier for hashtable */
            int multiplier = primes.get(i);

            hashTable = new HashTable<>(SIZE);
            hashTable.setResizable(false);
            hashTable.setMultiplier(multiplier);

            /* prints out data */
            StdOut.printf("%-20s%-20s%-20s%-20s", multiplier, put(), hitProbe(), missProbe());
            StdOut.println();

            /* for testing */
//            put();
//            StdOut.println(missProbe());
        }
    }

    /* returns average probes for put */
    private static double put() {
        hashTable.probeCounter = 1;
        for (String s : words)
            hashTable.put(s, s);
        return (double) hashTable.probeCounter / words.size();
    }

    /* returns average probes for hit */
    private static double hitProbe() {
        hashTable.probeCounter = 0;
        for (String s : words)
            hashTable.get(s);
        return (double) hashTable.probeCounter / words.size();
    }

    /* returns average probes for miss */
    private static double missProbe() {
        hashTable.probeCounter = 0;
        for (String s : words)
            hashTable.get(s + " ");
        return (double) hashTable.probeCounter / words.size();
    }

    /* loads dictionary */
    private static void loadDictionary() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DICTIONARY_LOCATION));
            String line = reader.readLine();
            while (line != null) {
                words.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    /* loads primeNumbers to test for sizing from 1000 to 10000 */
    private static void loadPrimes() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PRIMES_LOCATION));
            String line = reader.readLine();
            while (line != null) {
                primes.add(Integer.valueOf(line));
                line = reader.readLine();
            }
            reader.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
