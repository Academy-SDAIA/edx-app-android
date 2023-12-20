package org.edx.mobile.model.nafath

import com.google.gson.annotations.SerializedName

data class NafathRegisterUserRequest(
    @SerializedName("nafath_id")
    val  nafath_id: String = "",
    @SerializedName("trans_id")
    val  trans_id: String = "",
    @SerializedName("is_mobile_app")
    val  is_mobile_app: String,
    @SerializedName("user_data")
    val  user_data: User
) {data class User(
    @SerializedName("username")
    val username :String ,
    @SerializedName("email")
    val email :String ,
    @SerializedName("activation_code")
    val activation_code :String ,
    @SerializedName("form")
    val form :Int
)}
