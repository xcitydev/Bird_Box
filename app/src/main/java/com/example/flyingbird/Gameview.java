package com.example.flyingbird;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Gameview extends SurfaceView implements Runnable{

    private Thread thread;
    private int screenX, screenY, score= 0;
    public static float screenRatioX, screenRatioY;
    private boolean isplaying;
    private Paint paint;
    private flight flight;
    private Background background1, background2;
    private List<Bullet> bullets;
    private Bird[] birds;
    private Random random;
    private Gameactivity activity;
    private boolean isgameover = false;
    private SharedPreferences prefs;
    private SoundPool soundpool;
    private int sound;
    public Gameview(Gameactivity activity, int screenX, int screenY) {
        super(activity);
        this.activity =activity;


        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundpool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else
            soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound = soundpool.load(activity, R.raw.shootw, 1);
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        flight = new flight(this, screenY, getResources());

        bullets = new ArrayList<>();

        background2.x = screenX;
        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        birds = new Bird[4];
        for (int i = 0; i<4; i++){
            Bird bird = new Bird(getResources());
            birds[i] = bird;
        }
        random = new Random();
    }

    @Override
    public void run() {
        while (isplaying){

            update ();
            draw ();
            sleep ();
        }

    }

    private void update(){

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        if(background1.x + background1.background.getWidth() < 0){
            background1.x = screenX;
        }
        if(background2.x + background2.background.getWidth() < 0){
            background2.x = screenX;
        }
        if(flight.isgoingup)
            flight.y -= 30* screenRatioY;
        else
            flight.y += 30 * screenRatioY;
        if(flight.y < 0)
            flight.y = 0;
        if (flight.y >= screenY - flight.height)
            flight.y = screenY - flight.height;
        List<Bullet> trash = new ArrayList<>();
        for (Bullet bullet: bullets) {
            if(bullet.x > screenX);
            trash.add(bullet);

            bullet.x += 50 * screenRatioX;
            for(Bird bird : birds){
                if(Rect.intersects(bird.getcollison(), bullet.getcollison())){
                    score++;
                    bird.x = -500;
                    bullet.x = screenX + 500;
                    bird.wasshot = true;
                }
            }
        }
        for(Bullet bullet: trash){
            bullets.remove(bullet);

        }
        for (Bird bird : birds){
            bird.x -= bird.speed;
            if(bird.x + bird.width < 0){
                if(!bird.wasshot){
                    isgameover = true;
                    return;
                }
                int bound = (int) (30 * screenRatioX);
                bird.speed = random.nextInt(bound);

                if(bird.speed < 10 * screenRatioX)
                    bird.speed = (int) (10 * screenRatioX);
                bird.x = screenX;
                bird.y = random.nextInt(screenY - bird.height);

                bird.wasshot = false;

            }
            if(Rect.intersects(bird.getcollison(), flight.getcollison())){
                isgameover = true;
                return;
            }


        }

    }
    private void draw (){
        if(getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for(Bird bird : birds){
                canvas.drawBitmap(bird.getbird(), bird.x, bird.y, paint);
            }
            canvas.drawText(score + "", screenX/2f, 160, paint );


            if(isgameover){
                isplaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                savehighscore();
                waitbeforeexiting();
                return;

            }


            canvas.drawBitmap(flight.getflight(), flight.x, flight.y, paint);

            for(Bullet bullet: bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }

    }

    private void waitbeforeexiting() {
        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void savehighscore() {
        if(prefs.getInt("Highsore", 0)< score){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("Higscore",score);
            editor.apply();
        }
    }

    private void sleep (){
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume () {
        isplaying = true;
        thread= new Thread(this);
        thread.start();

    }
    public void pause () {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX() < screenX / 2) {
                    flight.isgoingup = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isgoingup = false;
                if(event.getX() < screenX / 2)
                    flight.tooshoot++;
                break;
        }
        return true;
    }

    public void newbullet() {
        if(prefs.getBoolean("ismute", false))
            soundpool.play(sound, 1,1,0,0, 1);
        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        bullets.add(bullet);
    }
}
