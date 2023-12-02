package surcharge.utils.img

import android.net.Uri
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import io.github.cdimascio.dotenv.dotenv

fun upload(image: Uri, artist: String, name: String, url: MutableState<String>, progress: MutableFloatState): String {

    val requestId = MediaManager.get().upload(image)
        .unsigned("prints")
        .option("public_id", "$artist/$name")
        .callback(ImgCallback(url, progress))
        .dispatch()

    return ""
}

class ImgCallback(private val url: MutableState<String>, private val progress: MutableFloatState) : UploadCallback {


    override fun onStart(requestId: String) {
        // your code here
    }

    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
        // example code starts here
        progress.floatValue = (bytes.toDouble() / totalBytes).toFloat()
        // post progress to app UI (e.g. progress bar, notification)
        // example code ends here
    }

    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
       url.value = resultData?.get("secure_url").toString()
    }

    override fun onError(requestId: String, error: ErrorInfo) {
        // your code here
    }

    override fun onReschedule(requestId: String, error: ErrorInfo) {
        // your code here
    }
}