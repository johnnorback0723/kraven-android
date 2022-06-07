package com.kraven.ui.base;


import io.reactivex.disposables.Disposable;

/**
 * Created by hlink21 on 25/4/16.
 */
public interface RootView {

    void showMessage(String message);

    void showLoader();

    void hideLoader();

    void hideKeyBoard();

    void showKeyBoard();

    void onError(Throwable throwable);

    void addDisposable(Disposable disposable);

    void showGreenMessage(String message);

}
