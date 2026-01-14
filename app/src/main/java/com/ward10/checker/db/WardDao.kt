package com.ward10.checker.db

import androidx.room.*

@Dao
interface WardDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<WardPerson>)

    @Query("SELECT COUNT(*) FROM ward_people")
    suspend fun totalCount(): Int

    @Query(
        "SELECT * FROM ward_people " +
        "WHERE name LIKE '%' || :query || '%' " +
        "   OR mobile LIKE '%' || :query || '%' " +
        "ORDER BY isChecked DESC, name ASC " +
        "LIMIT 200"
    )
    suspend fun search(query: String): List<WardPerson>

    @Query("UPDATE ward_people SET isChecked = 1 WHERE id = :id")
    suspend fun markChecked(id: Int)

    @Query("SELECT * FROM ward_people WHERE isChecked = 1 ORDER BY name ASC")
    suspend fun getCheckedPeople(): List<WardPerson>
}
