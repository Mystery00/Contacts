package com.weilylab.contacts.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.gson.Gson
import com.weilylab.contacts.ContactHelper
import com.weilylab.contacts.ContactResponse
import com.weilylab.contacts.PhoneEmailAdapter
import com.weilylab.contacts.R
import com.weilylab.contacts.classes.Contact
import com.weilylab.contacts.classes.Email
import com.weilylab.contacts.classes.Phone
import com.weilylab.contacts.classes.Response
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_new_contact.*
import kotlinx.android.synthetic.main.content_new_contact.*
import vip.mystery0.tools.logs.Logs
import java.util.ArrayList

class NewContactActivity : AppCompatActivity()
{
	companion object
	{
		private val TAG = "NewContactActivity"
	}

	private lateinit var contact: Contact
	private var isUpdate = false
	private val phoneList = ArrayList<Phone>()
	private val emailList = ArrayList<Email>()
	private val showPhoneList = ArrayList<String>()
	private val showEmailList = ArrayList<String>()
	private lateinit var updateDialog: ZLoadingDialog
	private val retrofit = ContactHelper.getInstance().retrofit

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_new_contact)
		setSupportActionBar(toolbar)

		if (intent.getSerializableExtra("contact") != null)
		{
			isUpdate = true
			contact = intent.getSerializableExtra("contact") as Contact
			contactName.editText?.setText(contact.contactName)
			contactMark.editText?.setText(contact.contactMark)
			phoneList.addAll(contact.phoneList)
			emailList.addAll(contact.emailList)
			phoneList.forEach {
				showPhoneList.add(it.phoneNumber)
			}
			emailList.forEach {
				showEmailList.add(it.emailAddress)
			}
		}

		updateDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText("插入中......")

		val phoneAdapter = PhoneEmailAdapter(showPhoneList)
		val emailAdapter = PhoneEmailAdapter(showEmailList)

		tel_recycler.layoutManager = LinearLayoutManager(this)
		email_recycler.layoutManager = LinearLayoutManager(this)

		tel_recycler.adapter = phoneAdapter
		email_recycler.adapter = emailAdapter

		tel_add.setOnClickListener {
			val temp = tel_edit.editText?.text.toString()
			showPhoneList.add(temp)
			val phone = Phone()
			phone.phoneNumber = temp
			phone.phoneType = tel_type_edit.editText?.text.toString()
			phoneList.add(phone)
			phoneAdapter.notifyItemInserted(phoneList.size - 1)
		}
		email_add.setOnClickListener {
			val temp = email_edit.editText?.text.toString()
			showEmailList.add(temp)
			val email = Email()
			email.emailAddress = temp
			emailList.add(email)
			emailAdapter.notifyItemInserted(emailList.size - 1)
		}
		phoneAdapter.setDeleteListener(object : PhoneEmailAdapter.DeleteListener
		{
			override fun onDelete(position: Int)
			{
				phoneList.removeAt(position)
			}
		})
		emailAdapter.setDeleteListener(object : PhoneEmailAdapter.DeleteListener
		{
			override fun onDelete(position: Int)
			{
				emailList.removeAt(position)
			}
		})

		fab.setOnClickListener {
			val observer = object : Observer<Response>
			{
				private lateinit var response: Response

				override fun onSubscribe(d: Disposable)
				{
					Logs.i(TAG, "onSubscribe: ")
					updateDialog.show()
				}

				override fun onNext(t: Response)
				{
					Logs.i(TAG, "onNext: " + t.code)
					response = t
				}

				override fun onError(e: Throwable)
				{
					e.printStackTrace()
					updateDialog.dismiss()
				}

				override fun onComplete()
				{
					Logs.i(TAG, "onComplete: ")
					Toast.makeText(this@NewContactActivity, response.message, Toast.LENGTH_SHORT)
							.show()
					updateDialog.dismiss()
					if (response.code == 0)
					{
						finish()
					}
				}
			}
			val observable = Observable.create<Response> { subscriber ->
				val gson = Gson()
				val contactName = contactName.editText?.text.toString()
				val contactMark = contactMark.editText?.text.toString()
				val phoneList = gson.toJson(phoneList)
				val emailList = gson.toJson(emailList)
				val service = retrofit.create(ContactResponse::class.java)
				val response = if (isUpdate)
				{
					val call = service.contactUpdate(contact.contactName, contactName, contactMark, phoneList, emailList, "update", ContactHelper.getInstance().username)
					call.execute()
				}
				else
				{
					val call = service.contactInsert(contactName, contactMark, phoneList, emailList, "insert", ContactHelper.getInstance().username)
					call.execute()
				}
				if (response.isSuccessful)
				{
					subscriber.onNext(response.body()!!)
					subscriber.onComplete()
				}
				else
				{
					Logs.i(TAG, "onCreate: " + response.errorBody()?.string())
				}
			}

			observable.subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(observer)
		}
	}

}
