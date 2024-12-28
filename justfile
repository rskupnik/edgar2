rpi_user := "pi"
rpi_hostname := "raspberrypi5.local"
rpi_workdir := "/home/pi/edgar"

app_path := "app/build/libs/app.jar"
tasks_path := "assistant/playwright-tasks"

image_name := "edgar"
container_name := "edgar"

build:
    ./gradlew build

ssh:
    ssh {{rpi_user}}@{{rpi_hostname}}

log:
    ssh {{rpi_user}}@{{rpi_hostname}} 'docker logs -f {{container_name}}'

deploy: build
    rsync Dockerfile {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/
    rsync -z --progress {{app_path}} {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/app.jar
    rsync -z {{tasks_path}}/*.py {{rpi_user}}@{{rpi_hostname}}:{{rpi_workdir}}/tasks/
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd {{rpi_workdir}} && docker build --build-arg JAR_SOURCE_PATH=./app.jar --build-arg TASKS_PATH=./tasks/*.py -t {{image_name}} .'

start:
    ssh {{rpi_user}}@{{rpi_hostname}} 'cd {{rpi_workdir}} && docker run -d --name {{container_name}} {{image_name}} > /dev/null 2>&1'

stop:
    ssh {{rpi_user}}@{{rpi_hostname}} 'docker stop {{container_name}} && docker rm {{container_name}} > /dev/null 2>&1'