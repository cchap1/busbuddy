package cs371m.csc2726.busbuddy;

import android.os.Handler;
import android.os.HandlerThread;


public class SecondHandler {
    public interface IUpdate {
        void checkDriver();
    }
    protected Runnable rateLimitRequest;
    protected final int rateLimitMillis = 1000;
    protected IUpdate iUpdate;
    protected Handler handler;
    protected HandlerThread handlerThread;
    public SecondHandler(final IUpdate iUpdate) {
        handlerThread = new HandlerThread("SecondHandler");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        this.iUpdate = iUpdate;
        rateLimitRequest = new Runnable(){
            @Override
            public void run() {
                iUpdate.checkDriver();
                handler.postDelayed(this, rateLimitMillis);
            }
        };
        handler.postDelayed(rateLimitRequest, rateLimitMillis);
    }
}
