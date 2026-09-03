package com.seuusuario.netflixlite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webViewNetflix);

        // Otimizações cruciais de desempenho para hardware fraco (Android 4.4)
        WebSettings settings = webView.getSettings();
        
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        
        // Gerenciamento de Cache para economizar RAM
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Desativar recursos que consomem muita RAM e não são vitais para streaming
        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkImage(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        // Forçar aceleração de hardware na GPU da TV
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // User-Agent de um navegador desktop moderno ou Smart TV para evitar bloqueios
        // (Nota: No Android 4.4, se a Netflix recusar por incompatibilidade de HTML5, 
        // podemos tentar simular um browser de PC robusto)
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");

        // Impedir que links abram em um navegador externo (manter dentro do app)
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Injetar script CSS/JS se precisar esconder elementos da página web
            }
        });

        // Carrega o site da Netflix
        webView.loadUrl("https://www.netflix.com/login");
    }

    // Gerenciamento do Controle Remoto da TV (Botão Voltar)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.clearCache(true);
            webView.destroy();
        }
        super.onDestroy();
    }
}
