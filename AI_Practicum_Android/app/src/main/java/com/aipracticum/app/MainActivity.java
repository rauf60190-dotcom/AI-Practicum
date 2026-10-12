package com.aipracticum.app;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.webkit.WebViewAssetLoader;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final String TAG = "AIPracticum";
    private WebView webView;
    private WebViewAssetLoader assetLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(247, 248, 252));
        getWindow().setNavigationBarColor(Color.rgb(247, 248, 252));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSafeBrowsingEnabled(true);

        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return assetLoader.shouldInterceptRequest(Uri.parse(url));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                return !("https".equalsIgnoreCase(uri.getScheme())
                        && "appassets.androidplatform.net".equalsIgnoreCase(uri.getHost()));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                return !("https".equalsIgnoreCase(uri.getScheme())
                        && "appassets.androidplatform.net".equalsIgnoreCase(uri.getHost()));
            }
        });

        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    private final class AndroidBridge {
        @JavascriptInterface
        public boolean saveTextFile(String requestedName, String requestedMime, String content) {
            String filename = sanitizeFilename(requestedName);
            String mime = safeMimeType(requestedMime);
            if (content == null || mime == null || !hasAllowedExtension(filename, mime)) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Поддерживаются только Markdown- и текстовые документы.", Toast.LENGTH_LONG).show());
                return false;
            }
            Uri uri = null;
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                values.put(MediaStore.MediaColumns.MIME_TYPE, mime);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS + "/AI Practicum");
                values.put(MediaStore.MediaColumns.IS_PENDING, 1);
                uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new IllegalStateException("MediaStore insert returned null");
                try (OutputStream stream = getContentResolver().openOutputStream(uri, "w")) {
                    if (stream == null) throw new IllegalStateException("Output stream unavailable");
                    stream.write(content.getBytes(StandardCharsets.UTF_8));
                    stream.flush();
                }
                ContentValues ready = new ContentValues();
                ready.put(MediaStore.MediaColumns.IS_PENDING, 0);
                getContentResolver().update(uri, ready, null, null);
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Файл сохранён: Загрузки/AI Practicum/" + filename, Toast.LENGTH_LONG).show());
                return true;
            } catch (Exception ex) {
                if (uri != null) {
                    try {
                        getContentResolver().delete(uri, null, null);
                    } catch (Exception cleanupError) {
                        Log.w(TAG, "Could not remove incomplete download", cleanupError);
                    }
                }
                Log.e(TAG, "Could not save downloaded document", ex);
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Не удалось сохранить файл. Проверь свободное место.", Toast.LENGTH_LONG).show());
                return false;
            }
        }
    }

    private static String sanitizeFilename(String input) {
        String value = input == null ? "document.txt" : input.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_").trim();
        if (value.isEmpty() || value.equals(".") || value.equals("..")) value = "document.txt";
        if (value.length() > 120) value = value.substring(0, 120);
        return value;
    }

    private static String safeMimeType(String input) {
        if (input == null) return null;
        String value = input.split(";", 2)[0].trim().toLowerCase(java.util.Locale.ROOT);
        if ("text/markdown".equals(value)) return "text/markdown";
        if ("text/plain".equals(value)) return "text/plain";
        return null;
    }

    private static boolean hasAllowedExtension(String filename, String mime) {
        String value = filename.toLowerCase(java.util.Locale.ROOT);
        return ("text/markdown".equals(mime) && value.endsWith(".md"))
                || ("text/plain".equals(mime) && value.endsWith(".txt"));
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("AndroidBridge");
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
