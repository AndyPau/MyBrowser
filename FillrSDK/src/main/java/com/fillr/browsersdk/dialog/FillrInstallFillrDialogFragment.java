/*
 * Copyright 2015-present Pop Tech Pty Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fillr.browsersdk.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.fillr.browsersdk.Fillr;
import com.fillr.browsersdk.R;
import com.fillr.browsersdk.utilities.FillrFontUtility;

/**
 * Created by AlexZhaoBin on 4/06/15.
 */
public class FillrInstallFillrDialogFragment extends DialogFragment {


    public static final String FRAGMENT_TAG = "installfillrdialogfragment";

    public static FillrInstallFillrDialogFragment newInstance() {
        FillrInstallFillrDialogFragment f = new FillrInstallFillrDialogFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.com_fillr_dialog_fragment_install_fillr, null);
        TextView titleText = (TextView) view.findViewById(R.id.dialog_title_text);
        TextView contentText = (TextView) view.findViewById(R.id.dialog_content_text);

        Button closeDialog = (Button) view.findViewById(R.id.id_btn_no);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        closeDialog.setTransformationMethod(null);
        Button approveDialog = (Button) view.findViewById(R.id.id_btn_yes);
        approveDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fillr.getInstance().downloadFillrApp();
                dismiss();
            }
        });
        approveDialog.setTransformationMethod(null);
        FillrFontUtility.getInstance().setCustomFont(getActivity(), FillrFontUtility.FONT_TYPE.ROBOTO_REGULAR, false, titleText, contentText, closeDialog, approveDialog);

        return view;
    }
}
