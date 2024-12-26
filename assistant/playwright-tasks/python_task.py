import os
from abc import ABC, abstractmethod

class PythonTask(ABC):

    def __init__(self, pipe_path: str):
        self.sync_pipe_path = pipe_path
        self.async_pipe_path = f"{pipe_path}_async"

    def execute(self):
        self.prepare()
        self.perform_task()

    def prepare(self):
        if not os.path.exists(self.sync_pipe_path):
            os.mkfifo(self.sync_pipe_path)

    @abstractmethod
    def perform_task(self):
        pass

    def pipe_write(self, content: str):
        with open(self.sync_pipe_path, "w") as pipe:
            pipe.write(f"{content}\n")

    def pipe_read(self) -> str:
        with open(self.sync_pipe_path, "r") as pipe:
            return pipe.read().strip()

    def pipe_write_async(self, content: str):
        with open(self.async_pipe_path, "w") as pipe:
            pipe.write(f"{content}\n")
