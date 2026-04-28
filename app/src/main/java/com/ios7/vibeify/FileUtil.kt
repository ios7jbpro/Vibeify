package com.ios7.vibeify

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LightingColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

object FileUtil {
    private fun createNewFile(path: String) {
        val lastSep = path.lastIndexOf(File.separator)
        if (lastSep > 0) {
            val dirPath = path.substring(0, lastSep)
            makeDir(dirPath)
        }

        val file = File(path)
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFile(path: String): String {
        createNewFile(path)
        val sb = StringBuilder()
        var fileReader: FileReader? = null
        try {
            fileReader = FileReader(File(path))
            val buffer = CharArray(1024)
            while (true) {
                val length = fileReader.read(buffer)
                if (length <= 0) {
                    break
                }
                sb.append(String(buffer, 0, length))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fileReader?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    fun writeFile(path: String, content: String) {
        createNewFile(path)
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(File(path), false)
            fileWriter.write(content)
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fileWriter?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun copyFile(sourcePath: String, destPath: String) {
        if (!isExistFile(sourcePath)) {
            return
        }
        createNewFile(destPath)

        var fis: FileInputStream? = null
        var fos: FileOutputStream? = null
        try {
            fis = FileInputStream(sourcePath)
            fos = FileOutputStream(destPath, false)
            val buffer = ByteArray(1024)
            while (true) {
                val length = fis.read(buffer)
                if (length <= 0) {
                    break
                }
                fos.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun copyDir(oldPath: String, newPath: String) {
        val oldFile = File(oldPath)
        val files = oldFile.listFiles() ?: return
        val newFile = File(newPath)
        if (!newFile.exists()) {
            newFile.mkdirs()
        }
        for (file in files) {
            if (file.isFile) {
                copyFile(file.path, "$newPath/${file.name}")
            } else if (file.isDirectory) {
                copyDir(file.path, "$newPath/${file.name}")
            }
        }
    }

    fun moveFile(sourcePath: String, destPath: String) {
        copyFile(sourcePath, destPath)
        deleteFile(sourcePath)
    }

    fun deleteFile(path: String) {
        val file = File(path)
        if (!file.exists()) {
            return
        }
        if (file.isFile) {
            file.delete()
            return
        }

        val fileArr = file.listFiles()
        if (fileArr != null) {
            for (subFile in fileArr) {
                if (subFile.isDirectory) {
                    deleteFile(subFile.absolutePath)
                }
                if (subFile.isFile) {
                    subFile.delete()
                }
            }
        }
        file.delete()
    }

    fun isExistFile(path: String): Boolean = File(path).exists()

    fun makeDir(path: String) {
        if (!isExistFile(path)) {
            File(path).mkdirs()
        }
    }

    fun listDir(path: String, list: ArrayList<String>?) {
        val dir = File(path)
        if (!dir.exists() || dir.isFile || list == null) {
            return
        }

        val listFiles = dir.listFiles() ?: return
        if (listFiles.isEmpty()) {
            return
        }

        list.clear()
        for (file in listFiles) {
            list.add(file.absolutePath)
        }
    }

    fun isDirectory(path: String): Boolean = isExistFile(path) && File(path).isDirectory

    fun isFile(path: String): Boolean = isExistFile(path) && File(path).isFile

    fun getFileLength(path: String): Long = if (isExistFile(path)) File(path).length() else 0L

    fun getExternalStorageDir(): String = Environment.getExternalStorageDirectory().absolutePath

    fun getPackageDataDir(context: Context): String = context.getExternalFilesDir(null)!!.absolutePath

    fun getPublicDir(type: String): String = Environment.getExternalStoragePublicDirectory(type).absolutePath

    fun convertUriToFilePath(context: Context, uri: Uri): String? {
        var path: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val split = DocumentsContract.getDocumentId(uri).split(":")
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    path = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val split = DocumentsContract.getDocumentId(uri).split(":")
                val type = split[0]
                path =
                    if ("raw".equals(type, ignoreCase = true)) {
                        split[1]
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && "msf".equals(type, ignoreCase = true)) {
                        getDataColumn(context, MediaStore.Downloads.EXTERNAL_CONTENT_URI, "_id=?", arrayOf(split[1]))
                    } else {
                        val contentUri =
                            ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                DocumentsContract.getDocumentId(uri).toLong(),
                            )
                        getDataColumn(context, contentUri, null, null)
                    }
            } else if (isMediaDocument(uri)) {
                val split = DocumentsContract.getDocumentId(uri).split(":")
                val type = split[0]
                val contentUri =
                    when (type) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }
                path = getDataColumn(context, contentUri, "_id=?", arrayOf(split[1]))
            }
        } else if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true)) {
            path = getDataColumn(context, uri, null, null)
        } else if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true)) {
            path = uri.path
        }

        return if (path != null) {
            try {
                URLDecoder.decode(path, "UTF-8")
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            context.contentResolver.query(uri ?: return null, projection, selection, selectionArgs, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(columnIndex)
                }
            }
        } catch (_: Exception) {
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority

    private fun saveBitmap(bitmap: Bitmap, destPath: String) {
        createNewFile(destPath)
        try {
            FileOutputStream(File(destPath)).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getScaledBitmap(path: String, max: Int): Bitmap {
        val src = BitmapFactory.decodeFile(path)
        var width = src.width
        var height = src.height
        val rate: Float

        if (width > height) {
            rate = max / width.toFloat()
            height = (height * rate).toInt()
            width = max
        } else {
            rate = max / height.toFloat()
            width = (width * rate).toInt()
            height = max
        }

        return Bitmap.createScaledBitmap(src, width, height, true)
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            var halfHeight = height / 2
            var halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun decodeSampleBitmapFromPath(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, options)
    }

    fun resizeBitmapFileRetainRatio(fromPath: String, destPath: String, max: Int) {
        if (!isExistFile(fromPath)) return
        saveBitmap(getScaledBitmap(fromPath, max), destPath)
    }

    fun resizeBitmapFileToSquare(fromPath: String, destPath: String, max: Int) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        saveBitmap(Bitmap.createScaledBitmap(src, max, max, true), destPath)
    }

    fun resizeBitmapFileToCircle(fromPath: String, destPath: String) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val bitmap = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val color = 0xff424242.toInt()
        val paint = Paint()
        val rect = Rect(0, 0, src.width, src.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(src.width / 2f, src.height / 2f, src.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, rect, rect, paint)

        saveBitmap(bitmap, destPath)
    }

    fun resizeBitmapFileWithRoundedBorder(fromPath: String, destPath: String, pixels: Int) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val bitmap = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val color = 0xff424242.toInt()
        val paint = Paint()
        val rect = Rect(0, 0, src.width, src.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, rect, rect, paint)

        saveBitmap(bitmap, destPath)
    }

    fun cropBitmapFileFromCenter(fromPath: String, destPath: String, w: Int, h: Int) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val width = src.width
        val height = src.height
        if (width < w && height < h) return

        val x = if (width > w) (width - w) / 2 else 0
        val y = if (height > h) (height - h) / 2 else 0
        val cw = if (w > width) width else w
        val ch = if (h > height) height else h

        saveBitmap(Bitmap.createBitmap(src, x, y, cw, ch), destPath)
    }

    fun rotateBitmapFile(fromPath: String, destPath: String, angle: Float) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val matrix = Matrix().apply { postRotate(angle) }
        saveBitmap(Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true), destPath)
    }

    fun scaleBitmapFile(fromPath: String, destPath: String, x: Float, y: Float) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val matrix = Matrix().apply { postScale(x, y) }
        saveBitmap(Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true), destPath)
    }

    fun skewBitmapFile(fromPath: String, destPath: String, x: Float, y: Float) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val matrix = Matrix().apply { postSkew(x, y) }
        saveBitmap(Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true), destPath)
    }

    fun setBitmapFileColorFilter(fromPath: String, destPath: String, color: Int) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val bitmap = Bitmap.createBitmap(src, 0, 0, src.width - 1, src.height - 1)
        val paint = Paint()
        val filter: ColorFilter = LightingColorFilter(color, 1)
        paint.colorFilter = filter
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        saveBitmap(bitmap, destPath)
    }

    fun setBitmapFileBrightness(fromPath: String, destPath: String, brightness: Float) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val cm =
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, brightness,
                    0f, 1f, 0f, 0f, brightness,
                    0f, 0f, 1f, 0f, brightness,
                    0f, 0f, 0f, 1f, 0f,
                ),
            )
        val bitmap = Bitmap.createBitmap(src.width, src.height, src.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(src, 0f, 0f, paint)
        saveBitmap(bitmap, destPath)
    }

    fun setBitmapFileContrast(fromPath: String, destPath: String, contrast: Float) {
        if (!isExistFile(fromPath)) return
        val src = BitmapFactory.decodeFile(fromPath)
        val cm =
            ColorMatrix(
                floatArrayOf(
                    contrast, 0f, 0f, 0f, 0f,
                    0f, contrast, 0f, 0f, 0f,
                    0f, 0f, contrast, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f,
                ),
            )
        val bitmap = Bitmap.createBitmap(src.width, src.height, src.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(src, 0f, 0f, paint)
        saveBitmap(bitmap, destPath)
    }

    fun getJpegRotate(filePath: String): Int {
        var rotate = 0
        try {
            val exif = ExifInterface(filePath)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
            }
        } catch (_: IOException) {
            return 0
        }
        return rotate
    }

    fun createNewPictureFile(context: Context): File {
        val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val fileName = date.format(Date()) + ".jpg"
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_DCIM)!!.absolutePath +
                File.separator +
                fileName,
        )
    }
}
