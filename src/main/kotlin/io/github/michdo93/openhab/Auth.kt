// Generated from Auth.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Auth(private val client: OpenHABClient) {

    /**
     * Retrieve the API tokens associated with the authenticated user.
     *
     * @param language (Optional) Language setting for the API request.
     *
     * @return JSON response from the server.
     */
    suspend fun getAPITokens(
    ): Any? = withContext(Dispatchers.IO) {
        getAPITokensBlocking()
    }

    private fun getAPITokensBlocking(): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        try {
            response = client.get("/auth/apitokens", Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 401)) {
                return Py.dict("error", "User is not authenticated.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "User not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "User not found.")
        } else if (Py.eq(status_code, 401)) {
            return Py.dict("error", "User is not authenticated.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Revoke a specific API token associated with the authenticated user.
     *
     * @param tokenName Name of the API token to be revoked.
     * @param language (Optional) Language setting for the API request.
     *
     * @return JSON response from the server.
     */
    suspend fun revokeAPIToken(
        tokenName: String,
    ): Any? = withContext(Dispatchers.IO) {
        revokeAPITokenBlocking(tokenName)
    }

    private fun revokeAPITokenBlocking(tokenName: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/auth/apitokens/" + Py.str(tokenName)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 401)) {
                return Py.dict("error", "User is not authenticated.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "User not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "User not found.")
        } else if (Py.eq(status_code, 401)) {
            return Py.dict("error", "User is not authenticated.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Terminate the session associated with a refresh token.
     *
     * @param refreshToken The refresh token used to delete the session.
     * @param id The user ID associated with the refresh token.
     *
     * @return JSON response from the server.
     */
    suspend fun logout(
        refreshToken: String,
        sessionID: String,
    ): Any? = withContext(Dispatchers.IO) {
        logoutBlocking(refreshToken, sessionID)
    }

    private fun logoutBlocking(refreshToken: String, sessionID: String): Any? {
        var data: Any? = null
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/x-www-form-urlencoded")
        data = Py.dict("refresh_token", refreshToken, "id", sessionID)
        try {
            response = client.post("/auth/logout", Py.m(headers), data, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 401)) {
                return Py.dict("error", "User is not authenticated.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "User not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "User not found.")
        } else if (Py.eq(status_code, 401)) {
            return Py.dict("error", "User is not authenticated.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Retrieve the sessions associated with the authenticated user.
     *
     * @return JSON response from the server.
     */
    suspend fun getSessions(
    ): Any? = withContext(Dispatchers.IO) {
        getSessionsBlocking()
    }

    private fun getSessionsBlocking(): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        try {
            response = client.get("/auth/sessions", Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 401)) {
                return Py.dict("error", "User is not authenticated.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "User not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "User not found.")
        } else if (Py.eq(status_code, 401)) {
            return Py.dict("error", "User is not authenticated.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Obtain access and refresh tokens.
     *
     * @param useCookie (Optional) Whether to use cookies for the session.
     * @param grantType (Optional) The type of grant being requested.
     * @param code (Optional) Authorization code for authentication.
     * @param redirectUri (Optional) Redirect URI for OAuth authentication.
     * @param clientID (Optional) Client ID for authentication.
     * @param refreshToken (Optional) Refresh token for token renewal.
     * @param codeVerifier (Optional) Code verifier for PKCE authentication.
     *
     * @return JSON response from the server.
     */
    suspend fun getToken(
        useCookie: Boolean? = false,
        grantType: String? = null,
        code: String? = null,
        redirectURI: String? = null,
        clientID: String? = null,
        refreshToken: String? = null,
        codeVerifier: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getTokenBlocking(useCookie, grantType, code, redirectURI, clientID, refreshToken, codeVerifier)
    }

    private fun getTokenBlocking(useCookie: Boolean? = false, grantType: String? = null, code: String? = null, redirectURI: String? = null, clientID: String? = null, refreshToken: String? = null, codeVerifier: String? = null): Any? {
        var body: Any? = null
        var encodedBody: Any? = null
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/x-www-form-urlencoded", "Accept", "application/json")
        params = Py.dict("useCookie", Py.lower(Py.str(useCookie)))
        body = Py.dict("grant_type", grantType, "code", code, "redirect_uri", redirectURI, "client_id", clientID, "refresh_token", refreshToken, "code_verifier", codeVerifier)
        body = Py.withoutNone(body)
        encodedBody = Py.urlencode(body)
        try {
            response = client.post("/auth/token", Py.m(headers), encodedBody, Py.m(params))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Invalid request parameters.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return response
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Invalid request parameters.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
