package edu.buu.daowe.Util;

import com.baidubce.model.AbstractBceRequest;

public interface BceProgressCallback<T extends AbstractBceRequest> {
    // request为下载的请求
    // currentSize为当前下载的大小(单位:byte)
    // totalSize为本次请求需要下载的总大小(单位:byte)
    void onProgress(T request, long currentSize, long totalSize);
}
