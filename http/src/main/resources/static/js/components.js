Vue.component('old-toggle', {
    props: ['endpoint', 'device'],
    template: `
        <div class="singleEndpoint">
            <label class="toggle-control">
                <input type="checkbox" v-on:change="$root.sendCommand(device, endpoint)" v-model="device.status.endpoints[endpoint.path].enabled"/>
                <span class="control"></span>
            </label>
            <span class="endpointToggleDescription">{{ endpoint.path }}</span>
            {{ device.status.endpoints[endpoint.path].enabled }}
        </div>
    `
})

Vue.component('toggle', {
    props: ['endpoint', 'device'],
    template: `
        <div class="singleTile">
            <h2>Title</h2>
            <input type="checkbox" v-on:change="$root.sendCommand(device, endpoint)"/>
        </div>
    `
})