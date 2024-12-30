rpi_user := "pi"
rpi_hostname := "raspberrypi5.local"
rpi_workdir := "/home/pi/edgar"

app_path := "app/build/libs/app.jar"
tasks_path := "assistant/playwright-tasks"

image_name := "edgar"
container_name := "edgar"

# Build the entire app
build:
    ./gradlew build

# SSH into Raspberry Pi
ssh:
    ssh {{rpi_user}}@{{rpi_hostname}}

# Display logs of the running app
log:
    ssh {{rpi_user}}@{{rpi_hostname}} 'docker logs -f {{container_name}}'

# Build the app and upload it to Raspberry Pi, then build the Docker image there
deploy: build
    rsync Dockerfile {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/
    rsync -z --progress {{app_path}} {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/app.jar
    rsync -z {{tasks_path}}/*.py {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/tasks/
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd {{rpi_workdir}} && docker build --build-arg JAR_SOURCE_PATH=./app.jar --build-arg TASKS_PATH=./tasks/*.py -t {{image_name}} .'

# Start the app
start deviceConfig="device-config.json" dashboard="demo.json":
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd {{rpi_workdir}} && docker run -d --name {{container_name}} -p 8080:8080 {{image_name}} --deviceConfig {{rpi_workdir}}/{{deviceConfig}} --dashboard {{rpi_workdir}}/dashboards/{{dashboard}} > /dev/null 2>&1'

# Stop the app
stop:
    ssh {{rpi_user}}@{{rpi_hostname}} 'docker stop {{container_name}} && docker rm {{container_name}} > /dev/null 2>&1'

# Restart the app
restart: stop && start
    echo 'Restarting...'

# Send a dashboard file
update-dashboard dashboard="demo":
    rsync app/src/main/resources/{{dashboard}}.json {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/dashboards/

# Update he device-config.json file
update-config:
    rsync app/src/main/resources/device-config.json {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/

# Provision the machine with necessary software (Docker)
provision:
    ssh {{rpi_user}}@{{rpi_hostname}} 'curl -fsSL https://get.docker.com -o get-docker.sh && sudo sh get-docker.sh && sudo usermod -aG docker pi'
