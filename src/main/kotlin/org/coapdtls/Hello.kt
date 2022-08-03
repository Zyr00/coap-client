package org.coapdtls

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.Utils
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.elements.DtlsEndpointContext
import org.eclipse.californium.elements.config.Configuration
import org.eclipse.californium.scandium.DTLSConnector
import org.eclipse.californium.scandium.config.DtlsConfig
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore
import java.net.URI

fun main() {
    println("Starting dtls coap server ... ")
    val serverUri = URI("coaps://127.0.0.1:5684/hello-world")
    val client = CoapClient(serverUri)

    DtlsConfig.register()

    val configuration = Configuration.getStandard()
    val builder = DtlsConnectorConfig.builder(configuration)
        .setAdvancedPskStore(AdvancedSinglePskStore("FF:FF:FF:FF:FF", "maneljacacantas".toByteArray()))

    val coap = CoapEndpoint.builder().setConfiguration(configuration).setConnector(DTLSConnector(builder.build()))
    client.endpoint = coap.build()

    println("get request")
    printResponse(client.get());

    println("post request")
    val resp = client.post("{ \"temperature\": 23 }".toByteArray(), MediaTypeRegistry.APPLICATION_JSON)
    printResponse(resp)

    client.shutdown()
}

fun printResponse(response: CoapResponse?) {
    if (response != null) {
        println("${response.code} - ${response.code.name}")
        println("${response.options}")
        println(response.responseText)
        println("Advanced: ")
        val context = response.advanced().sourceContext
        val identity = context.peerIdentity
        if (identity != null)
            println(context.peerIdentity)
        else
            println("Anonymous")
        println(context.get(DtlsEndpointContext.KEY_CIPHER))
        println(Utils.prettyPrint(response))
    } else
        println("No response received.")

    println("\n")
}