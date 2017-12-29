package com.jetslice.imageeditortest;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

public class MainActivity extends AppCompatActivity{

        ImageView imageView;
        Button mButton;
    TextView textView;
    Button mButton2;
    public static final int PICK_IMAGE = 1;
    public static final int EDIT_IMAGE = 2;
    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_view);
        mButton = (Button) findViewById(R.id.image_select_button);
        textView = (TextView) findViewById(R.id.uri_text);
        mButton2 = (Button) findViewById(R.id.image_filter_button);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageEditorIntent = new AdobeImageIntent.Builder(getApplicationContext())
                        .setData(uri)
                        .build();
                startActivityForResult(imageEditorIntent, EDIT_IMAGE);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case PICK_IMAGE:
                    uri= data.getData();

                /* 4) Make a case for the request code we passed to startActivityForResult() */
                case EDIT_IMAGE:

                    /* 5) Show the image! */
                    Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                    imageView.setImageURI(editedImageUri);

                    break;
            }
        }
    }




    }

