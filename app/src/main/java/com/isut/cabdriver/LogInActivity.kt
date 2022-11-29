package com.isut.cabdriver

import android.R.attr.country
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.iid.FirebaseInstanceId
import com.hbb20.CountryCodePicker
import com.isut.cabdriver.apiclient.APIClient
import com.isut.cabdriver.apiclient.APIInterface
import com.isut.cabdriver.model.logins.LogInModels
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LogInActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener{
    var tv_singup: TextView? = null
    var spinners: Spinner? = null
    var tv_forgotpass: TextView? = null
    var etname: TextInputEditText? = null
    var etpassword: TextInputEditText? = null
    var btnlogin: Button? = null
    var progress: ProgressDialog? = null
    var mainout: ConstraintLayout? = null

    var jsonObject = JSONObject()
    var token: String? = null
    var codeList: ArrayList<String>? = ArrayList()
    var codeList2: ArrayList<String>? = ArrayList()
    var selected_country_code: String? = null
    var ccp: CountryCodePicker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        tv_singup = findViewById(R.id.tv_singup)
        spinners = findViewById(R.id.spinners)
        etpassword = findViewById(R.id.etpass)
        etname = findViewById(R.id.etname)
        mainout = findViewById(R.id.mainout)
        codeList!!.add("USA:+1")
        codeList!!.add("IND:+91")
        codeList2!!.add("+1")
        codeList2!!.add("+91")
        spinners?.setOnItemSelectedListener(this);
        tv_forgotpass = findViewById(R.id.tv_forgotpass)
        btnlogin = findViewById(R.id.btnlogin)
        ccp = findViewById(R.id.ccp)
        val aa: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codeList!!)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        //Setting the ArrayAdapter data on the Spinner
        spinners!!.setAdapter(aa)
        tv_singup?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        btnlogin?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                token = task.result.token
                Log.d("TAG", token!!)
            })
        tv_forgotpass?.setOnClickListener {
            val intent = Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }

    fun onCountryPickerClick(view: View?) {
        ccp!!.setOnCountryChangeListener { //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
          //  selected_country_code = ccp!!.selectedCountryCodeWithPlus

        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_singup -> {
                val i = Intent(this@LogInActivity, SignupActivity::class.java)
                startActivity(i)
            }
            R.id.btnlogin -> {
                val strphne = etname!!.text.toString()
                val strpass = etpassword!!.text.toString()
                if (strphne.isEmpty()) {
                    // firstname.setError(getString(R.string.input_error_name));
                    Snackbar.make(mainout!!, getString(R.string.enter_phone), Snackbar.LENGTH_SHORT)
                        .setActionTextColor(
                            Color.WHITE
                        ).show()
                    etname!!.requestFocus()
                    return
                }
                    if (strpass.isEmpty()) {
                        // firstname.setError(getString(R.string.input_error_name));
                        Snackbar.make(mainout!!, getString(R.string.enter_pass), Snackbar.LENGTH_SHORT)
                            .setActionTextColor(
                                Color.WHITE
                            ).show()
                        etpassword!!.requestFocus()
                        return
                    }

                       // selected_country_code = ccp!!.selectedCountryCodeWithPlus

                        val intent =
                            Intent(this@LogInActivity, LoginVerifivcationCodeActivity::class.java)
                        intent.putExtra("mobile", strphne)
                        intent.putExtra("password", strpass)
                        intent.putExtra("code2", selected_country_code)
                        startActivity(intent)
                    }

            }
        }



    fun submit() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        try {
            jsonObject.put("username", etname!!.text.toString().trim { it <= ' ' })
            jsonObject.put("password", etname!!.text.toString().trim { it <= ' ' })
            jsonObject.put("appId", token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val bodyRequest =
            RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.login(bodyRequest)
        call?.enqueue(object : Callback<LogInModels> {
            override fun onResponse(call: Call<LogInModels>, response: Response<LogInModels>) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                    val myObj = response.body()


                    /* Intent i = new Intent(SignupActivity.this,HomeActivity.class);
                    startActivity(i);*/
                    //getotp();
                    //userdatwe(response.body().getUserId(),phone);
                    // int ids = response.body().get();
                    val i = Intent(this@LogInActivity, MainActivity::class.java)
                    edit.putInt(FixValue.User_ID, myObj!!.data.userDetails.id)
                    edit.putString(FixValue.FIRSTNAME, myObj.data.userDetails.fullName)
                    edit.putString(FixValue.MOBILE, myObj.data.userDetails.mobileNumber)
                    edit.putString(FixValue.TOKEN, response.body()!!.data.token)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    edit.putString(FixValue.IN_lOGIN, "IN_lOGIN")
                    edit.apply()
                    startActivity(i)
                    finish()
                    Toast.makeText(
                        this@LogInActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                } else {
                    Toast.makeText(
                        this@LogInActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<LogInModels>, t: Throwable) {}
        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selected_country_code = codeList2?.get(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}