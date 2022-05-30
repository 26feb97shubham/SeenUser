package com.seen.user.model

import com.google.gson.annotations.SerializedName

data class TrackFinalResultResponse (

	@SerializedName("track_final_result" ) var trackFinalResult  : ArrayList<TrackFinalResult> = arrayListOf(),
	@SerializedName("Error"              ) var Error             : String?                     = null,
	@SerializedName("ErrorDetails"       ) var ErrorDetails      : String?                     = null,
	@SerializedName("is_no_data"         ) var isNoData          : String?                     = null,
	@SerializedName("tracking_no"        ) var trackingNo        : String?                     = null,
	@SerializedName("tracking_ref_no"    ) var trackingRefNo     : String?                     = null,
	@SerializedName("status_en"          ) var statusEn          : String?                     = null,
	@SerializedName("status_ar"          ) var statusAr          : String?                     = null,
	@SerializedName("sender_name"        ) var senderName        : String?                     = null,
	@SerializedName("sender_contactno"   ) var senderContactno   : String?                     = null,
	@SerializedName("receiver_contactno" ) var receiverContactno : String?                     = null

)


data class TrackFinalResult (

	@SerializedName("location_en"     ) var locationEn    : String? = null,
	@SerializedName("location_ar"     ) var locationAr    : String? = null,
	@SerializedName("remarks_en"      ) var remarksEn     : String? = null,
	@SerializedName("remarks_ar"      ) var remarksAr     : String? = null,
	@SerializedName("time_stamp"      ) var timeStamp     : String? = null,
	@SerializedName("sub_status_code" ) var subStatusCode : String? = null,
	@SerializedName("url"             ) var url           : String? = null

)

