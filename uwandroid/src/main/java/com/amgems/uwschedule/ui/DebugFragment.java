package com.amgems.uwschedule.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import android.widget.CompoundButton;
import android.widget.RadioButton;
import com.amgems.uwschedule.R;
import com.amgems.uwschedule.util.NetUtils;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple fragment that can be used for debugging. Allows the rendering
 * of an input String as HTML. Observes a debug data source and updates
 * this fragments view of that data accordingly.
 */
public class DebugFragment extends Fragment implements View.OnClickListener {

    private static final String MIME_TYPE_HTML = "text/html";
    private static final String MIME_TYPE_PLAINTEXT = "text/plain";

    // mDebugContent should never hold a null value
    private String mDebugContent;
    private WebView mDebugWebview;

    private RadioButton mHtmlRadioButton;
    private RadioButton mPlainRadioButton;

    DebugFragment() {
        mDebugContent = "";
    }

    public static DebugFragment newInstance() {
        return new DebugFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.debug_fragment, container, false);
        mDebugWebview = (WebView) parentView.findViewById(R.id.debug_webview);
        mHtmlRadioButton = (RadioButton) parentView.findViewById(R.id.render_html_checkbox);
        mPlainRadioButton = (RadioButton) parentView.findViewById(R.id.render_plaintext_checkbox);
        mHtmlRadioButton.setOnClickListener(this);
        mPlainRadioButton.setOnClickListener(this);
        return parentView;
    }

    /**
     * Loads the following string into this DebugFragment
     *
     * The rendering type used will correspond the appropriate
     * rendering type currently selected.
     *
     * @param contentString The plaintext of the content to load
     */
    private void loadContent(String contentString) {
        mDebugContent = contentString;
        performRender();
    }

    @Override
    public void onClick(View v) {
        performRender();
    }

    private void performRender() {
        if (mPlainRadioButton.isChecked()) {
            renderAsHtml();
        } else {
            renderAsPlaintext();
        }
    }

    private void renderAsHtml() {
        mDebugWebview.loadData(mDebugContent, MIME_TYPE_HTML, NetUtils.CHARSET);
    }

    private void renderAsPlaintext() {
        mDebugWebview.loadData(mDebugContent, MIME_TYPE_PLAINTEXT, NetUtils.CHARSET);
    }

}
