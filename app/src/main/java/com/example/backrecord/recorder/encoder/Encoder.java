package com.example.backrecord.recorder.encoder;


import java.io.IOException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
public interface Encoder {

    /* loaded from: C:\Users\30955\Desktop\apktool\dex2jar\ZuiGameHelper\classes5.dex */
    public interface Callback {
        void onError(Encoder encoder, Exception exc);
    }

    void prepare() throws IOException;

    void release();

    void setCallback(Callback callback);

    void stop();
}