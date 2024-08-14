function chooseRandomColor() {
    let colors = ["#a7226e", "#ec2049", "#f26b38", "#f7db4f", "#2f9599"];
    return colors[Math.floor(Math.random() * colors.length)];
}

function findTile(x, y, layout) {
    for (const tile of layout) {
        if (tile.x === x && tile.y === y) {
            return tile;
        }
    }
    return null;
}

function inBounds(tile, x, y) {
    switch (tile.type) {
        case "single":
            return tile.x === x && tile.y === y;
        case "vertical":
            return tile.x === x && (tile.y === y || y === tile.y + 1);
        case "horizontal":
            return tile.y === y && (tile.x === x || x === tile.x + 1);
        case "quad":
            return (x === tile.x || x === tile.x + 1) && (y === tile.y || y === tile.y + 1);
    }
}

function notOccupied(x, y, layout) {
    for (const tile of layout) {
        if (inBounds(tile, x, y)) {
            return false;
        }
    }
    return true;
}

function fillTiles(layout, boundX, boundY) {
    var output = [];
    for (var ix = 0; ix < boundX; ix++) {
        for (var iy = 0; iy < boundY; iy++) {
            var foundTile = findTile(ix, iy, layout);
            if (foundTile) {
                foundTile['clazz'] = determineClass(foundTile);
                foundTile['color'] = chooseRandomColor();
                output.push(foundTile);
            } else if (notOccupied(ix, iy, layout)) {
                output.push({
                    "x": ix,
                    "y": iy,
                    "type": "single",
                    "clazz": `${ix}_${iy}`,
                    "color": chooseRandomColor(),
                    "deviceType": 'none'
                });
            }
        }
    }
    return output;
}

function determineClass(tile) {
    switch (tile.type) {
        case "single":
            return `${tile.x}_${tile.y}`;
        case "horizontal":
            return `h_${tile.x}_${tile.y}`;
        case "vertical":
            return `v_${tile.x}_${tile.y}`;
        case "quad":
            return `q_${tile.x}_${tile.y}`;
    }
}

function processDevices(data) {
    let output = {};
    for (const device of data) {
        let endpoints = {};
        for (const endpoint of device.endpoints) {
            endpoints[endpoint.path] = endpoint;
        }
        device.endpoints = endpoints;

        output[device.id] = device;
    }
    return output;
}

/**
 * Produces an array of tiles that have the interval property set
 */
function findIntervalDevices(tiles, devices) {
    let output = [];
    for (const tile of tiles) {
        if (tile.endpoints) {
            for (const endpoint of tile.endpoints) {
                if (endpoint.activationType === "INTERVAL" && endpoint.properties && endpoint.properties.interval && endpoint.properties.interval !== 0) {
                    for (const [key, device] of Object.entries(devices)) {
                        if (device.id === tile.deviceId) {
                            output.push(tile);
                        }
                    }
                }
            }
        }
    }
    return output;
}

/**
 * Produce an object that contains the intervals and a list of tiles that should call on those intervals:
 * ex:
 * {
 *     60: {
 *         "myDeviceId": {
 *             ...
 *         },
 *         "myOtherDeviceId": {
 *             ...
 *         }
 *     }
 * }
 */
function createIntervalCheckups(intervalCheckups, tilesWithIntervals) {
    for (const tile of tilesWithIntervals) {
        for (const endpoint of tile.endpoints) {
            if (endpoint.activationType !== "INTERVAL")
                continue

            if (endpoint.properties && endpoint.properties.interval && endpoint.properties.interval !== 0) {
                let interval = endpoint.properties.interval
                if (!intervalCheckups[interval]) {
                    intervalCheckups[interval] = {};
                }
                intervalCheckups[interval][tile.deviceId] = tile;
            }
        }
    }
}

function cleanIntervalCheckups(intervalCheckups, devices) {
    let toBeRemoved = [];
    for (const [k, v] of Object.entries(intervalCheckups)) {
        for (const [kk, vv] of Object.entries(v)) {
            if (!devices[kk] || !devices[kk].responsive) {
                toBeRemoved.push(k);
            }
        }
    }

    for (const tbr of toBeRemoved) {
        delete intervalCheckups[tbr];
    }
}

function handleIntervalChecks(processedTiles, devices, intervalCheckups, intervals, tileProcessIntervalDataFuncs, responses) {
    // Find tiles with the interval property set
    let tilesWithIntervals = findIntervalDevices(processedTiles, devices);
    // console.log("TILES WITH INTERVALS")
    // console.log(tilesWithIntervals)

    // Populate the interval checkups object
    createIntervalCheckups(intervalCheckups, tilesWithIntervals)
    cleanIntervalCheckups(intervalCheckups, devices)

    // console.log("INTERVAL CHECKUPS")
    // console.log(intervalCheckups)

    /**
     * For all interval checkups, create an interval if none yet exists and remove one if no devices are present
     * {
     *     60: {
     *         "basementCamGas": {
     *             "deviceId": "basementCamGas",
     *             ...
     *         }
     *     }
     * }
     */
    for (const [key, value] of Object.entries(intervalCheckups)) {
        if (!intervals[key]) {
            // console.log("Adding interval: " + key);
            intervals[key] = setInterval(() => {
                for (const [id, tile] of Object.entries(value)) {
                    for (const endpoint of tile.endpoints) {
                        if (endpoint.activationType !== "INTERVAL")
                            continue

                        axios({
                            url: "http://" + window.location.host + "/devices/" + tile.deviceId + endpoint.id,
                            method: 'POST',
                            responseType: endpoint.responseType ? endpoint.responseType : "json"
                        }).then((response) => {
                            // console.log("Putting response to ["+tile.deviceId+"]["+endpoint.id+"]")
                            // console.log("The response is: ")
                            // console.log(response)
                            if (!responses[tile.deviceId]) {
                                responses[tile.deviceId] = {}
                            }
                            responses[tile.deviceId][endpoint.id] = tileProcessIntervalDataFuncs[tile.deviceType][endpoint.id](response);
                            // console.log("Responses object:")
                            // console.log(responses)
                        });
                    }
                }
            }, key * 1000);
        }
    }

    // Check to see if some intervals need to be removed
    let toBeRemoved = [];
    for (const [intervalValue, intervalInstance] of Object.entries(intervals)) {
        if (!intervalCheckups[intervalValue]) {
            clearInterval(intervalInstance);
            toBeRemoved.push(intervalValue);
        }
    }

    for (const tbr of toBeRemoved) {
        // console.log("Removing interval: " + tbr);
        delete intervals[tbr];
    }
}