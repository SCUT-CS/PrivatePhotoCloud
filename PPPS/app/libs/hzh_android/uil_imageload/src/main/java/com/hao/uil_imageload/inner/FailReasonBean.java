package com.hao.uil_imageload.inner;

import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * FailReason的包装类
 * @author WaterWood
 */
public class FailReasonBean {

    private FailReason failReason;

    public FailReason getFailReason() {
        return failReason;
    }

    public void setFailReason(FailReason failReason) {
        this.failReason = failReason;
    }
}
