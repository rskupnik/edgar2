import os
from abc import ABC, abstractmethod

class PythonTask(ABC):

    # INTERNAL METHODS, DO NOT CALL EXPLICITLY

    def __init__(self, pipe_path: str):
        self.sync_pipe_path = pipe_path
        self.async_pipe_path = f"{pipe_path}_async"

    def execute(self):
        self.prepare()
        self.perform_task()

    def prepare(self):
        if not os.path.exists(self.sync_pipe_path):
            os.mkfifo(self.sync_pipe_path)
        if not os.path.exists(self.async_pipe_path):
            os.mkfifo(self.async_pipe_path)

    # ABSTRACT METHODS

    @abstractmethod
    def perform_task(self):
        pass

    # FUNCTIONAL METHODS, PREFER TO USE THOSE

    """
    Request user input and wait until it is provided
    """
    def request_user_input(self, msg: str):
        self.pipe_write_async(f"input: {msg}")
        return self.pipe_read()

    """
    Send msg to user
    """
    def notify_user(self, msg: str):
        self.pipe_write_async(f"output: {msg}")

    def send_output(self, output: str):
        self.pipe_write_async("OUTPUT_BEGIN")
        self.pipe_write_async(output)
        self.pipe_write_async("OUTPUT_END")

    # LOW-LEVEL METHODS, ONLY CALL IF YOU KNOW WHAT YOU ARE DOING

    """
    Write on the sync pipe
    """
    def pipe_write(self, content: str):
        with open(self.sync_pipe_path, "w") as pipe:
            pipe.write(f"{content}\n")

    """
    Read from the sync pipe
    """
    def pipe_read(self) -> str:
        with open(self.sync_pipe_path, "r") as pipe:
            return pipe.read().strip()

    """
    Write on the async pipe
    """
    def pipe_write_async(self, content: str):
        with open(self.async_pipe_path, "w") as pipe:
            pipe.write(f"{content}\n")
