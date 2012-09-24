/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.telephony.uicc;

import android.os.*;
import android.util.Log;

import com.android.internal.telephony.CommandsInterface;

/**
 * {@hide}
 */
public final class RuimFileHandler extends IccFileHandler {
    static final String LOG_TAG = "CDMA";

    //***** Instance Variables

    //***** Constructor
    public RuimFileHandler(UiccCardApplication app, String aid, CommandsInterface ci) {
        super(app, aid, ci);
    }

    //***** Overridden from IccFileHandler

    @Override
    public void loadEFImgTransparent(int fileid, int highOffset, int lowOffset,
            int length, Message onLoaded) {
        Message response = obtainMessage(EVENT_READ_ICON_DONE, fileid, 0,
                onLoaded);

        mCi.iccIOForApp(COMMAND_GET_RESPONSE, fileid, getEFPath(fileid), 0, 0,
                GET_RESPONSE_EF_IMG_SIZE_BYTES, null, null,
                mAid, response);
    }

    @Override
    public void loadEFTransparent(int fileid, Message message) {
        if (fileid == EF_CSIM_EPRL) {
            Message response = obtainMessage(EVENT_READ_BINARY_DONE, fileid, 0, message);

            /*mCi.iccIOForApp(COMMAND_READ_BINARY, fileid, getEFPath(fileid), 0, 0, */
            mCi.iccIOForApp(COMMAND_GET_RESPONSE, fileid, "img", 0, 0,
                READ_RECORD_MODE_ABSOLUTE, null, null, mAid, response);
        } else {
            super.loadEFTransparent(fileid, message);
        }
    }

    @Override
    protected String getEFPath(int efid) {
		// Both EF_ADN and EF_CSIM_LI are referring to same constant value 0x6F3A.
        // So cannot derive different paths for them using exisitng logic
        // hence added work around to derive path for EF_ADN.
        if (efid == EF_ADN) {
            return MF_SIM + DF_TELECOM;
        }
        switch(efid) {
        case EF_SMS:
        case EF_CST:
        case EF_RUIM_SPN:
        case EF_CSIM_LI:
        case EF_CSIM_MDN:
        case EF_CSIM_IMSIM:
        case EF_CSIM_SF_EUIMID:
        case EF_CSIM_CDMAHOME:
        case EF_CSIM_EPRL:
            Log.d(LOG_TAG, "[CsimFileHandler] getEFPath for " + efid);
            return MF_SIM + DF_ADFISIM;
        case EF_FDN:
        case EF_MSISDN:
            return MF_SIM + DF_TELECOM;
        }
        return getCommonIccEFPath(efid);
    }

    @Override
    protected void logd(String msg) {
        Log.d(LOG_TAG, "[RuimFileHandler] " + msg);
    }

    @Override
    protected void loge(String msg) {
        Log.e(LOG_TAG, "[RuimFileHandler] " + msg);
    }

}
