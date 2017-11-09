package com.weilylab.contacts.classes

import java.io.Serializable

/**
 * Created by myste.
 */
class Contact:Serializable
{
	var contactID = 0
	lateinit var contactName: String
	var contactInit = '0'
	lateinit var contactMark: String
	var phoneList = ArrayList<Phone>()
	var emailList = ArrayList<Email>()
	var userID = 0
	override fun toString(): String
	{
		return "Contact(contactID=$contactID, contactName='$contactName', contactInit=$contactInit, contactMark='$contactMark', phoneList=$phoneList, emailList=$emailList, userID=$userID)"
	}


}