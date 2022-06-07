package com.kraven.utils;

import android.content.Intent;

public interface OtpReceivedInterface {
    void onSuccess(Intent intent);
    void onFailure();
}