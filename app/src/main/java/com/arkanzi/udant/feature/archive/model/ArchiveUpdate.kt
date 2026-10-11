package com.arkanzi.udant.feature.archive.model

sealed interface ArchiveUpdate {

    val savedArticleId: String

    data class NotArchived(
        override val savedArticleId: String
    ) : ArchiveUpdate

    data class Queued(
        override val savedArticleId: String
    ) : ArchiveUpdate

    data class Archiving(
        override val savedArticleId: String
    ) : ArchiveUpdate

    data class Failed(
        override val savedArticleId: String
    ) : ArchiveUpdate

    data class Completed(
        override val savedArticleId: String,
        val archiveUri: String
    ) : ArchiveUpdate
}