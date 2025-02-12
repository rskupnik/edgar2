import sys
import time

from python_task import PythonTask
from playwright.sync_api import sync_playwright

class PythonTaskImplementation(PythonTask):

    def perform_task(self):
        url = "https://moj.gov.pl/uslugi/engine/ng/index?xFormsAppName=UprawnieniaKierowcow&xFormsOrigin=EXTERNAL"

        self.notify_user("Testing driver license check")

        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            page = browser.new_context().new_page()
            page.set_default_timeout(10000)
            page.goto(url, wait_until="domcontentloaded")

#             page.screenshot(path="screenshot0.png", full_page=True)

            page.fill("#imiePierwsze", "RADOSŁAW")
            page.fill("#nazwisko", "SKUPNIK")
            page.fill("#seriaNumerBlankietuDruku", "I1203025")

            page.click(".btn-primary")

#             time.sleep(3)
#
#             page.screenshot(path="screenshot.png", full_page=True)

            result_info = page.wait_for_selector("upki-search-result-info", timeout=10000)

            strong_element = result_info.query_selector("strong")
            strong_text = strong_element.text_content()

            self.notify_user(f"Wynik: {strong_text}")

            browser.close()


if __name__ == "__main__":
    PythonTaskImplementation(sys.argv[1]).execute()