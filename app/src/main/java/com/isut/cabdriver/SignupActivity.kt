package com.isut.cabdriver

import android.app.DatePickerDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.DatabaseReference
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import com.google.firebase.database.FirebaseDatabase
import com.isut.cabdriver.user.UserModel
import com.hbb20.CountryCodePicker
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.android.material.snackbar.Snackbar
import android.util.Patterns
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.*
import org.json.JSONException
import okhttp3.RequestBody
import com.isut.cabdriver.apiclient.APIClient
import com.isut.cabdriver.apiclient.APIInterface
import com.isut.cabdriver.model.RegisterModel
import okhttp3.MediaType
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity : AppCompatActivity(), View.OnClickListener , AdapterView.OnItemSelectedListener{
    var etname: EditText? = null
    var etemamil: EditText? = null
    var etphone: EditText? = null
    var spinners: Spinner? = null
    var codeList: ArrayList<String>? = ArrayList()
    var codeList2: ArrayList<String>? = ArrayList()
    var tvsigns: TextView? = null
    var etcarnane: EditText? = null
    var passwordflag = false
    var cnfpasswordflag = false
    var matchpasswordflag = false
    var etdriver_password: TextView? = null
    var et_confirm_password: TextView? = null
    var etdriver_lince_date: EditText? = null
    var etcarmodel: EditText? = null
    var etlicense: EditText? = null
    var etlocation: EditText? = null
    var btnlogin: AppCompatButton? = null
    var reference: DatabaseReference? = null
    var mainout: ConstraintLayout? = null
    var progress: ProgressDialog? = null
    var jsonObject = JSONObject()
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var nameflag = false
    var emailflag = false
    var dateflag = false
    var phoneflag = false
    var cabnoflag = false
    var cabmodelflag = false
    var datepicker: DatePickerDialog? = null
    var MONTHS= arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    var licenselag = false
    var locationflag = false
    var firebaseDatabase: FirebaseDatabase? = null
    var droverModelList: List<UserModel?> = ArrayList()
    var selected_country_code: String? = null
    var date: String? = null
    var databaseReference: DatabaseReference? = null
    var ccp: CountryCodePicker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        et_confirm_password = findViewById(R.id.et_confirm_password)
        spinners = findViewById(R.id.spinners)
        etdriver_password = findViewById(R.id.etdriver_password)
        etname = findViewById(R.id.etname)
        etemamil = findViewById(R.id.et_email)
        spinners?.setOnItemSelectedListener(this);
        codeList!!.add("USA:+1")
        codeList!!.add("IND:+91")
        codeList2!!.add("+1")
        codeList2!!.add("+91")
        etdriver_lince_date = findViewById(R.id.etdriver_lince_date)
        etphone = findViewById(R.id.etphone)
        etcarnane = findViewById(R.id.etcarnane)
        tvsigns = findViewById(R.id.tvsigns)
        val aa: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codeList!!)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        //Setting the ArrayAdapter data on the Spinner
        spinners!!.setAdapter(aa)
        ccp = findViewById(R.id.ccp)
        etlocation = findViewById(R.id.etlocation)
        btnlogin = findViewById(R.id.btnlogin)
        mainout = findViewById(R.id.mainout)
        tvsigns?.text = "@ilstu.edu"
       // reference = FirebaseDatabase.getInstance().getReference("Driver")
        FirebaseApp.initializeApp(this)
        btnlogin?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        etdriver_lince_date?.setOnClickListener {
            calender(etdriver_lince_date!!)
        }
    }

    fun onCountryPickerClick(view: View?) {
        ccp!!.setOnCountryChangeListener { //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
            //selected_country_code = ccp!!.selectedCountryCodeWithPlus
        }
    }
    fun calender(editText: TextView) {
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]
        var fm  = ""
        var months  = ""

        // date picker dialog
        datepicker = DatePickerDialog(this, R.style.MySpinnerDatePickerStyle,
            { view, year, monthOfYear, dayOfMonth -> //
                var monthname :String = MONTHS[monthOfYear];
                fm = dayOfMonth.toString()
                months = dayOfMonth.toString()
                val aa = monthOfYear + 1
                if (dayOfMonth<10){
                    fm="0"+dayOfMonth
                }
                if (aa<10){
                    months="0"+aa
                }
                cldr.set(year,monthOfYear,dayOfMonth)

                val dates = dayOfMonth.toString()+"-"+monthOfYear+"-"+year
                date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(cldr.time);
                Log.e("aaa","aaa"+date)
                editText.setText("$dayOfMonth-$monthname-$year")

            }, year, month, day)
        datepicker!!
            .getDatePicker().setSpinnersShown(true)
        datepicker?.datePicker?.minDate = (System.currentTimeMillis());
        datepicker!!.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnlogin -> register()
        }
    }

    fun register() {
        val strname = etname!!.text.toString()
        val stremail = etemamil!!.text.toString()
        val strphne = etphone!!.text.toString()
        val strpass = etdriver_password!!.text.toString()
        val strcnfpass = et_confirm_password!!.text.toString()

        val expiredate = etdriver_lince_date!!.text.toString()
        if (strname.isEmpty()) {
            // firstname.setError(getString(R.string.input_error_name));
            Snackbar.make(mainout!!, getString(R.string.enter_name), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            nameflag = false
            etname!!.requestFocus()
            return
        } else {
            nameflag = true
        }
        if (strphne.isEmpty()) {
            //editTextEmail.setError(getString(R.string.input_error_email));
            Snackbar.make(mainout!!, getString(R.string.enter_phone), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            phoneflag = false
            etphone!!.requestFocus()
            return
        } else {
            phoneflag = true
        }
        emailflag = if (stremail.contains("@") && stremail.isEmpty()  ) {
            Snackbar.make(mainout!!, getString(R.string.enter_mail), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            false
        } else {

            true
        }
        dateflag = if ( expiredate.isEmpty()  ) {
            Snackbar.make(mainout!!, getString(R.string.enter_date), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            false
        } else {

            true
        }

        passwordflag = if (strpass.isEmpty()  ) {
            Snackbar.make(mainout!!, getString(R.string.enter_pass), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            false
        } else {

            true
        }
        cnfpasswordflag = if (strcnfpass.isEmpty()  ) {
            Snackbar.make(mainout!!, getString(R.string.enter_cnf), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            false
        } else {

            true
        }
        matchpasswordflag = if (!strcnfpass.equals(strcnfpass)  ) {
            Snackbar.make(mainout!!, getString(R.string.enter_cnf), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            false
        } else {

            true
        }

        if (nameflag && phoneflag && emailflag && passwordflag && cnfpasswordflag  && matchpasswordflag) {


            //  progress = new ProgressDialog(SignupActivity.this, R.style.AppCompatAlertDialogStyle);
            //progress.setTitle("Loading");
            //progress.setMessage("Wait while loading...");
            //  progress.show();
            //  submit();
          //  selected_country_code = ccp!!.selectedCountryCodeWithPlus
            val intent = Intent(this@SignupActivity, VerifivcationCodeActivity::class.java)
            intent.putExtra("mobile", strphne)
            intent.putExtra("name", strname)
            intent.putExtra("email", stremail+tvsigns?.text.toString())
            intent.putExtra("password", strpass)
            intent.putExtra("cabno", "")
            intent.putExtra("canmodel", "")
            intent.putExtra("lincnse", "")
            intent.putExtra("location", "")
            intent.putExtra("licenceExpired", date)
            intent.putExtra("code2", selected_country_code)
            startActivity(intent)
        }
    }

    fun submit() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        try {
            jsonObject.put("fullName", etname!!.text.toString())
            jsonObject.put("mobileNumber", etphone!!.text.toString())
            jsonObject.put("role", 1)
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
                    val i = Intent(this@SignupActivity, LogInActivity::class.java)
                    // edit.putInt(User_ID,myObj.getData().getId());
                    //edit.putString(IN_lOGIN,"IN_lOGIN");
                    //edit.apply();
                    startActivity(i)
                    finish()
                    Toast.makeText(
                        this@SignupActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<RegisterModel>, t: Throwable) {}
        })
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selected_country_code = codeList2?.get(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}