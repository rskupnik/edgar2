import sys

from python_task import PythonTask

# This is an example that doesn't do anything
# It is here to copy-paste and bootstrap new scripts easily
class PythonTaskImplementation(PythonTask):

    def perform_task(self):
        # YOUR IMPLEMENTATION GOES HERE
        # self.request_user_input() available
        pass


if __name__ == "__main__":
    PythonTaskImplementation(sys.argv[1]).execute()