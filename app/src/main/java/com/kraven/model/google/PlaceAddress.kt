package com.kraven.model.google

import com.google.gson.annotations.SerializedName

data class PlaceAddress(
    @field:SerializedName("status")
    var status: String? = null,
    @field:SerializedName("predictions")
    var predictions: List<Predictions>? = null
)

data class Predictions(
        @field:SerializedName("description")
    var description: String? = null,
        @field:SerializedName("id")
    var id: String? = null,
        @field:SerializedName("place_id")
    var placeId: String? = null,
        @field:SerializedName("reference")
    var reference: String? = null,
        @field:SerializedName("structured_formatting")
    var structuredFormatting: StructuredFormatting? = null,
        @field:SerializedName("matched_substrings")
    var matchedSubstrings: List<MatchedSubstrings>? = null,
        @field:SerializedName("terms")
    var terms: List<Terms>? = null,
        @field:SerializedName("types")
    var types: List<String>? = null
)

data class StructuredFormatting(
    @field:SerializedName("main_text")
    var mainText: String? = null,
    @field:SerializedName("secondary_text")
    var secondaryText: String? = null,
    @field:SerializedName("main_text_matched_substrings")
    var mainTextMatchedSubstrings: List<MainTextMatchedSubstrings>? = null
)

data class MainTextMatchedSubstrings(
    @field:SerializedName("length")
    var length: Int = 0,
    @field:SerializedName("offset")
    var offset: Int = 0
)

data class MatchedSubstrings(
    @field:SerializedName("length")
    var length: Int = 0,
    @field:SerializedName("offset")
    var offset: Int = 0
)

data class Terms(
    @field:SerializedName("offset")
    var offset: Int = 0,
    @field:SerializedName("value")
    var value: String? = null
)