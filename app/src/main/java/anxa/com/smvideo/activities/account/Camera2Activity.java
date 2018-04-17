package anxa.com.smvideo.activities.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.BuildConfig;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.util.AppUtil;


public class Camera2Activity extends Activity implements OnClickListener {


    Bitmap bitmap;
    Button btn_Retake;
    Button btn_Use;
    ImageView photoTaken;
    public static final int REQUEST_CODE_GALLERY = 99;
    public static final int REQUEST_CODE_CAMERA = 98;

    static int maxWidthCameraView = 0;
    static int maxHeightCameraView = 0;
    ImageServices imS = new ImageServices();

    BitmapFactory.Options options = new BitmapFactory.Options();
    ;
    static Uri capturedImageUri = null;
    int mediaType;
    Calendar cal = Calendar.getInstance();
    int maxBitmapHeight = 0;
    int maxBitmapWidth = 0;


    File file = new File(Environment.getExternalStorageDirectory() + "/" + cal.getTimeInMillis() + "_hapilabs_photo.jpg");
    private boolean isGallery = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera2_frames);

        Canvas c = new Canvas();
        maxBitmapHeight = c.getMaximumBitmapHeight();
        maxBitmapWidth = c.getMaximumBitmapWidth();


        options.inSampleSize = 2;
        if (savedInstanceState != null) {
            String fileUri = savedInstanceState.getString("file-uri");
            if (!fileUri.equals(""))
                capturedImageUri = Uri.parse(fileUri);
        } else {
            mediaType = getIntent().getIntExtra("MEDIA_TYPE", 99);
        }

        btn_Retake = (Button) findViewById(R.id.btn_retake);
        btn_Retake.setOnClickListener(this);

        btn_Use = (Button) findViewById(R.id.btn_use);
        btn_Use.setOnClickListener(this);


        photoTaken = (ImageView) findViewById(R.id.photo);

        if (mediaType == REQUEST_CODE_CAMERA) {
            pickImageCamera();
        } else if (mediaType == REQUEST_CODE_GALLERY) {
            pickImageGallery();
        } else
            loadPreviewScreen();

    }

    private void loadPreviewScreen() {
        photoTaken.setImageBitmap(bitmap);

    }


    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    @Override
    public void onResume() {

        super.onResume();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
            onBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBack() {
    }

    private void nextPage(boolean hasPhoto, int inSampleSize) {


        Intent mainIntent = new Intent();
        if (hasPhoto) {
            if (isGallery) {


                ByteArrayOutputStream bs = new ByteArrayOutputStream();

                if (bitmap != null)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bs);

                if (capturedImageUri == null || capturedImageUri.getPath() == null)
                    capturedImageUri = imS.getOutputImageFileUri(this);


                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(capturedImageUri.getPath());
                    fos.write(bs.toByteArray());
                    fos.close();

                    mainIntent.putExtra("ACTIVITY_PHOTO", capturedImageUri.getPath());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    mainIntent.putExtra("ACTIVITY_PHOTO_BYTE", bitmap); //cannot write to file write bitmap instead
                }


                mainIntent.putExtra("ACTIVITY_ISGALLERY", true);
                mainIntent.putExtra("ACTIVITY_PHOTO", capturedImageUri.getPath());

                mainIntent.putExtra("ACTIVITY_SAMPLESIZE", inSampleSize);

            } else {

                try {
                    mainIntent.putExtra("ACTIVITY_PHOTO", capturedImageUri.getPath());
                } catch (Exception e) {
                    mainIntent.putExtra("ACTIVITY_PHOTO_BYTE", bitmap); //cannot write to file write bitmap instead
                    if (bitmap.getWidth() > ApplicationData.getInstance().maxWidthCameraView || bitmap.getHeight() > ApplicationData.getInstance().maxHeightCameraView) {
                        options.outWidth = bitmap.getWidth();
                        options.outHeight = bitmap.getHeight();
                        mainIntent.putExtra("ACTIVITY_SAMPLESIZE", AppUtil.calculateInSampleSize(options, ApplicationData.getInstance().maxWidthCameraView, ApplicationData.getInstance().maxHeightCameraView));
                    } else
                        mainIntent.putExtra("ACTIVITY_SAMPLESIZE", inSampleSize);
                }

            }

        }


        try {
            Bundle b = getIntent().getExtras();
            byte _type = b.getByte("ACTIVITY_TYPE");
            int _intensity = b.getInt("ACTIVITY_INTENSITY", 0);
            mainIntent.putExtra("ACTIVITY_TYPE", _type);
            mainIntent.putExtra("ACTIVITY_INTENSITY", _intensity);


        } catch (Exception e) {

        }

        setResult(RESULT_OK, mainIntent);

        if (bitmap != null) {
            if (bitmap.isRecycled())
                bitmap.recycle();
            bitmap = null;
            System.gc();
        }

        finish();


    }

    private void showCameraDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.camera_dialogs);
        dialog.setTitle("Select Image: ");
        LinearLayout cameraOption = (LinearLayout) dialog.findViewById(R.id.image_camera);

        LinearLayout galleryOption = (LinearLayout) dialog.findViewById(R.id.image_gallery);

        cameraOption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cameraPage(Camera2Activity.REQUEST_CODE_CAMERA);
            }
        });

        galleryOption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cameraPage(Camera2Activity.REQUEST_CODE_GALLERY);
            }
        });

        dialog.show();

    }

    private void cameraPage(int imageCode) {
        if (imageCode == REQUEST_CODE_CAMERA) {
            pickImageCamera();
        } else
            pickImageGallery();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_Retake) {
            showCameraDialog();
        } else if (v == btn_Use) {
            nextPage(true, 0);
        }
    }


    public void pickImageCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        capturedImageUri = imS.getOutputImageFileUri(this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }


    public void pickImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


//        outState.putSerializable("LOCALE", BuildConfig.Language);
//        LanguageHelper.changeLocale(this.getResources(), BuildConfig.Language);

        if (capturedImageUri == null) {
            outState.putString("file-uri", "");

        } else {
            outState.putString("file-uri", capturedImageUri.toString());
        }
        outState.putInt("MEDIA_TYPE", 101);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            try {


                InputStream stream = getContentResolver().openInputStream(data.getData());

                if (bitmap != null) {
                    if (bitmap.isRecycled())
                        bitmap.recycle();
                    bitmap = null;
                }
                System.gc();

                try {
                    bitmap = BitmapFactory.decodeStream(stream);
                } catch (OutOfMemoryError oome) {
                    if (bitmap != null) {
                        if (bitmap.isRecycled())
                            bitmap.recycle();
                        bitmap = null;
                        System.gc();
                    }

                    bitmap = BitmapFactory.decodeStream(stream, null, options);

                }

                stream.close();
                data = null;
                System.gc();
                isGallery = true;
                if (bitmap.getWidth() > 4096 || bitmap.getHeight() > 4096)
                    nextPage(true, 2);
                else
                    nextPage(true, 0);


            } catch (Exception e) {
            }

        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
//            LanguageHelper.changeLocale(this.getResources(), BuildConfig.Language);
            System.gc();
            isGallery = false;
            nextPage(true, 0);

        } else
            nextPage(false, 0);
    }


    class ImageServices {

        private String getTempDirectoryPath(Context ctx) {
            File cache;

            // SD Card Mounted
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Android/data/" + ctx.getPackageName() + "/cache/");
            }
            // Use internal storage
            else {
                cache = ctx.getCacheDir();
            }

            // Create the cache directory if it doesn't exist
            if (!cache.exists()) {
                cache.mkdirs();
            }

            return cache.getAbsolutePath();
        }

        @SuppressLint("SimpleDateFormat")
        public Uri getOutputImageFileUri(Context ctx) {
            // TODO: check the presence of SDCard

            String tstamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File file = new File(getTempDirectoryPath(ctx), "IMG_" + tstamp + ".jpg");

            return Uri.fromFile(file);

        }
    }

}