import re
from typing import NamedTuple
import os

# Code structure from official 're' module documentation
class Token(NamedTuple):
    type: str
    value: str
    line: int
    column: int

# T0-D0: first comment!
# Todo: Second comment using different regular expression

# todo urgent: Something that needs to be done immediately

""" ToDo urgent:
Multi-line TODO comment
for an urgent bug
"""


# bbbb
def tokenize(code):
    token_specification = [
        ("single_line", r'([Tt][Oo0][\-\_]?[Dd][Oo0]:).*'),            
        ('multiline',   r'(\"{3} [Tt][Oo0][\-\_]?[Dd][Oo0]:)[^.]*(\"{3})'), 
        ('newline',  r'\n')
    ]
    tok_regex = '|'.join('(?P<%s>%s)' % pair for pair in token_specification)
    line_num = 1
    line_start = 0
    multi_line_num = None
    end_last_token = -1
    for mo in re.finditer(tok_regex, code):

        if multi_line_num:
            line_num += multi_line_num
            multi_line_num = None

        kind = mo.lastgroup
        column = mo.start() - line_start

        if kind in ["multiline", "single_line"]:
            value = mo.group()
            multi_line_num = 1
            value = re.sub("\n", "", value.strip())
            value = re.sub(" +", " ", value.strip())
            index = re.search(":", value).span()[0]
            if index:
                index = re.search(":", value).span()[0]

            if kind == 'multiline':
                multi_line_num = len(re.findall("\n", mo.group())) + 1
                value = value[index + 1: -3]
            else:
                value = value[index + 1:]

            end_last_token = mo.end()
            _ = Token(kind, value, line_num, column)
            yield f'{_.type} --> Line: {line_num}, comment: \033[1m\033[31m {value} \033[0m'
        
        """ ToDo: 
        multi-line.
        Not urgent
        """
        if kind == 'newline':
            line_start = mo.end()
            if mo.start() != end_last_token:
                line_num += 1
            else:
                end_last_token = -1
            continue


    """ ToDo soon:
    testing multiline.
    with soon todo and some special characters:
    ,./']\]()&^'
    """


