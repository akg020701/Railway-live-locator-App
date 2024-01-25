package com.example.imagedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class liveMap extends AppCompatActivity {

    /*
     * Pos- 1,2,3,4,5...
     * Speed...
     */
    ImageView imageView,pointer;
    Button moveBTN,a,b,c,d,e,f;
    int x=0,y=0,count=0,i=0;
    String TAG="nandu",targetS="",speedS="";
//    Handler handler;
    int curPos=1,tarPos,speed,tar,speedH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_map);
        imageView=findViewById(R.id.imageView);
        pointer=findViewById(R.id.pointer);
        moveBTN=findViewById(R.id.button);
        a=findViewById(R.id.a);
        b=findViewById(R.id.b);
        c=findViewById(R.id.c);
        d=findViewById(R.id.d);
        e=findViewById(R.id.e);
        f=findViewById(R.id.f);

        int[] XArray = new int[x]; //create an array to hold all X coordinates in image
        int iterator = 0;
        while (iterator < x) {
            XArray[iterator] = iterator;
            iterator++;
        }
        int [] YArray = new int[y]; //create an array to hold all Y coordinates in image
        iterator = 0;
        while (iterator < y) {
            YArray[iterator] = iterator;
            iterator++;
        }

        moveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count==0) {
                    pointer.setX(a.getX() - 105f);
                    pointer.setY(a.getY() - 247f);
                    count++;
                    return;
                }

                switch(count){
                    case 1:translate(pointer,b,4 );
                    case 2:translate(pointer,c,1 );
                    case 3:translate(pointer,d,3 );
                    case 4:translate(pointer,e,1 );
                    case 5:translate(pointer,f,2 );
                    default:break;
                }
                count++;
                if(count>5)
                    count=0;
                /*
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        translate(pointer,b,4 );
                    }
                },4000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        translate(pointer,c,1 );
                    }
                },1000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        translate(pointer,d,3 );
                    }
                },3000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        translate(pointer,e,1 );
                    }
                },1000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        translate(pointer,f,4 );
                    }
                },4000);
                */
            }
        });


//      a= pointer.getX();
//      b= pointer.getY();
//        Log.d(tag,"position is "+a+" "+b);
//        for (int xVal : XArray) {
//            for (int yVal : YArray) {
//                Color color = bmpImage.getColor(xVal,yVal);
//                if(color.red()<0.05 && color.green()<0.05 && color.blue()<0.05)
//                {
//                    translate(pointer,xVal,yVal);
//                    count++;
//                    Log.d(tag,"start "+count);
//                    pointer.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            pointer.setX(xVal);
//                            pointer.setY(yVal);
////                            setContentView(pointer);
////                            Log.d(tag,"position is "+xVal+" "+yVal);
//                        }
//                    },1000);
//                }
//            }
//        }
    }
    Handler mHandler= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String tempMsg= (String) msg.obj;
            Log.d(TAG,"Message is "+tempMsg);
            while (tempMsg.charAt(i) != '$') {
                targetS += tempMsg.charAt(i);
                i++;
            }
            i++;
            while (i != tempMsg.length()) {
                speedS += tempMsg.charAt(i);
                i++;
            }
            tar = Integer.valueOf(targetS);
            speedH = Integer.valueOf(speedS);
            //Do your stuff here
            drawPosition(curPos,tar,speedH);
            curPos=tar;
            return true;
        }
    });

    private void translate(View viewToMove, View target, int t) {
        viewToMove.animate()
                .x(target.getX()-105f)
                .y(target.getY()-247f)
                .setDuration(t*1000)
                .start();
    }
    public void drawPosition(int curPos, int tarPos, int speed){
        while (true) {
            switch (tarPos) {
                case 2:
                    translate(pointer, b, 250 / speed);
                    curPos = 2;
                    break;
                case 3:
                    switch (curPos) {
                        case 1:
                            translate(pointer, b, 250 / speed);
                            curPos = 2;
                            break;
                        case 2:
                            translate(pointer, c, 100 / speed);
                            curPos = 3;
                            break;
                    }
                    break;
                case 4:
                    switch (curPos) {
                        case 1:
                            translate(pointer, b, 250 / speed);
                            curPos = 2;
                            break;
                        case 2:
                            translate(pointer, c, 100 / speed);
                            curPos = 3;
                            break;
                        case 3:
                            translate(pointer, d, 300 / speed);
                            curPos = 4;
                            break;
                    }
                    break;
                case 5:
                    switch (curPos) {
                        case 1:
                            translate(pointer, b, 250 / speed);
                            curPos = 2;
                            break;
                        case 2:
                            translate(pointer, e, 400 / speed);
                            curPos = 5;
                            break;
                        case 3:
                            translate(pointer, d, 300 / speed);
                            curPos = 4;
                            break;
                        case 4:
                            translate(pointer, e, 100 / speed);
                            curPos = 5;
                            break;
                    }
                    break;
                case 6:
                    switch (curPos) {
                        case 1:
                            translate(pointer, b, 250 / speed);
                            curPos = 2;
                            break;
                        case 2:
                            translate(pointer, e, 400 / speed);
                            curPos = 5;
                            break;
                        case 3:
                            translate(pointer, d, 300 / speed);
                            curPos = 4;
                            break;
                        case 4:
                            translate(pointer, e, 100 / speed);
                            curPos = 5;
                            break;
                        case 5:
                            translate(pointer, f, 250 / speed);
                            curPos = 6;
                            break;
                    }
                    break;
            }
            if (curPos == tarPos)
                break;
        }
    }
}