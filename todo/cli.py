import argparse
from todo.tokenize import tokenize
from todo.utils import get_files, print_todos, save_todos
import sys


def main():
    # create a parser object
    parser = argparse.ArgumentParser(description = "Read directories")
     
    # add argument
    parser.add_argument("dirs",
                        nargs = '*',
                        metavar = "dir",
                        type = str,
                        default = ['./'],
                        help = "Find all files within directories")

    # Urgent priority (activate using -u or --urgent)
    parser.add_argument(
        "-u", "--urgent",
        action="store_true",
        help=("Retrieve 'urgent' TODO comments. "))

    # Soon priority (activate using -s or --soon) 
    parser.add_argument(
        "-s", "--soon",
        action="store_true",
        help=("Retrieve 'soon' TODO comments. "))
 
    parser.add_argument(
        "-o", "--output",
        nargs = "?",
        dest = "out",
        const='./todo.txt',
        default= None,
        help=("Save comments in an external file"))

    # parse the arguments from standard input
    args = parser.parse_args()
    
    _ = get_files(args.dirs)
    urgent_todos = []
    reg_todos = []
    soon_todos = []
    todos = []
    for file in _:
        with open(file, "r") as f:
            for token in tokenize(f.read(), file, urgent = args.urgent, soon = args.soon):

                if args.out:
                    todos.append(token)
                else:
                    if "urgent" in token.re_type:
                        urgent_todos.append(token)
                    elif "soon" in token.re_type:
                        soon_todos.append(token)
                    else:
                        reg_todos.append(token)

    if args.out:
        # Store results in a txt file
        save_todos(args.out, todos)

    else:
        # Print to terminal 
        print_todos(urgent_todos)
        print_todos(soon_todos)
        print_todos(reg_todos)