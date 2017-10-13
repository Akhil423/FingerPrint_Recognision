package com.example.greenhat.b_passer;




import android.content.Context;
import android.content.Intent;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.os.Build;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;
import android.database.Cursor;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.R.attr.name;
import static android.R.attr.text;
import static android.R.attr.version;
import static java.security.AccessController.getContext;


public class Register extends AppCompatActivity {

    ImageProcess insertdata;
    Context context;
    Cursor factory;

    private static final int CAMERA_REQUEST = 1888;
    int b[]=new int[]{R.drawable.whorl,R.drawable.rl,R.drawable.ll,R.drawable.arch,R.drawable.t};


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status) {
                case LoaderCallbackInterface.SUCCESS:
                    Toast.makeText(getApplicationContext(),"connected",Toast.LENGTH_SHORT).show();
                    //from now onwards, you can use OpenCV API
                    Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
                    break;
                case LoaderCallbackInterface.INIT_FAILED:
                    Toast.makeText(getApplicationContext(),"init failed",Toast.LENGTH_SHORT).show();
                    break;
                case LoaderCallbackInterface.INSTALL_CANCELED:
                    Toast.makeText(getApplicationContext(),"install cancelled",Toast.LENGTH_SHORT).show();
                    break;
                case LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION:
                    Toast.makeText(getApplicationContext(),"incompatible version",Toast.LENGTH_SHORT).show();
                    break;
                case LoaderCallbackInterface.MARKET_ERROR:
                    Toast.makeText(getApplicationContext(),"market error",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"manager install",Toast.LENGTH_SHORT).show();
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //initialize OpenCV manager
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }

String ne;
int height1,height2,width1,width2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        EditText nm=(EditText)findViewById(R.id.nme);
         ne=nm.getText().toString();

        Button b=(Button)findViewById(R.id.regb);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cam=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cam,CAMERA_REQUEST);

            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
           photo=Bitmap.createScaledBitmap(photo,512,512,true);

            insertdata=new ImageProcess(getBaseContext(), "Register.db", null,1);

          /*  ByteArrayOutputStream stream1= new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG,100,stream1);
            byte a2[]=stream1.toByteArray();

            insertdata.insert(a2,"tarch");
            Toast.makeText(getApplicationContext(),"is null",Toast.LENGTH_LONG).show();*/
//

            Intent rew=new Intent(this.getApplicationContext(),Home.class);



            int width, height;
           height = photo.getHeight();
           width = photo.getWidth();

              Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
              Canvas c = new Canvas(bmpGrayscale);
              Paint paint = new Paint();
              ColorMatrix cm = new ColorMatrix();
              cm.setSaturation(0);
              ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
              paint.setColorFilter(f);
              c.drawBitmap(photo, 0, 0, paint);

       ByteArrayOutputStream stream= new ByteArrayOutputStream();
           bmpGrayscale.compress(Bitmap.CompressFormat.JPEG,100,stream);
           byte a[]=stream.toByteArray();
//
//            int[] be1=new int[bmpGrayscale.getHeight()*bmpGrayscale.getWidth()];
//            bmpGrayscale.getPixels(be1,0,bmpGrayscale.getWidth(),0,0,bmpGrayscale.getWidth(),bmpGrayscale.getHeight());



            int i=5,flag=0;double compare=0,count=0;


           while(--i>=0){


               Bitmap bi=BitmapFactory.decodeResource(getBaseContext().getResources(),b[i]);
               bi=Bitmap.createScaledBitmap(bi,512,512,true);



               Mat img1 = new Mat();
               Utils.bitmapToMat(photo, img1);
               Mat img2 = new Mat();
               Utils.bitmapToMat(bi, img2);
               Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGBA2GRAY);
               Imgproc.cvtColor(img2, img2, Imgproc.COLOR_RGBA2GRAY);
               img1.convertTo(img1, CvType.CV_32F);
               img2.convertTo(img2, CvType.CV_32F);
               //Log.d("ImageComparator", "img1:"+img1.rows()+"x"+img1.cols()+" img2:"+img2.rows()+"x"+img2.cols());
               Mat hist1 = new Mat();
               Mat hist2 = new Mat();
               MatOfInt histSize = new MatOfInt(180);
               MatOfInt channels = new MatOfInt(0);
               ArrayList<Mat> bgr_planes1= new ArrayList<Mat>();
               ArrayList<Mat> bgr_planes2= new ArrayList<Mat>();
               Core.split(img1, bgr_planes1);
               Core.split(img2, bgr_planes2);
               MatOfFloat histRanges = new MatOfFloat (0f, 180f);
               boolean accumulate = false;
               Imgproc.calcHist(bgr_planes1, channels, new Mat(), hist1, histSize, histRanges, accumulate);
               Core.normalize(hist1, hist1, 0, hist1.rows(), Core.NORM_MINMAX, -1, new Mat());
               Imgproc.calcHist(bgr_planes2, channels, new Mat(), hist2, histSize, histRanges, accumulate);
               Core.normalize(hist2, hist2, 0, hist2.rows(), Core.NORM_MINMAX, -1, new Mat());
               img1.convertTo(img1, CvType.CV_32F);
               img2.convertTo(img2, CvType.CV_32F);
               hist1.convertTo(hist1, CvType.CV_32F);
               hist2.convertTo(hist2, CvType.CV_32F);

                compare = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CHISQR);
               Log.d("ImageComparator", "compare: "+compare);
               if(count<compare) {
                  count=compare;
                   flag++;
               }


            }

            if(flag==4) {
                insertdata.insert(a, ne ,"tarch");
                Toast.makeText(getApplicationContext(),"tarch",Toast.LENGTH_SHORT).show();
                startActivity(rew);
            }
           else if(flag==3) {
                insertdata.insert(a,ne ,"arch");
                Toast.makeText(getApplicationContext(),"arch",Toast.LENGTH_SHORT).show();
                startActivity(rew);
            }
            else if(flag==2) {
                insertdata.insert(a,ne ,"lloop");
                Toast.makeText(getApplicationContext(),"lloop",Toast.LENGTH_SHORT).show();
                startActivity(rew);
            }
            else if(flag==1) {
                insertdata.insert(a,ne ,"rloop");
                Toast.makeText(getApplicationContext(),"rloop",Toast.LENGTH_SHORT).show();
                startActivity(rew);
            }
            else if(flag==0) {
                insertdata.insert(a,ne ,"whorl");
                Toast.makeText(getApplicationContext(),"whorl",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"registered",Toast.LENGTH_SHORT).show();
                startActivity(rew);

            }




        }
    }


}