# importing required modules
import argparse

def main():
    # create a parser object
    parser = argparse.ArgumentParser(description = "Read directories")
     
    # add argument
    parser.add_argument("walk", nargs = '*', metavar = "dir", type = str,
                         help = "All .py files within directories will be printed ")

     
    # parse the arguments from standard input
    args = parser.parse_args()
     
    # check if add argument has any input data.
    # If it has, then print sum of the given numbers
    if len(args.walk) != 0:
        import os
        for root, dirs, files in os.walk(args.walk[0], topdown=True):
            print(f"Root:\n {root}\n")
            print(f"Directories:\n {dirs}\n")
            print(f"Files:\n {files}\n")

        # with os.scandir(args.walk[0]) as it:
        #     for entry in it:
        #         if entry.name.endswith('.py') and entry.is_file():
        #             print(entry.name)


