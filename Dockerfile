FROM eclipse-temurin:21-jdk AS base

# Install Python 3.10
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3.10 \
    python3-venv \
    python3-pip \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && java -version && python3 --version

# Setup venv and playwright
WORKDIR /app/tasks
RUN python3 -m venv venv && \
    ./venv/bin/pip install --upgrade pip && \
    ./venv/bin/pip install --no-cache-dir playwright && \
    ./venv/bin/playwright install-deps && \
    ./venv/bin/playwright install chromium

# Copy the dynamic parts
ARG JAR_SOURCE_PATH=app/build/libs/app.jar
ARG TASKS_PATH=assistant/playwright-tasks/*.py

COPY ${TASKS_PATH} /app/tasks/
COPY ${JAR_SOURCE_PATH} /app/app.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD []
