package com.kraven.ui.cart

class CartSubTotalEvent(s:Float,q:String,id:String,itemPrice:String,totalQty:String){
    val dishPriceTopping=s
    val qty=q
    val id=id
    val itemPrice=itemPrice
    val totalQty=totalQty
}