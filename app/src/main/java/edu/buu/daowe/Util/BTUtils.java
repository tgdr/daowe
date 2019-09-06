package edu.buu.daowe.Util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

public class BTUtils {
    Context ct;
    BluetoothClient mClient;
    String[] result;
    Handler handler;
    int timeid = -1;
    boolean ppflag = false;

    public BTUtils(Context ct, Handler handler, int timeid) {
        this.ct = ct;
        this.handler = handler;
        this.timeid = timeid;
    }

    public void startscanner(final int majorid, final int minorid) {

        mClient = new BluetoothClient(ct);

        SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(2000, 1)   // 先扫BLE设备3次，每次3s

                //  .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s,在实际工作中没用到经典蓝牙的扫描

                //  .searchBluetoothLeDevice(10000)      // 再扫BLE设备2s
                .build();

        mClient.search(request, new SearchResponse() {


            @Override

            public void onSearchStarted() {//开始搜素

            }

            @Override
            public void onDeviceFounded(SearchResult device) {//找到设备 可通过manufacture过滤

                if (device.getName().equals("BUU")) {
//                    if(ScanRecordUtil.getMajorId(device.scanRecord) == majorid && ScanRecordUtil.getMinorId(device.scanRecord)==minorid){
//                        result[0]= ScanRecordUtil.getUUID(device.scanRecord);
//                        result[1] = String.valueOf(RssiUtil.getDistance(device.rssi));
//                        Log.e("scansuccess",result[0]);
//                    }


                    String getresult[] = ScanRecordUtil.getScanResult(device.scanRecord);
                    String uuid = getresult[0];
                    int major = Integer.parseInt(getresult[1]);
                    int minor = Integer.parseInt(getresult[2]);
                    Log.e(major + " " + majorid, minor + " " + minorid);
                    if (major == majorid && minor == minorid) {
                        //      Toast.makeText(ct,"匹配成功！uuid："+uuid,Toast.LENGTH_SHORT).show();
                        if (ppflag == false) {
                            result = new String[4];
                            result[0] = uuid;
                            result[1] = String.valueOf(major);
                            result[2] = String.valueOf(minor);
                            result[3] = String.valueOf(RssiUtil.getDistance(device.rssi));
                            Message msg = new Message();
                            msg.arg2 = timeid;
                            msg.what = 0x521;
                            msg.obj = result;
                            handler.sendMessage(msg);
                            ppflag = true;
                        }

                    } else {
                        Message msg = new Message();
                        msg.arg2 = -1;
                        msg.what = 0x521;
                        msg.obj = null;
                        handler.sendMessage(msg);
                    }
                    Beacon beacon = new Beacon(device.scanRecord);


//                    Log.e("UUid",ScanRecordUtil.getUUID(device.scanRecord));
//                    Log.e("MajorId",ScanRecordUtil.getMajorId(device.scanRecord)+"");
//                    Log.e("MinorId",ScanRecordUtil.getMinorId(device.scanRecord)+"");
//                    Log.e("Distance",RssiUtil.getDistance(device.rssi)+"");
                    //  ScanRecordUtil.test(device.scanRecord);
                }
                //     BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                //   Log.e("device"+device.getName(),beacon.mBytes);
                // ScanRecordUtil.test(device.scanRecord);

            }

            @Override

            public void onSearchStopped() {//搜索停止

            }

            @Override

            public void onSearchCanceled() {//搜索取消

            }

        });

    }

    public void stopscanner() {
        mClient.stopSearch();

    }
}
