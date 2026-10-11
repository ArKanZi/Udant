package com.arkanzi.udant.extension.thetimesofindia.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ToiFeedResponseDto(
    val pg: ToiPaginationDto? = null,
    val items: List<ToiFeedItemDto> = emptyList(),
    val debugTrace: JsonObject? = null,
    val adsConfig: ToiAdsConfigDto? = null,
)

@Serializable
data class ToiPaginationDto(
    val tr: String? = null,
    val pp: String? = null,
    val tp: String? = null,
    val cp: String? = null,
) {
    val currentPage: Int?
        get() = cp?.toIntOrNull()

    val totalPages: Int?
        get() = tp?.toIntOrNull()

    val totalRecords: Int?
        get() = tr?.toIntOrNull()

    val hasNextPage: Boolean
        get() {
            val current = currentPage ?: return false
            val total = totalPages ?: return false
            return current < total
        }
}

@Serializable
data class ToiFeedItemDto(
    // Common article/feed fields
    val updatedTime: String? = null,
    val pos: String? = null,
    val feedSource: String? = null,
    val upd: String? = null,
    val lpt: String? = null,
    val dl: String? = null,
    val hl: String? = null,
    val cmstype: String? = null,
    val id: String? = null,
    val hostId: String? = null,
    val dm: String? = null,
    val titleforurl: String? = null,
    val tn: String? = null,
    val type: String? = null,

    // Publisher and article metadata
    val pubInfo: ToiPublisherDto? = null,
    val noc: String? = null,
    val topicTree: String? = null,
    val au1: String? = null,
    val au: String? = null,
    val wu: String? = null,
    val su: String? = null,
    val cs: String? = null,
    val seo: String? = null,
    val kws: String? = null,
    val source: String? = null,
    val cat: String? = null,
    val uid: String? = null,
    val ag: String? = null,
    val originalMsid: String? = null,

    // Image and layout
    val imageHeight: String? = null,
    val imageWidth: String? = null,
    val imageid: String? = null,
    val pid: String? = null,
    val listingFolderId: String? = null,
    val expanded: Boolean? = null,
    val resizeMode: Int? = null,
    val orientation: String? = null,
    val primeImage: List<ToiPrimeImageDto>? = null,

    // Flexible CMS metadata: retains every key returned by TOI
    val metainfo: Map<String, ToiMetaInfoValueDto>? = null,

    // Source and branding
    val pc: String? = null,
    val brand: String? = null,
    val webSeries: Boolean? = null,
    val author: List<ToiAuthorDto>? = null,
    val sectionInfo: List<ToiSectionDto>? = null,

    // Article content and status
    val isLiveArticle: Boolean? = null,
    val requestMsid: String? = null,
    val headline: ToiHeadlineDto? = null,
    val aiSummaryDesc: String? = null,
    val deeplink: String? = null,
    val signalsAiIrrReasonCode: String? = null,

    // Live blog
    val isLiveBlog: String? = null,
    val liveblog: ToiLiveBlogDto? = null,
    val lbc: Int? = null,

    // Video
    val hasvideo: String? = null,
    val videoPreviewUrl: String? = null,
    val du: String? = null,

    // Nested/related content
    val childCount: Int? = null,
    val pitems: List<ToiRelatedItemDto>? = null,

    // Non-article feed entries
    val mrecData: ToiAdDataDto? = null,
    val msid: String? = null,
    val defaulturl: String? = null,
    val pageName: String? = null,
    val rowCount: Int? = null,
    val abbreviation: String? = null,
    val ctaText: String? = null,
    val description: String? = null,
    val overrideLink: String? = null,
)

@Serializable
data class ToiPublisherDto(
    val pn: String? = null,
    val pnEng: String? = null,
    val channel: String? = null,
    val pnu: String? = null,
    val pid: Int? = null,
    val lid: Int? = null,
    val lang: String? = null,
)

@Serializable
data class ToiSectionDto(
    val name: String? = null,
    val deeplink: String? = null,
)

@Serializable
data class ToiMetaInfoValueDto(
    val id: Int? = null,
    val value: String? = null,
)

@Serializable
data class ToiRelatedItemDto(
    val expanded: Boolean? = null,
    val webSeries: Boolean? = null,
    val imageid: String? = null,
)

@Serializable
data class ToiLiveBlogDto(
    val keyevent: Boolean? = null,
)

// --------------------------------------------------------------------
// Ad configuration returned at the root of the feed response
// --------------------------------------------------------------------

@Serializable
data class ToiAdsConfigDto(
    val headerAdData: ToiAdDataDto? = null,
    val footerAdData: ToiAdDataDto? = null,
    val spareAdData: ToiSpareAdDataDto? = null,
)

@Serializable
data class ToiAdDataDto(
    val key: String? = null,
    val dfp: String? = null,
    val dfpCodeCountryWise: Map<String, String>? = null,
    val ctn: String? = null,
    val aps: String? = null,
    val size: List<Int>? = null,
    val mrecSizes: String? = null,

    val configIndia: ToiAdRegionConfigDto? = null,
    val configExIndia: ToiAdRegionConfigDto? = null,
    val configRestrictedRegion: ToiAdRegionConfigDto? = null,

    val prebidConfigId: String? = null,
)

@Serializable
data class ToiAdRegionConfigDto(
    val isToRefresh: Boolean? = null,
    val isManualImpression: Boolean? = null,
    val isToLoadLazy: Boolean? = null,
    val sdkWaterFall: String? = null,
    val isEnabled: Boolean? = null,
)

@Serializable
data class ToiSpareAdDataDto(
    val ctnRecommended: String? = null,
    val securl: String? = null,
)
