package com.isut.cabdriver

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.isut.cabdriver.adapter.RideListAdapter
import android.widget.TextView
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.isut.cabdriver.apiclient.APIClient
import com.isut.cabdriver.apiclient.APIInterface
import com.isut.cabdriver.model.cabs.CabsModel
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.isut.cabdriver.model.cabs.DataItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class HistroyActivity : AppCompatActivity() {
    var rec_his: RecyclerView? = null
    var imgsback: ImageView? = null
    var rideListAdapter: RideListAdapter? = null
    var progress: ProgressDialog? = null
    var Header = "Bearer"
    var cablist: List<DataItem> = ArrayList()
    var tv_empty: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histroy)
        imgsback = findViewById(R.id.imgsback)
        rec_his = findViewById(R.id.rec_his)
        tv_empty = findViewById(R.id.tv_empty)
        progress = ProgressDialog(this@HistroyActivity, R.style.AppCompatAlertDialogStyle)
        progress!!.setTitle("Loading")
        progress!!.setMessage("Wait while loading...")
        progress!!.show()
        submit()

        imgsback!!.setOnClickListener {
            onBackPressed()
        }
    }

    fun submit() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")
        val id = sharedpreferences.getInt(FixValue.User_ID, 0)

        // RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val url = FixValue.baseurl + "driver/" + id + "/bookings"
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.gercablist(Header, url)
        call?.enqueue(object : Callback<CabsModel> {
            override fun onResponse(call: Call<CabsModel>, response: Response<CabsModel>) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                    val myObj = response.body()
                    cablist = response.body()!!.data
                    if (cablist.size != 0) {
                        rideListAdapter = RideListAdapter(this@HistroyActivity, cablist)
                        rec_his!!.adapter = rideListAdapter
                        rec_his!!.setHasFixedSize(true)
                        val linearLayoutManager1 = LinearLayoutManager(
                            this@HistroyActivity,
                            LinearLayoutManager.VERTICAL,
                            true
                        )
                        rec_his!!.layoutManager = linearLayoutManager1

                        /* Intent i = new Intent(SignupActivity.this,HomeActivity.class);
                    startActivity(i);*/
                        //getotp();
                        //userdatwe(response.body().getUserId(),phone);
                        // int ids = response.body().get();
                        Toast.makeText(
                            this@HistroyActivity,
                            "" + response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        progress!!.dismiss()
                    } else {
                        tv_empty!!.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        this@HistroyActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<CabsModel>, t: Throwable) {
                progress!!.dismiss()
            }
        })
    }
}