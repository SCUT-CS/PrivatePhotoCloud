package com.hao.zxinglib.zxing.inner;

public interface QrcodeScanInterface {
    void scanSuccess(String info);
    void scanFailure(String info);
}
