function chooseRandomColor() {
    //let colors = ["#ff9966", "#ffcc99", "#ff9999", "#8be9fe", "#99ffff"];
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

function getTile(x, y, type) {
    switch (type) {
        case "single":
            return document.getElementsByClassName(`${x}_${y}`)[0];
        case "horizontal":
            return document.getElementsByClassName(`h_${x}_${y}`)[0];
        case "vertical":
            return document.getElementsByClassName(`v_${x}_${y}`)[0];
        case "quad":
            return document.getElementsByClassName(`q_${x}_${y}`)[0];
        default:
            return null;
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

function findIntervalDevices(tiles, devices) {
    let output = [];
    for (const tile of tiles) {
        if (tile.properties && tile.properties.interval && tile.properties.interval !== 0) {
            for (const [key, device] of Object.entries(devices)) {
                if (device.id === tile.deviceId) {
                    output.push(tile);
                }
            }
        }
    }
    return output;
}

function createIntervalCheckups(intervalCheckups, tilesWithIntervals) {
    for (const tile of tilesWithIntervals) {
        let interval = tile.properties.interval;
        if (!intervalCheckups[interval]) {
            intervalCheckups[interval] = {};
        }
        intervalCheckups[interval][tile.deviceId] = tile;
    }
}

function handleIntervalChecks(processedTiles, devices, intervalCheckups, intervals, tileProcessIntervalDataFuncs, responses) {
    // Figure out if some intervals need to be set
    let tilesWithIntervals = findIntervalDevices(processedTiles, devices);
    console.log(tilesWithIntervals)

    // TODO: Add the endpoint to be called on the specific interval
    createIntervalCheckups(intervalCheckups, tilesWithIntervals)
    console.log(intervalCheckups)

    /**
     * {
     *     60: {
     *         "basementCamGas": {
     *             "deviceId": "basementCamGas",
     *             ...
     *         }
     *     }
     * }
     */
    // TODO: Create an interval if not yet created
    for (const [key, value] of Object.entries(intervalCheckups)) {
        if (!intervals[key]) {
            console.log("Adding interval: " + key);
            intervals[key] = setInterval(() => {
                // TODO: Implement the interval so it calls the endpoints
                for (const [id, tile] of Object.entries(value)) {
                    console.log("Sending request to " + id);
                    axios({
                        url: "http://" + window.location.host + "/devices/" + tile.deviceId + tile.endpointId,
                        method: 'POST',
                        responseType: 'arraybuffer' // TODO: This isn't always an arraybuffer
                    }).then((response) => {
                        responses[tile.deviceId] = tileProcessIntervalDataFuncs[tile.deviceType](response);
                    });
                }
            }, key * 1000);
        }
    }
}