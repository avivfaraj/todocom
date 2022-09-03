# todocom (Todo Comments)
CLI program that retrieves all TODO comments from file(s) and prints them in terminal/shell. It was created in order to automatically update a list of TODO tasks by simply adding "TODO:" comments in the code. It also enables prioritization of tasks by using "TODO soon:" or "TODO urgent". 
To create the TODO list, simply open terminal and run the following command:
```
todo [folder/file]
```

This command will print out all TODO comments that were found in the code, sorted by their prioritization: urgent, soon and regular. 
_Urgent_ tasks will be printed in RED, _soon_ in CYAN and _regular_ comments in WHITE to make it easier to read. There is also an option to filter comments by their priotization:
```
# Prints urgent TODOs
todo -u [folder/file]
```
Or:
```
# Prints soon TODOs
todo -s [folder/file]
```

Finally, there is an option to save the list in a text file (stores as regular text without colors):
```
# Store results in a txt file
todo -o [path/to/sample.txt] [folder/file]
```


## Setup
```
pip install todocom
```