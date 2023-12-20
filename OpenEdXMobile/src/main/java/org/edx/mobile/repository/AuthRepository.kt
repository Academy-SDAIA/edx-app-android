package org.edx.mobile.repository

import android.os.Bundle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.edx.mobile.authentication.LoginAPI
import org.edx.mobile.core.EdxEnvironment
import org.edx.mobile.extenstion.isNotNullOrEmpty
import org.edx.mobile.http.model.NetworkResponseCallback
import org.edx.mobile.http.model.Result
import org.edx.mobile.injection.DataSourceDispatcher
import org.edx.mobile.model.authentication.AuthResponse
import org.edx.mobile.model.nafath.NafathCheckStatusModel
import org.edx.mobile.model.nafath.NafathInitiateRequestModel
import org.edx.mobile.model.nafath.NafathRegisterUser
import org.edx.mobile.model.nafath.NafathRegisterUserCheckStatusRequest
import org.edx.mobile.model.nafath.NafathRegisterUserRequest
import org.edx.mobile.module.prefs.LoginPrefs
import org.edx.mobile.social.SocialAuthSource
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    private val environment: EdxEnvironment,
    private val loginAPI: LoginAPI,
    @DataSourceDispatcher val dispatcher: CoroutineDispatcher,
) {
    suspend fun loginUsingEmail(
        email: String,
        password: String,
    ): AuthResponse = withContext(dispatcher) {
        try {
            loginAPI.logInUsingEmail(email, password)
        } catch (exception: Exception) {
            throw exception
        }
    }


    fun InitiateRequest(
        nafathId: String,
        callback: NetworkResponseCallback<NafathInitiateRequestModel>
    ) {
        loginAPI.initiateReq(nafathId).enqueue(object : Callback<NafathInitiateRequestModel> {
            override fun onResponse(
                call: Call<NafathInitiateRequestModel>,
                response: Response<NafathInitiateRequestModel>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        callback.onSuccess(
                            Result.Success<NafathInitiateRequestModel>(
                                isSuccessful = response.isSuccessful,
                                data = response.body(),
                                code = response.code(),
                                message = response.message()
                            )
                        )
                    }
                } else {
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.errorBody()!!.string())
                        val entityId: String = jsonObject.getString("error")
                        val message = entityId.split(" ").toTypedArray()
                        callback.onSuccess(
                        Result.Success<NafathInitiateRequestModel>(
                            isSuccessful = response.isSuccessful,
                            data = response.body(),
                            code = response.code(),
                            message = message[0]
                        )
                    )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

            }

            override fun onFailure(call: Call<NafathInitiateRequestModel>, t: Throwable) {
                callback.onError(Result.Error(t))
            }
        })
    }


    fun checkStatus(
        map: HashMap<String, String>,
        callback: NetworkResponseCallback<NafathCheckStatusModel>
    ) {
        loginAPI.checkStatus(map).enqueue(object : Callback<NafathCheckStatusModel> {
            override fun onResponse(
                call: Call<NafathCheckStatusModel>,
                response: Response<NafathCheckStatusModel>
            ) {
                if (response.code() == 200){
                    if (response.body() != null) {
                        callback.onSuccess(
                            Result.Success<NafathCheckStatusModel>(
                                isSuccessful = response.isSuccessful,
                                data = response.body(),
                                code = response.code(),
                                message = response.message()
                            )
                        )
                    }
                }else {
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.errorBody()!!.string())
                        val entityId: String = jsonObject.getString("error")
                        val message = entityId.split(" ").toTypedArray()
                        callback.onSuccess(
                            Result.Success<NafathCheckStatusModel>(
                                isSuccessful = response.isSuccessful,
                                data = response.body(),
                                code = response.code(),
                                message = message[0]
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

            }

            override fun onFailure(call: Call<NafathCheckStatusModel>, t: Throwable) {
                callback.onError(Result.Error(t))
            }
        })
    }

    fun registerUser(
        nafathId: NafathRegisterUserRequest,
        callback: NetworkResponseCallback<NafathRegisterUser>
    ) {
        loginAPI.registerUser(nafathId).enqueue(object : Callback<NafathRegisterUser> {
            override fun onResponse(
                call: Call<NafathRegisterUser>,
                response: Response<NafathRegisterUser>
            ) {

                if (response.code() == 201) {
                    if (response.body() != null) {
                        callback.onSuccess(
                            Result.Success<NafathRegisterUser>(
                                isSuccessful = response.isSuccessful,
                                data = response.body(),
                                code = response.code(),
                                message = response.message()
                            )
                        )
                    }
                } else if (response.code() == 400) {
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.errorBody()!!.string())
                        val entityId: String = jsonObject.getString("error")
                        val message = entityId.split(" ").toTypedArray()
                        callback.onSuccess(
                            Result.Success<NafathRegisterUser>(
                                isSuccessful = response.isSuccessful,
                                data = response.body(),
                                code = response.code(),
                                message = message[0]
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<NafathRegisterUser>, t: Throwable) {
                callback.onError(Result.Error(t))
            }
        })
    }


    fun registerUserCheckStatus(
        nafathId: NafathRegisterUserCheckStatusRequest,
        callback: NetworkResponseCallback<NafathCheckStatusModel>
    ) {
        loginAPI.registerUserCheckStatus(nafathId)
            .enqueue(object : Callback<NafathCheckStatusModel> {
                override fun onResponse(
                    call: Call<NafathCheckStatusModel>,
                    response: Response<NafathCheckStatusModel>
                ) {
                    if (response.code() == 200){if (response.body() != null) {
                        callback.onSuccess(
                            Result.Success<NafathCheckStatusModel>(
                                isSuccessful = response.isSuccessful,
                                data = response.body(),
                                code = response.code(),
                                message = response.message()
                            )
                        )
                    }}else if(response.code() == 400){
                        var jsonObject: JSONObject? = null
                        try {
                            jsonObject = JSONObject(response.errorBody()!!.string())
                            val entityId: String = jsonObject.getString("error")
                            val message = entityId.split(" ").toTypedArray()
                            callback.onSuccess(
                                Result.Success<NafathCheckStatusModel>(
                                    isSuccessful = response.isSuccessful,
                                    data = response.body(),
                                    code = response.code(),
                                    message = message[0]
                                )
                            )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }

                }

                override fun onFailure(call: Call<NafathCheckStatusModel>, t: Throwable) {
                    callback.onError(Result.Error(t))
                }
            })
    }


    suspend fun loginUsingNafath(
        map: HashMap<String, String>,
    ): AuthResponse = withContext(dispatcher) {
        try {
            loginAPI.loginUsingNafath(map)
        } catch (exception: Exception) {
            throw exception
        }
    }


    suspend fun registerAccount(
        formFields: Bundle,
    ): AuthResponse? {
        val accessToken = environment.loginPrefs.socialLoginAccessToken
        val provider = environment.loginPrefs.socialLoginProvider
        val backendSourceType = SocialAuthSource.fromString(provider)

        // Set honor_code and terms_of_service to true
        formFields.putString("honor_code", "true")
        formFields.putString("terms_of_service", "true")

        // Set parameter required by social registration
        if (accessToken.isNotNullOrEmpty()) {
            formFields.putString("access_token", accessToken)
            formFields.putString("provider", provider)
            formFields.putString("client_id", environment.config.oAuthClientId)
        }

        return withContext(dispatcher) {
            try {
                val response = when (backendSourceType) {
                    SocialAuthSource.GOOGLE -> accessToken?.let {
                        loginAPI.registerUsingGoogle(formFields, it)
                    }

                    SocialAuthSource.FACEBOOK -> accessToken?.let {
                        loginAPI.registerUsingFacebook(formFields, it)
                    }

                    SocialAuthSource.MICROSOFT -> accessToken?.let {
                        loginAPI.registerUsingMicrosoft(formFields, it)
                    }

                    else -> loginAPI.registerUsingEmail(formFields)
                }
                return@withContext response
            } catch (exception: Exception) {
                throw exception
            }
        }
    }

    suspend fun loginUsingSocialAccount(
        accessToken: String,
        backend: String
    ): AuthResponse = withContext(dispatcher) {

        val loginFunction = when (backend.lowercase()) {
            LoginPrefs.BACKEND_FACEBOOK -> loginAPI::logInUsingFacebook
            LoginPrefs.BACKEND_GOOGLE -> loginAPI::logInUsingGoogle
            LoginPrefs.BACKEND_MICROSOFT -> loginAPI::logInUsingMicrosoft
            else -> throw IllegalArgumentException("Unknown backend: $backend")
        }

        try {
            loginFunction(accessToken)
        } catch (exception: LoginAPI.AccountNotLinkedException) {
            throw exception
        } catch (exception: Exception) {
            throw exception
        }
    }
}
