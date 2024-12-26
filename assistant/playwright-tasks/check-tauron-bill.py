import sys

from python_task import PythonTask
from playwright.sync_api import sync_playwright

class PythonTaskImplementation(PythonTask):

    def perform_task(self):
        url = "https://logowanie.tauron.pl/login"

        print("Python: Waiting for label input from Java...")
        label_id = self.request_user_input("Please provide the label_id for test")
        print(f"Python: Received label ID from Java: {label_id}")

        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            page = browser.new_page()
            page.goto(url, wait_until="domcontentloaded")
            label_text = page.locator(f"label[for='{label_id}']").text_content()
            print(f"Label text: {label_text}")

            browser.close()

            print("Python: Sending output to Java...")
            self.pipe_write_async(f"output: {label_text}")


if __name__ == "__main__":
    PythonTaskImplementation(sys.argv[1]).execute()