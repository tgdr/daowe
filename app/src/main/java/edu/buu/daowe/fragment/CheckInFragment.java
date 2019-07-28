package edu.buu.daowe.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.Util.RssiUtil;
import edu.buu.daowe.Util.SharedPreferenceUtil;
import edu.buu.daowe.services.Receivedata;
import edu.buu.daowe.services.ScanBeaconService;

public class CheckInFragment extends Fragment {
    Button btn_checkin;
    TextView mText;
    ScanBeaconService.MyBinder mybinder;
    SharedPreferenceUtil sharedPreferenceUtil;
    ScanBeaconService mys;
    DaoWeApplication app;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (DaoWeApplication) getActivity().getApplication();
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getActivity().startActivity(enableBtIntent);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_checkin,null);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mText = view.findViewById(R.id.txttemps);
        btn_checkin = view.findViewById( R.id.btn_checkin);
        sharedPreferenceUtil = new SharedPreferenceUtil(getActivity());

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder myb) {


                    //   Log.e("ffffffffffffffff","ffffffffffffffffff");
                mybinder = (ScanBeaconService.MyBinder) myb;

                  mys = mybinder.getService();
                  if(mys.getErrorCode() == 1){
                      Toast.makeText(getActivity(),"不在教室范围之内",Toast.LENGTH_SHORT).show();
                  }
                 mys.setReceivedata(new Receivedata() {
                     @Override
                     public void update(ScanResult scanResult) {
                     //    Log.e("innerinnerinner","iupupupupupupupupupu");
                         if (scanResult!=null){
                         double distance = RssiUtil.getDistance(scanResult.getRssi());
                         final String address =  scanResult.getDevice().getAddress();
                         mText.setText("rssi:"+scanResult.getRssi()+"\n"+"二者间的距离为"+distance);
                         if(distance<=20){
                            btn_checkin.setVisibility(View.VISIBLE);
                         }
                         else{
                             btn_checkin.setVisibility(View.INVISIBLE);
                         }
                         btn_checkin.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                         .setMessage("签到时间为"+new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date())+"\n"+"教室ID为："+address).setTitle("签到成功");
                                 builder.show();
                             }
                         });
                     }
                         else{
                             Toast.makeText(getActivity(),"不在教室范围之内",Toast.LENGTH_SHORT).show();
                         }

                     }

                     @Override
                     public void update(int a) {
                         Toast.makeText(getActivity(),"不在教室范围之内",Toast.LENGTH_SHORT).show();
                     }
                 });


            }


            @Override
            public void onServiceDisconnected(ComponentName name) {
                    Log.e("disdisdissdids","disdisdisdisdis"+name);
            }
        };
        Intent intent = new Intent(getActivity(), ScanBeaconService.class);
        //getActivity().startService(intent);

        getActivity().bindService(intent,serviceConnection,getActivity().BIND_AUTO_CREATE);

     //   getActivity().startForegroundService(intent);


    }
    @Override
    public void onResume() {
        super.onResume();
        mText.setText(ms2Date(Long.parseLong(sharedPreferenceUtil.getTime())));
    }

    public static String ms2Date(long _ms){
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }
}
