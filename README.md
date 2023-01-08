# todocom (Todo Comments)
CLI program that retrieves all TODO comments from file(s) and prints them in terminal/shell. It was created in order to automatically update a list of TODO tasks by simply adding "TODO:" comments in the code ([Comments Format](#comments-format)). It also enables prioritization of tasks by using "TODO soon:" or "TODO urgent". 
To create the TODO list, simply open terminal and run the following command:
```
todo [folder/file]
```
![gen_todo](https://user-images.githubusercontent.com/73610201/211216011-27e057b0-0420-4d90-8950-999f75583566.gif)

This command will print out all TODO comments that were found in the code, sorted by their prioritization: urgent, soon and regular. 
_Urgent_ tasks will be printed in RED, _soon_ in CYAN and _regular_ comments in WHITE to make it easier to read. There is also an option to filter comments by their priotization:
```
# Prints urgent TODOs
todo -u [folder/file]
```
![urgent_todo](https://user-images.githubusercontent.com/73610201/211216002-c00860d3-7a61-425f-8cb2-939de85c01ec.gif)

Or:
```
# Prints soon TODOs
todo -s [folder/file]
```
![soon_todo](https://user-images.githubusercontent.com/73610201/211216007-f4eabb81-76d0-42c5-9334-0f13857e809b.gif)

Comments can also be assigned to a user by adding "Todo @username" comment:
```
# Prints soon TODOs
todo -a [USERNAME] [folder/file]
```

![assigned_todo](https://user-images.githubusercontent.com/73610201/211216263-ca453589-e490-49b3-a839-65315366f34f.gif)

Finally, there is an option to save the list in a text file (stores as regular text without colors):
```
# Store results in a txt file![dummy](https://user-images.githubusercontent.com/73610201/211216021-a4859641-69a6-4e94-8f8c-2c0cafd0a455.png)

todo -o [path/to/sample.txt] [folder/file]
```

## Setup
```
pip install todocom
```

## Comments Format
There are two types of comments: single line and multi-line. Currently, multi-line comments (docstrings) are only supported in Python, but single line should work for most programming languages.

Format is flexible and can be lower-case, upper-case or a combination of both. Below are several examples:
```
1. TODO:
2. TODo:
3. TOD0:
4. ToD0:
5. To-D0:
6. to-do:
```

In _Urgent_ and _soon_ comments the TODO part is flexible as shown above, but must be followed by either _urgent_ or _soon_ in lower-case:
 ```
1. TO-DO soon:
2. tODo soon: 
3. ToD0 urgent:
4. T0-D0 urgent:
```
