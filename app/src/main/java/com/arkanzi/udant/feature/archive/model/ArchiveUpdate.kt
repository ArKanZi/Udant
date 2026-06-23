package com.arkanzi.udant.feature.archive.model

sealed interface ArchiveUpdate {

    val savedArticleId: Long

    data class NotArchived(
        override val savedArticleId: Long
    ) : ArchiveUpdate

    data class Queued(
        override val savedArticleId: Long
    ) : ArchiveUpdate

    data class Archiving(
        override val savedArticleId: Long
    ) : ArchiveUpdate

    data class Failed(
        override val savedArticleId: Long
    ) : ArchiveUpdate

    data class Completed(
        override val savedArticleId: Long,
        val archiveUri: String
    ) : ArchiveUpdate
}