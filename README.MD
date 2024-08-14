# Edgar

Edgar is a custom home automation system that orchestrates various
self-made IOT devices and gives control of them over a dashboard.

## How it works

There is a backend component and a frontend component.

*Backend* is an HTTP app that is able to serve the dashboard and
orchestrate the devices. Devices can be registered with the backend
and are then displayed on the dashboard as configured.
The backend also proxies commands coming from the frontend to devices
and allows for re-discovery of devices that lost connection.

*Frontend* is a Vue app which displays a tile-based dashboard,
which allows for controlling the devices (tiles) in a graphical manner.
Basically a GUI.

## Domain

**Device** is an IoT Arduino device with custom code loaded onto it.
That code makes it able to register with the orchestrator and expose
*Endpoints* which can be used to control the device.

**Endpoint** is an HTTP endpoint exposed by a *Device* which denotes
a single functionality of that *Device*. For example, a *Device* that
enables/disables flow of AC current can have a /on and /off endpoint.

**Dashboard** is a definition of how the *Devices* and their *Endpoints*
are displayed in the GUI.

**Tile** is a single part of the *Dashboard* responsible for controlling
a single **Device**

## How to build and deploy

Look into the Makefile - everything is there.
If you want to build, deploy and observe the log - run `make ship && make log`

## How to test

Currently, only the core of the application has unit tests that describe
and test the expected behaviour.

HTTP layer is untested.

## Configuration

When the app runs, it accepts two command arguments: `--deviceConfig <file>` and `--dashboard <file`
(this one can be repeated).
Device config file, as the name suggests, configures the devices themselves - 
things like caching their responses, etc.
Dashboard file configures a dashboard (so the GUI). There can be multiple dashboards
(although this has not been tested in practice yet). If a dashboard file
is named `demo` it will be available under `/dashboards/demo`.
