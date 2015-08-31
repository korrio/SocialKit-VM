package co.aquario.socialkit.fragment;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.adapter.AdapterMaxPoints;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.util.Purchase;
import co.aquario.socialkit.util.Utils;

public class MaxpointFragment extends BaseFragment {

    private static String userId;



    int[] resId = { R.drawable.mp_01
            , R.drawable.mp_02, R.drawable.mp_03
            , R.drawable.mp_04, R.drawable.mp_05
            , R.drawable.mp_06};

    String[] list = { "100 Points", "165 Points", "345 Points"
            , "1,080 Points", "1,875 Points", "3,899 Points"};

    String[] bath = { "2.99", "4.99", "9.99"
            , "29.99", "49.99", "99.99"};

    // ProductID
    private final String productID = "coins_1";	// Test Product ID by
    private final String productID2 = "coins_2";
    private final String productID3 = "coins_3";

    private Context context;
    private String tag;

    private IInAppBillingService mService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    // Purchase
    private Purchase purchaseOwned;

    public static MaxpointFragment newInstance(String id) {
        MaxpointFragment mFragment = new MaxpointFragment();
        //userId = id;
        Bundle mBundle = new Bundle();
        mBundle.putString("USER_ID", id);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }

        AQuery aq = new AQuery(getActivity());
        String currentMaxpointApi = "https://www.vdomax.com/paymentm/api/here.php?action=getbalance&user_id="+ VMApp.mPref.userId().getOr("0")+"&mobile=1";
        aq.ajax(currentMaxpointApi, JSONObject.class, this, "getCurrentMp");

    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mpTv.setText(currentMpStr);

    }

    TextView mpTv;

    String currentMp = "";
    String currentMpStr = "";

    public void getCurrentMp(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        Log.e("jo", jo.toString(4));
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        if (jo != null) {

            currentMp = jo.optString("current_amount");
            currentMpStr = formatter.format(currentMp) + " MP";

            Utils.showToast(currentMpStr);

        }
    }

    View headerView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.maxpoint_main, container, false);

        Intent billintIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        billintIntent.setPackage("com.android.vending");
        tag = "in_app_billing_ex2";
        final boolean blnBind = getActivity().bindService(billintIntent,
                mServiceConn, Context.BIND_AUTO_CREATE);


        //Toast.makeText(context, "bindService - return " + String.valueOf(blnBind), Toast.LENGTH_SHORT).show();
        Log.i(tag, "bindService - return " + String.valueOf(blnBind));

        context = getActivity();
        ListView mListView = (ListView)rootView.findViewById(R.id.list_view);
        AdapterMaxPoints adapterMaxPoints = new AdapterMaxPoints(getActivity(),list,resId,bath);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.item_header_maxpoint, mListView, false);
        mpTv = (TextView) headerView.findViewById(R.id.current_maxpoint);

        mListView.addHeaderView(headerView);
        mListView.setAdapter(adapterMaxPoints);
        // log tag



        mListView.setAdapter(adapterMaxPoints);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 1){
                    if (!blnBind) return;
                    if (mService == null) return;

                    ArrayList<String> skuList = new ArrayList<String>();
                    skuList.add(productID);
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                    Bundle skuDetails;
                    try {
                        skuDetails = mService.getSkuDetails(3, getActivity().getPackageName(), "inapp", querySkus);

                        // Toast.makeText(context, "getSkuDetails() - success return Bundle", Toast.LENGTH_SHORT).show();
                        Log.i(tag, "getSkuDetails() - success return Bundle");
                    } catch (RemoteException e) {
                        e.printStackTrace();

                        //Toast.makeText(context, "getSkuDetails() - fail!", Toast.LENGTH_SHORT).show();
                        Log.w(tag, "getSkuDetails() - fail!");
                        return;
                    }

                    int response = skuDetails.getInt("RESPONSE_CODE");
                    //Toast.makeText(context, "getSkuDetails() - \"RESPONSE_CODE\" return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                    Log.i(tag, "getSkuDetails() - \"RESPONSE_CODE\" return " + String.valueOf(response));

                    if (response != 0) return;

                    ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                    Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\" return " + responseList.toString());

                    if (responseList.size() == 0) return;

                    for (String thisResponse : responseList) {
                        try {
                            JSONObject object = new JSONObject(thisResponse);

                            String sku   = object.getString("productId");
                            String title = object.getString("title");
                            String price = object.getString("price");

                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"productId\" return " + sku);
                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"title\" return " + title);
                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"price\" return " + price);

                            if (!sku.equals(productID)) continue;

                            Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");

                            //Toast.makeText(context, "getBuyIntent() - success return Bundle", Toast.LENGTH_SHORT).show();
                            Log.i(tag, "getBuyIntent() - success return Bundle");

                            response = buyIntentBundle.getInt("RESPONSE_CODE");
                            //Toast.makeText(context, "getBuyIntent() - \"RESPONSE_CODE\" return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                            Log.i(tag, "getBuyIntent() - \"RESPONSE_CODE\" return " + String.valueOf(response));

                            if (response != 0) continue;

                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();

                            //Toast.makeText(context, "getSkuDetails() - fail!", Toast.LENGTH_SHORT).show();
                            Log.w(tag, "getBuyIntent() - fail!");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }if(i == 2){
                    if (!blnBind) return;
                    if (mService == null) return;

                    ArrayList<String> skuList = new ArrayList<String>();
                    skuList.add(productID2);
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                    Bundle skuDetails;
                    try {
                        skuDetails = mService.getSkuDetails(3, getActivity().getPackageName(), "inapp", querySkus);

                        //Toast.makeText(context, "getSkuDetails() - success return Bundle", Toast.LENGTH_SHORT).show();
                        Log.i(tag, "getSkuDetails() - success return Bundle");
                    } catch (RemoteException e) {
                        e.printStackTrace();

                        //Toast.makeText(context, "getSkuDetails() - fail!", Toast.LENGTH_SHORT).show();
                        Log.w(tag, "getSkuDetails() - fail!");
                        return;
                    }

                    int response = skuDetails.getInt("RESPONSE_CODE");
                    //Toast.makeText(context, "getSkuDetails() - \"RESPONSE_CODE\" return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                    Log.i(tag, "getSkuDetails() - \"RESPONSE_CODE\" return " + String.valueOf(response));

                    if (response != 0) return;

                    ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                    Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\" return " + responseList.toString());

                    if (responseList.size() == 0) return;

                    for (String thisResponse : responseList) {
                        try {
                            JSONObject object = new JSONObject(thisResponse);

                            String sku   = object.getString("productId");
                            String title = object.getString("title");
                            String price = object.getString("price");

                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"productId\" return " + sku);
                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"title\" return " + title);
                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"price\" return " + price);

                            if (!sku.equals(productID2)) continue;

                            Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");

                            //Toast.makeText(context, "getBuyIntent() - success return Bundle", Toast.LENGTH_SHORT).show();
                            Log.i(tag, "getBuyIntent() - success return Bundle");

                            response = buyIntentBundle.getInt("RESPONSE_CODE");
                            //Toast.makeText(context, "getBuyIntent() - \"RESPONSE_CODE\" return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                            Log.i(tag, "getBuyIntent() - \"RESPONSE_CODE\" return " + String.valueOf(response));

                            if (response != 0) continue;

                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();

                            Toast.makeText(context, "getSkuDetails() - fail!", Toast.LENGTH_SHORT).show();
                            Log.w(tag, "getBuyIntent() - fail!");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }if(i == 3){
                    if (!blnBind) return;
                    if (mService == null) return;

                    ArrayList<String> skuList = new ArrayList<String>();
                    skuList.add(productID3);
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                    Bundle skuDetails;
                    try {
                        skuDetails = mService.getSkuDetails(3, getActivity().getPackageName(), "inapp", querySkus);

                        //Toast.makeText(context, "getSkuDetails() - success return Bundle", Toast.LENGTH_SHORT).show();
                        Log.i(tag, "getSkuDetails() - success return Bundle");
                    } catch (RemoteException e) {
                        e.printStackTrace();

                        Toast.makeText(context, "getSkuDetails() - fail!", Toast.LENGTH_SHORT).show();
                        Log.w(tag, "getSkuDetails() - fail!");
                        return;
                    }

                    int response = skuDetails.getInt("RESPONSE_CODE");
                    //Toast.makeText(context, "getSkuDetails() - \"RESPONSE_CODE\" return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                    Log.i(tag, "getSkuDetails() - \"RESPONSE_CODE\" return " + String.valueOf(response));

                    if (response != 0) return;

                    ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                    Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\" return " + responseList.toString());

                    if (responseList.size() == 0) return;

                    for (String thisResponse : responseList) {
                        try {
                            JSONObject object = new JSONObject(thisResponse);

                            String sku   = object.getString("productId");
                            String title = object.getString("title");
                            String price = object.getString("price");

                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"productId\" return " + sku);
                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"title\" return " + title);
                            Log.i(tag, "getSkuDetails() - \"DETAILS_LIST\":\"price\" return " + price);

                            if (!sku.equals(productID3)) continue;

                            Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");

                            Toast.makeText(context, "getBuyIntent() - success return Bundle", Toast.LENGTH_SHORT).show();
                            Log.i(tag, "getBuyIntent() - success return Bundle");

                            response = buyIntentBundle.getInt("RESPONSE_CODE");
                            //Toast.makeText(context, "getBuyIntent() - \"RESPONSE_CODE\" return " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                            Log.i(tag, "getBuyIntent() - \"RESPONSE_CODE\" return " + String.valueOf(response));

                            if (response != 0) continue;

                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();

                            //Toast.makeText(context, "getSkuDetails() - fail!", Toast.LENGTH_SHORT).show();
                            Log.w(tag, "getBuyIntent() - fail!");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode != getActivity().RESULT_OK) return;

            int responseCode = data.getIntExtra("RESPONSE_CODE", 1);
            Toast.makeText(context, "onActivityResult() - \"RESPONSE_CODE\" return " + String.valueOf(responseCode), Toast.LENGTH_SHORT).show();
            Log.i(tag, "onActivityResult() - \"RESPONSE_CODE\" return " + String.valueOf(responseCode));

            if (responseCode != 0) return;

            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            Log.i(tag, "onActivityResult() - \"INAPP_PURCHASE_DATA\" return " + purchaseData.toString());
            Log.i(tag, "onActivityResult() - \"INAPP_DATA_SIGNATURE\" return " + dataSignature.toString());

            // TODO: management purchase result
        }
    }

    @Override
    public void onDestroy() {
        // Unbind Service
        if (mService != null)
            getActivity().unbindService(mServiceConn);

        super.onDestroy();
    }

}
