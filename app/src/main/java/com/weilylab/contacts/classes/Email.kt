package com.weilylab.contacts.classes

import java.io.Serializable

/**
 * Created by myste.
 */
class Email: Serializable
{
	var emailID = 0
	lateinit var emailAddress: String
	var contactID = 0
}