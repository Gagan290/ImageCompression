package com.compressor.OtherImageComprassion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cocosw.bottomsheet.BottomSheet;
import com.compressor.sample.R;

import java.io.File;
import java.io.IOException;

/**
 * Capture image from camera and set in imageview by reducing its size without affecting quality.
 */
public class ImageComprassionActivity extends AppCompatActivity {

    public static String imageFilePath;//Made it static as need to override the original image with compressed image.
    private final int REQUEST_CODE_CLICK_IMAGE = 1002, REQUEST_CODE_GALLERY_IMAGE = 1003;
    private Button mbtnCapture;
    private ImageView mivImage;
    private Context mContext;
    private BottomSheet mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_comprassion);
        mContext = ImageComprassionActivity.this;
        mbtnCapture = (Button) findViewById(R.id.button);
        mivImage = (ImageView) findViewById(R.id.imageView);
        imageFilePath = CommonUtils.getFilename();

        Log.d("Image Path===", imageFilePath);
        mbtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionBottomSheetDialog();
            }
        });
        createBottomSheetDialog();
    }

    /**
     * Request for camera app to open and capture image.
     *
     * @param isFromGallery-if true then launch gallery app else camera app.
     */
    public void startIntent(boolean isFromGallery) {
        if (!isFromGallery) {
            File imageFile = new File(imageFilePath);
            Uri imageFileUri = Uri.fromFile(imageFile); // convert path to Uri
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);   // set the image file name
            startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
        } else if (isFromGallery) {
            File imageFile = new File(imageFilePath);
            Uri imageFileUri = Uri.fromFile(imageFile); // convert path to Uri
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);   // set the image file name
            startActivityForResult(
                    Intent.createChooser(intent, "Select File"),
                    REQUEST_CODE_GALLERY_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CLICK_IMAGE) {
            new ImageCompression().execute(imageFilePath);
        } else if (requestCode == REQUEST_CODE_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            final String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            //copy the selected file of gallery into app's sdcard folder and perform the compression operations on it.
            //And override the original image with the newly resized image.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CommonUtils.copyFile(picturePath, imageFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            new ImageCompression().execute(imageFilePath);

        }
    }


    /**
     * Show online options with the bottom sheet dialog with hangout,viber,skype calling options
     */
    private void showOptionBottomSheetDialog() {
        mBottomSheetDialog.show();
    }

    /**
     * Create a bottomsheet dialog.
     */
    public void createBottomSheetDialog() {
        BottomSheet.Builder builder = new BottomSheet.Builder(ImageComprassionActivity.this).title("Choose Option").sheet(R
                .menu
                .image_selection_option).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.it_camera:
                        startIntent(false);
                        break;
                    case R.id.it_gallery:
                        startIntent(true);

                        break;
                    case R.id.it_cancel:

                        break;
                }
                CommonUtils.hideKeyboard(ImageComprassionActivity.this);
            }
        });
        mBottomSheetDialog = builder.build();
    }

    @Override
    protected void onStop() {
        if (mBottomSheetDialog != null) {
            if (mBottomSheetDialog.isShowing()) {
                mBottomSheetDialog.hide();
            }
        }
        super.onStop();
    }

    /**
     * Asynchronos task to reduce an image size without affecting its quality and set in imageview.
     */
    public class ImageCompression extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length == 0 || strings[0] == null)
                return null;

            return CommonUtils.compressImage(strings[0]);
        }

        protected void onPostExecute(String imagePath) {
            // imagePath is path of new compressed image.
            mivImage.setImageBitmap(BitmapFactory.decodeFile(new File(imagePath).getAbsolutePath()));
        }
    }
}
