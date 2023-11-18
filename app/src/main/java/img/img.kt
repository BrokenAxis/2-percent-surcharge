package img

import com.cloudinary.Cloudinary
import io.github.cdimascio.dotenv.dotenv

private val dotenv = dotenv{
    directory = "/assets"
    filename = "env" // instead of '.env', use 'env'
}

private val cloudinary = Cloudinary(dotenv["CLOUDINARY_URL"])
interface img {
    fun upload(image: String, artist: String, id: Number): String {
        val options = mapOf("public_id" to "$artist/$id",)
        return ""
    }
}