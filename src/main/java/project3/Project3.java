package project3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oviya
 */
public class Project3 {

    final static String MAGIC_NUMBER = "4348PRJ3";

    public static void main(String[] args) {
        //no commoand or correct file name/needed args
        if (args.length < 2) {
            System.err.println("[ERROR]: Incorrect Usage");
            System.exit(1);
        }

        //swtich statements to cover each required command
        try {
            switch (args[0].toLowerCase()) {
                //create new file with file name given
                case "create":
                    //make sure file name is given
                    if (args.length == 2) {
                        //make file
                        createNewFile(args[1]);
                    } else {
                        //invalid command/user input
                        System.err.println("[ERROR]: Invalid arguments");
                        System.exit(1);
                    }
                    break;

                case "insert":
                    //make sure file name, key, value are given
                    if (args.length == 4) {
                        //insert
                        insertKey(args[1], Long.parseLong(args[2]), Long.parseLong(args[3]));
                    } else {
                        //invalid command/user input
                        System.err.println("[ERROR]: Invalid arguments");
                        System.exit(1);
                    }
                    break;

                case "search":
                    //make sure file name and key are given
                    if (args.length == 3) {
                        //search
                        searchKey(args[1], Long.parseLong(args[2]));
                    } else {
                        //invalid command/user input
                        System.err.println("[ERROR]: Invalid arguments");
                        System.exit(1);
                    }
                    break;

                case "load":
                    //make sure both file names are given
                    if (args.length == 3) {
                        //load
                        loadFromCSV(args[1], args[2]);
                    } else {
                        //invalid command/user input
                        System.err.println("[ERROR]: Invalid arguments");
                        System.exit(1);
                    }
                    break;

                case "print":
                    //make sure file name is given
                    if (args.length == 2) {
                        //print
                        printIndex(args[1]);
                    } else {
                        //invalid command/user input
                        System.err.println("[ERROR]: Invalid arguments");
                        System.exit(1);
                    }
                    break;

                case "extract":
                    //make sure both file names are given
                    if (args.length == 3) {
                        //extract
                        extractToCSV(args[1], args[2]);
                    } else {
                        //invalid command/user input
                        System.err.println("[ERROR]: Invalid arguments");
                        System.exit(1);
                    }
                    break;

                default:
                    //invalid command/user input
                    System.err.println("[ERROR]: Invalid command");
                    System.exit(1);
            }
            //error handling
        } catch (NumberFormatException e) {
            System.err.println("[ERROR]: Program failed - Number format exception");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("[ERROR]: Program failed - IO exception");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("[ERROR]: Program failed");
            System.exit(1);
        }
        return;
    }

//-------------------------------------------------------------------------------------------------------------------------------------
    //new file function
    public static void createNewFile(String filename) throws IOException {
        File file = new File(filename);

        //check if file exists
        if (file.exists()) {
            System.out.println("[ERROR]: File already exists.");
            System.exit(1);
        }

        //create new file with header
        try (RandomAccessFile newfile = new RandomAccessFile(file, "rw")) {
            Header header = new Header();
            setHeader(newfile, header);
            System.out.println("[SUCCESS]: File created.");
        }
    }

    //insert value function
    public static void insertKey(String filename, long key, long value) throws IOException {
        File file = new File(filename);

        //check if file exists and validate
        validateIndexFile(file);

        try (RandomAccessFile newfile = new RandomAccessFile(file, "rw")) {
            //read and get header info
            Header header = getHeader(newfile);

            //check if root/tree exists
            if (header.rootId == 0) {
                //tree is empty --> make root
                Node rootNode = new Node();
                rootNode.blockId = header.nextBlockId;
                rootNode.parentId = 0;
                rootNode.keys[0] = key;
                rootNode.values[0] = value;
                rootNode.numKeys = 1;

                //update header and header info
                header.rootId = header.nextBlockId;
                header.nextBlockId++;
                setHeader(newfile, header);
                writeNode(newfile, rootNode);

                //success
                System.out.println("[SUCCESS]: Key inserted.");
                return;
            } //add node to existing tree
            else {
                Node root = readNode(newfile, header.rootId);
                insertNode(newfile, header, root, key, value);
            }
        }
    }

    //insert node function
    public static void insertNode(RandomAccessFile newfile, Header header, Node node, long key, long value) throws IOException {
        //store current node
        Node currentNode = node;

        //see if there is space for new nodes
        while (!currentNode.checkLeaf()) {
            int i = 0;
            while (i < currentNode.numKeys && key > currentNode.keys[i]) {
                i++;
            }

            //check if key exists --> update value
            if (i < currentNode.numKeys && key == currentNode.keys[i]) {
                currentNode.values[i] = value;
                writeNode(newfile, currentNode);
                System.out.println("[SUCCESS]: Key updated.");
                return;
            }

            //track child and update/discard
            long childId = currentNode.children[i];
            Node childNode = readNode(newfile, childId);
            currentNode = childNode;
        }

        //check if existing is a leaf --> update if so
        int i = 0;
        while (i < currentNode.numKeys && key > currentNode.keys[i]) {
            i++;
        }

        if (i < currentNode.numKeys && key == currentNode.keys[i]) {
            currentNode.values[i] = value;
            writeNode(newfile, currentNode);
            System.out.println("[SUCCESS]: Key updated");
            return;
        }

        //add as leaf
        if (currentNode.numKeys < 19) {
            //check if there is space
            for (int j = currentNode.numKeys; j > i; j--) {
                currentNode.keys[j] = currentNode.keys[j - 1];
                currentNode.values[j] = currentNode.values[j - 1];
            }

            //add new value and update number of nodes
            currentNode.keys[i] = key;
            currentNode.values[i] = value;
            currentNode.numKeys++;

            //update file
            writeNode(newfile, currentNode);
            System.out.println("[SUCCESS]: Key inserted.");
            return;
        }
        //check if node is full --> if so, split 
        splitNode(newfile, header, currentNode, key, value);
    }

    //split node function
    public static void splitNode(RandomAccessFile newfile, Header header, Node node, long key, long value) throws IOException {
        //stable storage for all existing + new values to make sure nothing is lost
        long[] tempKeys = new long[20];
        long[] tempValues = new long[20];
        long[] tempChildren = new long[21];

        //copy into arrays
        for (int i = 0; i < 19; i++) {
            tempKeys[i] = node.keys[i];
            tempValues[i] = node.values[i];
        }
        for (int i = 0; i <= 19; i++) {
            tempChildren[i] = node.children[i];
        }

        //find space for new key
        int i = 18;
        while (i >= 0 && key < tempKeys[i]) {
            tempKeys[i + 1] = tempKeys[i];
            tempValues[i + 1] = tempValues[i];
            tempChildren[i + 2] = tempChildren[i + 1];
            i--;
        }

        //insert new key
        tempKeys[i + 1] = key;
        tempValues[i + 1] = value;

        //create new node
        Node newNode = new Node();
        newNode.blockId = header.nextBlockId++;
        newNode.parentId = node.parentId;

        //find midpoit to break
        int mid = 19 / 2;

        //first half stays in original node --> update the information of the original node
        node.numKeys = 0;
        for (i = 0; i < mid; i++) {
            node.keys[i] = tempKeys[i];
            node.values[i] = tempValues[i];
            node.children[i] = tempChildren[i];
            node.numKeys++;
        }
        node.children[mid] = tempChildren[mid];

        // second node is new --> update the information of the new node
        long midKey = tempKeys[mid];
        long midValue = tempValues[mid];
        for (i = mid + 1; i <= 19; i++) {
            newNode.keys[i - (mid + 1)] = tempKeys[i];
            newNode.values[i - (mid + 1)] = tempValues[i];
            newNode.children[i - (mid + 1)] = tempChildren[i];
            newNode.numKeys++;
        }
        newNode.children[newNode.numKeys] = tempChildren[20];

        //see if leaf
        if (!node.checkLeaf()) {
            //if not --> update children
            for (i = 0; i <= node.numKeys; i++) {
                if (node.children[i] != 0) {
                    Node childNode = readNode(newfile, node.children[i]);
                    childNode.parentId = node.blockId;
                    writeNode(newfile, childNode);
                }
            }

            //update parent pointers
            for (i = 0; i <= newNode.numKeys; i++) {
                if (newNode.children[i] != 0) {
                    Node childNode = readNode(newfile, newNode.children[i]);
                    childNode.parentId = newNode.blockId;
                    writeNode(newfile, childNode);
                }
            }
        }

        // if node was root --> create and update root
        if (node.parentId == 0) {
            Node newRoot = new Node();
            newRoot.blockId = header.nextBlockId++;
            newRoot.parentId = 0;
            newRoot.keys[0] = midKey;
            newRoot.values[0] = midValue;
            newRoot.numKeys = 1;

            //immediate children 
            newRoot.children[0] = node.blockId;
            newRoot.children[1] = newNode.blockId;

            //move parents
            node.parentId = newRoot.blockId;
            newNode.parentId = newRoot.blockId;

            //update header to have new root info + write to file
            header.rootId = newRoot.blockId;

            //write all updates to file
            setHeader(newfile, header);
            writeNode(newfile, node);
            writeNode(newfile, newNode);
            writeNode(newfile, newRoot);

            System.out.println("[SUCCESS]: Key inserted.");

        } //node was not root --> update parent
        else {
            Node parent = readNode(newfile, node.parentId);
            i = 0;
            while (i < parent.numKeys && midKey > parent.keys[i]) {
                i++;
            }

            //make space for new key + child --> insert it as well
            for (int j = parent.numKeys; j > i; j--) {
                parent.keys[j] = parent.keys[j - 1];
                parent.values[j] = parent.values[j - 1];
                parent.children[j + 1] = parent.children[j];
            }

            parent.keys[i] = midKey;
            parent.values[i] = midValue;
            parent.children[i + 1] = newNode.blockId;
            parent.numKeys++;

            //write to file
            writeNode(newfile, node);
            writeNode(newfile, newNode);

            //check if parent node is full --> split it
            if (parent.numKeys == 19) {
                Node tempParent = new Node();
                for (int j = 0; j < parent.numKeys; j++) {
                    tempParent.keys[j] = parent.keys[j];
                    tempParent.values[j] = parent.values[j];
                }
                for (int j = 0; j <= parent.numKeys; j++) {
                    tempParent.children[j] = parent.children[j];
                }
                tempParent.numKeys = parent.numKeys;
                tempParent.blockId = parent.blockId;
                tempParent.parentId = parent.parentId;

                //srite to file
                writeNode(newfile, parent);

                // confinute split
                long dummyKey = tempParent.keys[tempParent.numKeys - 1] + 1;
                long dummyValue = 0;
                splitNode(newfile, header, tempParent, dummyKey, dummyValue);
            } else {
                //write to file
                writeNode(newfile, parent);
                System.out.println("[SUCCESS]: Key inserted.");
            }
        }
    }

    //search for key function
    public static void searchKey(String filename, long key) throws IOException {
        File file = new File(filename);

        //see if file exists
        validateIndexFile(file);
        try (RandomAccessFile newfile = new RandomAccessFile(file, "r")) {
            //get header info
            Header header = getHeader(newfile);

            //see if root exists --> if not, no tree exists
            if (header.rootId == 0) {
                System.err.println("[ERROR]: Tree is empty, key not found.");
                return;
            }

            //tree exisits --> search 
            Node currentNode = readNode(newfile, header.rootId);
            while (true) {
                //check for key bycomparing key value with existing keys
                int i = 0;
                while (i < currentNode.numKeys && key > currentNode.keys[i]) {
                    i++;
                }

                // if key found --> return
                if (i < currentNode.numKeys && key == currentNode.keys[i]) {
                    System.out.println("[SUCCESS]: Key: " + key + ", Value:  " + currentNode.values[i]);
                    return;
                }

                // key doesn't exist --> return error
                if (currentNode.checkLeaf()) {
                    System.err.println("[ERROR]: Key not found.");
                    return;
                }

                //track child
                long childId = currentNode.children[i];
                currentNode = readNode(newfile, childId);
            }
        }
    }

    //load from csv function
    public static void loadFromCSV(String indexFilename, String csvFilename) throws IOException {
        //create new files for both and validate both
        File indexFile = new File(indexFilename);
        File csvFile = new File(csvFilename);
        validateIndexFile(indexFile);
        if (!csvFile.exists()) {
            throw new IOException("Error: CSV file " + csvFilename + " does not exist.");
        }

        //start reading csv file
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int count = 0;
            //read by line
            while ((line = reader.readLine()) != null) {
                //parse by line by comma
                String[] parts = line.split(",");

                try {
                    //input validation
                    long key = Long.parseLong(parts[0].trim());
                    long value = Long.parseLong(parts[1].trim());
                    //insert
                    insertKey(indexFilename, key, value);
                    count++;
                } catch (NumberFormatException e) {
                    System.err.println("[ERROR]");
                }
            }
            System.out.println("[SUCCESS]: Loaded file: " + csvFilename);
        }
    }

    //print index file function
    public static void printIndex(String filename) throws IOException {
        //check if file exists and validate
        File file = new File(filename);
        validateIndexFile(file);

        //open and read file
        try (RandomAccessFile newfile = new RandomAccessFile(file, "r")) {
            //read header info
            Header header = getHeader(newfile);

            //check if tree exists --> return if not
            if (header.rootId == 0) {
                System.out.println("[ERROR]: Index is empty");
                return;
            }

            //print each node as approached
            System.out.println("[SUCCESS]: Here are the key/value pairs in the index:");
            List<KeyAndValue> pairs = new ArrayList<>();
            recGetKeyAndValues(newfile, header.rootId, pairs);
            for (KeyAndValue pair : pairs) {
                System.out.println(pair.key + ", " + pair.value);
            }
        }
    }

    //get all keys function
    public static void recGetKeyAndValues(RandomAccessFile newfile, long nodeId, List<KeyAndValue> pairs) throws IOException {
        //if tree is empty --> return
        if (nodeId == 0) {
            return;
        }
        //start tracker node
        Node node = readNode(newfile, nodeId);

        //if leaf --> get all key/value pairs from the node
        if (node.checkLeaf()) {
            for (int i = 0; i < node.numKeys; i++) {
                pairs.add(new KeyAndValue(node.keys[i], node.values[i]));
            }
            return;
        }
        //if not leaf --> in order traversal of all nodes
        for (int i = 0; i < node.numKeys; i++) {
            //left child
            recGetKeyAndValues(newfile, node.children[i], pairs);
            pairs.add(new KeyAndValue(node.keys[i], node.values[i]));
        }
        //right child
        recGetKeyAndValues(newfile, node.children[node.numKeys], pairs);
    }

    //push to csv file function
    public static void extractToCSV(String indexFilename, String csvFilename) throws IOException {
        //create both files and validate booth
        File indexFile = new File(indexFilename);
        File csvFile = new File(csvFilename);
        validateIndexFile(indexFile);
        if (csvFile.exists()) {
            System.err.println("[ERROR]: CSV file with that name already exists.");
            System.exit(1);
        }

        //open and read index file + write to csv file
        try (RandomAccessFile newfile = new RandomAccessFile(indexFile, "r"); BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {

            //get header info
            Header header = getHeader(newfile);

            //if tree is empty --> return
            if (header.rootId == 0) {
                return;
            }

            //get all key/value pairs + write them to csv
            List<KeyAndValue> pairs = new ArrayList<>();
            recGetKeyAndValues(newfile, header.rootId, pairs);

            for (KeyAndValue pair : pairs) {
                writer.write(pair.key + "," + pair.value);
                writer.newLine();
            }
            System.out.println("[SUCCESS]: Loaded key/value pairs to " + csvFilename);
        }
    }

    //validate index file function
    public static void validateIndexFile(File file) throws IOException {
        //check if file exists
        if (!file.exists()) {
            System.err.println("[ERROR]: File does not exist.");
            System.exit(1);
        }

        //check if magic number exists
        try (RandomAccessFile newfile = new RandomAccessFile(file, "r")) {
            if (newfile.length() < 512) {
                throw new IOException("[ERROR]: File size is too small");
            }

            //get magic number
            byte[] magicBytes = new byte[8];
            newfile.readFully(magicBytes);
            String magic = new String(magicBytes, StandardCharsets.US_ASCII);

            if (!MAGIC_NUMBER.equals(magic)) {
                throw new IOException("[ERROR]: File invalid.");
            }
        }
    }

    //get header info function
    public static Header getHeader(RandomAccessFile newfile) throws IOException {
        //create new header object
        Header header = new Header();

        //get to start of file
        newfile.seek(0);

        //get needed info 
        //magic number
        byte[] magicBytes = new byte[8];
        newfile.readFully(magicBytes);
        header.magicNumber = new String(magicBytes, StandardCharsets.US_ASCII);
        //root ID
        header.rootId = newfile.readLong();
        //next block ID
        header.nextBlockId = newfile.readLong();

        //return after getting all info
        return header;
    }

    // set header function
    public static void setHeader(RandomAccessFile newFile, Header header) throws IOException {
        //get to start of file
        newFile.seek(0);

        //set all header info
        //set magic number
        newFile.write(header.magicNumber.getBytes(StandardCharsets.US_ASCII));
        //set root ID
        newFile.writeLong(header.rootId);
        //set next block ID
        newFile.writeLong(header.nextBlockId);

        //initialize rest
        byte[] zeros = new byte[488];
        newFile.write(zeros);
        return;
    }

    //read node from file function
    public static Node readNode(RandomAccessFile newfile, long blockId) throws IOException {
        //create new node object
        Node node = new Node();

        //get offset for file
        long offset = blockId * 512;
        newfile.seek(offset);

        //get node header info
        node.blockId = newfile.readLong();
        node.parentId = newfile.readLong();
        node.numKeys = (int) newfile.readLong();

        //get keys, values, pointers
        //keys
        for (int i = 0; i < 19; i++) {
            node.keys[i] = newfile.readLong();
        }
        //values
        for (int i = 0; i < 19; i++) {
            node.values[i] = newfile.readLong();
        }
        //pointers
        for (int i = 0; i < 20; i++) {
            node.children[i] = newfile.readLong();
        }
        return node;
    }

    //write node to file function
    public static void writeNode(RandomAccessFile newfile, Node node) throws IOException {
        //get offset for file
        long offset = node.blockId * 512;
        newfile.seek(offset);

        //set node header
        newfile.writeLong(node.blockId);
        newfile.writeLong(node.parentId);
        newfile.writeLong(node.numKeys);

        //set all
        //keys
        for (int i = 0; i < 19; i++) {
            newfile.writeLong(node.keys[i]);
        }
        //values
        for (int i = 0; i < 19; i++) {
            newfile.writeLong(node.values[i]);
        }
        //pointers
        for (int i = 0; i < 20; i++) {
            newfile.writeLong(node.children[i]);
        }
        //set rest of with 0
        int remainingBytes = 24;
        if (remainingBytes > 0) {
            byte[] zeros = new byte[remainingBytes];
            newfile.write(zeros);
        }
    }
}
