package com.kraven.ui.payment.fragment;

public interface OnEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}