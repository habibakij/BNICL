package com.csi.bnic.Insurance.Web;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.aapbd.appbajarlib.view.WebViewFormatter;
import com.csi.bnic.R;

public class ActivityWeb extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webView);
        WebViewFormatter.formatWebViewWithClient(webView,true,true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        int intValue = getIntent().getIntExtra("passValue",0);
        try {
            if (intValue == 1){
                webView.loadUrl("https://drive.google.com/open?id=1coOIwQPMKGdhXxr8Jp-lgT9lRxqWiYSD");
            }if (intValue == 2){
                webView.loadUrl("https://drive.google.com/open?id=1y3FvlJwrdU1RGsdKX9g0tbPrZsL0WFX1");
            }if (intValue == 3){
                webView.loadUrl("https://bnicl.net/about-us/");
            }if (intValue == 4){
                webView.loadUrl("https://bnicl.net/company-profile/");
            }if (intValue == 5){
                webView.loadUrl("https://bnicl.net/board-of-directors/");
            }if (intValue == 6){
                webView.loadUrl("https://bnicl.net/management-list/");
            }if (intValue == 7){
                webView.loadUrl("https://bnicl.net/branches-office/");
            }if (intValue == 8){
                webView.loadUrl("https://bnicl.net/claim-information/");
            }
        }catch (Exception e){

        }
    }
    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            webView.canGoBack();
            webView.clearCache(true);
            webView.clearView();
            webView.destroyDrawingCache();
            webView.loadUrl("about:blank");
            finish();
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            return false;
        }
        else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            return false;
        }
        else
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);


    }
}
