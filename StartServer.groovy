import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder

import static DateUtil.parseDate

println "Starting server listening on port 9090"
HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0)
server.createContext("/", [
        handle:{
            handle(it)

        }
] as HttpHandler)
server.setExecutor(null)
server.start()

void handle(HttpExchange e) {
    try {
        doHandle(e)
    } catch (IllegalArgumentException ex) {
        renderErrorHtmlResponse(e, 400, ex.message)
    } catch (Exception ex) {
        renderErrorHtmlResponse(e, 500, "Unexpected error happened: ${ex.message}")
    }
}

def doHandle(HttpExchange e) {
    def path = e.requestURI.path
    if (path.matches("/checkAvailability/.*")) {
        def date = e.requestURI.path.replaceAll(/.*checkAvailability\//, '')
        println "Getting availability for date: $date"
        sendResponse(e, BookingService.checkAvailability(parseDate(date)))
    } else if (path.matches("/bookRoom")) {
        def text = e.requestBody.text
        println "Handling booking request: $text"
        sendResponse(e, BookingService.bookRoom(parseJson(text)))
    }
}


def parseJson(String json) {
    if (json.isAllWhitespace()) {
        throw new IllegalArgumentException("Empty json request. Make sure that the json is specified in POST request body")
    }
    try {
        new JsonSlurper().parseText(json)
    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid json request: $json. Make sure that key names and text values are quoted with \"")
    }
}

def renderErrorHtmlResponse(HttpExchange h, int status, String message) {
    h.responseHeaders.add("Content-Type", "text/html")
    h.responseHeaders.add("Access-Control-Allow-Origin", "*");

    def writer = new StringWriter()
    def html = new MarkupBuilder(writer)
    html.html {
        body {
            p {
                b("${status}.")
                mkp.yield " ${message}"
            }
        }
    }

    writeResponse(h, writer.toString(), status)
}

def writeResponse(HttpExchange e, String response, final int status) {
    e.sendResponseHeaders(status, response.length());

    OutputStream os = e.getResponseBody()
    os.write(response.getBytes())
    os.close()
}

def sendResponse(HttpExchange e, def data) {
    e.responseHeaders.add("Content-Type", "application/json")
    e.responseHeaders.add("Access-Control-Allow-Origin", "*");

    String response = new JsonBuilder(data).toString();

    writeResponse(e, response, 200)
}
