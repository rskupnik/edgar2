import sys
import time

from python_task import PythonTask
from playwright.sync_api import sync_playwright

class PythonTaskImplementation(PythonTask):

    def perform_task(self):
        url = "https://logowanie.tauron.pl/login"
        username = sys.argv[2]
        password = sys.argv[3]

        self.notify_user(":dollar: :cloud_lightning: Attempting to pay Tauron bill")

        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            page = browser.new_context().new_page()
            page.set_default_timeout(10000)
            page.goto(url, wait_until="domcontentloaded")

            page.fill("#username1", username)
            page.fill("#password1", password)
            page.click(".button-pink")
            page.click(".popup-close")

            pay_button = page.locator(".ebok-button-pay-agreement")
            if pay_button.count() <= 0:
                self.notify_user("Nothing to pay!")
                browser.close()
                return

            try:
                page.wait_for_selector("#ebokItems .amount-column .amount", timeout=10000)
                amount_text = page.locator("#ebokItems .amount-column .amount").text_content().strip()
                self.notify_user(f"Amount to pay: {amount_text.strip()}")
            except Exception as e:
                print(f"Error: Element not found. Details: {e}")
                self.notify_user("Something went wrong :(")
                browser.close()
                return

            blik = self.request_user_input("Provide BLIK code")

            self.notify_user("Thanks! Initiating transaction, please accept on phone")

            page.click(".ebok-button-pay-agreement")
            try:
                page.wait_for_selector("#payway-radio-BLIK", timeout=20000)
                page.click("#payway-radio-BLIK")
                page.fill("#customerEmail", "r.skupnik@gmail.com")
                page.click(".submit-wrapper .btn")

                page.wait_for_selector("#input-core", timeout=20000)
                page.fill("#input-core", blik)
                page.locator('[id="blik-button primary extended"]').click()
            except Exception as e:
                print(f"Error: {e}")
                self.notify_user("Something went wrong :(")
                browser.close()
                return

            self.notify_user("Payment triggered! Please accept on your phone")

            try:
                with page.expect_navigation(timeout=25000):
                    pass
                self.notify_user("Payment finished!")
                browser.close()
                return
            except TimeoutError as e:
                self.notify_user("Timed out, payment not finished")
                browser.close()
                return

            browser.close()


if __name__ == "__main__":
    PythonTaskImplementation(sys.argv[1]).execute()