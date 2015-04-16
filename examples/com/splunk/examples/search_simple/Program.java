package com.splunk.examples.search_simple;

import com.splunk.Args;
import com.splunk.HttpException;
import com.splunk.Service;
import com.splunk.Command;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

// Note: not all search parameters are exposed to the CLI for this example.
public class Program {

    public static void main(String[] args) {
        String[] query = {"Source URI"};
        try {
            run(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void run(String[] args) throws IOException {
        Command command = Command.splunk("search");
        command.parse(args);
        System.setProperty("https.protocols", "TLSv1");

        if (command.args.length != 1)
            Command.error("Search expression required");
        String query = "search 'Source URI'";

        Service service = Service.connect(command.opts);

        // Check the syntax of the query.
        try {
            Args parseArgs = new Args("parse_only", true);
            service.parse(query, parseArgs);
        }
        catch (HttpException e) {
            String detail = e.getDetail();
            Command.error("query '%s' is invalid: %s", query, detail);
        }

        // This is the simplest form of searching splunk. Note that additional
        // arguments are allowed, but they are not shown in this example.
        InputStream stream = service.oneshotSearch(query);

        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        try {
            OutputStreamWriter writer = new OutputStreamWriter(System.out);
            try {
                int size = 1024;
                char[] buffer = new char[size];
                while (true) {
                    int count = reader.read(buffer);
                    if (count == -1) break;
                    writer.write(buffer, 0, count);
                }
        
                writer.write("\n");
            }
            finally {
                writer.close();
            }
        }
        finally {
            reader.close();
        }
    }
}
