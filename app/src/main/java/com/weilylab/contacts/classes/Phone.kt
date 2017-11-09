package com.weilylab.contacts.classes

import java.io.Serializable

/**
 * Created by myste.
 */
class Phone:Serializable
{
	var phoneID = 0
	lateinit var phoneNumber: String
	lateinit var phoneType: String
	var contactID = 0
}