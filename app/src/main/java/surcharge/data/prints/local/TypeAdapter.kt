package surcharge.data.prints.local

import com.google.gson.InstanceCreator
import surcharge.types.Item
import surcharge.types.PrintItem
import java.lang.reflect.Type

class ItemInstanceCreator: InstanceCreator<Item> {
    override fun createInstance(type: Type?): Item {
        return PrintItem()

    }
}