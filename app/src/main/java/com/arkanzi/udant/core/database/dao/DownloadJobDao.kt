package com.arkanzi.udant.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.model.DownloadStatus
import com.arkanzi.udant.core.job.download.model.DownloadType
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadJobDao {

    @Query("SELECT EXISTS(SELECT 1 FROM download_jobs)")
    suspend fun hasQueue(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(
        job: DownloadJobEntity
    )


    @Query("""
    SELECT *
    FROM download_jobs
    WHERE status = :status
    ORDER BY createdAt ASC
    LIMIT 1
""")
    suspend fun getNextJobByStatus(
        status: DownloadStatus
    ): DownloadJobEntity?

    @Query("""
        UPDATE download_jobs
        SET status = :status,
            updatedAt = :updatedAt
        WHERE jobId = :jobId
    """)
    suspend fun updateStatus(
        jobId: String,
        status: DownloadStatus,
        updatedAt: Long
    )

    @Query("""
        UPDATE download_jobs
        SET status = :status,
            updatedAt = :updatedAt
    """)
    suspend fun updateAllStatus(
        status: DownloadStatus,
        updatedAt: Long
    )

    @Query("""
        DELETE FROM download_jobs
        WHERE jobId = :jobId
    """)
    suspend fun deleteJob(
        jobId: String
    )

    @Query("""
        DELETE FROM download_jobs
    """)
    suspend fun clear()

    @Query("""
    SELECT *
    FROM download_jobs
    WHERE referenceId = :referenceId AND jobType = :jobType
    LIMIT 1
""")
    suspend fun getDownloadJobByReferenceId(
        referenceId: Long,
        jobType: DownloadType
    ): DownloadJobEntity?

    @Query("""
    SELECT *
    FROM download_jobs
""")
    fun getJobs(): Flow<List<DownloadJobEntity>>
}