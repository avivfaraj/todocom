from todo.utils import get_files, print_todos
import os


directory = "./tests/files"
files_ls = ["/t.py",
            "/1/2/cli.py",
            "/java/FoodSearch.java",
            "/java/HealthTracker.java"]

def test_get_files():
    files_set = set([directory + i for i in files_ls])
    _ = set(get_files([directory]))
    final_set = set(_ - files_set)
    assert len(final_set) == 0, "One or more files do not match"
        