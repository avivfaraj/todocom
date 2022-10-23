from todocom.tokenize import tokenize, Token


# Configs
file = "./tests/files/t.py"
tokens = set(
    [
        Token("single_line", file, "first comment!", 12),
        Token("single_line", file, "Second comment using different regular expression", 13),
        Token("urgent", file, "Something that needs to be done immediately", 15),
        Token("urgent_multiline", file, "Multi-line TODO comment for an urgent bug", 17),
        Token("multiline", file, "multi-line. Not urgent", 63),
        Token("soon_multiline", file, "testing multiline. with soon todo and some special characters: ,./']\]()&^'", 76)  # noqa: W605, E501
    ])


# Test functions
def test_tokenize_regular():
    """
    Testing all types of TODO comments.
    """
    with open(file, "r") as f:

        # Tokenize code in file
        gen = tokenize(f.read(), file, urgent = False, soon = False, assigned = None)

    # Create a set with all tokens
    gen_set = set(token for token in gen)

    # Ensure both sets are the same
    assert gen_set.difference(tokens) == set()


def test_tokenize_soon():
    """
    Testing only 'TODO soon' comments
    """
    soon_tokens = set(token for token in tokens if "soon" in token.re_type)

    with open(file, "r") as f:
        gen = tokenize(f.read(), file, urgent = False, soon = True, assigned = None)

    # Create a set with all tokens
    gen_set = set(token for token in gen)

    # Ensure both sets are the same
    assert gen_set.difference(soon_tokens) == set()


def test_tokenize_urgent():
    """
    Testing only 'TODO urgent' comments
    """
    urgent_tokens = set(token for token in tokens if "urgent" in token.re_type)

    with open(file, "r") as f:
        gen = tokenize(f.read(), file, urgent = True, soon = False, assigned = None)

    # Create a set with all tokens
    gen_set = set(token for token in gen)

    # Ensure both sets are the same
    assert gen_set.difference(urgent_tokens) == set()
