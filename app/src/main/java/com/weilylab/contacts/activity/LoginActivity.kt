package com.weilylab.contacts.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.weilylab.contacts.ContactHelper
import com.weilylab.contacts.ContactResponse

import com.weilylab.contacts.R
import com.weilylab.contacts.classes.Response
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_login.*
import vip.mystery0.tools.logs.Logs

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity()
{
	companion object
	{
		private val TAG = "LoginActivity"
	}

	private val retrofit = ContactHelper.getInstance().retrofit
	private lateinit var loginDialog: ZLoadingDialog

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		loginDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText("登录中......")

		password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
			if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL)
			{
				attemptLogin()
				return@OnEditorActionListener true
			}
			false
		})

		email_sign_in_button.setOnClickListener { attemptLogin() }
	}

	private fun attemptLogin()
	{
		username.error = null
		password.error = null

		val usernameStr = username.text.toString()
		val passwordStr = password.text.toString()

		var cancel = false
		var focusView: View? = null

		if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr))
		{
			password.error = getString(R.string.error_invalid_password)
			focusView = password
			cancel = true
		}

		if (TextUtils.isEmpty(usernameStr))
		{
			username.error = getString(R.string.error_field_required)
			focusView = username
			cancel = true
		}

		if (cancel)
		{
			focusView?.requestFocus()
		}
		else
		{
			login()
		}
	}

	private fun isPasswordValid(password: String): Boolean
	{
		return password.length > 3
	}

	private fun login()
	{
		val usernameStr = username.text.toString()
		val passwordStr = password.text.toString()

		val observer = object : Observer<Response>
		{
			private lateinit var response: Response

			override fun onSubscribe(d: Disposable)
			{
				Logs.i(TAG, "onSubscribe: ")
				loginDialog.show()
			}

			override fun onNext(t: Response)
			{
				Logs.i(TAG, "onNext: " + t.code)
				response = t
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
				loginDialog.dismiss()
			}

			override fun onComplete()
			{
				Logs.i(TAG, "onComplete: ")
				loginDialog.dismiss()
				if (response.code == 0)
				{
					ContactHelper.getInstance().username = usernameStr
					startActivity(Intent(this@LoginActivity, MainActivity::class.java))
					finish()
				}
				else
				{
					Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT)
							.show()
				}
			}
		}

		val observable = Observable.create<Response> { subscriber ->
			val service = retrofit.create(ContactResponse::class.java)
			val loginCall = service.login(usernameStr, passwordStr)
			val loginResult = loginCall.execute()
			if (loginResult.isSuccessful)
			{
				val loginResponse = loginResult.body()!!
				subscriber.onNext(loginResponse)
				if (loginResponse.code == 0)
				{
					subscriber.onComplete()
					return@create
				}
				if (loginResponse.code == 1)
				{
					val registerCall = service.register(usernameStr, passwordStr)
					val registerResult = registerCall.execute()
					if (registerResult.isSuccessful)
					{
						val registerResponse = registerResult.body()!!
						subscriber.onNext(registerResponse)
						subscriber.onComplete()
					}
					else
					{
						subscriber.onError(Exception("error"))
					}
					return@create
				}
				subscriber.onComplete()
			}
			else
			{
				subscriber.onError(Exception("error"))
			}
		}

		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}
}
