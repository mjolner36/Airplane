package com.mjolner.airplane;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine {

    BackgroundImage backgroundImage;
    Bird bird;
    static int gameState;
    ArrayList<Tube> tubes;
    Random random;
    int score; // Stores the score
    int scoringTube; // Keeps track of scoring tube
    Paint scorePaint;

    public GameEngine() {
        backgroundImage = new BackgroundImage();
        bird = new Bird();
        //0 = Not Started
        //1 = Playing
        //2 = GameOver
        gameState = 0;
        tubes = new ArrayList<>();
        random = new Random();
        for (int i = 0; i < AppConstants.numberOfTubes; i++) {
            int tubeX = AppConstants.SCREEN_WIDTH + i * AppConstants.distanceBetweenTubes;
            // Get topTubeOffsetY
            int topTUbeOffsetY = AppConstants.minTubeOffsetY + random.nextInt(AppConstants.maxTubeOffsetY - AppConstants.minTubeOffsetY + 1);
            // Now create Tube objects
            Tube tube = new Tube(tubeX, topTUbeOffsetY);
            tubes.add(tube);
        }
        score = 0;
        scoringTube = 0;
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLUE);
        scorePaint.setTextSize(80);
        scorePaint.setTextAlign(Paint.Align.LEFT);
        scorePaint.setFakeBoldText(true);
    }

    public void updateAndDrawTubes(Canvas canvas) {
        if (gameState == 1) {
            if ((tubes.get(scoringTube).getTubeX() < bird.getX() + AppConstants.getBitmapBank().getBirdWidth())
                    && (tubes.get(scoringTube).getTopTubeOffsetY() > bird.getY()
                    || tubes.get(scoringTube).getBottomTubeY() < (bird.getY() +
                    AppConstants.getBitmapBank().getBirdHeight()))) {
                //Go to game over
                //Log.d("Game", "over");
                AppConstants.getSoundBank().playHit();
                Context context = AppConstants.gameActivityContext;
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("score", score);
                context.startActivity(intent);
                ((Activity) context).finish();
            } else if (tubes.get(scoringTube).getTubeX() < bird.getX() - AppConstants.getBitmapBank().getTubeWidth()) {
                score++;
                scoringTube++;
                if (scoringTube > AppConstants.numberOfTubes - 1) {
                    scoringTube = 0;
                }
                AppConstants.getSoundBank().playPoint();
            }
            for (int i = 0; i < AppConstants.numberOfTubes; i++) {
                if (tubes.get(i).getTubeX() < -AppConstants.getBitmapBank().getTubeWidth()) {
                    tubes.get(i).setTubeX(tubes.get(i).getTubeX() + AppConstants.numberOfTubes * AppConstants.distanceBetweenTubes);
                    int topTubeOffsetY = AppConstants.minTubeOffsetY + random.nextInt(AppConstants.maxTubeOffsetY - AppConstants.minTubeOffsetY + 1);
                    tubes.get(i).setTopTubeOffsetY(topTubeOffsetY);
                }
                tubes.get(i).setTubeX(tubes.get(i).getTubeX() - AppConstants.tubeVelocity);
                canvas.drawBitmap(AppConstants.getBitmapBank().getTubeTop(), tubes.get(i).getTubeX(), tubes.get(i).getTopTubeY(), null);
                canvas.drawBitmap(AppConstants.getBitmapBank().getTubeBottom(), tubes.get(i).getTubeX(), tubes.get(i).getBottomTubeY(), null);
            }
            canvas.drawText("SCORE: " + score, 0, 110, scorePaint);

        }
    }

    public void updateAndDrawBackgroundImage(Canvas canvas) {
        backgroundImage.setX(backgroundImage.getX() - backgroundImage.getVelocity());
        if (backgroundImage.getX() < -AppConstants.getBitmapBank().getBackgroundWidth()) {
            {
                backgroundImage.setX(0);
            }
        }
        canvas.drawBitmap(AppConstants.getBitmapBank().getBackground(), backgroundImage.getX(), backgroundImage.getY(), null);
        if (backgroundImage.getX() < -(AppConstants.getBitmapBank().getBackgroundWidth() - AppConstants.SCREEN_WIDTH)) {
            canvas.drawBitmap(AppConstants.getBitmapBank().getBackground(), backgroundImage.getX() + AppConstants.getBitmapBank().getBackgroundWidth(), backgroundImage.getY(), null);

        }
    }

    public void updateAndDrawBird(Canvas canvas) {
        if (gameState == 1) {
            if (bird.getY() < (AppConstants.SCREEN_HEIGHT - AppConstants.getBitmapBank().getBirdHeight()) || bird.getVelocity() < 0) {
                bird.setVelocity(bird.getVelocity() + AppConstants.gravity);
                bird.setY(bird.getY() + bird.getVelocity());
            }
        }
        int currentFrame = bird.getCurretFrame();
        canvas.drawBitmap(AppConstants.getBitmapBank().getBird(currentFrame), bird.getX(), bird.getY(), null);
        currentFrame++;
        // If it exceeds maxframe re-initialize to 0
        if (currentFrame > bird.maxFrame) {
            currentFrame = 0;
        }
        bird.setCurretFrame(currentFrame);
    }
}
