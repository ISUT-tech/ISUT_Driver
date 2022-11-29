package com.isut.cabdriver

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import android.graphics.Bitmap
import org.json.JSONObject
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import okhttp3.RequestBody
import com.isut.cabdriver.apiclient.APIClient
import com.isut.cabdriver.apiclient.APIInterface
import com.isut.cabdriver.model.addcar.AddCarModel
import android.content.Intent
import android.widget.Toast
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.content.pm.ActivityInfo
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import okhttp3.MultipartBody
import com.isut.cabdriver.model.cabimg.CabImgModel
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.Exception
import java.util.HashMap

class CabFiilActivity : AppCompatActivity(), View.OnClickListener {
    var etname: EditText? = null
    var etemamil: EditText? = null
    var etphone: EditText? = null
    var etcarnane: EditText? = null
    var etcarmodel: EditText? = null
    var etlicense: EditText? = null
    var etlocation: EditText? = null
    var etidenty: EditText? = null
    var radioSudan: RadioButton? = null
    var radioSUv: RadioButton? = null
    var etlocation2: EditText? = null
    var btnlogin: AppCompatButton? = null
    var mainout: ConstraintLayout? = null
    var cabimg: ImageView? = null
    var progress: ProgressDialog? = null
    var picturePath: String? = null
    var filename: String? = null
    var bitmap: Bitmap? = null
    var photfile: File? = null
    var cabType: String? = "0"
    var jsonObject = JSONObject()
    var Header = "Bearer"
    var strilepath: String? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var nameflag = false
    var emailflag = false
    var iscabtype = false
    var phoneflag = false
    var cabnoflag = false
    var cabmodelflag = false
    var licenselag = false
    var locationflag = false
    var itenfityflag = false
    var statflag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cab_fiil)
        etname = findViewById(R.id.etname)
        etlocation2 = findViewById(R.id.etlocation2)
        radioSudan = findViewById(R.id.radioSudan)
        etidenty = findViewById(R.id.etidenty)
        radioSUv = findViewById(R.id.radioSUv)
        etemamil = findViewById(R.id.etemamil)
        etphone = findViewById(R.id.etphone)
        etcarnane = findViewById(R.id.etcarnane)
        etcarmodel = findViewById(R.id.etcarmodel)
        etlicense = findViewById(R.id.etlicense)
        etlocation = findViewById(R.id.etlocation)
        btnlogin = findViewById(R.id.btnlogin)
        mainout = findViewById(R.id.mainout)
        cabimg = findViewById(R.id.cabimg)
        btnlogin?.setOnClickListener(this)
        cabimg?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnlogin -> register()
            R.id.cabimg -> if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    PERMISSION_WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectImage()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(PERMISSION_WRITE_EXTERNAL_STORAGE, CAMERA), 1)
                }
            }
        }

        radioSudan?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                radioSudan?.isChecked = true
                radioSUv?.isChecked = false
                cabType = "0";
            }
                //Do Whatever you want in isChecked
            }
        radioSUv?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    radioSudan?.isChecked = false
                    radioSUv?.isChecked = true
                    cabType = "1";
                }
                    //Do Whatever you want in isChecked
                }
    }

    fun register() {
        val strname = etname!!.text.toString()
        val stremail = etemamil!!.text.toString()
        val strphne = etphone!!.text.toString()
        val strcabno = etcarnane!!.text.toString()
        val strcabmodel = etcarmodel!!.text.toString()
        val strlincsne = etlicense!!.text.toString()
        val strlocation = etlocation!!.text.toString()
        val stritenty = etidenty!!.text.toString()
        val strstate = etlocation2!!.text.toString()
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

        if (strcabno.isEmpty()) {
            //editTextEmail.setError(getString(R.string.input_error_email));
            Snackbar.make(mainout!!, getString(R.string.enter_cabno), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            cabnoflag = false
            etcarnane!!.requestFocus()
            return
        } else {
            cabnoflag = true
        }
  if (cabType == null) {
            //editTextEmail.setError(getString(R.string.input_error_email));
            Snackbar.make(mainout!!, getString(R.string.enter_cabtype), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
      iscabtype = false
            etcarnane!!.requestFocus()
            return
        } else {
            iscabtype = true
        }




        if (strcabmodel.isEmpty()) {
            //editTextEmail.setError(getString(R.string.input_error_email));
            Snackbar.make(mainout!!, getString(R.string.enter_cabname), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            cabmodelflag = false
            etcarmodel!!.requestFocus()
            return
        } else {
            cabmodelflag = true
        }
        if (strlincsne.isEmpty()) {
            //editTextEmail.setError(getString(R.string.input_error_email));
            Snackbar.make(mainout!!, getString(R.string.enter_linsence), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            licenselag = false
            etlicense!!.requestFocus()
            return
        } else {
            licenselag = true
        }

        if (strlocation.isEmpty()) {
            //editTextEmail.setError(getString(R.string.input_error_email));
            Snackbar.make(mainout!!, getString(R.string.enterloc), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            locationflag = false
            etlocation!!.requestFocus()
            return
        } else {
            locationflag = true
        }
        if (strlocation.isEmpty()) {
            //editTextEmail.setError(getString(R.string.input_error_email));
            Snackbar.make(mainout!!, getString(R.string.enterstate), Snackbar.LENGTH_SHORT)
                .setActionTextColor(
                    Color.WHITE
                ).show()
            statflag = false
            etlocation2!!.requestFocus()
            return
        } else {
            statflag = true
        }
        if (nameflag  && cabnoflag && cabmodelflag && iscabtype && licenselag  && locationflag && statflag) {
            progress = ProgressDialog(this@CabFiilActivity, R.style.AppCompatAlertDialogStyle)
            progress!!.setTitle("Loading")
            progress!!.setMessage("Wait while loading...")
            progress!!.show()
            submit(
                strname,
                strcabno,
                strcabmodel,
                stremail,
                stritenty,
                strlincsne,
                strlocation,
                strstate,cabType!!
            )
            /*  Intent intent = new Intent(SignupActivity.this, VerifivcationCodeActivity.class);
                    intent.putExtra("mobile", strphne);
                    intent.putExtra("name", strname);
                    intent.putExtra("email", stremail);
                    intent.putExtra("cabno", strcabno);
                    intent.putExtra("canmodel", strcabmodel);
                    intent.putExtra("lincnse", strlincsne);
                    intent.putExtra("location", strlocation);
                    startActivity(intent);
*/
        }
    }

    fun submit(
        strname: String?,
        strcabno: String?,
        strcabmodel: String?,
        stremail: String?,
        stritenty: String?,
        strlincsne: String?,
        strlocation: String?,
        strstate: String?,
        cabType: String
    ) {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")
        val id = sharedpreferences.getInt(FixValue.User_ID, 0)
        try {
            jsonObject.put("carName", strname)
            jsonObject.put("carNumber", strcabno)
            jsonObject.put("carModel", strcabmodel)
            jsonObject.put("city", strstate)
            jsonObject.put("email", stremail)
            jsonObject.put("identityCard", stritenty)
            jsonObject.put("licenseNumber", strlincsne)
            jsonObject.put("location", strlocation)
            jsonObject.put("state", strstate)
            jsonObject.put("carType", cabType)
            jsonObject.put("userId", id)
            jsonObject.put("carImages", strilepath)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val bodyRequest =
            RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.addcar(Header, bodyRequest)
        call?.enqueue(object : Callback<AddCarModel> {
            override fun onResponse(call: Call<AddCarModel>, response: Response<AddCarModel>) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                    val myObj = response.body()


                    /* Intent i = new Intent(SignupActivity.this,HomeActivity.class);
                    startActivity(i);*/
                    //getotp();
                    //userdatwe(response.body().getUserId(),phone);
                    // int ids = response.body().get();
                    val i = Intent(this@CabFiilActivity, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(i)
                    Toast.makeText(
                        this@CabFiilActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                } else {
                    Toast.makeText(
                        this@CabFiilActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<AddCarModel>, t: Throwable) {}
        })
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@CabFiilActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                //  File file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/attachments").getPath(),
                //         System.currentTimeMillis()+ ".jpg");
                val f = File(Environment.getExternalStorageDirectory(), "image.jpg")
                val uri = FileProvider.getUriForFile(
                    this@CabFiilActivity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    f
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                Log.d("556677", "saveBitmapToFile: $f")
                photfile = f
                var tempFile: File? = null
                try {
                    tempFile = File.createTempFile(
                        "image",
                        ".jpeg",
                        File(Utility.getTempMediaDirectory(this@CabFiilActivity))
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val captureMediaFile = FileProvider.getUriForFile(
                    this@CabFiilActivity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    f
                )
                Log.e("capturemedia file url", "" + captureMediaFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, captureMediaFile)
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                intent.putExtra(
                    MediaStore.EXTRA_SCREEN_ORIENTATION,
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                )
                startActivityForResult(intent, 1)
            } else if (options[item] == "Choose from Gallery") {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    @SuppressLint("LongLogTag")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //SharedPreferences sharedpreferences = P1_Update_Account.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //SharedPreferences.Editor edit = sharedpreferences.edit();
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                var f = File(Environment.getExternalStorageDirectory().toString())
                for (temp in f.listFiles()) {
                    if (temp.name == "image.jpeg") {
                        f = temp
                        // photfile = f.getAbsoluteFile();
                        break
                    }
                }
                try {
                    val bitmapOptions = BitmapFactory.Options()
                    bitmap = BitmapFactory.decodeFile(
                        photfile!!.absolutePath,
                        bitmapOptions
                    )
                    Log.d("334455", "photfile: $photfile")
                    Log.d("334455", "onActivityResultabc: " + f.absolutePath)
                    Log.d("334455", "onActivityResultbitmp: $bitmap")
                    cabimg!!.setImageBitmap(bitmap)
                    saveimg()
                    val baos = ByteArrayOutputStream()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 25, baos) //bm is the bitmap object
                    val b = baos.toByteArray()

                    // Uri pic =  getImageUri(EditProfileActivity.this,bitmap);
                    //       picturePath = String.valueOf(pic);
                    //  encodedimg = Base64.encodeToString(b, Base64.DEFAULT);

                    //  Log.d("123456", "1245: "+encodedimg);
                    val fos = FileOutputStream(photfile)
                    fos.write(b)
                    fos.flush()
                    fos.close()
                    Log.d("334455", "onActivityResultbitmp: $photfile")
                    Log.d("334455", "fos: $fos")
                    val path = (Environment
                        .getExternalStorageDirectory()
                        .toString() + File.separator
                            + "Phoenix" + File.separator + "default")
                    f.delete()
                    //  photfile = f;
                    val outFile: OutputStream? = null
                    val file = File(path, System.currentTimeMillis().toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == 2) {
                val selectedImage = data!!.data
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                val c = this@CabFiilActivity.contentResolver.query(
                    selectedImage!!, filePath, null, null, null
                )
                c!!.moveToFirst()
                val columnIndex = c.getColumnIndex(filePath[0])
                val Path = c.getString(columnIndex)
                c.close()
                bitmap = BitmapFactory.decodeFile(Path)
                picturePath = selectedImage.toString()
                val u = Uri.parse(Path)
                photfile = File(u.path)
                Log.w(
                    "path of image from gallery......******************.........",
                    picturePath + ""
                )
                Glide.with(this)
                    .load(picturePath)
                    .into(cabimg!!)
                saveimg()
            }
        }
    }

    fun saveimg() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")
        val signUpMap = HashMap<String, RequestBody>()
        val maindoc = arrayOfNulls<MultipartBody.Part>(1)
        val mainpvi = RequestBody.create(MediaType.parse("image/*"), photfile)
        photfile!!.name
        var foo = photfile!!.name
        foo = foo.substring(0, foo.lastIndexOf('.'))
        signUpMap["fileName"] =
            RequestBody.create(MediaType.parse("multipart/form-data"), photfile!!.name)
        signUpMap["type"] = RequestBody.create(MediaType.parse("multipart/form-data"), "car")
        maindoc[0] = MultipartBody.Part.createFormData("file", photfile!!.path, mainpvi)


        // RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.editsimgfun(Header, signUpMap, maindoc)
        call?.enqueue(object : Callback<CabImgModel> {
            override fun onResponse(call: Call<CabImgModel>, response: Response<CabImgModel>) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                    val myObj = response.body()
                    strilepath = myObj!!.data
                    /* Intent i = new Intent(SignupActivity.this,HomeActivity.class);
                    startActivity(i);*/
                    //getotp();
                    //userdatwe(response.body().getUserId(),phone);
                    // int ids = response.body().get();
                    Toast.makeText(
                        this@CabFiilActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    //  progress.dismiss();
                } else {
                    Toast.makeText(
                        this@CabFiilActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    // progress.dismiss();
                }
            }

            override fun onFailure(call: Call<CabImgModel>, t: Throwable) {
                Toast.makeText(this@CabFiilActivity, "vv $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        const val CAMERA = Manifest.permission.CAMERA
        const val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}