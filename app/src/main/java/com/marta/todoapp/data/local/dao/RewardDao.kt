package com.marta.todoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.marta.todoapp.data.local.entity.RewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards WHERE isRedeemed = 0 ORDER BY starCost ASC")
    fun getAvailableRewards(): Flow<List<RewardEntity>>

    @Query("SELECT * FROM rewards WHERE isRedeemed = 1 ORDER BY redeemedAt DESC")
    fun getRedeemedRewards(): Flow<List<RewardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reward: RewardEntity): Long

    @Update
    suspend fun update(reward: RewardEntity)

    @Query("DELETE FROM rewards WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM rewards WHERE isRedeemed = 1")
    fun getRedeemedCount(): Flow<Int>
}
