import os
from pathlib import Path

# Constants
BOLD = "\033[1m"
RED = "\033[31m"
CYAN = "\033[1;36m"
WHITE = "\033[0m"

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


# Todo @avivfaraj: test
def format_todos(tokens = [], color = True):
    """
    A generator function that receives list of TODO tokens
    and returns each one as a string with all information.
    'urgent' and 'soon' comments will be printed in RED and
    CYAN respectively if 'color' is True.

    Parameters:
    ------------
    tokens: List[Token]
        List of all TODO tokens

    color: Boolean
        If True -> prints urgent and soon TODO comments in a different
        color to emphasize them.

    Return:
    ------------
    String with information about that TODO comment
    """
    if tokens:
        font_color = WHITE
        end = WHITE
        if color:
            if "urgent" in tokens[0].re_type:
                font_color = BOLD + RED

            elif "soon" in tokens[0].re_type:
                font_color = BOLD + CYAN
            else:
                font_color = WHITE
        else:
            font_color = ""
            end = ""

        for token in tokens:
            if not token.assign:
                yield (f'{token.file} --> pr: {token.re_type}, '
                       f'Line: {token.line}, comment: {font_color} {token.value} {end}')
            else:
                yield (f'{token.file} --> pr: {token.re_type}, assigned: {token.assign}, '
                       f'Line: {token.line}, comment: {font_color} {token.value} {end}')


def print_todos(tokens = []):
    """
    Print all TODOs to terminal
    """
    for i in format_todos(tokens):
        print(i)


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
        for i in format_todos(tokens, color = False):
            file.write(i + "\n")


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
