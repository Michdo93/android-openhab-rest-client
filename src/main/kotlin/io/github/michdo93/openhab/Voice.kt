// Generated from Voice.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
@file:Suppress("UNUSED_VARIABLE", "UNUSED_VALUE", "UNCHECKED_CAST", "RedundantSuppression", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNREACHABLE_CODE", "USELESS_CAST", "NAME_SHADOWING", "FunctionName", "LocalVariableName", "CanBeVal", "VARIABLE_WITH_REDUNDANT_INITIALIZER")

package io.github.michdo93.openhab

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * All public methods are `suspend` functions; they run the request on
 * [Dispatchers.IO] and may therefore be called from the main thread.
 *
 * @param client the [OpenHABClient] used for REST-API communication
 */
class Voice(private val client: OpenHABClient) {

    /**
     * Gets the default voice.
     *
     * @return A dictionary with the details of the default voice.
     */
    suspend fun getDefaultVoice(
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getDefaultVoiceBlocking(language)
    }

    private fun getDefaultVoiceBlocking(language: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/voice/defaultvoice", Py.m(Py.dict("Accept", "application/json", "Accept-Language", language)), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "No default voice was found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "No default voice was found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Start dialog processing for a given audio source.
     *
     * @param sourceID The ID of the audio source.
     * @param ksID The ID of the keyword spotter (optional).
     * @param sttID The ID of the speech-to-text system (optional).
     * @param ttsID The ID of the text-to-speech system (optional).
     * @param voiceID The ID of the voice (optional).
     * @param hliIDs A comma-separated list of interpreter IDs (optional).
     * @param sinkID The ID of the audio output (optional).
     * @param keyword The keyword used to start the dialog (optional).
     * @param listeningItem The name of the item to listen to (optional).
     * @param language The language for the request (optional).
     *
     * @return The response from the server.
     */
    suspend fun startDialog(
        sourceID: String,
        ksID: String? = null,
        sttID: String? = null,
        ttsID: String? = null,
        voiceID: String? = null,
        hliIDs: String? = null,
        sinkID: String? = null,
        keyword: String? = null,
        listeningItem: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        startDialogBlocking(sourceID, ksID, sttID, ttsID, voiceID, hliIDs, sinkID, keyword, listeningItem, language)
    }

    private fun startDialogBlocking(sourceID: String, ksID: String? = null, sttID: String? = null, ttsID: String? = null, voiceID: String? = null, hliIDs: String? = null, sinkID: String? = null, keyword: String? = null, listeningItem: String? = null, language: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post("/voice/dialog/start", Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language) else Py.dict())), null, Py.m(Py.dict("sourceId", sourceID, "ksId", ksID, "sttId", sttID, "ttsId", ttsID, "voiceId", voiceID, "hliIds", hliIDs, "sinkId", sinkID, "keyword", keyword, "listeningItem", listeningItem)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Services are missing or language is not supported by services or dialog processing is already started for the audio source.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Services are missing or language is not supported by services or dialog processing is already started for the audio source.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Stop dialog processing for a given audio source.
     *
     * @param sourceID The ID of the audio source.
     * @return The response from the server.
     */
    suspend fun stopDialog(
        sourceID: String,
    ): Any? = withContext(Dispatchers.IO) {
        stopDialogBlocking(sourceID)
    }

    private fun stopDialogBlocking(sourceID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post("/voice/dialog/stop", null, null, Py.m(Py.dict("sourceId", sourceID)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "No dialog processing is started for the audio source.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "No audio source was found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "No audio source was found.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "No dialog processing is started for the audio source.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Get the list of all interpreters.
     *
     * @param language The language for the request (optional).
     *
     * @return A list of interpreters if successful.
     */
    suspend fun getInterpreters(
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getInterpretersBlocking(language)
    }

    private fun getInterpretersBlocking(language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/voice/interpreters", Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (!Py.eq(status_code, 200)) {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Sends a text to the default human language interpreter.
     *
     * @param text The text to be interpreted.
     * @param language The language of the text.
     * @param IDs A list of interpreter IDs (optional).
     *
     * @return The response from the server.
     */
    suspend fun interpretText(
        text: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        interpretTextBlocking(text, language)
    }

    private fun interpretTextBlocking(text: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "text/plain", "Accept", "text/plain")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.post("/voice/interpreters", Py.m(headers), text, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", ("Interpretation exception occurs: " + Py.str(Py.str(err))))
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "No human language interpreter was found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "No human language interpreter was found.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Interpretation exception occurs.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets a single interpreter.
     *
     * @param interpreterID The ID of the interpreter.
     * @param language The language for the request (optional).
     *
     * @return The details of the interpreter.
     */
    suspend fun getInterpreter(
        interpreterID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getInterpreterBlocking(interpreterID, language)
    }

    private fun getInterpreterBlocking(interpreterID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/voice/interpreters/" + Py.str(interpreterID)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Interpreter not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Interpreter not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Sends a text to a given human language interpreter(s).
     *
     * @param text The text to be interpreted.
     * @param language The language of the text.
     * @param IDs A list of interpreter IDs.
     *
     * @return The response from the server.
     */
    suspend fun interpretTextBatch(
        text: String,
        IDs: List<*>,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        interpretTextBatchBlocking(text, IDs, language)
    }

    private fun interpretTextBatchBlocking(text: String, IDs: List<*>, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "text/plain", "Accept", "text/plain")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.post(("/voice/interpreters/" + Py.str(Py.join(",", IDs))), Py.m(headers), text, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", ("Interpretation exception occurs: " + Py.str(Py.str(err))))
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "No human language interpreter was found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "No human language interpreter was found.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Interpretation exception occurs.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Executes a simple dialog sequence without keyword spotting for a given audio source.
     *
     * @param sourceID The ID of the audio source.
     * @param sttID The ID of the speech-to-text system.
     * @param ttsID The ID of the text-to-speech system.
     * @param voiceID The ID of the voice.
     * @param hliIDs A list of interpreter IDs (optional).
     * @param sinkID The ID of the audio output (optional).
     * @param listeningItem The name of the item to listen to (optional).
     * @param language The language for the request (optional).
     *
     * @return The response from the server.
     */
    suspend fun listenAndAnswer(
        sourceID: String,
        sttID: String,
        ttsID: String,
        voiceID: String,
        hliIDs: List<*>,
        sinkID: String,
        listeningItem: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        listenAndAnswerBlocking(sourceID, sttID, ttsID, voiceID, hliIDs, sinkID, listeningItem, language)
    }

    private fun listenAndAnswerBlocking(sourceID: String, sttID: String, ttsID: String, voiceID: String, hliIDs: List<*>, sinkID: String, listeningItem: String, language: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post("/voice/listenandanswer", Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language) else Py.dict())), null, Py.m(Py.dict("sourceId", sourceID, "sttId", sttID, "ttsId", ttsID, "voiceId", voiceID, "hliIds", (if (Py.truthy(hliIDs)) Py.join(",", hliIDs) else null), "sinkId", sinkID, "listeningItem", listeningItem)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Services are missing or language is not supported by services or dialog processing is already started for the audio source.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "One of the given ids is wrong.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "One of the given ids is wrong.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Services are missing or language is not supported by services or dialog processing is already started for the audio source.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Speaks a given text with a given voice through the given audio sink.
     *
     * @param text The text to be spoken.
     * @param voiceID The ID of the voice.
     * @param sinkID The ID of the audio output.
     * @param volume The volume level (default: 100).
     *
     * @return The response from the server.
     */
    suspend fun sayText(
        text: String,
        voiceID: String,
        sinkID: String,
        volume: String? = "100",
    ): Any? = withContext(Dispatchers.IO) {
        sayTextBlocking(text, voiceID, sinkID, volume)
    }

    private fun sayTextBlocking(text: String, voiceID: String, sinkID: String, volume: String? = "100"): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post("/voice/say", Py.m(Py.dict("Content-Type", "text/plain")), text, Py.m(Py.dict("voiceid", voiceID, "sinkid", sinkID, "volume", volume)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (!Py.eq(status_code, 200)) {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Get the list of all voices.
     *
     * @return A list of voices if successful.
     */
    suspend fun getVoices(
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getVoicesBlocking(language)
    }

    private fun getVoicesBlocking(language: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/voice/voices", Py.m(Py.dict("Accept", "application/json", "Accept-Language", language)), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (!Py.eq(status_code, 200)) {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
