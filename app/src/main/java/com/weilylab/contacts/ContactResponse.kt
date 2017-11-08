package com.weilylab.contacts

import com.weilylab.contacts.classes.Contact
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by myste.
 */
interface ContactResponse
{
	@POST("/interface/getContactList.php")
	fun getContactList(@Query("username")username:String): Call<Contact>
}