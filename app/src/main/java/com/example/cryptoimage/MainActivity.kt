package com.example.cryptoimage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.activity_main.*
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class MainActivity : AppCompatActivity() {

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000
        //Permission code
        private val PERMISSION_CODE = 1001
        //state caesar
        private var caesar_state = 0
        //state xor
        private var xor_state = 0
        //state aes
        private var aes_state = 0
        //state des
        private var des_state = 0
        //state sdes
        private var sdes_state = 0
        //aes key
        private var key_aes = generateSecretKeyAes()
        //aes raw bytes
        private var aes_raw:ArrayList<ByteArray> = ArrayList<ByteArray>()
        //des raw bytes
        private var des_raw:ArrayList<ByteArray> = ArrayList<ByteArray>()
        //des key
        private var key_des = generateSecretKeyAes()

        // Generating secret key for AES and DES
        @Throws(Exception::class)
        fun generateSecretKeyAes(): SecretKey {
            val secureRandom = SecureRandom()
            val keyGenerator = KeyGenerator.getInstance("AES")
            //generate a key with secure random
            keyGenerator?.init(128, secureRandom)
            return keyGenerator.generateKey()
        }

        @Throws(Exception::class)
        fun generateSecretKeyDes(): SecretKey {
            val secureRandom = SecureRandom()
            val keyGenerator = KeyGenerator.getInstance("DES")
            //generate a key with secure random
            keyGenerator?.init(128, secureRandom)
            return keyGenerator.generateKey()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //BUTTON CLICK
        load_btn.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    getImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                getImageFromGallery();
            }
        }

        //Caesar button
        caesar.setOnClickListener {

            if (caesar_state == 0) {
                val image = CaesarEncrypt()
                imageView.setImageBitmap(image)
                caesar_state += 1
                Toast.makeText(this, "Encrytped", Toast.LENGTH_SHORT).show()

            }else{
                val image = CaesarDecrypt()
                imageView.setImageBitmap(image)
                caesar_state -= 1
                Toast.makeText(this, "Decrypted", Toast.LENGTH_SHORT).show()
            }

        }

        // Xor button
        xor.setOnClickListener {
            if (xor_state == 0) {
                val image = XorEncrypt()
                imageView.setImageBitmap(image)
                xor_state += 1
                Toast.makeText(this, "Encrytped", Toast.LENGTH_SHORT).show()

            }else{
                val image = XorEncrypt()
                imageView.setImageBitmap(image)
                xor_state -= 1
                Toast.makeText(this, "Decrypted", Toast.LENGTH_SHORT).show()
            }

        }

        //SDES button
        sdes.setOnClickListener {
            //TODO implementar clique sdes
            if (sdes_state == 0)
            {
                val image = SdesEncrypt()
                imageView.setImageBitmap(image)
                sdes_state += 1
                Toast.makeText(this, "Encrytped", Toast.LENGTH_SHORT).show()
            }else{
                val image = SdesDecrypt()
                imageView.setImageBitmap(image)
                sdes_state -= 1
                Toast.makeText(this, "Decrypted", Toast.LENGTH_SHORT).show()
            }

        }

        //AES button
        aes.setOnClickListener {
            if (aes_state == 0)
            {
                val (image, raw_pixels) = AesEncrypt()
                imageView.setImageBitmap(image)
                aes_raw = raw_pixels
                Toast.makeText(this, "Encrytped", Toast.LENGTH_SHORT).show()
                aes_state += 1
            }else{
                val image = AesDecrypt()
                imageView.setImageBitmap(image)
                aes_state -= 1
                aes_raw.clear()
                Toast.makeText(this, "Decrypted", Toast.LENGTH_SHORT).show()
            }
        }

        des.setOnClickListener {
            if (des_state == 0)
            {
                val (image, raw_pixels) = DesEncrypt()
                imageView.setImageBitmap(image)
                des_raw = raw_pixels
                Toast.makeText(this, "Encrytped", Toast.LENGTH_SHORT).show()
                des_state += 1
            }else{
                val image = DesDecrypt()
                imageView.setImageBitmap(image)
                des_state -= 1
                des_raw.clear()
                Toast.makeText(this, "Decrypted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun SdesDecrypt():Bitmap
    {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Loop through image pixels
        for(i in 0 until img_width)
        {
            for (j in 0 until img_height)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val sdes = Sdes()

                // Encrypt pixels
                val cor_final = sdes.decrypt(pixel_array)

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb( cor_final.get(0),cor_final.get(1), cor_final.get(2),cor_final.get(3) ))

                // Clear pixel array
                pixel_array.clear()
            }
        }

        return bmp_Copy
    }

    private fun SdesEncrypt():Bitmap
    {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Loop through image pixels
        for(i in 0 until img_width)
        {
            for (j in 0 until img_height)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val sdes = Sdes()

                // Encrypt pixels
                val cor_final = sdes.encrypt(pixel_array)

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb( cor_final.get(0),cor_final.get(1), cor_final.get(2),cor_final.get(3) ))

                // Clear pixel array
                pixel_array.clear()
            }
        }

        return bmp_Copy
    }

    private fun DesEncrypt(): Pair<Bitmap,ArrayList<ByteArray>>
    {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val bytes_img_array = ArrayList<ByteArray>()

        // Loop through image pixels
        for(i in 0 until img_width)
        {
            for (j in 0 until img_height)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Verifying values
                if (i == 0)
                {
                    Log.d("DEBUGANDO", "alpha ${i}, ${j} : ${alphaValue} " )
                    Log.d("DEBUGANDO", "red ${i}, ${j} : ${redValue} " )
                    Log.d("DEBUGANDO", "blue ${i}, ${j} : ${blueValue} " )
                    Log.d("DEBUGANDO", "green ${i}, ${j} : ${greenValue} " )

                }

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val values = byteArrayOf(
                    alphaValue.toByte(),
                    redValue.toByte(),
                    greenValue.toByte(),
                    blueValue.toByte()
                )

                //encrypt file
                val aes_enc = Aes()

                //alpha
                val new_color = aes_enc.encrypt(key_des, values)

                bytes_img_array.add(new_color)

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb(new_color.get(0).toInt(),new_color.get(1).toInt(), new_color.get(2).toInt(),new_color.get(3).toInt() ))

                // Clear pixel array
                pixel_array.clear()
            }
        }

        return Pair(bmp_Copy, bytes_img_array)

    }

    private fun DesDecrypt(): Bitmap
    {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        var control = 0
        // Loop through image pixels
        for(i in 0 until img_width)
        {
            for (j in 0 until img_height)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Verifying values
                if (i == 0)
                {
                    Log.d("DEBUGANDO", "alpha ${i}, ${j} : ${alphaValue} " )
                    Log.d("DEBUGANDO", "red ${i}, ${j} : ${redValue} " )
                    Log.d("DEBUGANDO", "blue ${i}, ${j} : ${blueValue} " )
                    Log.d("DEBUGANDO", "green ${i}, ${j} : ${greenValue} " )

                }

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val values = byteArrayOf(
                    alphaValue.toByte(),
                    redValue.toByte(),
                    greenValue.toByte(),
                    blueValue.toByte()
                )

                //encrypt file
                val aes_enc = Aes()

                //alpha
                val new_color = aes_enc.decrypt(key_des, des_raw.get(control))

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb(new_color.get(0).toInt(),new_color.get(1).toInt(), new_color.get(2).toInt(),new_color.get(3).toInt() ))

                // Clear pixel array
                pixel_array.clear()

                control +=1
            }
        }

        return bmp_Copy

    }

    private fun AesEncrypt(): Pair<Bitmap,ArrayList<ByteArray>>
    {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val bytes_img_array = ArrayList<ByteArray>()

        // Loop through image pixels
        for(i in 0 until img_width)
        {
            for (j in 0 until img_height)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Verifying values
                if (i == 0)
                {
                    Log.d("DEBUGANDO", "alpha ${i}, ${j} : ${alphaValue} " )
                    Log.d("DEBUGANDO", "red ${i}, ${j} : ${redValue} " )
                    Log.d("DEBUGANDO", "blue ${i}, ${j} : ${blueValue} " )
                    Log.d("DEBUGANDO", "green ${i}, ${j} : ${greenValue} " )

                }

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val values = byteArrayOf(
                    alphaValue.toByte(),
                    redValue.toByte(),
                    greenValue.toByte(),
                    blueValue.toByte()
                )

                //encrypt file
                val aes_enc = Aes()

                //alpha
                val new_color = aes_enc.encrypt(key_aes, values)

                bytes_img_array.add(new_color)

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb(new_color.get(0).toInt(),new_color.get(1).toInt(), new_color.get(2).toInt(),new_color.get(3).toInt() ))

                // Clear pixel array
                pixel_array.clear()
            }
        }

        return Pair(bmp_Copy, bytes_img_array)

    }

    private fun AesDecrypt(): Bitmap
    {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        var control = 0
        // Loop through image pixels
        for(i in 0 until img_width)
        {
            for (j in 0 until img_height)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Verifying values
                if (i == 0)
                {
                    Log.d("DEBUGANDO", "alpha ${i}, ${j} : ${alphaValue} " )
                    Log.d("DEBUGANDO", "red ${i}, ${j} : ${redValue} " )
                    Log.d("DEBUGANDO", "blue ${i}, ${j} : ${blueValue} " )
                    Log.d("DEBUGANDO", "green ${i}, ${j} : ${greenValue} " )

                }

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val values = byteArrayOf(
                    alphaValue.toByte(),
                    redValue.toByte(),
                    greenValue.toByte(),
                    blueValue.toByte()
                )

                //encrypt file
                val aes_enc = Aes()

                //alpha
                val new_color = aes_enc.decrypt(key_aes, aes_raw.get(control))

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb(new_color.get(0).toInt(),new_color.get(1).toInt(), new_color.get(2).toInt(),new_color.get(3).toInt() ))

                // Clear pixel array
                pixel_array.clear()

                control +=1
            }
        }

        return bmp_Copy

    }

    private fun XorEncrypt():Bitmap
    {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Loop through image pixels
        for(i in 0..img_width-1)
        {
            for (j in 0..img_height-1)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Verifying values
                if (i == 0)
                {
                    Log.d("DEBUGANDO", "alpha ${i}, ${j} : ${alphaValue} " )
                    Log.d("DEBUGANDO", "red ${i}, ${j} : ${redValue} " )
                    Log.d("DEBUGANDO", "blue ${i}, ${j} : ${blueValue} " )
                    Log.d("DEBUGANDO", "green ${i}, ${j} : ${greenValue} " )

                }

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val cifra_xor = Xor()

                // Encrypt pixels
                val cor_final = cifra_xor.encrypt(pixel_array)

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb( cor_final.get(0),cor_final.get(1), cor_final.get(2),cor_final.get(3) ))

                // Clear pixel array
                pixel_array.clear()
            }
        }

        return bmp_Copy
    }

    private fun CaesarEncrypt(): Bitmap {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Loop through image pixels
        for(i in 0..img_width-1)
        {
            for (j in 0..img_height-1)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Verifying values
                if (i == 0)
                {
                    Log.d("DEBUGANDO", "alpha ${i}, ${j} : ${alphaValue} " )
                    Log.d("DEBUGANDO", "red ${i}, ${j} : ${redValue} " )
                    Log.d("DEBUGANDO", "blue ${i}, ${j} : ${blueValue} " )
                    Log.d("DEBUGANDO", "green ${i}, ${j} : ${greenValue} " )

                }

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val cifra_de_cesar = Caesar()

                // Encrypt pixels
                val cor_final = cifra_de_cesar.encrypt(pixel_array)

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb( cor_final.get(0),cor_final.get(1), cor_final.get(2),cor_final.get(3) ))

                // Clear pixel array
                pixel_array.clear()
            }
        }

        return bmp_Copy
    }

    private fun CaesarDecrypt(): Bitmap {
        val pixel_array = ArrayList<Int>()
        val bitmap = imageView.drawable.toBitmap()
        val img_width = bitmap.width
        val img_height = bitmap.height
        val bmp_Copy: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Loop through image pixels
        for(i in 0 until img_width)
        {
            for (j in 0 until img_height)
            {
                // Get individual colors
                @ColorInt
                val cor = bitmap.getPixel(i,j)

                val alphaValue = Color.alpha(cor)
                val redValue = Color.red(cor)
                val blueValue = Color.blue(cor)
                val greenValue = Color.green(cor)

                // Verifying values
                if (i == 0)
                {
                    Log.d("DEBUGANDO", "alpha ${i}, ${j} : ${alphaValue} " )
                    Log.d("DEBUGANDO", "red ${i}, ${j} : ${redValue} " )
                    Log.d("DEBUGANDO", "blue ${i}, ${j} : ${blueValue} " )
                    Log.d("DEBUGANDO", "green ${i}, ${j} : ${greenValue} " )

                }

                // Add pixel to list
                pixel_array.addAll(listOf(alphaValue,redValue,greenValue, blueValue))

                val cifraDeCesar = Caesar()

                // Encrypt pixels
                val cor_final = cifraDeCesar.decrypt(pixel_array)

                // Set pixel in bitmap
                bmp_Copy.setPixel(i, j, Color.argb( cor_final.get(0),cor_final.get(1), cor_final.get(2),cor_final.get(3) ))

                // Clear pixel array
                pixel_array.clear()
            }
        }

        return bmp_Copy
    }

    private fun getImageFromGallery()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    getImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val selectedImage = data!!.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)
            cursor.close()
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
//            imageView.setImageURI(data?.data)
        }
    }
}
