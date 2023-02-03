package com.blackace.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.blackace.data.entity.db.SignBean

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 11:11 AM
 */
@Dao
interface SignDao {

    @Insert
    fun insert(bean: SignBean)

    @Query("select * from sign")
    fun all():List<SignBean>

    @Query("delete from sign where id=:id")
    fun delete(id: Int)
}
