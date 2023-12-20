package org.edx.mobile.viewModel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.edx.mobile.authentication.LoginAPI
import org.edx.mobile.core.IEdxEnvironment
import org.edx.mobile.exception.ErrorMessage
import org.edx.mobile.http.model.NetworkResponseCallback
import org.edx.mobile.http.model.Result
import org.edx.mobile.model.authentication.AuthResponse
import org.edx.mobile.model.course.ResetCourseDates
import org.edx.mobile.model.nafath.NafathInitiateRequestModel
import org.edx.mobile.model.nafath.NafathCheckStatusModel
import org.edx.mobile.model.nafath.NafathRegisterUser
import org.edx.mobile.model.nafath.NafathRegisterUserCheckStatusRequest
import org.edx.mobile.model.nafath.NafathRegisterUserRequest
import org.edx.mobile.repository.AuthRepository
import org.edx.mobile.social.SocialLoginDelegate.Feature
import org.edx.mobile.util.observer.Event
import org.edx.mobile.util.observer.postEvent

import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val environment: IEdxEnvironment,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _onLogin = MutableLiveData<Event<Boolean>>()
    val onLogin: LiveData<Event<Boolean>> = _onLogin

    private val _onRegister = MutableLiveData<Event<Boolean>>()
    val onRegister: LiveData<Event<Boolean>> = _onRegister

    private val _errorMessage = MutableLiveData<Event<ErrorMessage>>()
    val errorMessage: LiveData<Event<ErrorMessage>> = _errorMessage

    private val _socialLoginErrorMessage = MutableLiveData<Event<ErrorMessage>>()
    val socialLoginErrorMessage: LiveData<Event<ErrorMessage>> = _socialLoginErrorMessage

    private val _initiateRequest = MutableLiveData<Event<NafathInitiateRequestModel>>()
    val initiateRequest: LiveData<Event<NafathInitiateRequestModel>> = _initiateRequest

    private val _checkStatus = MutableLiveData<Event<NafathCheckStatusModel>>()
    val checkStatus: LiveData<Event<NafathCheckStatusModel>> = _checkStatus

    private val _initRegisterUser = MutableLiveData<Event<NafathRegisterUser>>()
    val initRegisterUser: LiveData<Event<NafathRegisterUser>> = _initRegisterUser

    private val _initCheckStatusRegisterUser = MutableLiveData<Event<NafathCheckStatusModel>>()
    val initCheckStatusRegisterUser: LiveData<Event<NafathCheckStatusModel>> = _initCheckStatusRegisterUser

    private val _showProgress = MutableLiveData<Event<Boolean>>()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

    fun loginUsingEmail(email: String, password: String) {
        _showProgress.postEvent(true)
        viewModelScope.launch {
            val response = kotlin.runCatching { authRepository.loginUsingEmail(email, password) }
            response.onSuccess {
                _onLogin.postEvent(it.isSuccess)
            }.onFailure {
                _errorMessage.postEvent(ErrorMessage(0, it))
            }
            _showProgress.postEvent(false)
        }
    }

    fun registerAccount(parameters: Bundle) {
        viewModelScope.launch {
            val response = kotlin.runCatching { authRepository.registerAccount(parameters) }
            response.onSuccess {
                _onRegister.postEvent(it?.isSuccess == true)
            }.onFailure {
                _errorMessage.postEvent(ErrorMessage(0, it))
            }
        }
    }

    fun loginUsingSocialAccount(
        accessToken: String,
        backend: String,
        feature: Feature
    ) {
        if (feature == Feature.SIGN_IN)
            _showProgress.postEvent(true)

        viewModelScope.launch {
            val response = kotlin.runCatching {
                authRepository.loginUsingSocialAccount(
                    accessToken,
                    backend
                )
            }

            response.onSuccess {
                if (feature == Feature.REGISTRATION) {
                    environment.loginPrefs.alreadyRegisteredLoggedIn = true
                    _onRegister.postEvent(true)
                } else {
                    _showProgress.postEvent(false)
                    _onLogin.postEvent(true)
                }
            }.onFailure {
                if (it is LoginAPI.AccountNotLinkedException) {
                    _socialLoginErrorMessage.postEvent(ErrorMessage(0, it))
                } else {
                    _errorMessage.postEvent(ErrorMessage(0, it))
                }
            }
            _showProgress.postEvent(false)
        }
    }




    fun InitiateRequest(nafathId: String) {
        _showProgress.postEvent(true)
        viewModelScope.launch {
            authRepository.InitiateRequest(nafathId, object: NetworkResponseCallback<NafathInitiateRequestModel>{
                override fun onSuccess(result: Result.Success<NafathInitiateRequestModel>) {
                    result?.let {
                        if (it.code==200){
                            _initiateRequest.postEvent(result.data!!)
                        }else{
                            _initiateRequest.postEvent(NafathInitiateRequestModel("","","",it.code.toString(),it.message))
                        }

                    }
                }
                override fun onError(error: Result.Error) {
                    _initiateRequest.postEvent(NafathInitiateRequestModel("","","","",""))
                }

            })
            _showProgress.postEvent(false)
        }
    }

    fun checkStatus(map: HashMap<String,String>) {
        _showProgress.postEvent(true)
        viewModelScope.launch {

            authRepository.checkStatus(map, object: NetworkResponseCallback<NafathCheckStatusModel>{
                override fun onSuccess(result: Result.Success<NafathCheckStatusModel>) {
                    result?.let {
                        if (it.code ==200){
                            _checkStatus.postEvent(result.data!!)
                        }else{
                            _checkStatus.postEvent(NafathCheckStatusModel("","",it.code.toString(),it.message))
                        }

                    }
                }
                override fun onError(error: Result.Error) {
                 _checkStatus.postEvent(NafathCheckStatusModel("","","",""))
                }

            })
            _showProgress.postEvent(false)
        }
    }

    fun registerUser(nafathId: NafathRegisterUserRequest) {
        _showProgress.postEvent(true)
        viewModelScope.launch {
            authRepository.registerUser(nafathId, object: NetworkResponseCallback<NafathRegisterUser>{
                override fun onSuccess(result: Result.Success<NafathRegisterUser>) {
                    result.let {
                        if (it.code==201){
                            _initRegisterUser.postEvent(it.data!!)
                        }else{
                           _initRegisterUser.postEvent(NafathRegisterUser(it.message,it.code.toString()))
                        }

                    }
                }
                override fun onError(error: Result.Error) {
                }
            })
            _showProgress.postEvent(false)
        }
    }



    fun registerUserCheckStatus(nafathId: NafathRegisterUserCheckStatusRequest) {
        _showProgress.postEvent(true)
        viewModelScope.launch {

            authRepository.registerUserCheckStatus(nafathId, object: NetworkResponseCallback<NafathCheckStatusModel>{
                override fun onSuccess(result: Result.Success<NafathCheckStatusModel>) {

                    result.let {

                        if (it.code ==200){
                            _initCheckStatusRegisterUser.postEvent(it.data!!)
                        }else{
                            _initCheckStatusRegisterUser.postEvent(NafathCheckStatusModel("","",it.code.toString(),it.message))
                        }
                    }
                }

                override fun onError(error: Result.Error) {
                    _initCheckStatusRegisterUser.postEvent(NafathCheckStatusModel("","","",""))
                }

            })
            _showProgress.postEvent(false)
        }
    }

    fun loginUsingNafath(map: HashMap<String,String>) {
//        _showProgress.postEvent(true)
//        viewModelScope.launch {
//
//            authRepository.loginUsingNafath(map, object: NetworkResponseCallback<AuthResponse>{
//                override fun onSuccess(result: Result.Success<AuthResponse>) {
//                    result.let {
//                        _onLogin.postEvent(it.isSuccessful)
//
//                    }
//                }
//                override fun onError(error: Result.Error) {
//                    _errorMessage.postEvent(ErrorMessage(0,error.throwable))
//                }
//            })
//            _showProgress.postEvent(false)
//        }
        _showProgress.postEvent(true)
        viewModelScope.launch {
            val response = kotlin.runCatching { authRepository.loginUsingNafath(map) }
            response.onSuccess {
                _onLogin.postEvent(it.isSuccess)
            }.onFailure {
                _errorMessage.postEvent(ErrorMessage(0, it))
            }
            _showProgress.postEvent(false)
        }
    }

}
