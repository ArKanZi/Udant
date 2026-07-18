package com.arkanzi.udant.core.job.download.repository

import com.arkanzi.udant.core.database.dao.DownloadJobDao
import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.download.model.DownloadStatus
import com.arkanzi.udant.core.job.download.model.DownloadType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(

    private val dao: DownloadJobDao

) {
    suspend fun getNextJobByStatus(status: DownloadStatus): DownloadJobEntity?{
        return dao.getNextJobByStatus(status)
    }

    suspend fun insertJob(
        job: DownloadJobEntity
    ) {
        return dao.insertJob(job)
    }

    suspend fun updateJob(
        job: DownloadJobEntity
    ) {
        dao.updateJob(job)
    }

    fun getJobsByStatus(
        status: DownloadStatus
    ): Flow<List<DownloadJobEntity>> {
        return dao.getJobsByStatus(status)
    }

    suspend fun getJobById(
        jobId: String
    ): DownloadJobEntity? {
        return dao.getJobById(jobId)
    }

    suspend fun updateStatus(
        jobId: String,
        status: DownloadStatus
    ) {
        dao.updateStatus(
            jobId = jobId,
            status = status,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun getDownloadJobByReferenceId(
        referenceId: Long,
        jobType: DownloadType
    ): DownloadJobEntity? {
        return dao.getDownloadJobByReferenceId(referenceId,jobType)
    }

    suspend fun deleteJob(
        jobId: String
    ) {
        dao.deleteJob(jobId)
    }

    suspend fun clear() {
        dao.clear()
    }

    fun getJobs(): Flow<List<DownloadJobEntity>> {
        return dao.getJobs()
    }
}