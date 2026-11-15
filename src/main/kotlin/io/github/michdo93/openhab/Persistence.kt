// Generated from Persistence.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Persistence(private val client: OpenHABClient) {

    /**
     * Gets a list of persistence services.
     *
     * @return A list of persistence services with IDs, labels, and types.
     */
    suspend fun getServices(
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getServicesBlocking(language)
    }

    private fun getServicesBlocking(language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/persistence", Py.m(headers), null)
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
     * Gets a persistence service configuration.
     *
     * @param serviceID The ID of the persistence service.
     *
     * @return The configuration of the service.
     */
    suspend fun getServiceConfiguration(
        serviceID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getServiceConfigurationBlocking(serviceID)
    }

    private fun getServiceConfigurationBlocking(serviceID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/persistence/" + Py.str(serviceID)), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Service configuration not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Service configuration not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Sets a persistence service configuration.
     *
     * @param serviceID The ID of the persistence service.
     * @param config The configuration data.
     *
     * @return The response from the API after modification.
     */
    suspend fun setServiceConfiguration(
        serviceID: String,
        config: Map<String, Any?>,
    ): Any? = withContext(Dispatchers.IO) {
        setServiceConfigurationBlocking(serviceID, config)
    }

    private fun setServiceConfigurationBlocking(serviceID: String, config: Map<String, Any?>): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            Py.set(config, "serviceId", serviceID)
            response = client.put(("/persistence/" + Py.str(serviceID)), Py.m(Py.dict("Content-Type", "application/json", "Accept", "application/json")), Py.dumps(config), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Payload invalid.")
            } else if (Py.eq(status_code, 405)) {
                return Py.dict("error", "PersistenceServiceConfiguration not editable.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 201)) {
            return Py.dict("message", "PersistenceServiceConfiguration created.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Payload invalid.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "PersistenceServiceConfiguration not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Deletes a persistence service configuration.
     *
     * @param serviceID The ID of the persistence service.
     *
     * @return The response from the API after deleting the configuration.
     */
    suspend fun deleteServiceConfiguration(
        serviceID: String,
    ): Any? = withContext(Dispatchers.IO) {
        deleteServiceConfigurationBlocking(serviceID)
    }

    private fun deleteServiceConfigurationBlocking(serviceID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/persistence/" + Py.str(serviceID)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Payload invalid.")
            } else if (Py.eq(status_code, 405)) {
                return Py.dict("error", "PersistenceServiceConfiguration not editable.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Payload invalid.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "PersistenceServiceConfiguration not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets a list of items available via a specific persistence service.
     *
     * @param serviceID Optional; The ID of the persistence service (default service if omitted).
     * @param itemName Optional; Only return information for this item.
     *
     * @return A list of items with their last and earliest timestamps.
     */
    suspend fun getItemsFromService(
        serviceID: String? = null,
        itemName: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getItemsFromServiceBlocking(serviceID, itemName)
    }

    private fun getItemsFromServiceBlocking(serviceID: String? = null, itemName: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/persistence/items", Py.m(Py.dict("Accept", "application/json")), Py.m(Py.dict("serviceId", serviceID, "itemName", itemName)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Unknown persistence service.")
            }
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "The persistence service does not provide item information.")
            }
            return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets item persistence data from the persistence service.
     *
     * @param itemName The name of the item.
     * @param serviceID Optional; The ID of the persistence service. If omitted, the default service is used.
     * @param startTime Optional; Start time (ISO 8601, e.g. "2026-10-30T00:00:00.000Z"). Defaults to 1 day before `endTime`.
     * @param endTime Optional; End time (ISO 8601). Defaults to the current time.
     * @param page Optional; Page number, starting at 0. Paging is only active if `pageLength` is set as well.
     * @param pageLength Optional; Number of data points per page. Without it, all data points are returned.
     * @param boundary Optional; Also return the values just before and after the time range (default: False).
     * @param itemState Optional; Add the current item state at the end of the series (default: False).
     * @param displayState Optional; Add a formatted display state to every data point (openHAB 5.0+).
     * @param language Optional; Value for the "Accept-Language" header.
     *
     * @return The retrieved data points of the item.
     */
    suspend fun getItemPersistenceData(
        itemName: String,
        serviceID: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        page: Int? = null,
        pageLength: Int? = null,
        boundary: Boolean? = false,
        itemState: Boolean? = false,
        displayState: Boolean? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getItemPersistenceDataBlocking(itemName, serviceID, startTime, endTime, page, pageLength, boundary, itemState, displayState, language)
    }

    private fun getItemPersistenceDataBlocking(itemName: String, serviceID: String? = null, startTime: String? = null, endTime: String? = null, page: Int? = null, pageLength: Int? = null, boundary: Boolean? = false, itemState: Boolean? = false, displayState: Boolean? = null, language: String? = null): Any? {
        var page_: Any? = page
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        if (Py.truthy(Py.and((pageLength !== null), (page_ === null)))) {
            page_ = 0
        }
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/persistence/items/" + Py.str(itemName)), Py.m(headers), Py.m(Py.dict("serviceId", serviceID, "starttime", startTime, "endtime", endTime, "page", page_, "pagelength", pageLength, "boundary", boundary, "itemState", itemState, "displayState", displayState)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Unknown Item or persistence service.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Unknown Item or persistence service.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Stores item persistence data into the persistence service.
     *
     * @param serviceID The ID of the persistence service.
     * @param itemName The name of the item.
     * @param time The time of the storage.
     * @param state The state of the item to be stored.
     *
     * @return The response from the API after storing the data.
     */
    suspend fun storeItemData(
        itemName: String,
        time: String,
        state: String,
        serviceID: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        storeItemDataBlocking(itemName, time, state, serviceID)
    }

    private fun storeItemDataBlocking(itemName: String, time: String, state: String, serviceID: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/persistence/items/" + Py.str(itemName)), null, null, Py.m(Py.dict("serviceId", serviceID, "time", time, "state", state)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Unknown Item or persistence service.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Unknown Item or persistence service.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Deletes item persistence data from a specific persistence service in a given time range.
     *
     * @param serviceID The ID of the persistence service.
     * @param itemName The name of the item.
     * @param startTime The start time of the data to be deleted.
     * @param endTime The end time of the data to be deleted.
     *
     * @return The response from the API after deleting the data.
     */
    suspend fun deleteItemData(
        itemName: String,
        startTime: String,
        endTime: String,
        serviceID: String,
    ): Any? = withContext(Dispatchers.IO) {
        deleteItemDataBlocking(itemName, startTime, endTime, serviceID)
    }

    private fun deleteItemDataBlocking(itemName: String, startTime: String, endTime: String, serviceID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/persistence/items/" + Py.str(itemName)), Py.m(Py.dict("Accept", "application/json")), null, Py.m(Py.dict("serviceId", serviceID, "starttime", startTime, "endtime", endTime)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", ("Invalid filter parameters or service not modifiable: " + Py.str(Py.str(err))))
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Unknown persistence service.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Unknown persistence service.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Invalid filter parameters.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
