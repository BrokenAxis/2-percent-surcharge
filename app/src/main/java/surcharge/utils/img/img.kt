package surcharge.utils.img

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Uploads a picture of a print stored under the filepath [artist]/[name]. On success the link is given by [url].
 * Will overwrite any existing image with the same [name].
 */
fun upload(
    image: Uri,
    artist: String,
    name: String,
    url: MutableState<String>,
    progress: MutableDoubleState,
    onSuccess: () -> Unit,
    scope: CoroutineScope,
    snackbar: SnackbarHostState
): String {

    val requestId = MediaManager.get().upload(image)
        .option("upload_preset", "prints")
        .option("public_id", "$artist/$name")
        .callback(ImgCallback(url, progress, onSuccess, scope, snackbar))
        .dispatch()

    return requestId
}

/**
 * Uploads a display picture for [artist]. On success the link is given by [url].
 * Overwrites any existing image with the same [artist].
 */
fun upload(
    image: Uri,
    artist: String,
    url: MutableState<String>,
    progress: MutableDoubleState,
    onSuccess: () -> Unit,
    scope: CoroutineScope,
    snackbar: SnackbarHostState
): String {

    val requestId = MediaManager.get().upload(image)
        .option("upload_preset", "display pictures")
        .option("public_id", artist)
        .callback(ImgCallback(url, progress, onSuccess, scope, snackbar))
        .dispatch()

    return requestId
}

private class ImgCallback(
    private val url: MutableState<String>,
    private val progress: MutableDoubleState,
    private val onSuccess: () -> Unit,
    private val scope: CoroutineScope,
    private val snackbar: SnackbarHostState
) : UploadCallback {

    override fun onStart(requestId: String) {
        // your code here
    }

    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
        progress.doubleValue = (bytes.toDouble() / totalBytes.toDouble())
    }

    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
        url.value = resultData?.get("secure_url").toString()
        onSuccess()
    }

    override fun onError(requestId: String, error: ErrorInfo) {
        scope.launch {
            withContext(Dispatchers.IO) {
                snackbar.showSnackbar("Error: $error")
            }
        }
    }

    override fun onReschedule(requestId: String, error: ErrorInfo) {
        scope.launch {
            withContext(Dispatchers.IO) {
                snackbar.showSnackbar("Error: $error")
            }
        }
    }
}