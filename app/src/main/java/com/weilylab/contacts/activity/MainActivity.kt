package com.weilylab.contacts.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import com.weilylab.contacts.ContactAdapter
import com.weilylab.contacts.ContactHelper
import com.weilylab.contacts.ContactResponse
import com.weilylab.contacts.R
import com.weilylab.contacts.classes.Contact
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import vip.mystery0.tools.logs.Logs
import java.util.ArrayList

class MainActivity : AppCompatActivity()
{
	companion object
	{
		private val TAG = "MainActivity"
	}

	private lateinit var adapter: ContactAdapter
	private var list = ArrayList<Contact>()

	private val observer = object : Observer<ArrayList<Contact>>
	{

		override fun onSubscribe(d: Disposable)
		{
			Logs.i(TAG, "onSubscribe: ")
		}

		override fun onNext(t: ArrayList<Contact>)
		{
			list.clear()
			list.addAll(t)
		}

		override fun onComplete()
		{
			adapter.notifyDataSetChanged()
		}

		override fun onError(e: Throwable)
		{
			Logs.wtf(TAG, "onError: ", e)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
		adapter = ContactAdapter(this@MainActivity, list)
		recyclerView.adapter = adapter

		fab.setOnClickListener {
			startActivity(Intent(this, NewContactActivity::class.java))
		}

		searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
		{
			override fun onQueryTextSubmit(query: String): Boolean
			{
				Logs.i(TAG, "onQueryTextSubmit: " + query)
				val observable = Observable.create<ArrayList<Contact>>({ subscriber ->
					val service = ContactHelper.getInstance().retrofit.create(ContactResponse::class.java)
					val call = service.contactSearch(query, "search", ContactHelper.getInstance().username)
					val response = call.execute()
					if (response.isSuccessful)
					{
						subscriber.onNext(response.body()!!)
						subscriber.onComplete()
					}
				})

				observable.subscribeOn(Schedulers.newThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(observer)
				return true
			}

			override fun onQueryTextChange(newText: String): Boolean
			{
				Logs.i(TAG, "onQueryTextChange: " + newText)
				if (newText.isEmpty())
					requestList()
				return true
			}
		})
		searchView.setOnCloseListener {
			requestList()
			true
		}
	}

	override fun onResume()
	{
		super.onResume()
		requestList()
	}

	fun requestList()
	{
		val observable = Observable.create<ArrayList<Contact>>({ subscriber ->
			val service = ContactHelper.getInstance().retrofit.create(ContactResponse::class.java)
			val call = service.getContactList(ContactHelper.getInstance().username)
			val response = call.execute()
			if (response.isSuccessful)
			{
				subscriber.onNext(response.body()!!)
				subscriber.onComplete()
			}
		})

		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}
}
