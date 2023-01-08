from typing import NamedTuple

# Constants
BOLD = "\033[1m"
RED = "\033[31m"
CYAN = "\033[1;36m"
WHITE = "\033[0m"
PURPLE = "\033[1;94m"
LIGHT_YELLOW = "\033[1;33m"
end = WHITE


class Token(NamedTuple):
    re_type: str
    file: str
    value: str
    line: int
    assign: str = None
    # Todo @avivfaraj: add TODO comments due by date
    # date:

    def print(self, color = True):
        """
        Return TODO comments with colors to differentiate between them.
        'urgent' and 'soon' comments will be printed in RED and
        CYAN respectively if 'color' is True.

        Parameters:
        ------------
        color: Boolean
            If True -> prints urgent and soon TODO comments in a different
            color to emphasize them.

        Return:
        ------------
        String with information about that TODO comment
        """
        font_color = WHITE
        end = WHITE

        if "urgent" in self.re_type:
            font_color = BOLD + RED

        elif "soon" in self.re_type:
            font_color = BOLD + CYAN
        else:
            font_color = WHITE

        if self.assign:
            return (f'{BOLD}{self.file}{end} * {PURPLE}{self.line}{end} * @{self.assign} '
                    f'{LIGHT_YELLOW}>>{end}{font_color} {self.value} {end}')

        return (f'{BOLD}{self.file}{end} * {PURPLE}{self.line}{end} * '
                f'{LIGHT_YELLOW}>>{end}{font_color} {self.value} {end}')

    def __str__(self):
        if self.assign:
            return (f'{self.file} * {self.line} * @{self.assign} '
                    f'>> {self.value}')

        return f'{self.file} * {self.line} * >> {self.value}'
