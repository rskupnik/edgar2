import sys

from python_task import PythonTask
from playwright.sync_api import sync_playwright

class PythonTaskImplementation(PythonTask):

    def perform_task(self):
        url = "https://logowanie.tauron.pl/login"
        username = sys.argv[2]
        password = sys.argv[3]

        self.notify_user("Checking the power bill amount due...")

        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            page = browser.new_context().new_page()
            page.set_default_timeout(5000)
            page.goto(url, wait_until="domcontentloaded")

            page.fill("#username1", username)
            page.fill("#password1", password)
            page.click(".button-pink")

            try:
                page.wait_for_selector("#ebokItems .amount-column .amount", timeout=5000)
                amount_text = page.locator("#ebokItems .amount-column .amount").text_content().strip()
                self.notify_user(f"Nearest payment: {amount_text.strip()}")
            except Exception as e:
                print(f"Error: Element not found. Details: {e}")
                self.notify_user("Something went wrong :(")

            browser.close()


if __name__ == "__main__":
    PythonTaskImplementation(sys.argv[1]).execute()