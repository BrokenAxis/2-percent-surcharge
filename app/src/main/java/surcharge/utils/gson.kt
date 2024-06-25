package surcharge.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Instant


class InstantDeserializer : JsonDeserializer<Instant?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Instant {
        return Instant.parse(json.asString)
    }
}

class InstantSerializer : JsonSerializer<Instant?> {
    @Throws(JsonParseException::class)
    override fun serialize(
        src: Instant?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

var gson: Gson = GsonBuilder()
    .registerTypeAdapter(Instant::class.java, InstantDeserializer())
    .registerTypeAdapter(Instant::class.java, InstantSerializer())
    .create()
