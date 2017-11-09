package com.weilylab.contacts

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by myste.
 */
class ContactHelper private constructor()
{
	companion object
	{
		private var helper: ContactHelper? = null
		fun getInstance(): ContactHelper
		{
			if (helper == null)
				helper = ContactHelper()
			return helper!!
		}
	}

	val client = OkHttpClient.Builder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.build()

	val retrofit = Retrofit.Builder()
			.client(client)
			.baseUrl("http://192.168.1.105/Contacts/")
			.addConverterFactory(GsonConverterFactory.create())
			.build()

	lateinit var username:String
}