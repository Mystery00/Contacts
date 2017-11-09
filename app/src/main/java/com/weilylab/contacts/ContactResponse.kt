package com.weilylab.contacts

import com.weilylab.contacts.classes.Contact
import com.weilylab.contacts.classes.Response
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by myste.
 */
interface ContactResponse
{
	@POST("interface/getContactList.php")
	fun getContactList(@Field("username") username: String): Call<Contact>

	@FormUrlEncoded
	@POST("interface/login.php")
	fun login(@Field("username") username: String, @Field("password") password: String): Call<Response>

	@FormUrlEncoded
	@POST("interface/register.php")
	fun register(@Field("username") username: String, @Field("password") password: String): Call<Response>
}