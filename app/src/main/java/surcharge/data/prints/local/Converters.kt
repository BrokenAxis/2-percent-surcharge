package surcharge.data.prints.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import surcharge.types.BundleItem
import surcharge.types.PrintItem
import surcharge.types.Size
import java.time.Instant
import java.util.EnumMap

class Converters {
    @TypeConverter
    fun fromMutableMap(value: MutableMap<String, Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToMutableMap(value: String): MutableMap<String, Int> {
        val type = object : TypeToken<Map<String, Int>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromPrintList(value: List<PrintItem>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToPrintList(value: String): List<PrintItem> {
        val type = object : TypeToken<List<PrintItem>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromSizeList(value: List<Size>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToSizeList(value: String): List<Size> {
        val type = object : TypeToken<List<Size>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromPrintItemList(value: ArrayList<PrintItem>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToPrintItemList(value: String): ArrayList<PrintItem> {
        val type = object : TypeToken<ArrayList<PrintItem>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromBundleItemList(value: ArrayList<BundleItem>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToBundleItemList(value: String): ArrayList<BundleItem> {
        val type = object : TypeToken<ArrayList<BundleItem>>() {}.type
        return Gson().fromJson(value, type)
    }

//    @TypeConverter
//    fun fromItemList(value: ArrayList<Item>): String {
//        val gson = GsonBuilder()
//        gson.registerTypeAdapter(Item::class.java, ItemInstanceCreator())
//        return gson.create().toJson(value)
//    }
//
//    @TypeConverter
//    fun stringToItemList(value: String): ArrayList<Item> {
//        val type = object : TypeToken<ArrayList<Item>>() {}.type
//        val gson = GsonBuilder()
//        gson.registerTypeAdapter(Item::class.java, ItemInstanceCreator())
//        return gson.create().fromJson(value, type)
//    }

    @TypeConverter
    fun fromInstant(value: Instant): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToInstant(value: String): Instant {
        return Instant.parse(value)
    }

    @TypeConverter
    fun fromEnumMap(value: EnumMap<Size, Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToEnumMap(value: String): EnumMap<Size, Int> {
        val type = object : TypeToken<EnumMap<Size, Int>>() {}.type
        return Gson().fromJson(value, type)
    }
}