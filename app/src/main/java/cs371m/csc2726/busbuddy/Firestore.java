package cs371m.csc2726.busbuddy;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

public class Firestore {

    protected FirebaseFirestore db;
    protected Auth auth;

    private static class Holder {
        public static Firestore helper = new Firestore();
    }
    // Every time you need the Net object, you must get it via this helper function
    public static Firestore getInstance() {
        return Holder.helper;
    }
    // Call init before using instance
    public static synchronized void init(Auth auth) {
        Holder.helper.db = FirebaseFirestore.getInstance();
        if( Holder.helper.db == null ) {
            Log.d("TAG", "XXX FirebaseFirestore is null!");
        }
        Holder.helper.auth = auth;
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        Holder.helper.db.setFirestoreSettings(settings);
    }

    void saveKid(PhotoObject photo) {
        Log.d("TAG", "saveKid: " + db);
        db.collection("kids").document("information")
                .set(photo);
    }

}
