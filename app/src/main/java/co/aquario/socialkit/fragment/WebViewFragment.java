package co.aquario.socialkit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.main.BaseFragment;

/**
 * Created by Mac on 7/20/15.
 */
public class WebViewFragment extends BaseFragment implements ObservableScrollViewCallbacks {
    public static WebViewFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString("URL",url);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public String url = "https://www.vdomax.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            url = getArguments().getString("URL");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_webview,container,false);

        ObservableWebView webView = (ObservableWebView) rootView.findViewById(R.id.webView);
        webView.setScrollViewCallbacks(this);
        webView.loadUrl(url);

        return rootView;
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
