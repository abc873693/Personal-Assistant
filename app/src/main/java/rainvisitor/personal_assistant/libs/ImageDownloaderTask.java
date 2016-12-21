package rainvisitor.personal_assistant.libs;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Ray on 2016/12/20.
 */

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;

    public ImageDownloaderTask(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return Utils.downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageDrawable(null);
                    imageView.setVisibility(View.GONE);
                }
            }
        }
    }
}
