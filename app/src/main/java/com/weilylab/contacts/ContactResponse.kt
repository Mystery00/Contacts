package com.weilylab.contacts

import com.weilylab.contacts.classes.Contact
import com.weilylab.contacts.classes.Response
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.ArrayList

/**
 * Created by myste.
 */
interface ContactResponse
{
	@FormUrlEncoded
	@POST("interface/getContactList.php")
	fun getContactList(@Field("username") username: String): Call<ArrayList<Contact>>

	@FormUrlEncoded
	@POST("interface/login.php")
	fun login(@Field("username") username: String, @Field("password") password: String): Call<Response>

	@FormUrlEncoded
	@POST("interface/register.php")
	fun register(@Field("username") username: String, @Field("password") password: String): Call<Response>

	@FormUrlEncoded
	@POST("interface/contactDo.php")
	fun contactInsert(@Field("contact_name") contactName: String, @Field("contact_mark") contactMark: String, @Field("phone_list") phoneList: String, @Field("email_list") emailList: String, @Field("action") action: String, @Field("username") username: String): Call<Response>

	@FormUrlEncoded
	@POST("interface/contactDo.php")
	fun contactDelete(@Field("contact_name") contactName: String, @Field("action") action: String, @Field("username") username: String): Call<Response>

	@FormUrlEncoded
	@POST("interface/contactDo.php")
	fun contactUpdate(@Field("old_contact_name") oldContactName: String, @Field("contact_name") contactName: String, @Field("contact_mark") contactMark: String, @Field("phone_list") phoneList: String, @Field("email_list") emailList: String, @Field("action") action: String, @Field("username") username: String): Call<Response>

	@FormUrlEncoded
	@POST("interface/contactDo.php")
	fun contactSearch(@Field("contact_name") contactName: String, @Field("action") action: String, @Field("username") username: String): Call<ArrayList<Contact>>
}