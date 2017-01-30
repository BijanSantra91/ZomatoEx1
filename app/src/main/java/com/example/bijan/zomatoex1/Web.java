package com.example.bijan.zomatoex1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Web extends Fragment {
    WebView webView;

    public Web() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web, container, false);
        webView = (WebView) v.findViewById(R.id.webview);
        Bundle bundle = getArguments();
        if (bundle != null){
            String url = bundle.getString("url");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(url);
            webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if ((i == keyEvent.KEYCODE_BACK) && webView.canGoBack()){
                        webView.goBack();
                        return true;
                    }
                    return false;
                }
            });
        }
        return v;
    }
}
