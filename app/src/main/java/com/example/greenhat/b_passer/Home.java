package com.example.greenhat.b_passer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


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

public class Home extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    ImageProcess insertdata;
    SQLiteDatabase db;

    Bitmap ba,bmpGrayscale1;
    int i=5,flag=0,check=0; double count=0,compare=0;
    String res=null;
    Cursor cursor=null;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button i=(Button) findViewById(R.id.register);
        Button i2=(Button) findViewById(R.id.login);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i1=new Intent(Home.this,Register.class);
                    startActivity(i1);

                 //  dispatchTakePictureIntent();

            }
        });

        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cam=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cam,CAMERA_REQUEST);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo1 = (Bitmap) data.getExtras().get("data");
            photo1=Bitmap.createScaledBitmap(photo1,512,512,false);

            insertdata=new ImageProcess(getBaseContext(), "Register.db", null,1);
            db=insertdata.getWritableDatabase();

            int width, height;
            height = photo1.getHeight();
            width = photo1.getWidth();

             bmpGrayscale1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(bmpGrayscale1);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
            c.drawBitmap(photo1, 0, 0, paint);

            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            bmpGrayscale1.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte a[]=stream.toByteArray();




            while(--i>=0){

                 ba= BitmapFactory.decodeResource(getBaseContext().getResources(),b[i]);
                ba=Bitmap.createScaledBitmap(ba,512,512,true);


                Mat img1 = new Mat();
                Utils.bitmapToMat(photo1, img1);
                Mat img2 = new Mat();
                Utils.bitmapToMat(ba, img2);
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

            Intent re=new Intent(Home.this,Register.class);

            count=0;compare=0;

          if(flag==0){


               cursor=db.rawQuery("select imageblob from whorl",null);

              cursor.moveToFirst();

              while(!cursor.isAfterLast()){

                  byte a2[]=cursor.getBlob(0);
                  Bitmap ba=BitmapFactory.decodeByteArray(a2,0,a2.length);

                  Mat img1 = new Mat();
                  Utils.bitmapToMat(photo1, img1);
                  Mat img2 = new Mat();
                  Utils.bitmapToMat(ba, img2);
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
                      check++;
                  }

                  cursor.moveToNext();

              }

              //

              cursor=db.rawQuery("select name from whorl where userid='"+check+"' ",null);

              cursor.moveToFirst();
              while(!cursor.isAfterLast()) {
                  res = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                  cursor.moveToNext();

              }

             // Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
              Toast.makeText(getApplicationContext(),"you are mr with uid"+Integer.toString(check),Toast.LENGTH_LONG).show();
              Toast.makeText(getApplicationContext(),Integer.toString(flag),Toast.LENGTH_LONG).show();

              Intent ch=new Intent(Home.this,Check.class);
              ch.putExtra("result",res);
              startActivity(ch);
          }


            if(flag==1){

                 cursor=db.rawQuery("SELECT imageblob FROM rloop",null);

                cursor.moveToFirst();

               while (!cursor.isAfterLast()){

                    byte a2[]=cursor.getBlob(cursor.getPosition());

//

                    Bitmap ba=BitmapFactory.decodeByteArray(a2,0,a2.length);

//
                    Mat img1 = new Mat();
                    Utils.bitmapToMat(photo1, img1);
                    Mat img2 = new Mat();
                    Utils.bitmapToMat(ba, img2);
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
                        check++;
                    }

                    cursor.moveToFirst();

                }

                //
                cursor=db.rawQuery("select name from rloop where userid='"+check+"' ",null);

                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    res = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                    cursor.moveToNext();

                }

                //Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"you are mr with uid"+Integer.toString(check),Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),Integer.toString(flag),Toast.LENGTH_LONG).show();

                Intent ch=new Intent(this.getApplicationContext(),Check.class);
                ch.putExtra("result",res);
                startActivity(ch);
            }

            if(flag==2){


                 cursor=db.rawQuery("SELECT imageblob FROM lloop",null);


                cursor.moveToFirst();

                while(!cursor.isAfterLast()){



                    byte a2[]=cursor.getBlob(cursor.getColumnIndexOrThrow("imageblob"));


                    Bitmap ba=BitmapFactory.decodeByteArray(a2,0,a2.length);
                    Mat img1 = new Mat();
                    Utils.bitmapToMat(photo1, img1);
                    Mat img2 = new Mat();
                    Utils.bitmapToMat(ba, img2);
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
                        check++;
                    }

                    cursor.moveToNext();

                }

                cursor=db.rawQuery("select name from lloop where userid='"+check+"'",null);

                    cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    res = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                    cursor.moveToNext();

                }
               // Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"you are mr with uid"+Integer.toString(check),Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),Integer.toString(flag),Toast.LENGTH_LONG).show();

                Intent ch=new Intent(this.getApplicationContext(),Check.class);
               ch.putExtra("result",Integer.toString(check));
                startActivity(ch);
            }

            if(flag==3){


                 cursor=db.rawQuery("SELECT imageblob FROM arch",null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()){

                    byte a2[]=cursor.getBlob(cursor.getPosition());
                    Bitmap ba=BitmapFactory.decodeByteArray(a2,0,a2.length);

                    Mat img1 = new Mat();
                    Utils.bitmapToMat(photo1, img1);
                    Mat img2 = new Mat();
                    Utils.bitmapToMat(ba, img2);
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

                    cursor.moveToNext();

                }

                cursor=db.rawQuery("select name from arch where userid='"+check+"' ",null);

                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    res = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                    cursor.moveToNext();

                }

                //Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"you are mr with uid"+Integer.toString(check),Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),Integer.toString(flag),Toast.LENGTH_LONG).show();

                Intent ch=new Intent(this.getApplicationContext(),Check.class);
                ch.putExtra("result",res);
                startActivity(ch);
            }

            if(flag==4){

                 cursor=db.rawQuery("SELECT imageblob FROM tarch",null);

                cursor.moveToFirst();

                while(!cursor.isAfterLast()){

                    byte a2[]=cursor.getBlob(cursor.getPosition());

                    Bitmap ba=BitmapFactory.decodeByteArray(a2,0,a2.length);

                    Mat img1 = new Mat();
                    Utils.bitmapToMat(photo1, img1);
                    Mat img2 = new Mat();
                    Utils.bitmapToMat(ba, img2);
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
                        check++;
                    }

                    cursor.moveToNext();

                }

                cursor=db.rawQuery("select name from tarch where userid='"+check+"' ",null);

                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    res = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                    cursor.moveToNext();

                }


               Toast.makeText(getApplicationContext(),"you are mr with uid"+Integer.toString(check),Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),Integer.toString(flag),Toast.LENGTH_LONG).show();

                Intent ch=new Intent(this.getApplicationContext(),Check.class);
                ch.putExtra("result",res);
                startActivity(ch);
            }

            else if(flag>5){
                Toast.makeText(getApplicationContext(),"no match found",Toast.LENGTH_SHORT).show();
            }



        }
    }




}

