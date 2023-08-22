import com.hazelcast.cluster.Member;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.MultiExecutionCallback;
import com.hazelcast.map.IMap;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class InvertedIndexingRemote {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("usage: java InvertedIndexingRemote keyword ");
        } else {
            String keyword = args[0];

            // Create a hashtable to store document frequencies
            final Hashtable<String, Integer> documentFrequencyMap = new Hashtable<>();

            // Create a Hazelcast instance
            HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

            // Start the timer
            final Date startTime = new Date();

            // Get the executor service and map
            IExecutorService executorService = hazelcastInstance.getExecutorService("exec");
            IMap<Member, Object> resultMap = hazelcastInstance.getMap("resultMap");

            // Define the inverted indexing task
            InvertedIndexingEach invertedIndexingEach = new InvertedIndexingEach(keyword);

            // Define the multi-execution callback
            MultiExecutionCallback multiExecutionCallback = new MultiExecutionCallback() {
                public void onResponse(Member member, Object response) {
                }

                public void onComplete(Map<Member, Object> resultMap) {
                    Iterator<Object> iterator = resultMap.values().iterator();

                    while (iterator.hasNext()) {
                        String[] resultArray;

                        do {
                            if (!iterator.hasNext()) {
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
                                return;
                            }
                            Object response = iterator.next();
                            System.out.println(response);
                            resultArray = ((String) response).split(" ");
                        } while (resultArray.length < 2);

                        for (int i = 0; i < resultArray.length; i += 2) {
                            documentFrequencyMap.put(resultArray[i], Integer.parseInt(resultArray[i + 1]));
                        }
                    }
                }
            };

            // Get the members of the cluster
            Set<Member> members = hazelcastInstance.getCluster().getMembers();

            // Submit the task to the members using the executor service and callback
            executorService.submitToMembers(invertedIndexingEach, members, multiExecutionCallback);
        }
    }
}
