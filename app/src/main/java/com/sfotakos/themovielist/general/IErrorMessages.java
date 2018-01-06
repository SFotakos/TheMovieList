package com.sfotakos.themovielist.general;

import android.support.annotation.Nullable;

/**
 * Created by spyridion on 06/01/18.
 */

public interface IErrorMessages {
    // Shows error message, a null message clears and hide the previous error message.
    void showErrorMessage(@Nullable String message);
}
