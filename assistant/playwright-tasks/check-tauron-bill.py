from playwright.sync_api import sync_playwright

def main():
    """
    Main function to scrape stats and generate SQL statements.
    """
    url = "https://logowanie.tauron.pl/login"

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        page.goto(url, wait_until="domcontentloaded")
        label_text = page.locator("label[for='username1']").text_content()
        print(f"Label text: {label_text}")

        browser.close()


if __name__ == "__main__":
    main()
