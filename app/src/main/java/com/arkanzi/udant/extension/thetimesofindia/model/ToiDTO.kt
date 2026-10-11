package com.arkanzi.udant.extension.thetimesofindia.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


@Serializable
data class ToiHomeResponseDto(
    val pg: ToiPageDto? = null,
    val items: List<ToiHomeItemDto> = emptyList(),
    val debugTrace: JsonElement? = null,
    val adsConfig: JsonElement? = null
)

@Serializable
data class ToiPageDto(
    val tr: String? = null,
    val pp: String? = null,
    val tp: String? = null,
    val cp: String? = null
)

@Serializable
data class ToiHomeItemDto(
    // Common fields
    val id: String? = null,
    val tn: String? = null,
    val type: String? = null,
    val pos: String? = null,
    val expanded: Boolean? = null,

    // Article metadata
    val updatedTime: String? = null,
    val feedSource: String? = null,
    val upd: String? = null,
    val lpt: String? = null,
    val dl: String? = null,
    val hl: String? = null,
    val cmstype: String? = null,
    val hostId: String? = null,
    val dm: String? = null,
    val titleforurl: String? = null,
    val noc: String? = null,
    val topicTree: String? = null,

    // Publication information
    val pubInfo: ToiPubInfoDto? = null,
    val au: String? = null,
    val au1: String? = null,
    val author: List<ToiAuthorDto>? = null,
    val source: String? = null,
    val cat: String? = null,
    val uid: String? = null,
    val ag: String? = null,
    val originalMsid: String? = null,

    // URLs and SEO
    val wu: String? = null,
    val su: String? = null,
    val seo: String? = null,
    val kws: String? = null,
    val deeplink: String? = null,

    // Images and dimensions
    val imageid: String? = null,
    val imageHeight: String? = null,
    val imageWidth: String? = null,
    val primeImage: List<ToiPrimeImageDto>? = null,

    // IDs can have different representations in the response
    val pid: JsonElement? = null,
    val listingFolderId: String? = null,
    val requestMsid: String? = null,

    // Article content
    val headline: ToiHeadlineDto? = null,
    val aiSummaryDesc: String? = null,
    val sectionInfo: List<ToiSectionInfoDto>? = null,
    val isLiveArticle: Boolean? = null,
    val appExclusive: ToiAppExclusiveDto? = null,

    // Other article metadata
    val pc: String? = null,
    val brand: String? = null,
    val webSeries: Boolean? = null,
    val cs: String? = null,

    // Live-event widget
    val liveEventData: ToiLiveEventDataDto? = null,

    // Live TV / video widget
    val channelLogo: String? = null,
    val caption: String? = null,
    val channelName: String? = null,
    val title: String? = null,
    val isAutoPlay: Boolean? = null,
    val videourl: String? = null,
    val radiourl: String? = null,
    val slide: String? = null,
    val channel_id: String? = null,
    val location_enabled: String? = null,
    val adUrl: String? = null,
    val fu: String? = null,
    val eid: String? = null,

    // Some widget-specific values vary by item
    val metainfo: JsonElement? = null,
    val adProperties: JsonElement? = null
)

@Serializable
data class ToiPubInfoDto(
    val pn: String? = null,
    val pnEng: String? = null,
    val channel: String? = null,
    val pnu: String? = null,
    val pid: JsonElement? = null,
    val lid: JsonElement? = null,
    val lang: String? = null
)

@Serializable
data class ToiAuthorDto(
    val name: String? = null,
    val deeplink: String? = null,
    val authorId: String? = null
)

@Serializable
data class ToiSectionInfoDto(
    val name: String? = null,
    val deeplink: String? = null
)

@Serializable
data class ToiHeadlineDto(
    val text: String? = null,
    val context: String? = null
)

@Serializable
data class ToiPrimeImageDto(
    val id: String? = null,
    val tn: String? = null,
    val dm: String? = null,
    val cc: String? = null,
    val h: String? = null,
    val w: String? = null
)

@Serializable
data class ToiAppExclusiveDto(
    val isAppExclusive: Boolean? = null,
    val appExclusiveDeeplink: String? = null
)

@Serializable
data class ToiLiveEventDataDto(
    val widgetTitle: String? = null,
    val widgetDeeplink: String? = null,
    val media: ToiMediaDto? = null,
    val liveLabelText: String? = null,
    val headline: String? = null,
    val liveUpdates: List<ToiLiveUpdateDto> = emptyList(),
    val liveUpdatesCount: Int? = null,
    val liveUpdatesDeeplink: String? = null,
    val liveUpdatesLinkText: String? = null,
    val relatedNews: List<ToiRelatedNewsDto> = emptyList(),
    val description: String? = null,
    val ctaText: String? = null,
    val ctaDeeplink: String? = null
)

@Serializable
data class ToiMediaDto(
    val type: String? = null,
    val imageid: String? = null
)

@Serializable
data class ToiLiveUpdateDto(
    val id: String? = null,
    val timestamp: Long? = null,
    val text: String? = null
)

@Serializable
data class ToiRelatedNewsDto(
    val id: String? = null,
    val title: String? = null,
    val tn: String? = null,
    val pc: String? = null,
    val dm: String? = null,
    val headline: ToiHeadlineDto? = null,
    val imageid: String? = null
)