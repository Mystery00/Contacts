package com.weilylab.contacts

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.weilylab.contacts.classes.Contact
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import vip.mystery0.tools.logs.Logs

class MainActivity : AppCompatActivity()
{
	companion object
	{
		private val TAG = "MainActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		val observer = object : Observer<Contact>
		{
			override fun onSubscribe(d: Disposable)
			{
				Logs.i(TAG, "onSubscribe: ")
			}

			override fun onNext(t: Contact)
			{
			}

			override fun onComplete()
			{
			}

			override fun onError(e: Throwable)
			{
				Logs.wtf(TAG, "onError: ", e)
			}
		}

		val observable = Observable.create<Contact>({
			val service = ContactHelper.getInstance().retrofit.create(ContactResponse::class.java)
			val call = service.getContactList("1231234")
			val response = call.execute()
			Logs.i(TAG, "onCreate: "+response.isSuccessful)
			if (response.isSuccessful)
			{
				Logs.i(TAG, "onCreate: " + response.body().toString())
			}
		})

		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)

		fab.setOnClickListener { view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		return when (item.itemId)
		{
			R.id.action_settings -> true
			else -> super.onOptionsItemSelected(item)
		}
	}
}
