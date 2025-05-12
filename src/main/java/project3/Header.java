package project3;

//class to store header and its info
public class Header {

    //varaibles needed for header
    String magicNumber;
    long rootId;
    long nextBlockId;

    //constructor
    public Header() {
        magicNumber = "4348PRJ3";
        rootId = 0;
        nextBlockId = 1; 
    }
}
