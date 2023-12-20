package org.edx.mobile.model.nafath


import com.google.gson.annotations.SerializedName

data class NafathCheckStatusModel(
    @SerializedName("status")
    val status: String, // REGISTERED ,Waiting ,Completed
    @SerializedName("error")
    val error: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String
)