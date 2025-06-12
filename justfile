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

build-image:
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd edgar && docker build --build-arg JAR_SOURCE_PATH="./app.jar" --build-arg TASKS_PATH="./tasks/*.py" -t edgar .'

push:
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd edgar && docker tag edgar rskupnik/ether:edgar'
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd edgar && docker push rskupnik/ether:edgar'

# SSH into Raspberry Pi
ssh:
    ssh {{rpi_user}}@{{rpi_hostname}}

# Display logs of the running app
log:
    ssh {{rpi_user}}@{{rpi_hostname}} 'docker logs -f {{container_name}}'

# Build the app and upload it to Raspberry Pi, then build the Docker image there
deploy: build
    rsync Dockerfile {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/
    rsync docker-compose.yml {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/
    rsync -z --progress {{app_path}} {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/app.jar
    rsync -z {{tasks_path}}/*.py {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/tasks/

# Start the app
start:
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd {{rpi_workdir}} && docker compose up -d --build'

# Stop the app
stop:
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd {{rpi_workdir}} && docker compose down'

# Restart the app
restart: stop && start
    echo 'Restarting...'

# Reload the app (stop, deploy, start)
reload: stop deploy start

# Send a dashboard file
update-dashboard dashboard="demo":
    rsync app/src/main/resources/{{dashboard}}.json {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/dashboards/

# Update he device-config.json file
update-config:
    rsync app/src/main/resources/device-config.json {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/

# Provision the machine with necessary software (Docker)
provision:
    ssh {{rpi_user}}@{{rpi_hostname}} 'curl -fsSL https://get.docker.com -o get-docker.sh && sudo sh get-docker.sh && sudo usermod -aG docker pi'
