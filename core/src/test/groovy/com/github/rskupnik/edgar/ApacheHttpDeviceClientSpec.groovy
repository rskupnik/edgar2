package com.github.rskupnik.edgar

import com.github.rskupnik.edgar.domain.CommandResponse
import com.github.rskupnik.edgar.domain.Device
import com.github.rskupnik.edgar.domain.DeviceEndpoint
import org.apache.http.HttpEntity
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.BasicHttpEntity
import spock.lang.Specification

class ApacheHttpDeviceClientSpec extends Specification {

    private final HttpClient statusClient = Mock(HttpClient)
    private final HttpClient commandClient = Mock(HttpClient)
    private final DeviceClient deviceClient = new ApacheHttpDeviceClient(statusClient, commandClient)

    def "should deem device alive when response is good"() {
        given:
        Device device = Dummies.DEVICE

        when:
        boolean isAlive = deviceClient.isAlive(device)

        then:
        1 * statusClient.execute({HttpGet req -> (req.getURI().toString() == "http://" + device.getIp() + "/isAlive") }) >> mockResponse(statusCode)
        isAlive == aliveStatus

        where:
        statusCode  | aliveStatus
        200         | true
        400         | false
        404         | false
        500         | false
        503         | false
    }

    def "should deem device dead when response is null"() {
        given:
        Device device = Dummies.DEVICE

        when:
        boolean isAlive = deviceClient.isAlive(device)

        then:
        1 * statusClient.execute({HttpGet req -> (req.getURI().toString() == "http://" + device.getIp() + "/isAlive") }) >> null
        !isAlive
    }

    def "should deem device dead when exception thrown by http client"() {
        given:
        Device device = Dummies.DEVICE

        when:
        boolean isAlive = deviceClient.isAlive(device)

        then:
        1 * statusClient.execute(_) >> {throw new IOException()}
        !isAlive
    }

    def "should send command"() {
        given:
        Device device = Dummies.DEVICE
        DeviceEndpoint endpoint = device.endpoints.get(0)

        when:
        CommandResponse response = deviceClient.sendCommand(device, endpoint, Collections.emptyMap())

        then:
        1 * commandClient.execute({ HttpRequestBase req ->
            req instanceof HttpPost
            req.URI.toString() == "http://" + device.getIp() + endpoint.getPath()
        }) >> mockResponse(200, "dummy body")
        response.statusCode == 200
        response.body.length == "dummy body".getBytes().length
    }

    def "should return error response on exception when sending"() {
        given:
        Device device = Dummies.DEVICE
        DeviceEndpoint endpoint = device.endpoints.get(0)

        when:
        CommandResponse response = deviceClient.sendCommand(device, endpoint, Collections.emptyMap())

        then:
        1 * commandClient.execute(_) >> {throw new IOException()}
        response.error
    }

    private CloseableHttpResponse mockResponse(int statusCode, String body = "body") {
        StatusLine statusLine = Mock(StatusLine)
        statusLine.getStatusCode() >> statusCode
        CloseableHttpResponse response = Mock(CloseableHttpResponse)
        response.getStatusLine() >> statusLine
        HttpEntity entity = new BasicHttpEntity()
        entity.setContent(new ByteArrayInputStream(body.getBytes()))
        entity.setContentLength(body.getBytes().length)
        response.getEntity() >> entity
        response.getAllHeaders() >> Arrays.asList()
        return response
    }
}
