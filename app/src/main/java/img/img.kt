package img

import android.net.Uri
import com.cloudinary.android.MediaManager
import io.github.cdimascio.dotenv.dotenv

private val dotenv = dotenv{
    directory = "/assets"
    filename = "env" // instead of '.env', use 'env'
}

interface img {
    fun upload(image: Uri, artist: String, name: String): String {
        return MediaManager.get().upload(image)
            .unsigned("print")
            .option("public_id", "$artist/$name")
            .dispatch()
    }
}