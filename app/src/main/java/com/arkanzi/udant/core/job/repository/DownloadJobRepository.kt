package com.arkanzi.udant.core.job.repository

import com.arkanzi.udant.core.database.dao.DownloadJobDao
import com.arkanzi.udant.core.database.entity.DownloadJobEntity
import com.arkanzi.udant.core.job.model.DownloadJobStatus
import com.arkanzi.udant.core.job.model.DownloadJobType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadJobRepository @Inject constructor(

    private val dao: DownloadJobDao

) {
    suspend fun getNextJobByStatus(status: DownloadJobStatus): DownloadJobEntity?{
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
        status: DownloadJobStatus
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
        status: DownloadJobStatus
    ) {
        dao.updateStatus(
            jobId = jobId,
            status = status,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun getDownloadJobByReferenceId(
        referenceId: Long,
        jobType: DownloadJobType
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