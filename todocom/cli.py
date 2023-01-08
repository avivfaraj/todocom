from todocom.tokenize import tokenize
from todocom.utils import (get_files,
                           print_todos,
                           save_todos,
                           cli_tool)


def main():

    parser = cli_tool()

    # parse the arguments from standard input
    args = parser.parse_args()

    _ = get_files(args.dirs)
    urgent_todos = []
    reg_todos = []
    soon_todos = []
    todos = []
    for file in _:
        with open(file, "r") as f:
            try:
                content = f.read()
            except UnicodeDecodeError:
                print(f" \033[33m **** Warning: Couldn't read file {file} **** \n \033[0m")
                continue

        # Tokenize file and iterate over tokens
        for token in tokenize(content,
                              file,
                              urgent = args.urgent,
                              soon = args.soon,
                              assigned = args.assigned):

            # Ensure tokens are put in the correct list
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
