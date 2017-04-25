package com.havrylyuk.weather.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.havrylyuk.weather.R;

/**
 *
 * Created by Igor Havrylyuk on 17.02.2017.
 */

public class AboutDialog extends DialogFragment implements View.OnClickListener {

    public static final String ABOUT_DIALOG_TAG = "About_Dialog_Tag";

    public static AboutDialog newInstance() {
        return new AboutDialog();
    }

    public AboutDialog() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog builder = new Dialog(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_about, null);
        TextView title = (TextView) view.findViewById(R.id.about_title_text);
        if (null != title) {
            title.setText(getString(R.string.about));
        }
        TextView message = (TextView) view.findViewById(R.id.dialog_message);
        if (null != message) {
            message.setText(fromHtml(getString(R.string.about_content)));
        }
        Button btnOk = (Button) view.findViewById(R.id.button_ok);
        if (null != btnOk) {
            btnOk.setOnClickListener(this);
        }
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setCanceledOnTouchOutside(false);
        builder.setCancelable(false);
        builder.setContentView(view);
        return  builder;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_ok) {
            dismiss();
        }
    }
    @SuppressWarnings("deprecation")
    private CharSequence fromHtml(@NonNull String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

}
