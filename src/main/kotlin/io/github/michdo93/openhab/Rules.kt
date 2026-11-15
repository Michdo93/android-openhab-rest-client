// Generated from Rules.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Rules(private val client: OpenHABClient) {

    /**
     * Get available rules, optionally filtered by tags and/or prefix.
     *
     * @param prefix Optional prefix to filter the results.
     * @param tags Optional tag array to filter the results.
     * @param summary If true, only summary fields will be returned.
     * @param staticDataOnly If true, only static data will be returned.
     *
     * @return A list of rules (JSON objects).
     */
    suspend fun getRules(
        prefix: String? = null,
        tags: Any? = null,
        summary: Boolean? = false,
        staticDataOnly: Boolean? = false,
    ): Any? = withContext(Dispatchers.IO) {
        getRulesBlocking(prefix, tags, summary, staticDataOnly)
    }

    private fun getRulesBlocking(prefix: String? = null, tags: Any? = null, summary: Boolean? = false, staticDataOnly: Boolean? = false): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/rules", Py.m(Py.dict("Accept", "application/json")), Py.m(Py.dict("prefix", prefix, "tags", tags, "summary", summary, "staticDataOnly", staticDataOnly)))
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
     * Creates a rule.
     *
     * @param ruleData The rule data to be created (as a dictionary).
     *
     * @return The created rule (JSON).
     */
    suspend fun createRule(
        ruleData: Map<String, Any?>,
    ): Any? = withContext(Dispatchers.IO) {
        createRuleBlocking(ruleData)
    }

    private fun createRuleBlocking(ruleData: Map<String, Any?>): Any? {
        var location: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post("/rules", Py.m(Py.dict("Content-Type", "application/json")), Py.dumps(ruleData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
                location = Py.getOr(response, "location", null)
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Creation refused: Missing required parameter.")
            } else if (Py.eq(status_code, 409)) {
                return Py.dict("error", "Creation refused: Rule with the same UID already exists.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 201)) {
            return Py.dict("message", "Rule successfully created.", "location", (if (Py.truthy(location)) location else "No Location headers provided."))
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Creation refused: Missing required parameter.")
        } else if (Py.eq(status_code, 409)) {
            return Py.dict("error", "Creation refused: Rule with the same UID already exists.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the rule corresponding to the given UID.
     *
     * @param ruleUID The UID of the rule to retrieve.
     *
     * @return The rule object (JSON).
     */
    suspend fun getRule(
        ruleUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getRuleBlocking(ruleUID)
    }

    private fun getRuleBlocking(ruleUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/rules/" + Py.str(ruleUID)), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Updates an existing rule corresponding to the given UID.
     *
     * @param ruleUID The UID of the rule to update.
     * @param ruleData The new rule data (as a dictionary).
     *
     * @return The updated rule (JSON).
     */
    suspend fun updateRule(
        ruleUID: String,
        ruleData: Map<String, Any?>,
    ): Any? = withContext(Dispatchers.IO) {
        updateRuleBlocking(ruleUID, ruleData)
    }

    private fun updateRuleBlocking(ruleUID: String, ruleData: Map<String, Any?>): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/rules/" + Py.str(ruleUID)), Py.m(Py.dict("Content-Type", "application/json")), Py.dumps(ruleData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Removes an existing rule corresponding to the given UID.
     *
     * @param ruleUID The UID of the rule to delete.
     *
     * @return The API response (status code).
     */
    suspend fun deleteRule(
        ruleUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        deleteRuleBlocking(ruleUID)
    }

    private fun deleteRuleBlocking(ruleUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/rules/" + Py.str(ruleUID)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the rule's module corresponding to the given category and ID.
     *
     * @param ruleUID The UID of the rule.
     * @param moduleCategory The category of the module.
     * @param moduleID The ID of the module.
     *
     * @return The module (JSON).
     */
    suspend fun getModule(
        ruleUID: String,
        moduleCategory: String,
        moduleID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getModuleBlocking(ruleUID, moduleCategory, moduleID)
    }

    private fun getModuleBlocking(ruleUID: String, moduleCategory: String, moduleID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/rules/" + Py.str(ruleUID) + "/" + Py.str(moduleCategory) + "/" + Py.str(moduleID)), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found or does not have a module with such Category and ID.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found or does not have a module with such Category and ID.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the module's configuration.
     *
     * @param ruleUID The UID of the rule.
     * @param moduleCategory The category of the module.
     * @param moduleID The ID of the module.
     *
     * @return The module configuration (JSON).
     */
    suspend fun getModuleConfig(
        ruleUID: String,
        moduleCategory: String,
        moduleID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getModuleConfigBlocking(ruleUID, moduleCategory, moduleID)
    }

    private fun getModuleConfigBlocking(ruleUID: String, moduleCategory: String, moduleID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/rules/" + Py.str(ruleUID) + "/" + Py.str(moduleCategory) + "/" + Py.str(moduleID) + "/config"), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found or does not have a module with such Category and ID.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found or does not have a module with such Category and ID.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the module's configuration parameter.
     *
     * @param ruleUID The UID of the rule.
     * @param moduleCategory The category of the module.
     * @param moduleID The ID of the module.
     * @param param The name of the configuration parameter.
     *
     * @return The configuration parameter value (JSON).
     */
    suspend fun getModuleConfigParam(
        ruleUID: String,
        moduleCategory: String,
        moduleID: String,
        param: String,
    ): Any? = withContext(Dispatchers.IO) {
        getModuleConfigParamBlocking(ruleUID, moduleCategory, moduleID, param)
    }

    private fun getModuleConfigParamBlocking(ruleUID: String, moduleCategory: String, moduleID: String, param: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/rules/" + Py.str(ruleUID) + "/" + Py.str(moduleCategory) + "/" + Py.str(moduleID) + "/config/" + Py.str(param)), Py.m(Py.dict("Accept", "text/plain")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found or does not have a module with such Category and ID.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found or does not have a module with such Category and ID.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Sets the module's configuration parameter value.
     *
     * @param ruleUID The UID of the rule.
     * @param moduleCategory The category of the module.
     * @param moduleID The ID of the module.
     * @param param The name of the configuration parameter.
     * @param value The value to set for the configuration parameter.
     *
     * @return The API response (status code).
     */
    suspend fun setModuleConfigParam(
        ruleUID: String,
        moduleCategory: String,
        moduleID: String,
        param: String,
        value: String,
    ): Any? = withContext(Dispatchers.IO) {
        setModuleConfigParamBlocking(ruleUID, moduleCategory, moduleID, param, value)
    }

    private fun setModuleConfigParamBlocking(ruleUID: String, moduleCategory: String, moduleID: String, param: String, value: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/rules/" + Py.str(ruleUID) + "/" + Py.str(moduleCategory) + "/" + Py.str(moduleID) + "/config/" + Py.str(param)), Py.m(Py.dict("Content-Type", "text/plain")), Py.str(value), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found or does not have a module with such Category and ID.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found or does not have a module with such Category and ID.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the rule actions.
     *
     * @param ruleUID The UID of the rule.
     *
     * @return A list of actions (JSON).
     */
    suspend fun getActions(
        ruleUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getActionsBlocking(ruleUID)
    }

    private fun getActionsBlocking(ruleUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/rules/" + Py.str(ruleUID) + "/actions"), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the rule conditions.
     *
     * @param ruleUID The UID of the rule.
     *
     * @return A list of conditions (JSON).
     */
    suspend fun getConditions(
        ruleUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getConditionsBlocking(ruleUID)
    }

    private fun getConditionsBlocking(ruleUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/rules/" + Py.str(ruleUID) + "/conditions"), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the rule configuration values.
     *
     * @param ruleUID The UID of the rule.
     *
     * @return The configuration of the rule (JSON).
     */
    suspend fun getConfiguration(
        ruleUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getConfigurationBlocking(ruleUID)
    }

    private fun getConfigurationBlocking(ruleUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/rules/" + Py.str(ruleUID) + "/config"), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Sets the rule configuration values.
     *
     * @param ruleUID The UID of the rule.
     * @param configData The new configuration data (as a dictionary).
     *
     * @return The updated configuration (JSON).
     */
    suspend fun updateConfiguration(
        ruleUID: String,
        configData: Map<String, Any?>,
    ): Any? = withContext(Dispatchers.IO) {
        updateConfigurationBlocking(ruleUID, configData)
    }

    private fun updateConfigurationBlocking(ruleUID: String, configData: Map<String, Any?>): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/rules/" + Py.str(ruleUID) + "/config"), Py.m(Py.dict("Content-Type", "application/json")), Py.dumps(configData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Sets the rule configuration values. Activates or deactivates the rule.
     *
     * @param ruleUID The UID of the rule.
     * @param enable If true, the rule will be activated. If false, the rule will be deactivated.
     *
     * @return The API response (status code).
     */
    suspend fun setRuleState(
        ruleUID: String,
        enable: Boolean,
    ): Any? = withContext(Dispatchers.IO) {
        setRuleStateBlocking(ruleUID, enable)
    }

    private fun setRuleStateBlocking(ruleUID: String, enable: Boolean): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post(("/rules/" + Py.str(ruleUID) + "/enable"), Py.m(Py.dict("Content-Type", "text/plain")), (if (Py.truthy(enable)) "true" else "false"), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    suspend fun enable(
        ruleUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        enableBlocking(ruleUID)
    }

    private fun enableBlocking(ruleUID: String): Any? {
        return setRuleStateBlocking(ruleUID = ruleUID, enable = true)
    }

    suspend fun disable(
        ruleUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        disableBlocking(ruleUID)
    }

    private fun disableBlocking(ruleUID: String): Any? {
        return setRuleStateBlocking(ruleUID = ruleUID, enable = false)
    }

    /**
     * Executes the rule's actions immediately.
     *
     * @param ruleUID The UID of the rule.
     * @param contextData Optional context data for executing the rule.
     *
     * @return The API response (status code).
     */
    suspend fun runNow(
        ruleUID: String,
        contextData: Map<String, Any?>? = null,
    ): Any? = withContext(Dispatchers.IO) {
        runNowBlocking(ruleUID, contextData)
    }

    private fun runNowBlocking(ruleUID: String, contextData: Map<String, Any?>? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post(("/rules/" + Py.str(ruleUID) + "/runnow"), Py.m(Py.dict("Content-Type", "application/json", "Accept", "application/json")), Py.dumps(Py.or(contextData, Py.dict())), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the rule triggers.
     *
     * @param ruleUID The UID of the rule.
     *
     * @return A list of triggers (JSON).
     */
    suspend fun getTriggers(
        ruleUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getTriggersBlocking(ruleUID)
    }

    private fun getTriggersBlocking(ruleUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/rules/" + Py.str(ruleUID) + "/triggers"), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Rule corresponding to the given UID was not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Rule corresponding to the given UID was not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Simulates the executions of rules filtered by tag 'Schedule' within the given times.
     *
     * @param fromTime Simulates the executions of rules filtered by tag 'Schedule' within the given times.
     * @param untilTime Simulates the executions of rules filtered by tag 'Schedule' within the given times.
     *
     * @return The simulation results (JSON).
     */
    suspend fun simulateSchedule(
        fromTime: String,
        untilTime: String,
    ): Any? = withContext(Dispatchers.IO) {
        simulateScheduleBlocking(fromTime, untilTime)
    }

    private fun simulateScheduleBlocking(fromTime: String, untilTime: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/rules/schedule/simulations", Py.m(Py.dict("Accept", "application/json")), Py.m(Py.dict("from", fromTime, "until", untilTime)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "The max. simulation duration of 180 days is exceeded.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "The max. simulation duration of 180 days is exceeded.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
