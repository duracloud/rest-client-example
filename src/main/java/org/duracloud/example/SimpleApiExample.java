package org.duracloud.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * Performs a few simple actions using the DuraCloud REST API client
 *
 * @author: Bill Branan
 * Date: April 03, 2015
 */
public class SimpleApiExample {

    private static final String DEFAULT_PORT = "443";
    private static final String DEFAULT_CONTEXT = "durastore";

    private String host;
    private String username;
    private String password;
    private String spaceId;
    private String contentPath;

    private static Options cmdOptions;

    public SimpleApiExample (String host,
                             String username,
                             String password,
                             String spaceId,
                             String contentPath) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.spaceId = spaceId;
        this.contentPath = contentPath;
    }

    public void run() throws ContentStoreException, IOException {
        System.out.println("Running Simple API Example with parameters:" +
                           "\nhost=" + host +
                           "\nuser name=" + username +
                           "\nspace name=" + spaceId +
                           "\ncontent path=" + contentPath);

        System.out.println("Setting up tool...");
        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(host, DEFAULT_PORT, DEFAULT_CONTEXT);
        Credential credential = new Credential(username, password);
        storeManager.login(credential);
        
        ContentStore store = storeManager.getPrimaryContentStore();
        
        // Print the list of spaces
        System.out.println("\n\n*** Spaces Listing ***\n\n");        
        List<String> spaces = store.getSpaces();
        for(String spaceId : spaces) {
            System.out.println("Space Name: " + spaceId);
            Map<String, String> spaceProperties =
                store.getSpaceProperties(spaceId);
            for(String propertiesKey : spaceProperties.keySet()) {
                System.out.println(propertiesKey + ": " +
                                   spaceProperties.get(propertiesKey));
            }
            System.out.println("---");
        }
        
        // Print space listing before adding content
        System.out.println("\n\n*** Content Listing of " + spaceId +
                           " - before file is added ***\n\n");
        printContentListing(store, spaceId);

        // Store file
        System.out.println("\n\n*** Storing file at path " + contentPath + " ***\n\n");
        File contentFile = new File(contentPath);
        if(!contentFile.exists()) {
            throw new RuntimeException("File at path " + contentPath + " does not exist!");
        }

        String contentId = contentFile.getName() + "-" + System.currentTimeMillis();
        FileInputStream contentStream = new FileInputStream(contentFile);
        String checksum =
            store.addContent(spaceId, contentId, contentStream,
                             contentFile.length(), null, null, null);
        System.out.println("Content added with checksum: " + checksum);

        // Print contents of space after content addition
        System.out.println("\n\n*** Content Listing of " + spaceId +
                           " - after file is added ***\n\n");
        printContentListing(store, spaceId);

        // Delete file
        store.deleteContent(spaceId, contentId);

        // Print contents of space after content is removed
        System.out.println("\n\n*** Content Listing of " + spaceId +
                           " - after file is deleted ***\n\n");
        printContentListing(store, spaceId);

        System.out.println("\n\nSimple API Example process complete.");
    }

    /**
     * Prints contents of space
     */
    private void printContentListing(ContentStore store, String spaceId)
        throws ContentStoreException {
        Iterator<String> spaceContents = store.getSpaceContents(spaceId, null);
        while(spaceContents.hasNext()) {
            System.out.println(spaceContents.next());
        }
    }

    public static void main(String[] args) throws Exception {
        cmdOptions = new Options();

        Option hostOption =
           new Option("h", "host", true,
                      "the host address of the DuraCloud " +
                      "DuraStore application");
        hostOption.setRequired(true);
        cmdOptions.addOption(hostOption);

        Option usernameOption =
           new Option("u", "username", true,
                      "the username necessary to perform writes to DuraStore");
        usernameOption.setRequired(true);
        cmdOptions.addOption(usernameOption);

        Option passwordOption =
           new Option("p", "password", true,
                      "the password necessary to perform writes to DuraStore");
        passwordOption.setRequired(true);
        cmdOptions.addOption(passwordOption);

        Option spaceIdOption =
            new Option("s", "spaceid", true, "the space identifier");
        spaceIdOption.setRequired(true);
        cmdOptions.addOption(spaceIdOption);

        Option contentPathOption =
           new Option("c", "content", true,
                      "full path to a local file which will be copied in DuraCloud");
        contentPathOption.setRequired(true);
        cmdOptions.addOption(contentPathOption);

        CommandLine cmd = null;
        try {
            CommandLineParser parser = new PosixParser();
            cmd = parser.parse(cmdOptions, args);
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            usage();
        }

        String host = cmd.getOptionValue("h");
        String username = cmd.getOptionValue("u");
        String password = cmd.getOptionValue("p");
        String spaceId = cmd.getOptionValue("s");
        String contentPath = cmd.getOptionValue("c");

        SimpleApiExample example =
            new SimpleApiExample(host, username, password, spaceId, contentPath);

        example.run();
    }

    private static void usage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Running the Simple Api Example", cmdOptions);
        System.exit(1);
    }

}