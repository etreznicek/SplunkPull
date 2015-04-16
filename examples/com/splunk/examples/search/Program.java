package com.splunk.examples.search;

import com.splunk.*;
import com.splunk.examples.explorer.StringArrayPropertyEditor;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

// Note: not all search parameters are exposed to the CLI for this example.
public class Program {



    public static void main(String[] args) throws IOException {
//Connect to Splunk
        System.setProperty("https.protocols", "TLSv1");
        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setUsername("eric.reznicek");
        loginArgs.setPassword("Nebraska14*");
        loginArgs.setHost("splunk-searchcluster-1.app.hudl.com");
        loginArgs.setPort(8089);
        com.splunk.Service service = com.splunk.Service.connect(loginArgs);
//Search args
        Args queryArgs = new Args();
        queryArgs.put("earliest_time", "-7m");
        queryArgs.put("latest_time",   "");
        String sourceUriQuery = "search \"source Uri\" \"[ERROR]\" NOT \"[PageError]\" | dedup username | fields username | table username";
        String diskSpaceQuery = "search \"not enough space on the disk\"\"[ERROR]\" | dedup username | fields username | table username";

// The search results are returned directly
        InputStream stream1 = service.oneshotSearch(sourceUriQuery, queryArgs);
        InputStream stream2 = service.oneshotSearch(diskSpaceQuery, queryArgs);

        FileWriter fw1 = new FileWriter("C:\\Users\\eric.reznicek\\Documents\\Support_Projects\\PROJECT_CRAI\\NewSourceURIUsers.csv");
        FileWriter fw2 = new FileWriter("C:\\Users\\eric.reznicek\\Documents\\Support_Projects\\PROJECT_CRAI\\NewDiskSpaceUsers.csv");

// Get the search results and use the built-in XML parser to display them
        try {
            ResultsReaderXml resultsReader = new ResultsReaderXml(stream1);
            HashMap<String, String> event;

            String recentUserEmailSourceUri = "";

            System.out.println("Beginning Source URI Search and Print...");
            System.out.println();
            System.out.println("Users Found:");
            System.out.println();

            while ((event = resultsReader.getNextEvent()) != null) {
                for (String key: event.keySet()) {
                    recentUserEmailSourceUri = event.get(key);

                    fw1.write(recentUserEmailSourceUri + "," + "\n");
                    System.out.println(recentUserEmailSourceUri);
                }
            }
            resultsReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fw1.flush();
        fw1.close();

        System.out.println("Done with Source URI file writing.");

        // Disk Space Search & Write
        try {
            ResultsReaderXml resultsReader = new ResultsReaderXml(stream2);
            HashMap<String, String> event;

            String recentUserEmailDiskSpace = "";

            System.out.println();
            System.out.println();
            System.out.println("Beginning Disk Space Search and Print...");
            System.out.println();
            System.out.println("Users Found:");
            System.out.println();

            while ((event = resultsReader.getNextEvent()) != null) {
                for (String key: event.keySet()) {
                    recentUserEmailDiskSpace = event.get(key);

                    fw2.write(recentUserEmailDiskSpace + "," + "\n");
                    System.out.println(recentUserEmailDiskSpace);
                }
            }
            resultsReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fw2.flush();
        fw2.close();

        System.out.println("Done with Disk Space file writing.");
    }
}