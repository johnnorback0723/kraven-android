package com.kraven.ui.review.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Review(@SerializedName("id")
                  @Expose
                  var id: String?,
                  @SerializedName("order_id")
                  @Expose
                  var order_id: String?,
                  @SerializedName("user_id")
                  @Expose
                  var user_id: Int?,
                  @SerializedName("vendor_id")
                  @Expose
                  var vendor_id: String?,
                  @SerializedName("rate")
                  @Expose
                  var rate: String?,
                  @SerializedName("comment")
                  @Expose
                  var comment: String?,
                  @SerializedName("profile_image")
                  @Expose
                  var profile_image: String?,
                  @SerializedName("name")
                  @Expose
                  var name: String?,
                  @SerializedName("insertdate")
                  @Expose
                  var insertdate: String?,
                  @SerializedName("vendor_type")
                  @Expose
                  var vendor_type: String?) {
}