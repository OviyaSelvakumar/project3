Project 3 - CS 4348 Oviya Selvakumar - OXS220001
-----------------------------------------------------------------------------------------------------------
OVERVIEW: The code consists of 4 main files: Project3.java, Node.java, KeyAndValue.java, and Header.java. Please run Project3.java as it contains the main function. The remaining files are class files.
-----------------------------------------------------------------------------------------------------------
NOTE: The project was pushed as one big commit to keep the .git in the project. This was an issue in the past 2 project submissions, but doing this should correct the problem.
-----------------------------------------------------------------------------------------------------------
FILE: Project3.java

This program contains one main program that taxes 6 commands:
create: a new index file of the given name
insert:  adds given key and value to given index file name
search: finds requested key in given file name
load: inserts all keys and values from given .csv file into given file name
print: prints all keys and values in given file name
extract: takes all keys and values an capies it into a new .csv file of given name

These commands are completed through a switch statement, and a variety of functions and classes are used here to achieve this.
-----------------------------------------------------------------------------------------------------------
FILE: Node.java

This class has a constructor to store info abou the nodes and pointers, specifically keys, the number of keys, its children an dits values. It also has a function to check if the node is a leaf.

-----------------------------------------------------------------------------------------------------------
FILE: KeyAndValue.java

This class has the constructor to store the key and value within it. 
-----------------------------------------------------------------------------------------------------------
FILE: Header.java

This header has a class with the information to set up the header, including the magic number.
-----------------------------------------------------------------------------------------------------------
INSTRUCTIONS: To run, follow the argument usage below:

create - project3 create test.idx
insert - project3 insert test.idx 15 100
search- project3 search test.idx 15
load - project3 load test.idx input.csv
print - project3 print test.idx
extract - project3 extract test.idx output.csv

The launch.json file in the project also has more running configurations to test the project
-----------------------------------------------------------------------------------------------------------
DETAILS: The Index File

The index file will be divided into blocks of 512 bytes. Each node of the btree will fit in one
512 byte block, and the file header will use the entire first block. 
-----------------------------------------------------------------------------------------------------------
DETAILS: Header Format

The header can be maintained in memory, but needs to be in sync with the file. The header will
have the following fields, in the order presented.
• 8-bytes: The magic number “4348PRJ3” (as a sequence of ASCII values).
• 8-bytes: The id of the block containing the root node. This field is zero if the tree is empty.
• 8-bytes: The id of the next block to be added to the file. This is the next location for a new node.
• The remaining bytes are unused.
-----------------------------------------------------------------------------------------------------------
DETAILS: The B-Tree

The b-tree should have minimal degree 10. This will give 19 key/value pairs, and 20 child pointers.
Each node will be stored in a single block with some header information. Below is the node block
fields in order.
• 8-bytes: The block id this node is stored in.
• 8-bytes: The block id this nodes parent is located. If this node is the root, then this field is zero.
• 8-bytes: Number of key/value pairs currently in this node.
• 152-bytes: A sequence of 19 64-bit keys
• 152-bytes: A sequence of 19 64-bit values
• 160-bytes: A sequence of 20 64-bit offsets. These block ids are the child pointers for this node.
If a child is a leaf node, the corresponding id will be zero.
• Remaining bytes are unused.
-----------------------------------------------------------------------------------------------------------
DETAILS: The filetree of the project is below:

└── project3/
    ├── pom.xml
    ├── devlog.md
    ├── output.csv
    ├── README.md
    ├── test.idx
    ├── .vscode/
    │   └── launch.json
    ├── .git/
    │   ├── config
    │   ├── HEAD
    │   ├── description
    │   ├── index
    │   ├── COMMIT_EDITMSG
    │   ├── hooks/
    │   │   ├── commit-msg.sample
    │   │   ├── pre-rebase.sample
    │   │   ├── pre-commit.sample
    │   │   ├── applypatch-msg.sample
    │   │   ├── fsmonitor-watchman.sample
    │   │   ├── pre-receive.sample
    │   │   ├── prepare-commit-msg.sample
    │   │   ├── post-update.sample
    │   │   ├── pre-merge-commit.sample
    │   │   ├── pre-applypatch.sample
    │   │   ├── pre-push.sample
    │   │   ├── update.sample
    │   │   └── push-to-checkout.sample
    │   ├── logs/
    │   │   ├── HEAD
    │   │   └── refs/
    │   │       ├── heads/
    │   │       │   └── main
    │   │       └── remotes/
    │   │           └── origin/
    │   │               └── main
    │   ├── info/
    │   │   └── exclude
    │   ├── refs/
    │   │   ├── heads/
    │   │   │   └── main
    │   │   └── remotes/
    │   │       └── origin/
    │   │           └── main
    │   └── objects/
    │       ├── d8/
    │       │   └── af5abec8c2d208d50de2512987c5dbe5963680
    │       ├── 5a/
    │       │   └── 9020c8afb7e879b80ae2d567833198efff805c
    │       └── 3b/
    │           └── 6b38a2c5902f776f9324acc3d5a6890d350a8a
    ├── target/
    │   ├── classes/
    │   │   └── project3/
    │   │       ├── Header.class
    │   │       ├── Project3.class
    │   │       ├── KeyAndValue.class
    │   │       └── Node.class
    │   └── maven-status/
    │       └── maven-compiler-plugin/
    │           └── compile/
    │               └── default-compile/
    │                   ├── inputFiles.lst
    │                   └── createdFiles.lst
    └── src/
        └── main/
            └── java/
                └── project3/
                    ├── Node.java
                    ├── KeyAndValue.java
                    ├── Project3.java
                    └── Header.java
