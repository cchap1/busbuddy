package cs371m.csc2726.busbuddy;

import android.content.Context;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// Store files in firebase storage
public class Storage {
    StorageReference photoStorage;
    Context context;

    private static class Holder {
        public static Storage helper = new Storage();
    }

    // Every time you need the Net object, you must get it via this helper function
    public static Storage getInstance() {
        return Holder.helper;
    }
    // Call init before using instance
    public static synchronized void init(Context context) {
        Holder.helper.context = context;
        // Initialize our reference, which is a photo collection that has per-user subcollections of photo files
        Holder.helper.photoStorage = FirebaseStorage.getInstance().getReference().child("photos");
    }
    protected StorageReference fileStorage(PhotoObject photoObject) {
        return photoStorage
                .child(photoObject.getUidOwner())
                .child(photoObject.getPhotoId() + ".jpg");
    }

    public void uploadJpg (PhotoObject photoObject, byte[] data) {
        // XXX Write me
        fileStorage(photoObject).putBytes(data);
    }

    public void displayJpg(PhotoObject photoObject, ImageView imageView) {
        // XXX Write me
        //GlideApp.with(context).load(fileStorage(photoObject)).into(imageView);
    }
}
