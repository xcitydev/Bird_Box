package com.example.flyingbird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.flyingbird.Gameview.screenRatioX;
import static com.example.flyingbird.Gameview.screenRatioY;

public class flight {
    public boolean isgoingup = false;
    public int tooshoot = 0;
    int x, y, width, height, wingc = 0, shootounter = 1;
    Bitmap fly11, fly22, shoot1, shoot2, shoot3, shoot4, shoot5, dead;
    private Gameview gameview;
    flight(Gameview gameview, int screenY, Resources res ) {
        this.gameview = gameview;
        fly11 = BitmapFactory.decodeResource(res, R.drawable.fly1);
        fly22 = BitmapFactory.decodeResource(res, R.drawable.fly2);

        width = fly11.getWidth();
        height = fly11.getHeight();

        width /= 4;
        height /= 4;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        fly11 = Bitmap.createScaledBitmap(fly11, width, height, false);
        fly22 = Bitmap.createScaledBitmap(fly22, width, height, false);

        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1);
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2);
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3);
        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4);
        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5);

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false);
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false);
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false);
        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, false);
        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, false);

        dead = BitmapFactory.decodeResource(res, R.drawable.dead);

        dead = Bitmap.createScaledBitmap(dead, width, height, false);



        y = screenY / 2;
        x = (int) ( 64 * screenRatioX);
    }
    Bitmap getflight () {

        if(tooshoot != 0) {

            if (shootounter == 1) {
                shootounter++;
                return shoot1;
            }
            if (shootounter == 2) {
                shootounter++;
                return shoot2;
            }

            if (shootounter == 3) {
                shootounter++;
                return shoot3;
            }
            if (shootounter == 4) {
                shootounter++;
                return shoot4;
            }
            shootounter = 1;
            tooshoot--;
            gameview.newbullet();


            return shoot5;
        }


        if(wingc == 0) {
            wingc++;
            return fly11;
        }
        wingc--;
        return fly22;
    }

    Rect getcollison() {
        return new Rect(x, y, x + width, y + height);
    }
    Bitmap getDead(){
        return dead;
    }
}
