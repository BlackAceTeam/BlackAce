package com.blackace.data.http

import com.blackace.BuildConfig
import com.blackace.data.entity.http.VersionBean
import com.blackace.data.entity.http.*
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 11:02 PM
 */
interface ApiService {

    @POST("/account/login/ANDROID/1")
    @FormUrlEncoded
    suspend fun login(@Field("account") username: String, @Field("password") password: String): BaseResult<LoginBean>

    @POST("/account/register/ANDROID/1")
    @FormUrlEncoded
    suspend fun register(
        @Field("account") username: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): BaseResult<RegisterBean>

    @POST("/account/reset_password/ANDROID/1")
    @FormUrlEncoded
    suspend fun changePassword(@Field("code") verify: String,@Field("password") newPass: String,@Field("account") account: String): BaseResult<Any>

    @POST("/account/reset_password_code/ANDROID/1")
    @FormUrlEncoded
    suspend fun sendEmailVerify(@Field("account") account: String): BaseResult<EmailBean>

    @POST("/task/feature/list/ANDROID/1")
    @FormUrlEncoded
    suspend fun featureList(@Field("supportVersion") supportVersion: Int = 1): BaseResult<BaseListBean<FeatureBean>>

    @POST("/task/delete/ANDROID/1")
    @FormUrlEncoded
    suspend fun taskDelete(@Field("taskNo") taskNo: String): BaseResult<Any>

    @POST("/task/create/ANDROID/1")
    @FormUrlEncoded
    suspend fun taskCreate(@FieldMap map: Map<String, String>): BaseResult<Any>

    @GET("/task/list/ANDROID/1")
    suspend fun taskList(@Query("start") start: String): MoreBean<BaseListBean<TaskBean>>

    @GET("/task/query/ANDROID/1")
    suspend fun taskQuery(@Query("taskNo") taskNo: String): BaseResult<BaseListBean<TaskBean>>

    @GET("/sys/version/ANDROID/1")
    suspend fun checkUpdate(@Query("version_code") versionCode:Int = BuildConfig.VERSION_CODE):BaseResult<VersionBean>
}
