# Refactor plan

## Separate HTTP Client

DONE

## Use isAlive instead of Status Check

Problem:
Right now every device has a /status endpoint that gets checked every 20s, which wastes battery life.
The only reason we need it right now is because we want to know the state of the TRIGGER device (on or off).

Solution:
Instead of every device having a /status endpoint, have the TRIGGER device expose an /isEnabled endpoint and query that
periodically.
Apart from that, have each device expose an /isAlive endpoint, which merely returns a 200 OK response to indicate
the device is alive.
The /isAlive endpoint can be used by the server to make sure a device is alive for such devices that do not
periodically call other endpoints. For devices that call endpoints periodically, use the response from those
to determine whether a device is alive or dead.

Hold the liveness status of a device in memory. Refresh that liveness status on each interaction with the device.
If a device exists that is not interacted with often enough, check the last liveness update on each `refreshStatus` call
and for such devices that have not been checked for some time, call `isAlive` and update the liveness status.

That way we avoid making useless calls to devices.

If a device is dead, grey it out on UI side. For such device, query `isAlive` on every refresh.

## Trigger refreshStatus on a clock server-side instead of each getDevices call

Right now we trigger refreshStatus on each call to getDevices. Instead, make the server refresh status of devices
by itself every x seconds. A call to getDevices will simply return the results since the last refresh and not
trigger the refresh again.

## Fix up the database interface and make it more coherent

Do we need Option/Optional? Make the method names coherent. Maybe generalize some stuff that is stored and retrieved
in the exact same way but is just a different object. Make separate DB entities and make them editable?

