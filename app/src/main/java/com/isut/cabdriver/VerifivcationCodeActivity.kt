package com.isut.cabdriver

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import com.google.firebase.database.DatabaseReference
import android.widget.TextView
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import android.os.Bundle
import com.google.firebase.FirebaseApp
import android.content.Intent
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.FirebaseException
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.android.material.snackbar.Snackbar
import android.view.View
import org.json.JSONException
import okhttp3.RequestBody
import com.isut.cabdriver.apiclient.APIClient
import com.isut.cabdriver.apiclient.APIInterface
import com.isut.cabdriver.model.RegisterModel
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class VerifivcationCodeActivity : AppCompatActivity() {
    private var mVerificationId: String? = null
    var btnlogin: AppCompatButton? = null

    //The edittext to input the code
    private var editTextCode: TextInputEditText? = null

    //firebase auth object
    private var mAuth: FirebaseAuth? = null
    var jsonObject = JSONObject()
    var progress: ProgressDialog? = null
    var mobile: String? = null
    var name: String? = null
    var password: String? = null
    var email: String? = null
    var cabno: String? = null
    var cabmodel: String? = null
    var linsence: String? = null
    var location: String? = null
    var licenceExpired: String? = null
    var selected_country_code: String? = null
    var reference: DatabaseReference? = null
    var tv_rent: TextView? = null
    private var mResendToken: ForceResendingToken? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifivcation_code)
        mAuth = FirebaseAuth.getInstance()
        editTextCode = findViewById(R.id.etname)
        tv_rent = findViewById(R.id.tv_rent)
        btnlogin = findViewById(R.id.btnlogin)
       // reference = FirebaseDatabase.getInstance().getReference("Driver")
        FirebaseApp.initializeApp(this)
        val user = mAuth!!.currentUser
        if (user != null) {
            mAuth!!.signOut()
            //    Toast.makeText(this, user.getEmail() + " Sign out!", Toast.LENGTH_SHORT).show();
        } else {
            //   Toast.makeText(this, "You aren't login Yet!", Toast.LENGTH_SHORT).show();
        }
        //getting mobile number from the previous activity
        //and sending the verification code to the number
        val intent = intent
        if (intent != null) {
            mobile = intent.getStringExtra("mobile")
            password = intent.getStringExtra("password")
            name = intent.getStringExtra("name")
            email = intent.getStringExtra("email")
            cabmodel = intent.getStringExtra("canmodel")
            cabno = intent.getStringExtra("cabno")
            linsence = intent.getStringExtra("lincnse")
            location = intent.getStringExtra("location")
            licenceExpired = intent.getStringExtra("licenceExpired")
            selected_country_code = intent.getStringExtra("code2")
        }
        sendVerificationCode(mobile, selected_country_code)
        btnlogin?.setOnClickListener(View.OnClickListener {
            val code = editTextCode?.getText().toString().trim { it <= ' ' }
            if (code.isEmpty() || code.length < 6) {
                editTextCode?.setError("Enter valid code")
                editTextCode?.requestFocus()
                return@OnClickListener
            }

            //verifying the code entered manually
            verifyVerificationCode(code)
        })
        tv_rent?.setOnClickListener(View.OnClickListener {
            resendVerificationCode(
                editTextCode?.getText().toString(), mResendToken
            )
        })
    }

    private fun resendVerificationCode(phoneNumber: String, token: ForceResendingToken?) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(10, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(token) // ForceResendingToken from callbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun sendVerificationCode(mobile: String?, cide: String?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            cide + mobile,  // Phone number to verify
            120,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallbacks
        )
    }

    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                //Getting the code sent by SMS
                val code = phoneAuthCredential.smsCode

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null) {
                    editTextCode!!.setText(code)
                    //verifying the code
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@VerifivcationCodeActivity, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                mResendToken = forceResendingToken

                //storing the verification id that is sent to the user
                mVerificationId = s
            }
        }

    private fun verifyVerificationCode(code: String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(mVerificationId, code)

        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@VerifivcationCodeActivity) { task ->
                if (task.isSuccessful) {
                    progress = ProgressDialog(
                        this@VerifivcationCodeActivity,
                        R.style.AppCompatAlertDialogStyle
                    )
                    progress!!.setTitle("Loading")
                    progress!!.setMessage("Wait while loading...")
                    progress!!.show()
                    submit()

                } else {

                    //verification unsuccessful.. display an error message
                    var message = "Somthing is wrong, we will fix it soon..."
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered..."
                    }
                    val snackbar =
                        Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG)
                    snackbar.setAction("Dismiss") { }
                    snackbar.show()
                }
            }
    }

    fun submit() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        try {
            jsonObject.put("fullName", name)
            jsonObject.put("mobileNumber", mobile)
            jsonObject.put("role", 3)
            jsonObject.put("email", email)
            jsonObject.put("password", password)
            jsonObject.put("licenseExpired", licenceExpired)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val bodyRequest =
            RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.signup(bodyRequest)
        call?.enqueue(object : Callback<RegisterModel> {
            override fun onResponse(call: Call<RegisterModel>, response: Response<RegisterModel>) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                    val myObj = response.body()


                    /* Intent i = new Intent(SignupActivity.this,HomeActivity.class);
                    startActivity(i);*/
                    //getotp();
                    //userdatwe(response.body().getUserId(),phone);
                    // int ids = response.body().get();
                    val i = Intent(this@VerifivcationCodeActivity, LogInActivity::class.java)
                    // edit.putInt(User_ID,myObj.getData().getId());
                    //edit.putString(IN_lOGIN,"IN_lOGIN");
                    //edit.apply();
                    startActivity(i)
                    finish()
                    Toast.makeText(
                        this@VerifivcationCodeActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                } else {
                    Toast.makeText(
                        this@VerifivcationCodeActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<RegisterModel>, t: Throwable) {}
        })
    }
}