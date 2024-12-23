import sys

from python_task import PythonTask
from playwright.sync_api import sync_playwright

class CheckTauronBill(PythonTask):

    def perform_task(self):
        url = "https://logowanie.tauron.pl/login"

        # Signal to Java that Python is ready
        print("Python: Signaling ready to Java...")
        self.pipe_write("READY")

        print("Python: Waiting for label input from Java...")
        label_id = self.pipe_read()
        print(f"Python: Received label ID from Java: {label_id}")

        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            page = browser.new_page()
            page.goto(url, wait_until="domcontentloaded")
            label_text = page.locator(f"label[for='{label_id}']").text_content()
            print(f"Label text: {label_text}")

            browser.close()

            print("Python: Sending output to Java...")
            self.pipe_write(label_text)


if __name__ == "__main__":
    CheckTauronBill(sys.argv[1]).execute()