package com.kraven.ui.home.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CuisinesItem(@SerializedName("cuisine")
                        val cuisine: String = "",
                        @SerializedName("id")
                        val id: Int = 0) : Parcelable

@Parcelize
data class Timing(@SerializedName("start_time")
                  var startTime: String = "",
                  @SerializedName("restaurant_id")
                  var restaurantId: Int = 0,
                  @SerializedName("end_time")
                  var endTime: String = "",
                  @SerializedName("insertdate")
                  val insertdate: String = "",
                  @SerializedName("date")
                  val date: String? = null,
                  @SerializedName("id")
                  var id: Int = 0,
                  @SerializedName("day")
                  val day: String = "",
                  @SerializedName("statusCode")
                  @Transient
                  var statusCode: Int = -1,
                  @SerializedName("status")
                  var status: String = "") : Parcelable

@Parcelize
data class Restaurants(@SerializedName("address")
                       val address: String? = null,
                       @SerializedName("main_branch_id")
                       val mainBranchId: String? = null,

                       @SerializedName("average_prep_time")
                       val averagePrepTime: String? = null,

                       @SerializedName("has_subbranch")
                       val hasSubBranch: String? = null,

                       @SerializedName("distance_radius")
                       val distanceRadius: Int? = null,

                       @SerializedName("reviews")
                       val reviews: Int? = null,

                       @SerializedName("distance")
                       val distance: Double? = null,

                       @SerializedName("delivery_type")
                       val deliveryType: String? = null,

                       @SerializedName("availability_status")
                       val availabilityStatus: String? = null,

                       @SerializedName("timing")
                       var timing: ArrayList<Timing>? = null,

                       @SerializedName("name")
                       val name: String? = null,

                       @SerializedName("rating")
                       val rating: Int? = null,

                       @SerializedName("banner")
                       val banner: String? = null,

                       @SerializedName("id")
                       val id: Int? = null,

                       @SerializedName("is_favourite")
                       var isFavourite: Int? = null,

                       @SerializedName("status")
                       val status: String? = null,

                       @SerializedName("cuisines")
                       val cuisines: List<CuisinesItem>?) : Parcelable


