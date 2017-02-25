package net.gotev.uploadservicedemo;

import android.content.Intent;
import android.widget.Toast;

import net.gotev.recycleradapter.AdapterItem;
import net.gotev.recycleradapter.RecyclerAdapter;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservicedemo.adapteritems.EmptyItem;
import net.gotev.uploadservicedemo.adapteritems.UploadItem;

import java.io.IOException;

import static net.gotev.uploadservicedemo.adapteritems.UploadItem.TYPE_FILE;
import static net.gotev.uploadservicedemo.adapteritems.UploadItem.TYPE_HEADER;
import static net.gotev.uploadservicedemo.adapteritems.UploadItem.TYPE_PARAMETER;

/**
 * @author Aleksandar Gotev
 */

public class MultipartUploadActivity extends UploadActivity {

    public static void show(BaseActivity activity) {
        activity.startActivity(new Intent(activity, MultipartUploadActivity.class));
    }

    @Override
    public AdapterItem getEmptyItem() {
        return new EmptyItem(R.string.empty_multipart_upload);
    }

    @Override
    public void onDone(String httpMethod, String serverUrl, RecyclerAdapter uploadItemsAdapter) {

        final MultipartUploadRequest request =
                new MultipartUploadRequest(this, serverUrl)
                .setMethod(httpMethod)
                .setUtf8Charset()
                .setNotificationConfig(getNotificationConfig(R.string.multipart_upload))
                .setMaxRetries(MAX_RETRIES)
                .setCustomUserAgent(getUserAgent())
                .setUsesFixedLengthStreamingMode(FIXED_LENGTH_STREAMING_MODE);

        forEachUploadItem(new ForEachDelegate() {

            @Override
            public void onUploadItem(UploadItem item) {

                switch (item.getType()) {
                    case TYPE_HEADER:
                        request.addHeader(item.getTitle(), item.getSubtitle());
                        break;

                    case TYPE_PARAMETER:
                        request.addParameter(item.getTitle(), item.getSubtitle());
                        break;

                    case TYPE_FILE:
                        try {
                            request.addFileToUpload(item.getSubtitle(), item.getTitle());
                        } catch (IOException exc) {
                            Toast.makeText(MultipartUploadActivity.this,
                                    getString(R.string.file_not_found, item.getSubtitle()),
                                    Toast.LENGTH_LONG).show();
                        }
                        break;

                    default:
                        break;
                }
            }

        });

        try {
            request.startUpload();
            finish();
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInfo() {
        openBrowser("https://github.com/gotev/android-upload-service/wiki/Recipes#http-multipartform-data-upload-rfc2388-");
    }
}