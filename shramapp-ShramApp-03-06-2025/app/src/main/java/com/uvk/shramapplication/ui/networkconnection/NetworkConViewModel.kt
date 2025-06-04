package com.uvk.shramapplication.ui.networkconnection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import kotlinx.coroutines.launch

class NetworkConViewModel (application: Application) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()
    val networkConListResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val requestSendResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val requestCancelResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val acceptRequestResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val rejectRequestResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val myConnectionResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val getSendRequestListResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val invitationResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val connDetailsResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()
    val removeConnectionResult: MutableLiveData<BaseResponse<NetworkResponse>> = MutableLiveData()



    fun getConnectionList(user_id: String,
                          keyword : String) {

        networkConListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getConnectionList(
                    userId = user_id,
                    keyword =keyword)

                if (response?.code() == 200) {
                    networkConListResult.value = BaseResponse.Success(response.body())
                } else {
                    networkConListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
               networkConListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun getMyConnectionList(user_id: String ,keyword :String) {

        myConnectionResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getMyConnectionList(
                    userId = user_id,
                    keyword = keyword
                )

                if (response?.code() == 200) {
                    myConnectionResult.value = BaseResponse.Success(response.body())
                } else {
                    myConnectionResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                myConnectionResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getSendRequestList(user_id: String) {

        getSendRequestListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getSendRequestList(
                    userId = user_id
                )

                if (response?.code() == 200) {
                    getSendRequestListResult.value = BaseResponse.Success(response.body())
                } else {
                    getSendRequestListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getSendRequestListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun getinvitationList(user_id: String) {

        invitationResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getinvitationList(
                    userId = user_id
                )

                if (response?.code() == 200) {
                    invitationResult.value = BaseResponse.Success(response.body())
                } else {
                    invitationResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                invitationResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun requestSend(token: String, sender_id: String, receiver_id: String) {

        requestSendResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.requestSend(
                    token = token,
                    senderId = sender_id,
                    receiverId = receiver_id
                )

                if (response?.code() == 200) {
                    requestSendResult.value = BaseResponse.Success(response.body())
                } else {
                    requestSendResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                requestSendResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun requestCancel( sender_id: String, network_id: String) {

        requestCancelResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.cancelRequest(
                    userId = sender_id,
                    networkId = network_id
                )

                if (response?.code() == 200) {
                    requestCancelResult.value = BaseResponse.Success(response.body())
                } else {
                    requestCancelResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                requestCancelResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun requestAccept(token: String, networkId: String, receiver_id: String) {

        acceptRequestResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.acceptRequest(
                    token = token,
                    networkId = networkId,
                    receiverId = receiver_id
                )

                if (response?.code() == 200) {
                    acceptRequestResult.value = BaseResponse.Success(response.body())
                } else {
                    acceptRequestResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                acceptRequestResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun removeConnection(networkId: String, user_id: String) {

        removeConnectionResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.removeConnection(
                    networkId = networkId,
                    userId = user_id
                )

                if (response?.code() == 200) {
                    removeConnectionResult.value = BaseResponse.Success(response.body())
                } else {
                    removeConnectionResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                removeConnectionResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun requestReject(token: String, networkId: String, receiver_id: String) {

        rejectRequestResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.rejectRequest(
                    token = token,
                    networkId = networkId,
                    receiverId = receiver_id
                )

                if (response?.code() == 200) {
                    rejectRequestResult.value = BaseResponse.Success(response.body())
                } else {
                    rejectRequestResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                rejectRequestResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun connectionDetails(token: String, user_id: String , network_id : String) {

        connDetailsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.connectionDetails(
                    token = token,
                    userId = user_id,
                    network_id = network_id
                )

                if (response?.code() == 200) {
                    connDetailsResult.value = BaseResponse.Success(response.body())
                } else {
                    connDetailsResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                connDetailsResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


}