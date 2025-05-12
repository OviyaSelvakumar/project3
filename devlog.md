May 10, 2025 - 2:02 PM

I am going to start the project by creating an outline first. This will include validating input, creating a large switch statement to create commands, error handling, and creating/opening files. I will start with that and fill in each command with functions and variables. Also, because I had Git submission issues last time, I will be adding my updates to the devlog and doing a final submission through Git at the end. Hopefully, this should fix the .git issues I had last time.

----------------------------------------------------------------------------------------------------------------
May 10, 2025 - 2:43 PM

The switch statement is completed with each of the commands, Now, I will be filling in each commands, starting with create, print and extract. Because all commands use file opening input/output, I will be creating them as functions and calling them. I am not fully sure how to read a .idx file and will be focusing on reading about that as well.

----------------------------------------------------------------------------------------------------------------
May 10, 2025 - 3:20 PM

When creating the functions for opening and closing files, I thought that functions would be good for the header and formatting of the file as well. But, I think it might be better to create objects so that the tree structure can be more easily updated. I will also be creating a Node class to store information and methods on how to build the B+ tree.

----------------------------------------------------------------------------------------------------------------
May 10, 2025 - 5:12 PM

I have completed the create command. When setting up the function to create a new file header, I realized that an object to store and update the header would be more easier --> Because of this, I ended up creating a separate class with the necessary info mentioned --> the magic number, the rood node's id, and the next block's id. This way it'll be easier to edit through funcions as well.

I will now be working on insert and search. Both will require functions and setting up for the B+ tree classes. This will also include traversal fucntons and and insert funcstions. I will create new classes for this and move from there. Once these 2 are ocompleted, the remaining functions will be easier as almost all remaining require insert or traversing.

----------------------------------------------------------------------------------------------------------------
May 10, 2025 - 7:37 PM

This took a little longer than expected but I think the traversals and inserts work now. I went ahead and did the print command as well to make sure the traversals and inserts work as needed. I will have to double check the file opening and validation for each command. I wanted to do that before the swtich statement to save code, but was not able to becasue of the file names being entered with the commands.

Tomorrow, I will be working on extract and load. The primary issue for this will be properly creating, reading, and writing from/into .idx and .csv files. After that, I will see if any more input or file validations/error handling is needed.

----------------------------------------------------------------------------------------------------------------
May 11, 2025 - 9:04 PM

I will be completing the load anbd extract commands today. After that, I will check to see if any of the needs error handling.

----------------------------------------------------------------------------------------------------------------
May 11, 2025 - 9:57 PM

When loading the the .csv file, I am having some issues with the spacing, and this is apparent when seeing the final file after. I'm not fully sure if this is becasue of my file opener or my code but I will be working on checking this before the extract command.

----------------------------------------------------------------------------------------------------------------
May 11, 2025 - 10:24 PM

I think I was able to fix this. I struggled with finding how to open .idx files on my machine but it works now and I will be using this format for the extract command as well. This is the final command to work on, and I will be cleaning up my code after.

----------------------------------------------------------------------------------------------------------------
May 11, 2025 - 11:17 PM

The extract function is compelted, and it was much easier with the search and traversal fucntions made before. As of now, the project works and has been tested with incorrect commands and inputs! I also tested to see how the program works with existing files of the same name and the error handling is still working. I will now be working on the README before submitting the final project.

----------------------------------------------------------------------------------------------------------------
May 11, 2025 - 11:35 PM

The README was added with a launch.json file that has examples of run configurations.
