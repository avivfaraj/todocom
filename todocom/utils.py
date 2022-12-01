import os
import argparse
from pathlib import Path

# Constants
BOLD = "\033[1m"
RED = "\033[31m"
CYAN = "\033[1;36m"
WHITE = "\033[0m"
PURPLE = "\033[1;94m"
LIGHT_YELLOW = "\033[1;33m"

# Ignore files/dirs lists
ignore_files = ["__main__.py",
                "__init__.py",
                ".DS_Store",
                "pyproject.toml",
                "poetry.lock",
                "README.md",
                ".gitignore",
                "LICENSE"]

ignore_dirs = ["__pycache__",
               "tests",
               ".git",
               ".github",
               "dist"]


def print_todos(tokens = []):
    """
    Print all TODOs to terminal
    """
    for i in tokens:
        print(i.print())


def save_todos(file_path, tokens = []):
    """
    Save all TODOs in an external file.
    Default file in current directory is: ./todo.txt
    """
    output_file = Path(file_path).expanduser().resolve()

    # Create new file or override the older file
    f = open(output_file, "w")
    f.close()

    # Append to the new file
    with open(output_file, "a") as file:
        for i in tokens:
            file.write(str(i) + "\n")


def get_files(path_ls = []):
    """
    Generator function that yields all files in a specific directory

    Parameter:
    ----------
    path_ls: List[str]
        List of files/directories to be checked

    Returns:
    ----------
    List of all files to be read and checked for TODO comments
    """
    for path in path_ls:
        if os.path.isfile(path):
            yield path

        if os.path.isdir(path):
            for root, dirs, files in os.walk(path, topdown=True):
                # Remove unwanted directories from list
                [dirs.remove(i) for i in ignore_dirs if i in dirs]

                # Remove unwanted files from list
                [files.remove(i) for i in ignore_files if i in files]

                for file in files:

                    if file not in ignore_files:
                        yield f'{root}/{file}'.replace("//", "/")


def cli_tool():
    """
    Define arguments for "todo" command (cli) using argparse package.

    Return:
    ------------
    argparse.ArgumentParser Object
    """

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

    # Output file argument
    parser.add_argument(
        "-o", "--output",
        nargs = "?",
        dest = "out",
        const = './todo.txt',
        default = None,
        help = ("Save comments in an external file"))

    # Assigned argument
    parser.add_argument(
        "-a", "--assigned",
        nargs = 1,
        metavar = "assigned_to",
        type = str,
        default = None,
        help = "Find all TODOs that were assigned to a specific user")

    return parser


def create_alphanum_dict():
    dict_temp = {"E": ["3"],
                 "Z": ["2"],
                 "I": ["1"],
                 "A": ["4"],
                 "F": ["7"],
                 "G": ["6", "9"],
                 "B": ["8"],
                 "O": ["0"]
                 }

    final_dict = dict_temp.copy()
    keys = final_dict.keys()
    for key, ls in dict_temp.items():
        nums = "".join(ls)
        for number in ls:
            if number in keys:
                final_dict[number].extend([key, key.lower()])
            else:
                final_dict[number] = [key, key.lower()]

            _ = nums.replace(number, "")
            if _:
                final_dict[number].extend([_])

    return final_dict


def asignee_name_regex(name: str):

    if name and isinstance(name, str):
        regex = ""
        alphanum_dict = create_alphanum_dict()
        for char in name:
            alphanum_set = set()
            if char == " ":
                regex += " "
                continue

            regex += "["
            if char.isalpha():
                upper = char.upper()
                alphanum_set.add(upper)
                alphanum_set.add(char.lower())

                if upper in alphanum_dict.keys():
                    alphanum_set.update(alphanum_dict[upper])

            else:
                alphanum_set.add(char)

                if char in alphanum_dict.keys():
                    alphanum_set.update(alphanum_dict[char])

            if alphanum_set:
                regex += "".join(alphanum_set)
            regex += "]"

        return regex


def specs(unique = None, assigned = None):
    """
    Defintion of regular expression (regex) for TODO comments

    Parameters:
    ------------
    unique: str | None
        For type str can be either "urgent", "soon", or "assign".
        None means returns all TODOs besides assigned ones.

    assigned: str
        Asignee's name

    Return:
    ------------
    Reguar expression for tokenization.
    """

    # Regex definitions - single comment, multi comment and the assignee's name
    single = '[Tt][Oo0][\\-\\_]?[Dd][Oo0]'
    multi = '\"{3} [Tt][Oo0][\\-\\_]?[Dd][Oo0]'
    name = asignee_name_regex(str(assigned)
                              .strip()
                              .replace('\'', "")
                              .replace("[", "")
                              .replace("]", ""))

    # Configurations
    token_specification = [
        ('single_line', '({}:).*'.format(single)),
        ('multiline', '({}:)[\\s\\S]*?(\"\"\")'.format(multi)),
        ('urgent', '({} urgent:).*'.format(single)),
        ('urgent_multiline', '({} urgent:)[\\s\\S]*?(\"\"\")'.format(multi)),
        ('soon', '({} soon:).*'.format(single)),
        ('soon_multiline', '({} soon:)[\\s\\S]*?(\"\"\")'.format(multi)),
        ('assign', '({} @{}).*'.format(single, name)),
        ('newline', r'\n')
    ]

    if unique in ["urgent", "soon", "assign"]:
        return '|'.join('(?P<%s>%s)' % pair
                        for pair in token_specification
                        if unique in pair[0] or "newline" in pair[0])
    else:
        return '|'.join('(?P<%s>%s)' % pair
                        for pair in token_specification
                        if "assign" not in pair[0])
