package com.example.myapplication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Base64

class MachineTokenFetcher {

    suspend fun fetchMachineToken(): String {
        // Replace these variables with your actual values
        val CUSTOMER_ID = "12382455-81cc-32e1-1b06-8c7ea7c934e2"
        val CUSTOMER_CHILD_ID = "614c9fbd-6650-de32-4820-4194434af45e"
        val API_KEY = "25af7bf34a733a169767e2dcc23749f6faaae5d4aded7b1c76e67ed4ca529ef7040b18895484ef756de54d8443ac822536951d28ca41b5a99f7d5d771a3fcc47"

        // Create a JSON payload
        val jsonPayload = """
            {
                "permissions": {
                    "preset": "one-sdk",
                    "reference": "MTF"
                }
            }
        """.trimIndent()

        // Construct the URL for the token endpoint
        val tokenUrl = URL("https://backend.kycaml.uat.frankiefinancial.io/auth/v2/machine-session")

        return withContext(Dispatchers.IO) {
            try {
                // Open a connection
                val connection = tokenUrl.openConnection() as HttpURLConnection

                // Set the HTTP request method to POST
                connection.requestMethod = "POST"

                // Set request headers
                connection.setRequestProperty("authorization", "machine " + Base64.getEncoder().encodeToString("$CUSTOMER_ID:$CUSTOMER_CHILD_ID:$API_KEY".toByteArray()))
                connection.setRequestProperty("Content-Type", "application/json")

                // Enable input/output streams
                connection.doInput = true
                connection.doOutput = true

                // Write the JSON payload to the request body
                val outputStream = DataOutputStream(connection.outputStream)
                val postData = jsonPayload.toByteArray(StandardCharsets.UTF_8)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                // Get the response
                val responseCode = connection.responseCode

                // Read the response content
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    var line: String?
                    val response = StringBuilder()
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line).append('\n')
                    }
                    reader.close()

                    // The response contains the token
                    return@withContext response.toString()
                } else {
                    // Handle the error or status code
                    return@withContext "Error: $responseCode"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext "Error: ${e.message}"
            }
        }
    }
}
