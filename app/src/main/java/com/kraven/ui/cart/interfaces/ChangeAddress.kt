package com.kraven.ui.cart.interfaces

import com.kraven.ui.address.model.Address


interface ChangeAddress {
    fun onChange(item: Address)
}