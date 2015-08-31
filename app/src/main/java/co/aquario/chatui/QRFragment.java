package co.aquario.chatui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.fragment.main.BaseFragment;

/**
 * Created by Mac on 7/20/15.
 */
public class QRFragment extends BaseFragment  {
    public static QRFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString("USER_ID",userId);
        QRFragment fragment = new QRFragment();
        fragment.setArguments(args);
        return fragment;
    }

    String userId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_qr,container,false);

        ImageView qrIv = (ImageView) rootView.findViewById(R.id.qr_here);

        qrIv.setImageBitmap(ChatUIActivity.encodeToQrCode(VMApp.mPref.username().getOr(""), 250, 250));

        return rootView;
    }


}
