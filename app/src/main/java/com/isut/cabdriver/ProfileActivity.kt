package com.isut.cabdriver

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.content.SharedPreferences
import com.mikhaellopez.circularimageview.CircularImageView
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.isut.cabdriver.apiclient.APIClient
import com.isut.cabdriver.apiclient.APIInterface
import com.isut.cabdriver.model.cabdetails.CabDetailsModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.isut.cabdriver.model.UpdateResponse
import com.isut.cabdriver.model.newuser.NewUserResponse
import com.isut.cabdriver.model.rewardpoint.RewardResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ProfileActivity : AppCompatActivity() {
    var txtname: TextView? = null
    var txtemail: TextView? = null
    var tv_tilte: TextView? = null
    var txtmobile: TextView? = null
    var tvcarmodel: TextView? = null
    var tvcarnum: TextView? = null
    var tv_report: TextView? = null
    var tv_lince: TextView? = null
    var tx_identy: TextView? = null
    var tvcarname: TextView? = null
    var editprofile: TextView? = null
    var txtb_place: TextView? = null
    var tv_edit: TextView? = null
    var tvstate: TextView? = null
    var tv_delete_account: TextView? = null
    var tv_btntaddcab: TextView? = null
    var sharedpreferences: SharedPreferences? = null
    var imageView24: CircularImageView? = null
    var progress: ProgressDialog? = null
    var strimgas: String? = null
    var Header = "Bearer"
    var jsonObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        editprofile = findViewById(R.id.editprofile)
        txtname = findViewById(R.id.textView58)
        tv_delete_account = findViewById(R.id.tv_delete_account)
        tv_edit = findViewById(R.id.tv_edit)
        tv_report = findViewById(R.id.tv_report)
        tv_btntaddcab = findViewById(R.id.tv_btntaddcab)
        txtmobile = findViewById(R.id.textView59)
        txtemail = findViewById(R.id.emailtxt)
        tvcarname = findViewById(R.id.gendertxt)
        tx_identy = findViewById(R.id.txtb_time)
        tv_lince = findViewById(R.id.txtrferrae)
        tvstate = findViewById(R.id.txtb_profession)
        txtb_place = findViewById(R.id.txtb_place)
        tvcarmodel = findViewById(R.id.dobtxt)
        tvcarnum = findViewById(R.id.txtwollet)
        imageView24 = findViewById(R.id.imageView24)
        progress = ProgressDialog(this@ProfileActivity, R.style.AppCompatAlertDialogStyle)
        progress!!.setTitle("Loading")
        progress!!.setMessage("Wait while loading...")
        progress!!.show()
        submit()
        tv_delete_account?.setOnClickListener {
            logdialog()
        }
        val name = sharedpreferences?.getString(FixValue.FIRSTNAME, "")
        val mobile = sharedpreferences?.getString(FixValue.MOBILE, "")
        val email = sharedpreferences?.getString(FixValue.EMAIL, "")
        tv_edit?.setOnClickListener {
            EditDialog(name.toString(),email.toString(),mobile.toString())

        }
    }
    fun  EditDialog(name: String, email: String, mobile: String)
    {
        val view: View = layoutInflater.inflate(R.layout.dailog_edit_profile, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Update Pofile")
        alertDialog.setCancelable(false)
        val tvname = view.findViewById(R.id.etname) as EditText
        val btnsave = view.findViewById(R.id.btnsave) as AppCompatButton
        val btncancel = view.findViewById(R.id.btncancel) as AppCompatButton
        val etphone = view.findViewById(R.id.etphone) as EditText
        val tvemail = view.findViewById(R.id.etemamil) as EditText
        tvname.setText(name)
        etphone.setText(mobile)
        tvemail.setText(email)

        btnsave.setOnClickListener {
            progress = ProgressDialog(this)
            progress!!.setTitle("Loading")
            progress!!.setMessage("Wait while loading...")
            progress!!.setCancelable(false) // disable dismiss by tapping outside of the dialog
            progress!!.show()
            updateProfile(tvname.text.toString(),etphone.text.toString(), tvemail.text.toString())
            alertDialog.dismiss();
        }
        btncancel.setOnClickListener {
            alertDialog.cancel();
        }



        alertDialog.setView(view);
        alertDialog.show();
    }

    fun updateProfile(name: String, phone: String, email: String) {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        val  id =sharedpreferences.getInt(FixValue.User_ID, 0)

        try {
            jsonObject.put("fullName", name)
            jsonObject.put("id", id.toString())
            jsonObject.put("email", email)
            jsonObject.put("mobileNumber", phone)
            jsonObject.put("role", "3")
            //  jsonObject.put("appId", token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val  Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")

        val bodyRequest =
            RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.update(Header,bodyRequest)

        call?.enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                    Toast.makeText(this@ProfileActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    edit.putInt(FixValue.User_ID, response.body()?.data!!.id)
                    edit.putString(FixValue.MOBILE, response.body()?.data!!.mobileNumber)
                    edit.putString(FixValue.FIRSTNAME, response.body()!!.data!!.fullName)
                    edit.putString(FixValue.EMAIL, response.body()!!.data!!.email)
                    txtname?.setText(response.body()!!.data!!.fullName)
                    txtemail?.setText(response.body()!!.data!!.email)
                    txtmobile?.setText(response.body()!!.data!!.mobileNumber)
                    edit.apply()
                    progress!!.dismiss()
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {

            }
        })
    }

    fun submit() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")
        val id = sharedpreferences.getInt(FixValue.User_ID, 0)
        val name = sharedpreferences.getString(FixValue.FIRSTNAME, "")
        val mobile = sharedpreferences.getString(FixValue.MOBILE, "")
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val url = FixValue.baseurl + "customer/" + id + "/cabs"
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.cabdetals(Header, url)
        call?.enqueue(object : Callback<NewUserResponse> {
            override fun onResponse(
                call: Call<NewUserResponse>,
                response: Response<NewUserResponse>
            ) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                    val myObj = response.body()!!.data.cabs
                    txtname!!.text = name
                    txtmobile!!.text = mobile
                    txtemail!!.text = response.body()!!.data.email

                    if(response.body()?.data!!.rewardPoints != null)
                        tv_report!!.text = "Reward Point: "+response.body()?.data!!.rewardPoints.toString()
                    else   tv_report!!.text = "Reward Point: 0"

                    for (i in response.body()!!.data.cabs.indices) {
                        tvcarmodel!!.text = response.body()!!.data.cabs[i].carModel
                        tvcarnum!!.text = response.body()!!.data.cabs[i].carNumber
                        if(response.body()!!.data.licenseNumber != null)
                        {
                            tv_lince!!.text = response.body()!!.data.licenseNumber.toString()

                        }
                        else
                        {
                            tv_lince!!.text =""
                        }
                        tx_identy!!.text = response.body()!!.data.cabs[i].identityCard
                        tvcarname!!.text = response.body()!!.data.cabs[i].carName
                        txtb_place!!.text = response.body()!!.data.cabs[i].city
                        tvstate!!.text = response.body()!!.data.cabs[i].state

                        if (response.body()!!.data.cabs[i].carImages != null) {
                            //  getimgpath(myObj.getData().get(i).getCarImages());
                            Glide.with(this@ProfileActivity)
                                .load("http://157.230.81.151:8080/api/file/getFile/" +  response.body()!!.data.cabs[i].carImages)
                                .into(imageView24!!)
                            Glide.with(this@ProfileActivity)
                                .load("http://157.230.81.151:8080/api/file/getFile/" +  response.body()!!.data.cabs[i].carImages)
                                .apply(
                                    RequestOptions().override(100, 100)
                                        .placeholder(R.drawable.user).centerCrop()
                                )
                                .into(imageView24!!)
                        }
                    }
                    Toast.makeText(
                        this@ProfileActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<NewUserResponse>, t: Throwable) {
                progress!!.dismiss()
            }
        })
    }

    fun getimgpath(file: String) {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")
        val signUpMap = HashMap<String, RequestBody>()
        signUpMap["file"] = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        signUpMap["type"] = RequestBody.create(MediaType.parse("multipart/form-data"), "car")
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val url = FixValue.baseurl + "/api/file/car/" + file
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.geimgpath(Header, url)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Glide.with(this@ProfileActivity)
                        .load(
                            "http://143.198.75.109:9090/api/file/getFile/" + response.body()
                                .toString()
                        )
                        .into(imageView24!!)
                    progress!!.dismiss()
                } else {
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progress!!.dismiss()
            }
        })
    }

    fun deleteAccount() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        val  id =sharedpreferences.getInt(FixValue.User_ID, 0)
        val url = FixValue.baseurl.plus("customer/$id/delete")

        val  Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")


        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.deleteAccount(Header,url)

        call?.enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                    Toast.makeText(this@ProfileActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    val preferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.clear()
                    editor.apply()
                    againSetData()

                    val intent = Intent(this@ProfileActivity, LogInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                    progress!!.dismiss()
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {

            }
        })
    }

    fun logdialog() {
        val alert = AlertDialog.Builder(this)
        alert.setMessage("Are you sure you want to Delete Account?")
        alert.setPositiveButton("YES") { dialog, which ->
            progress = ProgressDialog(this)
            progress!!.setTitle("Loading")
            progress!!.setMessage("Wait while loading...")
            progress!!.setCancelable(false) // disable dismiss by tapping outside of the dialog
            progress!!.show()
            deleteAccount()
        }
        alert.setNegativeButton("NO") { dialog, which -> // close dialog
            dialog.cancel()
        }
        alert.show()
    }

    private fun againSetData() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        edit.putString(FixValue.IN_lOGIN, "")
        //edit.putString(IsAppOpenFirst,OUT_LOGIN);
        edit.commit()
    }

}