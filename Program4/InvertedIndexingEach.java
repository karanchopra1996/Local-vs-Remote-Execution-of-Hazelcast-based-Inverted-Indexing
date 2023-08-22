import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.IMap;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.Callable;

public class InvertedIndexingEach implements Callable<String>, HazelcastInstanceAware, Serializable {
    private String keyword;
    private HazelcastInstance hazelcastInstance;

    public InvertedIndexingEach() {
    }

    public InvertedIndexingEach(String keyword) {
        this.keyword = keyword;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public String call() throws Exception {
        System.out.println("Started inverted indexing");

        // Hashtable to store document frequencies
        Hashtable<String, Integer> documentFrequencyMap = new Hashtable<>();

        // Get the map of files
        IMap<String, String> filesMap = hazelcastInstance.getMap("files");

        // Iterate over local keys
        Iterator<String> iterator = filesMap.localKeySet().iterator();

        while (iterator.hasNext()) {
            String documentId = iterator.next();
            String documentContents = filesMap.get(documentId);
            String[] words = documentContents.split(" ");
            int frequency = 0;

            // Count the frequency of the keyword in the document
            for (String word : words) {
                if (word.equalsIgnoreCase(keyword)) {
                    frequency++;
                }
            }
            // If frequency is greater than zero, add document to the documentFrequencyMap
            if (frequency > 0) {
                documentFrequencyMap.put(documentId, frequency);
            }
        }

        String result = "";

        // Build the result string
        for (Enumeration<String> keys = documentFrequencyMap.keys(); keys.hasMoreElements(); ) {
            String documentId = keys.nextElement();
            String frequency = documentFrequencyMap.get(documentId).toString();
            result += documentId + " " + frequency + " ";
        }

        System.out.println(result);
        return result;
    }
}
