package com.jetslice.imageeditortest;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.graphics.Typeface.createFromAsset;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    TextView ltv;
    ImageView imageView;
    ImageView headerimg;
    ImageButton sharebtn;
    public static final int PICK_IMAGE = 1;
    public static final int EDIT_IMAGE = 2;
    Uri uri = null;
    Bitmap bitmapx;

    Dialog imgfullscreen;

    public static final int RequestPermissionCode = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-3226522640848175~2991698927");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ltv = (TextView) findViewById(R.id.textViewff);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        ltv.setAnimation(animation);
        Typeface type = createFromAsset(getAssets(), "fonts/billabong.ttf");
        ltv.setText("Retroshots");
        ltv.setTypeface(type);


        ImageButton captureRot = (ImageButton) findViewById(R.id.button2xx);
        captureRot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotatebtn));

        imgfullscreen = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        imgfullscreen.setContentView(R.layout.full_screen_img);
        imageView = (ImageView) findViewById(R.id.aviary_result);
        imageView.setVisibility(View.GONE);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        headerimg = (ImageView) findViewById(R.id.aviary_intro);
        sharebtn = (ImageButton) findViewById(R.id.aviary_share);
        sharebtn.setVisibility(View.GONE);
        int imagesToShow[] = {R.drawable.water1, R.drawable.water2, R.drawable.water3, R.drawable.water4, R.drawable.water5};
        animate(headerimg, imagesToShow, 0, true);
    }


    public void editifyImage(View v) {

        // Adding if condition inside button.

        // If All permission is enabled successfully then this block will execute.
        if(CheckingPermissionIsEnabledOrNot())
        {

            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        }

        // If, If permission is not enabled then else condition will execute.
        else {

            //Calling method to enable permission.
            RequestMultiplePermission();

        }
    }

    public void saveImageToExternal(Bitmap bm) throws IOException {
//Create Path to save Image
        File path = new File("/sdcard/DCIM/Retroshots Pics"); //Creates app specific folder
        path.mkdirs();
        String imgName = "Retroshot-" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(path, imgName); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try {
            out.flush();
            out.close();
            MediaScannerConnection.scanFile(this, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch (Exception e) {
        }
        throw new IOException();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:
                    uri = data.getData();
                    Intent imageEditorIntent = new AdobeImageIntent.Builder(getApplicationContext()).setData(uri).build();
                    startActivityForResult(imageEditorIntent, EDIT_IMAGE);
                case EDIT_IMAGE:
                    Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                    imageView.setVisibility(View.VISIBLE);
                    sharebtn.setVisibility(View.VISIBLE);
                    imageView.setImageURI(editedImageUri);
                    imageView.setClickable(true);
                    ltv.setVisibility(View.GONE);
                    headerimg.setVisibility(View.GONE);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageView fs = imgfullscreen.findViewById(R.id.full_screen_show);
                            fs.setImageURI(editedImageUri);
                            fs.setOnTouchListener(new ImageMatrixTouchHandler(imgfullscreen.getContext()));
                            imgfullscreen.show();
                        }
                    });
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                    }

                    try {
                        bitmapx = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(String.valueOf(editedImageUri)));
                        saveImageToExternal(bitmapx);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sharebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri uri = Uri.parse(String.valueOf(editedImageUri));
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("image/*");
                            String shareBody = "Edited using RetroShots";
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        }
                    });
                    break;
            }
        }
    }

    public void toAbout(View v) {
        Intent i = new Intent(WelcomeActivity.this, AboutScreen.class);
        startActivity(i);
    }

    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {

        //imageView <-- The View which displays the images
        //images[] <-- Holds R references to the images to display
        //imageIndex <-- index of the first image to show in images[]
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 500; // Configure time values here
        int timeBetween = 1000 * 5;
        int fadeOutDuration = 1000;

        imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1, forever); //Calls itself until it gets to the end of the array
                } else {
                    if (forever) {
                        animate(imageView, images, 0, forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }

    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAudioPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission && RecordAudioPermission) {

                        Toast.makeText(WelcomeActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(WelcomeActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


}