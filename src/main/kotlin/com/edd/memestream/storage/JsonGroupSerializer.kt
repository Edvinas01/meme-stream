package com.edd.memestream.storage

import com.fasterxml.jackson.databind.ObjectMapper
import org.mapdb.DataInput2
import org.mapdb.DataOutput2
import org.mapdb.serializer.GroupSerializerObjectArray

class JsonGroupSerializer<T>(
        private val mapper: ObjectMapper,
        private val type: Class<T>
) : GroupSerializerObjectArray<T>() {

    override fun serialize(out: DataOutput2, value: T) {
        out.writeUTF(mapper.writeValueAsString(value))
    }

    override fun deserialize(input: DataInput2, available: Int): T {
        return mapper.readValue<T>(input.readUTF(), type)
    }
}