from typing import NamedTuple
import re


class Token(NamedTuple):
    re_type: str
    file: str
    value: str
    line: int
    assign: str = None
    # Todo @avivfaraj: add TODO comments due by date
    # date:


def create_alphanum_dict():
    dict_temp = {"E": ["3"],
                 "Z": ["2"],
                 "I": ["1"],
                 "A": ["4"],
                 "F": ["7"],
                 "G": ["6", "9"],
                 "B": ["8"],
                 "L": ["1"],
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
                    alphanum_set.update(alphanum_dict[upper])

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


def tokenize(code, file, **kwargs):
    """
    Followed instructions in 're' module documentation and
    modified the code to include TODO regex.

    Types of TODOs:
        1. single_line      ---> should work with most files
        2. multiline        ---> specifically designed for python docstrings
        3. urgent           ---> should work with most files
        4. urgent_multiline ---> specifically designed for python docstrings
        5. soon             ---> should work with most files
        6. soon_multiline   ---> specifically designed for python docstrings
        ** newline          ---> required for counting rows!
    """

    if kwargs["urgent"]:
        tok_regex = specs("urgent")

    elif kwargs["soon"]:
        tok_regex = specs("soon")

    elif kwargs["assigned"]:
        tok_regex = specs("assign", assigned = kwargs["assigned"])

    else:
        tok_regex = specs()

    assign_to, et_index = None, -1
    line_num = 1
    multi_line_num = None
    end_last_token = -1

    # iteration over tokens found in code
    for mo in re.finditer(tok_regex, code):

        # Ensure last token was multi-line
        if multi_line_num:

            # Must add the number of rows in the comment
            # Or else the next tokens will be shifted!
            line_num += multi_line_num
            multi_line_num = None

        # Type of token (TODO) found
        kind = mo.lastgroup

        # Ensure comment is an actual TODO
        if kind != "newline":

            # Store TODO comment
            value = mo.group()

            # Clean comment and find its starting index
            value = re.sub("\n", " ", value.strip())
            value = re.sub(" +", " ", value.strip())

            index = re.search(":", value).span()[0]

            if kind == "assign":
                et_index = re.search("@", value).span()[0]

            multi_line_num = 1

            # Ensure ":" was found
            if index:
                index = re.search(":", value).span()[0]

            # Ensure TODO is multiline
            if 'multiline' in kind:

                # Calculate number of lines to add
                multi_line_num = len(re.findall("\n", mo.group())) + 1

                # Store the comment without the trailing double quotes (""")
                value = value[index + 1: -3].strip()

            else:
                if kind != "assign":
                    assign_to = None
                else:
                    assign_to = value[et_index + 1: index].strip()

                # Comment is single line --> store it all
                value = value[index + 1:].strip()

            # Store the last char in the token
            # Required to prevent "\n" from being considered twice
            end_last_token = mo.end()

            yield Token(kind, file, value, line_num, assign_to)

        # Ensure token is a newline
        if kind == 'newline':

            # Ensure newline is not used twice
            if mo.start() != end_last_token:
                line_num += 1
            else:
                end_last_token = -1
