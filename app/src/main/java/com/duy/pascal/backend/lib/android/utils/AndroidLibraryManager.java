/*
 *  Copyright 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.backend.lib.android.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import com.duy.pascal.backend.lib.PascalLibrary;
import com.duy.pascal.backend.lib.android.temp.AndroidUtilsLib;
import com.googlecode.sl4a.jsonrpc.RpcReceiverManager;

import java.lang.reflect.Method;

public class AndroidLibraryManager extends RpcReceiverManager {

    private Context mContext;
    private int mSdkLevel;

    public AndroidLibraryManager(int sdkLevel, Activity context) {
        mSdkLevel = sdkLevel;
        if (context != null) {
            mContext = context.getApplicationContext();
        }
    }

    public int getSdkLevel() {
        return mSdkLevel;
    }

    @Nullable
    public Context getContext() {
        return mContext;
    }

    @Override
    public Object invoke(Class<? extends PascalLibrary> clazz, Method method, Object[] args)
            throws Exception {
        return super.invoke(clazz, method, args);
    }

    public AndroidUtilsLib.Resources getAndroidFacadeResources() {
        return new AndroidUtilsLib.Resources() {
            @Override
            public int getLogo48() {
                // TODO(Alexey): As an alternative, ask application for resource ids.
                String packageName = mContext.getPackageName();
                return mContext.getResources().getIdentifier("script_logo_48", "drawable", packageName);
            }
        };
    }
}
