// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("xwalk")
// TODO(yongsheng): remove public modifier.
public class XWalkHttpAuthHandler {

    private final boolean mFirstAttempt;
    private int mNativeXWalkHttpAuthHandler;

    private XWalkHttpAuthHandler(int nativeXWalkHttpAuthHandler, boolean firstAttempt) {
        mNativeXWalkHttpAuthHandler = nativeXWalkHttpAuthHandler;
        mFirstAttempt = firstAttempt;
    }

    @CalledByNative
    public static XWalkHttpAuthHandler create(int nativeXWalkAuthHandler, boolean firstAttempt) {
        return new XWalkHttpAuthHandler(nativeXWalkAuthHandler, firstAttempt);
    }

    public void proceed(String username, String password) {
        if (mNativeXWalkHttpAuthHandler != 0) {
            nativeProceed(mNativeXWalkHttpAuthHandler, username, password);
            mNativeXWalkHttpAuthHandler = 0;
        }
    }

    public void cancel() {
        if (mNativeXWalkHttpAuthHandler != 0) {
            nativeCancel(mNativeXWalkHttpAuthHandler);
            mNativeXWalkHttpAuthHandler = 0;
        }
    }

    public boolean isFirstAttempt() {
        return mFirstAttempt;
    }

    @CalledByNative
    void handlerDestroyed() {
        mNativeXWalkHttpAuthHandler = 0;
    }

    private native void nativeProceed(int nativeXWalkHttpAuthHandler,
                                      String username, String password);

    private native void nativeCancel(int nativeXWalkHttpAuthHandler);
}

