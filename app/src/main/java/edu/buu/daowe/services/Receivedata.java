package edu.buu.daowe.services;

import android.bluetooth.le.ScanResult;

public interface Receivedata {
    void update(ScanResult scanResult);
    void update(int a);
}
