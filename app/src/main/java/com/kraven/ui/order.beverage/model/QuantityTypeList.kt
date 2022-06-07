package com.kraven.ui.order.beverage.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class QuantityTypeList(@SerializedName("quantity_type_list")
                            @Expose
                            var quantityTypeList: HashMap<String,String>? = null,
                            @SerializedName("beverage_list")
                            @Expose
                            var beverageList: HashMap<String,String>? = null)
