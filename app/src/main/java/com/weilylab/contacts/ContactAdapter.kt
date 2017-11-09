package com.weilylab.contacts

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.weilylab.contacts.activity.NewContactActivity
import com.weilylab.contacts.classes.Contact
import com.weilylab.contacts.classes.Response
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.util.ArrayList

/**
 * Created by mystery0.
 */
class ContactAdapter(private val context: Context,
					 private val list: ArrayList<Contact>) : RecyclerView.Adapter<ContactAdapter.ViewHolder>()
{
	companion object
	{
		private val TAG = "ContactAdapter"
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val contact = list[position]
		holder.textViewName.text = contact.contactName
		if (contact.phoneList.size > 0)
			holder.textViewPhone.text = contact.phoneList[0].phoneNumber
		else
			holder.textViewPhone.text = ""
		holder.itemView.setOnClickListener {
			val items = Array(2, { i ->
				when (i)
				{
					0 -> "删除"
					1 -> "更改"
					else -> "null"
				}
			})
			AlertDialog.Builder(context)
					.setTitle("请选择操作")
					.setItems(items) { _, which ->
						request(which, contact, position)
					}
					.show()
		}
	}

	private fun request(which: Int, contact: Contact, position: Int)
	{
		if (which == 1)
		{
			val intent = Intent(context, NewContactActivity::class.java)
			intent.putExtra("contact", contact)
			context.startActivity(intent)
			return
		}
		val observer = object : Observer<Response>
		{
			private lateinit var dialog: ZLoadingDialog
			private lateinit var response: Response

			override fun onSubscribe(d: Disposable)
			{
				Logs.i(TAG, "onSubscribe: ")
				dialog = ZLoadingDialog(context)
						.setLoadingBuilder(Z_TYPE.STAR_LOADING)
						.setHintText("请求数据中......")
				dialog.show()
			}

			override fun onNext(t: Response)
			{
				Logs.i(TAG, "onNext: " + t.code)
				response = t
			}

			override fun onComplete()
			{
				dialog.dismiss()
				Logs.i(TAG, "onComplete: ")
				Toast.makeText(context, response.message, Toast.LENGTH_SHORT)
						.show()
				if (response.code == 0 && which == 0)
				{
					list.remove(contact)
					notifyItemRemoved(position)
				}
			}

			override fun onError(e: Throwable)
			{
				dialog.dismiss()
				e.printStackTrace()
				Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
						.show()
			}
		}

		val observable = Observable.create<Response>({ subscriber ->
			val service = ContactHelper.getInstance().retrofit.create(ContactResponse::class.java)
			val call = when (which)
			{
				0 -> service.contactDelete(contact.contactName, "delete", ContactHelper.getInstance().username)
				else -> throw NullPointerException("error")
			}
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

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount(): Int
	{
		return list.size
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		var textViewName: TextView = itemView.findViewById(R.id.textView_name)
		var textViewPhone: TextView = itemView.findViewById(R.id.textView_phone)
	}
}