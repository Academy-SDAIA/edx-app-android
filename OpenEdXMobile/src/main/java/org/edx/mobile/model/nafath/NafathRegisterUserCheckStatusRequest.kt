package org.edx.mobile.model.nafath

import com.google.gson.annotations.SerializedName

data class NafathRegisterUserCheckStatusRequest(
    @SerializedName("nafath_id")
    val  nafath_id: String ,
    @SerializedName("trans_id")
    val  trans_id: String ,
    @SerializedName("is_mobile_app")
    val  is_mobile_app: String,
    @SerializedName("user_data")
    val  user_data: User
) {data class User(
    @SerializedName("name")
    val name :String ,
    @SerializedName("username")
    val username :String ,
    @SerializedName("email")
    val email :String ,
    @SerializedName("phone_number")
    val phone_number :String ,
    @SerializedName("gender")
    val gender :String ,
    @SerializedName("linkedin_account")
    val linkedin_account :String ,
    @SerializedName("date_of_birth")
    val date_of_birth :String ,
    @SerializedName("region")
    val region :String,
    @SerializedName("city")
    val city :String,
    @SerializedName("address_line")
    val address_line :String,
    @SerializedName("level_of_education")
    val level_of_education :String,
    @SerializedName("english_language_level")
    val english_language_level :String,
    @SerializedName("employment_status")
    val employment_status :String,
    @SerializedName("work_experience_level")
    val work_experience_level :String,
    @SerializedName("job_title")
    val job_title :String,
    @SerializedName("activation_code")
    val activation_code :String,
    @SerializedName("year_of_birth")
    val year_of_birth :Int

)}
