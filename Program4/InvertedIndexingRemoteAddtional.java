import com.hazelcast.cluster.Member;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.MultiExecutionCallback;

import java.util.*;
import java.util.concurrent.Callable;

public class InvertedIndexingRemoteAddtional {
    public static void main(String[] args) throws Exception {
        // Check for keyword argument
        if (args.length != 1) {
            System.out.println("usage: java MultiExecutionExample keyword ");
            return;
        }

        // Retrieve the keyword from the command-line arguments
        String keyword = args[0];

        // Create a Hazelcast instance and retrieve the executor service
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IExecutorService executorService = hazelcastInstance.getExecutorService("exec");

        // Prepare a local map to store the results
        Hashtable<String, Integer> local = new Hashtable<>();

        // Start a timer to measure the elapsed time
        Date startTime = new Date();

        // Create a Callable task for inverted indexing
        Callable<String> invertedIndexingEach = new InvertedIndexingEach(keyword);

        // Create a MultiExecutionCallback to handle the response
        MultiExecutionCallback callback = new MultiExecutionCallback() {
            @Override
            public void onResponse(Member member, Object response) {
                // No action needed
            }

            @Override
            public void onComplete(Map<Member, Object> responses) {
                // Prepare a local map to consolidate the responses
                Hashtable<String, Integer> local = new Hashtable<>();

                // Process each response message
                for (Object response : responses.values()) {
                    String message = (String) response;
                    String[] resultArray = message.split(" ");

                    // Extract document ID and frequency pairs and update the local map
                    for (int i = 0; i < resultArray.length; i += 2) {
                        local.put(resultArray[i], Integer.parseInt(resultArray[i + 1]));
                    }
                }

                // Stop the timer and display the results
                Date endTime = new Date();
                Iterator<String> iterator = local.keySet().iterator();
                while (iterator.hasNext()) {
                    String fileName = iterator.next();
                    int count = local.get(fileName);
                    System.out.println("File[" + fileName + "] has " + count);
                }
                System.out.println("");
                System.out.println("Elapsed time: " + (endTime.getTime() - startTime.getTime()));
            }
        };

        // Retrieve the cluster members and assign them to parent sets based on IP address
        Set<Member> clusterMembers = hazelcastInstance.getCluster().getMembers();
        Set<Member> parent1 = new HashSet<>();
        Set<Member> parent2 = new HashSet<>();
        Set<Member> parent3 = new HashSet<>();

        for (Member member : clusterMembers) {
            String child = member.getAddress().getHost();
            System.out.println("Next in hierarchy is " + child);

            if (child.equals("10.158.82.61") || child.equals("10.158.82.62") || child.equals("10.158.82.63")) {
                parent1.add(member);
            }
            if (child.equals("10.158.82.64") || child.equals("10.158.82.65") || child.equals("10.158.82.62")) {
                parent2.add(member);
            }
            if (child.equals("10.158.82.66") || child.equals("10.158.82.67") || child.equals("10.158.82.63")) {
                parent3.add(member);
            }
        }
        System.out.println(parent1);
        System.out.println(parent2);
        System.out.println(parent3);

        // Submit the Callable task to the parent sets and provide the callback
        executorService.submitToMembers(invertedIndexingEach, parent1, callback);
        executorService.submitToMembers(invertedIndexingEach, parent2, callback);
        executorService.submitToMembers(invertedIndexingEach, parent3, callback);
    }
}
