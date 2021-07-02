// Randomize colors
function randomizeColors() {
    let colors = ["#ff9966", "#ffcc99", "#ff9999", "#8be9fe", "#99ffff"];
    let tiles = document.getElementsByClassName("tile");
    for (var i = 0; i < tiles.length; i++) {
        let tile = tiles.item(i);
        const random_color = colors[Math.floor(Math.random() * colors.length)];
        tile.style.backgroundColor = random_color;
    }
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
                output.push(foundTile);
            } else if (notOccupied(ix, iy, layout)) {
                output.push({
                    "x": ix,
                    "y": iy,
                    "type": "single"
                });
            }
        }
    }
    return output;
}

/**
 * [
 *  {
 *      "x": 0,
 *      "y": 0,
 *      "type": single|vertical|horizontal|quad
 *  }
 * ]
 * @param layout
 */
function placeTiles(layout) {
    let container = document.getElementById("container");
    for (const tile of layout) {
        let div = document.createElement('div');
        switch (tile.type) {
            case "single":
                div.className = `tile ${tile.x}_${tile.y}`;
                break;
            case "horizontal":
                div.className = `tile h_${tile.x}_${tile.y}`;
                break;
            case "vertical":
                div.className = `tile v_${tile.x}_${tile.y}`;
                break;
            case "quad":
                div.className = `tile q_${tile.x}_${tile.y}`;
                break;
        }
        container.appendChild(div);
    }
}