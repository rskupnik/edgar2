import os

from playwright.sync_api import sync_playwright

def main():
    url = "https://logowanie.tauron.pl/login"
    pipe_path = "/tmp/my_pipe"

    # Ensure the named pipe exists
    if not os.path.exists(pipe_path):
        os.mkfifo(pipe_path)

    # Signal to Java that Python is ready
    print("Python: Signaling ready to Java...")
    with open(pipe_path, "w") as pipe:
        pipe.write("READY\n")

    print("Python: Waiting for label input from Java...")

    # Open the named pipe and read the label ID from Java
    with open(pipe_path, "r") as pipe:
        label_id = pipe.read().strip()
        print(f"Python: Received label ID from Java: {label_id}")

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        page.goto(url, wait_until="domcontentloaded")
        label_text = page.locator(f"label[for='{label_id}']").text_content()
        print(f"Label text: {label_text}")

        browser.close()

        # Signal to Java that Python is ready
        print("Python: Sending output to Java...")
        with open(pipe_path, "w") as pipe:
            pipe.write(f"{label_text}\n")


if __name__ == "__main__":
    main()
