import sys
import time
import json

from python_task import PythonTask
from playwright.sync_api import sync_playwright

class PythonTaskImplementation(PythonTask):

    def perform_task(self):
        # Read input
        file_path = "/tmp/driverCheckInput"

        try:
            with open(file_path, "r") as file:
                content = file.read()
                data = json.loads(content)
                if isinstance(data, list):
                    for item in data:
                        print(f"Imie: {item.get('imie')}, Nazwisko: {item.get('nazwisko')}")
        except FileNotFoundError:
            print(f"File {file_path} not found.")
        except Exception as e:
            print(f"Error reading file: {e}")



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
#             page.fill("#imiePierwsze", "MAREK")
#             page.fill("#nazwisko", "SZUL")
#             page.fill("#seriaNumerBlankietuDruku", "Z0216477")

            page.click(".btn-primary")

#             time.sleep(3)
#
#             page.screenshot(path="screenshot.png", full_page=True)

            document_found = page.wait_for_selector("upki-search-result-info", timeout=10000).query_selector("strong").text_content()
            if document_found == "Nie znaleziono dokumentu":
                self.notify_user("Nie znaleziono dokumentu")
                self.send_output(f"[{{\"name\": \"RADOSŁAW\", \"surname\": \"SKUPNIK\", \"documentId\": \"I1203025\", \"status\": \"Not found\"}}]")
            else:
                state = page.wait_for_selector("upki-search-results", timeout=10000).query_selector(".stan").query_selector("strong").text_content()
                self.notify_user(f"Stan dokumentu: {state}")
                self.send_output(f"[{{\"name\": \"RADOSŁAW\", \"surname\": \"SKUPNIK\", \"documentId\": \"I1203025\", \"status\": \"{state}\"}}]")

            browser.close()


if __name__ == "__main__":
    PythonTaskImplementation(sys.argv[1]).execute()