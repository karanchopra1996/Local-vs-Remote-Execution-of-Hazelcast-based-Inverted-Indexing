import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

public class InvertedIndexingLocal {
    public InvertedIndexingLocal() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("usage: java InvertedIndexingLocal keyword ");
        } else {
            String keyword = args[0];

            // Create a hashtable to store document frequencies
            Hashtable<String, Integer> documentFrequencyMap = new Hashtable<>();

            // Create a Hazelcast instance
            HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

            // Start the timer
            Date startTime = new Date();

            // Get the map containing the documents
            IMap<String, String> filesMap = hazelcastInstance.getMap("files");

            // Iterate over the documents
            Iterator<String> iterator = filesMap.keySet().iterator();
            while (iterator.hasNext()) {
                String documentId = iterator.next();
                String documentContents = filesMap.get(documentId);
                String[] words = documentContents.split(" ");
                int frequency = 0;

                // Count the frequency of the keyword in the document
                for (String word : words) {
                    if (word.toLowerCase().equals(keyword.toLowerCase())) {
                        frequency++;
                    }
                }

                // Add the document and its frequency to the hashtable
                if (frequency > 0) {
                    documentFrequencyMap.put(documentId, frequency);
                }
            }

            // Stop the timer
            Date endTime = new Date();

            // Print the results
            Iterator<String> keysIterator = documentFrequencyMap.keySet().iterator();
            while (keysIterator.hasNext()) {
                String documentId = keysIterator.next();
                System.out.println("File[" + documentId + "] has " + documentFrequencyMap.get(documentId));
            }

            System.out.println(" ");
            System.out.println("Elapsed time = " + (endTime.getTime() - startTime.getTime()));
        }
    }
}
