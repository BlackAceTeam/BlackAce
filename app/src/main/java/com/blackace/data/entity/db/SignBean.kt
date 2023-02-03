package com.blackace.data.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 11:01 AM
 */
@Entity(tableName = "sign")
data class SignBean(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val path: String,
    @ColumnInfo
    val password: String,
    @ColumnInfo(name = "alias_name")
    val aliasName: String,
    @ColumnInfo(name = "alias_password")
    val aliasPass: String
)
