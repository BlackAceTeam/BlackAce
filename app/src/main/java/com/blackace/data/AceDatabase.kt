package com.blackace.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blackace.data.dao.SignDao
import com.blackace.data.entity.db.SignBean
import com.blackace.util.holder.ContextHolder

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 11:06 AM
 */
@Database(entities = [SignBean::class], version = 1)
abstract class AceDatabase : RoomDatabase() {

    abstract fun signDao():SignDao
}

val database by lazy {
    Room.databaseBuilder(ContextHolder.get(), AceDatabase::class.java, "Ace.db").build()
}
