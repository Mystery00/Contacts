package com.weilylab.contacts.classes

/**
 * Created by myste.
 */
class Contact
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