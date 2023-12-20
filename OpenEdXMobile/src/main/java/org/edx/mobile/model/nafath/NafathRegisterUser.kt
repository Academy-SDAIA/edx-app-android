package org.edx.mobile.model.nafath


import com.google.gson.annotations.SerializedName

data class NafathRegisterUser(
    @SerializedName("successMessage")
    val successMessage: String,
    @SerializedName("code")
    val code: String
)