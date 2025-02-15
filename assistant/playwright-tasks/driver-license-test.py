import sys
import time
import json

from python_task import PythonTask
from playwright.sync_api import sync_playwright

INPUT_FILE = "/tmp/driverCheckInput"
URL = "https://moj.gov.pl/uslugi/engine/ng/index?xFormsAppName=UprawnieniaKierowcow&xFormsOrigin=EXTERNAL"

class PythonTaskImplementation(PythonTask):

    def perform_task(self):
        input_data = json.loads(self.read_file(INPUT_FILE))

        output = []
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            page = browser.new_context().new_page()
            page.set_default_timeout(10000)
            page.goto(URL, wait_until="domcontentloaded")

            for item in input_data:
                page.fill("#imiePierwsze", item.get('imie'))
                page.fill("#nazwisko", item.get('nazwisko'))
                page.fill("#seriaNumerBlankietuDruku", item.get('numer_dokumentu'))

                page.click(".btn-primary")

                document_found = page.wait_for_selector("upki-search-result-info", timeout=10000).query_selector("strong").text_content()
                if document_found == "Nie znaleziono dokumentu":
                    output.append({"imie": item.get('imie'), "nazwisko": item.get('nazwisko'), "dokument": item.get('numer_dokumentu'), "stan": "Nie znaleziono dokumentu"})
                else:
                    state = page.wait_for_selector("upki-search-results", timeout=10000).query_selector(".stan").query_selector("strong").text_content()
                    output.append({"imie": item.get('imie'), "nazwisko": item.get('nazwisko'), "dokument": item.get('numer_dokumentu'), "stan": state})

            self.send_output(json.dumps(output, ensure_ascii=False))
            browser.close()


if __name__ == "__main__":
    PythonTaskImplementation(sys.argv[1]).execute()