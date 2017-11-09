package com.weilylab.contacts

import android.widget.ImageButton
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList


/**
 * Created by mystery0.
 */
class PhoneEmailAdapter(
		private val showList: ArrayList<String>) : RecyclerView.Adapter<PhoneEmailAdapter.ViewHolder>()
{
	private var deleteListener: DeleteListener? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_email_phone, parent, false)
		return ViewHolder(view)
	}

	fun setDeleteListener(deleteListener: DeleteListener)
	{
		this.deleteListener = deleteListener
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.textView.text = showList[position]
		holder.btn_delete.setOnClickListener {
			notifyItemRemoved(position)
			showList.removeAt(position)
			deleteListener?.onDelete(position)
		}
	}

	override fun getItemCount(): Int
	{
		return showList.size
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		var textView: TextView = itemView.findViewById(R.id.email_phone)
		var btn_delete: ImageButton = itemView.findViewById(R.id.delete)
	}

	interface DeleteListener
	{
		fun onDelete(position: Int)
	}
}