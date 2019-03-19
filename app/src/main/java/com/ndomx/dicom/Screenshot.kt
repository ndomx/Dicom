package com.ndomx.dicom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.util.*

class Screenshot
{
    companion object
    {
        private const val TAG = "Screenshot"

        private val tempFilename: String get() {
            return with (Calendar.getInstance()) {
                val year = get(Calendar.YEAR)
                val day = get(Calendar.DAY_OF_YEAR)
                val hour = get(Calendar.HOUR_OF_DAY)
                val min = get(Calendar.MINUTE)
                "temp-$year$day$hour$min.jpg"
            }
        }

        fun takeScreenshot(v: View): Bitmap
        {
            val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            v.background.draw(canvas)
            v.draw(canvas)

            Log.i(TAG, "Drew canvas")
            return bitmap
        }

        fun saveFile(bitmap: Bitmap): File
        {
            Log.i(TAG, "Filename = $tempFilename")
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + tempFilename

            Log.i(TAG, "Path = $path")
            val file = File(path)
            var out: FileOutputStream? = null
            try
            {
                out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
                out.close()
                Log.i(TAG, "Success")
            }
            catch (e: Exception)
            {
                Log.e(TAG, e.message)
                out?.flush()
                out?.close()
            }

            return file
        }

        fun saveFileCached(context: Context, bitmap: Bitmap): File
        {
            val dir = context.cacheDir
            val file = File(dir, tempFilename)

            var out: FileOutputStream? = null
            try
            {
                out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
                out.close()
                Log.i(TAG, "Success")
            }
            catch (e: Exception)
            {
                Log.e(TAG, e.message)
                out?.flush()
                out?.close()
            }

            return file
        }
    }
}