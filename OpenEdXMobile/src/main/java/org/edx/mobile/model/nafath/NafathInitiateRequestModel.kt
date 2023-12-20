package org.edx.mobile.model.nafath


import com.google.gson.annotations.SerializedName

data class NafathInitiateRequestModel(
    @SerializedName("random")
    val random: String, // 85
    @SerializedName("error")
    val error: String, // 85
    @SerializedName("trans_id")
    val transId: String, // bf60fee1-002c-4d89-8fe9-aeef15ed12f2
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String
)