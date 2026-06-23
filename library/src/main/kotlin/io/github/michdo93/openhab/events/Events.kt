package io.github.michdo93.openhab.events

import io.github.michdo93.openhab.client.OpenHABClient
import io.github.michdo93.openhab.client.SseSession

/** openHAB General Events SSE API. */
class Events(private val client: OpenHABClient) {
    fun getEvents(topics: String? = null): SseSession {
        val url = client.baseUrl.trimEnd('/') + "/rest/events" +
                (if (!topics.isNullOrEmpty()) "?topics=$topics" else "")
        return client.sse(url)
    }
    fun initiateStateTracker(): SseSession =
        client.sse("${client.baseUrl.trimEnd('/')}/rest/events/states")

    suspend fun updateSSEConnectionItems(connectionId: String, itemsJson: String): String =
        client.post("/events/states/$connectionId", body = itemsJson)
}

/** openHAB Item Events SSE API. */
class ItemEvents(private val client: OpenHABClient) {
    private fun sse(topic: String) =
        client.sse("${client.baseUrl.trimEnd('/')}/rest/events?topics=$topic")

    fun itemEvent()                                         = sse("openhab/items")
    fun itemAddedEvent(name: String = "*")                  = sse("openhab/items/$name/added")
    fun itemRemovedEvent(name: String = "*")                = sse("openhab/items/$name/removed")
    fun itemUpdatedEvent(name: String = "*")                = sse("openhab/items/$name/updated")
    fun itemCommandEvent(name: String = "*")                = sse("openhab/items/$name/command")
    fun itemStateEvent(name: String = "*")                  = sse("openhab/items/$name/state")
    fun itemStatePredictedEvent(name: String = "*")         = sse("openhab/items/$name/statepredicted")
    fun itemStateChangedEvent(name: String = "*")           = sse("openhab/items/$name/statechanged")
    fun groupItemStateChangedEvent(item: String, member: String) =
        sse("openhab/items/$item/$member/statechanged")
}

/** openHAB Thing Events SSE API. */
class ThingEvents(private val client: OpenHABClient) {
    private fun sse(topic: String) =
        client.sse("${client.baseUrl.trimEnd('/')}/rest/events?topics=$topic")

    fun thingAddedEvent(uid: String = "*")             = sse("openhab/things/$uid/added")
    fun thingRemovedEvent(uid: String = "*")           = sse("openhab/things/$uid/removed")
    fun thingUpdatedEvent(uid: String = "*")           = sse("openhab/things/$uid/updated")
    fun thingStatusInfoEvent(uid: String = "*")        = sse("openhab/things/$uid/status")
    fun thingStatusInfoChangedEvent(uid: String = "*") = sse("openhab/things/$uid/statuschanged")
}

/** openHAB Inbox Events SSE API. */
class InboxEvents(private val client: OpenHABClient) {
    private fun sse(topic: String) =
        client.sse("${client.baseUrl.trimEnd('/')}/rest/events?topics=$topic")

    fun inboxAddedEvent(uid: String = "*")   = sse("openhab/inbox/$uid/added")
    fun inboxRemovedEvent(uid: String = "*") = sse("openhab/inbox/$uid/removed")
    fun inboxUpdatedEvent(uid: String = "*") = sse("openhab/inbox/$uid/updated")
}

/** openHAB Link Events SSE API. */
class LinkEvents(private val client: OpenHABClient) {
    private fun sse(topic: String) =
        client.sse("${client.baseUrl.trimEnd('/')}/rest/events?topics=$topic")

    fun itemChannelLinkAddedEvent(item: String = "*", ch: String = "*") =
        sse("openhab/links/$item-$ch/added")
    fun itemChannelLinkRemovedEvent(item: String = "*", ch: String = "*") =
        sse("openhab/links/$item-$ch/removed")
}

/** openHAB Channel Events SSE API. */
class ChannelEvents(private val client: OpenHABClient) {
    private fun sse(topic: String) =
        client.sse("${client.baseUrl.trimEnd('/')}/rest/events?topics=$topic")

    fun channelDescriptionChangedEvent(uid: String = "*") =
        sse("openhab/channels/$uid/descriptionchanged")
    fun channelTriggeredEvent(uid: String = "*") =
        sse("openhab/channels/$uid/triggered")
}
