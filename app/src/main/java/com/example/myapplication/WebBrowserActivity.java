package com.example.myapplication;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WebBrowserActivity extends AppCompatActivity {
    private WebView webView;
    private EditText etUrl;
    private static final String HOME_URL = "https://www.google.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);
        
        webView = findViewById(R.id.web_view);
        etUrl = findViewById(R.id.et_url);
        
        setupWebView();
        setupNavigationControls();
        setupQuickAccessButtons();
        
        // Cargar Google como página inicial
        webView.loadUrl(HOME_URL);
    }
    
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                etUrl.setText(url);
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Aquí podrías mostrar una barra de progreso
            }
        });
    }
    
    private void setupNavigationControls() {
        // Botones de navegación
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
        
        findViewById(R.id.btn_forward).setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });
        
        findViewById(R.id.btn_refresh).setOnClickListener(v -> {
            webView.reload();
        });
        
        findViewById(R.id.btn_home).setOnClickListener(v -> {
            webView.loadUrl(HOME_URL);
        });
        
        findViewById(R.id.btn_go).setOnClickListener(v -> {
            loadUrl();
        });
        
        // Enter en la barra de URL
        etUrl.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                loadUrl();
                return true;
            }
            return false;
        });
    }
    
    private void setupQuickAccessButtons() {
        findViewById(R.id.btn_google).setOnClickListener(v -> {
            webView.loadUrl("https://www.google.com");
        });
        
        findViewById(R.id.btn_youtube).setOnClickListener(v -> {
            webView.loadUrl("https://www.youtube.com");
        });
        
        findViewById(R.id.btn_close).setOnClickListener(v -> {
            finish();
        });
    }
    
    private void loadUrl() {
        String url = etUrl.getText().toString().trim();
        
        if (url.isEmpty()) {
            return;
        }
        
        // Si no empieza con http/https, buscar en Google
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (url.contains(".")) {
                // Probablemente es una URL sin protocolo
                url = "https://" + url;
            } else {
                // Buscar en Google
                url = "https://www.google.com/search?q=" + url.replace(" ", "+");
            }
        }
        
        webView.loadUrl(url);
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
} 