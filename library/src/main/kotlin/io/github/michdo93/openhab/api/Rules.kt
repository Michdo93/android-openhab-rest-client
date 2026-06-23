package io.github.michdo93.openhab.api

import io.github.michdo93.openhab.client.OpenHABClient

/** openHAB Rules REST API. */
class Rules(private val client: OpenHABClient) {

    suspend fun getRules(prefix: String? = null, tags: String? = null, summary: Boolean = false, staticDataOnly: Boolean = false): String =
        client.get("/rules", mapOf("prefix" to prefix, "tags" to tags, "summary" to summary.toString(), "staticDataOnly" to staticDataOnly.toString()))

    suspend fun createRule(json: String): String  = client.post("/rules", body = json)
    suspend fun getRule(uid: String): String      = client.get("/rules/$uid")
    suspend fun updateRule(uid: String, json: String): String = client.put("/rules/$uid", body = json)
    suspend fun deleteRule(uid: String): String   = client.delete("/rules/$uid")

    suspend fun getModule(uid: String, cat: String, mid: String): String =
        client.get("/rules/$uid/$cat/$mid")

    suspend fun getModuleConfig(uid: String, cat: String, mid: String): String =
        client.get("/rules/$uid/$cat/$mid/config")

    suspend fun getModuleConfigParam(uid: String, cat: String, mid: String, param: String): String =
        client.get("/rules/$uid/$cat/$mid/config/$param")

    suspend fun setModuleConfigParam(uid: String, cat: String, mid: String, param: String, value: String): String =
        client.put("/rules/$uid/$cat/$mid/config/$param", body = value, contentType = "text/plain")

    suspend fun getActions(uid: String): String    = client.get("/rules/$uid/actions")
    suspend fun getConditions(uid: String): String = client.get("/rules/$uid/conditions")
    suspend fun getTriggers(uid: String): String   = client.get("/rules/$uid/triggers")
    suspend fun getConfiguration(uid: String): String = client.get("/rules/$uid/config")

    suspend fun updateConfiguration(uid: String, configJson: String): String =
        client.put("/rules/$uid/config", body = configJson)

    suspend fun setRuleState(uid: String, enable: Boolean): String =
        client.post("/rules/$uid/enable", body = enable.toString(), contentType = "text/plain")

    suspend fun enable(uid: String): String  = setRuleState(uid, true)
    suspend fun disable(uid: String): String = setRuleState(uid, false)

    suspend fun runNow(uid: String, contextJson: String? = null): String =
        client.post("/rules/$uid/runnow", body = contextJson)

    suspend fun simulateSchedule(from: String, until: String): String =
        client.get("/rules/schedule/simulations", mapOf("from" to from, "until" to until))
}
