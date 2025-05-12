package project3;

public class Node {

    //variables needed for node information
    long blockId;
    long parentId;
    int numKeys;
    long[] keys;    
    long[] values;  
    long[] children;

    //constructor with key/size limits
    public Node() {
        keys = new long[19];
        values = new long[19];
        children = new long[20];
        numKeys = 0;
    }

    //check if node is leaf function
    boolean checkLeaf() {
        //loop through children
        for (int i = 0; i < 20; i++) {
            //if a child is not null --> not a leaf --> return false
            if (children[i] != 0) {
                return false;
            }
        }
        //else --> return true
        return true;
    }
}
