package source;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;


public class Platforms extends LinkedList {

    private GameObject last;
    private Image blackHole;
    private LinkedList<Image> jokerImg, platImg, bonuses;
    private float maxX, offset = 10, spaceBetween = 0, destroyLevelDown, destroyLevelUp = -150, addHeight, jumpDistance, blackHoleOffset = 30;
    private int platChance = 1, jokerChance = 200, blackHoleChance = 200, springChance = 3, rocketChance = 50, shieldChance = 2;
    private float[] scoreArray = {200, 1000, 3000, 6000, 10000}, probabilityArray = {2,3,4,5,6},
                  floatPlatChance = {4,4,3,2,2}, breakPlatChance = {3,3,3,3,3};

    public Platforms(float initX, float initY, float destroyLevelDown,
                     LinkedList<Image> platImg, LinkedList<Image> jokerImg, Image blackHole,LinkedList<Image> bonuses, float addHeight){
        this.platImg = platImg;
        this.bonuses = bonuses;
        this.maxX = Game.width - platImg.get(0).getWidth(null);
        this.destroyLevelDown = destroyLevelDown;
        this.jokerImg = jokerImg;
        this.blackHole = blackHole;
        this.addHeight = addHeight;
        jumpDistance = Game.height * 0.5f * 0.5f;

        last = new Platform(initX,initY);
        add(last);
    }

    public void generateNewPlatforms(){
        //GameObject last = (GameObject)getLast();
        float curY = last.y - last.obImg.getHeight(null) - offset - spaceBetween;

        if(curY > -10){
            int probability;

            if(rand(jokerChance) == 0){
                float initX = rand(maxX);
                int type = rand(2);
                if(rand(shieldChance) == 0) {
                    float velocity = 0;
                    if (type == 0){
                        last = new Platform(initX, curY);
                        add(last);
                    }
                    else {
                        FloatingPlatform fp = new FloatingPlatform(initX, curY);
                        velocity = fp.getVelocity();
                        last = fp;
                        add(last);
                    }
                    curY +=5;
                    float x = initX + rand(last.obImg.getWidth(null) - bonuses.get(0).getWidth(null));
                    float maxX = initX + last.obImg.getWidth(null) - x;
                    float minX = x - initX;
                    add(new Shield(x, curY, velocity, maxX, minX));
                    curY -= platImg.get(type).getHeight(null) + jumpDistance * 0.35f + rand(jumpDistance * 0.35f) + bonuses.get(0).getHeight(null);
                }
                int i = 1 + rand(3);
                for (int j = 1; j <= i; j++) {
                    initX = rand(maxX);
                    type = rand(2);

                    if (type == 0) {
                        last = new Platform(initX, curY);
                        add(last);
                    } else {
                        last = new FloatingPlatform(initX, curY);
                        add(last);
                    }
                    if(j != i)
                        curY -= jumpDistance * 0.5f + rand(jumpDistance * 0.5f);
                }

                initX = rand(maxX);
                curY -= jokerImg.get(0).getHeight(null) + 30;
                add(new Joker(initX, curY, jokerImg));

                initX = rand(maxX);
                type = rand(2);
                curY -= platImg.get(type).getHeight(null) + 30;
                if (type == 0) {
                    this.last = new Platform(initX, curY);
                    add(this.last);
                } else {
                    this.last = new FloatingPlatform(initX, curY);
                    add(this.last);
                }
                spaceBetween = 0;
                return;
            }

            if(rand(blackHoleChance) == 0){
                int multiplier = 2;
                float sum = 0;
                float initX = rand(maxX);
                int type = rand(2);
                if(type == 0)
                    add(new Platform(initX, curY));
                else
                    add(new FloatingPlatform(initX, curY));

                int side = rand(2);
                float offset = blackHoleOffset * 0.5f + rand(blackHoleOffset*0.5f);
                initX = side * (Game.width - blackHole.getWidth(null)*multiplier - offset) + (1-side)*offset;
                sum += blackHole.getHeight(null)*multiplier + jumpDistance * 0.5f + rand(jumpDistance*0.5f);
                add(new BlackHole(initX, curY - sum, multiplier));

                float curPos = blackHole.getWidth(null) * multiplier + platImg.get(0).getWidth(null);
                initX = curPos + offset + rand(maxX - curPos*2 - offset);
                add(new Platform(initX, curY-sum*0.5f));

                initX = rand(maxX);
                curY -= sum*0.5f + jumpDistance * 0.85f + rand(jumpDistance*0.15f);
                type = rand(2);
                if(type == 0) {
                    last = new Platform(initX, curY);
                    add(last);
                }
                else {
                    last = new FloatingPlatform(initX, curY);
                    add(last);
                }
                spaceBetween = 0;
                return;
            }

            if(last.y - curY > jumpDistance) {
                float initX = rand(maxX);
                int type = rand(2);
                if(type == 0) {
                    last = new Platform(initX, curY);
                    add(last);
                }
                else {
                    last = new FloatingPlatform(initX, curY);
                    add(last);
                }
                spaceBetween = 0;
                return;
            }

            platChance = (int) probabilityArray[binarySearch(Score.score, scoreArray)];
            probability = rand(platChance);

            if(probability == 0) {
                int plat = 0;
                int floating = rand(floatPlatChance[binarySearch(Score.score, scoreArray)]);
                int breaking = rand(breakPlatChance[binarySearch(Score.score, scoreArray)]);
                if (floating == 0)
                    plat = 1;
                if (breaking == 0)
                    plat = 2;

                float initX = rand(maxX);
                if (plat == 2) {
                    add(new BreakingPlatform(initX, curY));
                    spaceBetween += platImg.get(plat).getHeight(null) *2 + offset;
                } else {
                    int spring = (int) (Math.random() * springChance);
                    int rocket = (int) (Math.random() * rocketChance);

                    float velocity = 0;
                    if (plat == 0) {
                        last = new Platform(initX, curY);
                        add(last);
                    } else {
                        FloatingPlatform fp = new FloatingPlatform(initX, curY);
                        velocity = fp.getVelocity();
                        last = fp;
                        add(last);
                    }

                    if (spring == 0) {
                        float x = initX + rand(last.obImg.getWidth(null) - bonuses.get(2).getWidth(null));
                        float maxX = initX + last.obImg.getWidth(null) - x;
                        float minX = x - initX;
                        add(new Spring(x, curY + 5, velocity, maxX, minX));
                    } else if (rocket == 0) {
                        float x = initX + rand(last.obImg.getWidth(null) - bonuses.get(1).getWidth(null));
                        float maxX = initX + last.obImg.getWidth(null) - x;
                        float minX = x - initX;
                        add(new Rocket(x, curY + 5, velocity, maxX, minX));
                    }
                    spaceBetween = 0;
                }
            } else
                spaceBetween += last.obImg.getHeight(null) + offset;

        }
    }

    public void generateFirstPlatforms(){
        float curY = last.y - platImg.get(0).getHeight(null) - offset;
        while(curY > 0){
            int probability = (int)(Math.random()*platChance);
            if(probability == 0){
                float initX = (int)(Math.random()*maxX);
                last = new Platform(initX,curY);
                add(last);
            }
            curY += - platImg.get(0).getHeight(null) - offset;
        }
    }

    public GameObject checkCollision(GameObject gameObject){
        for (int i = 0; i < size(); i++) {
            GameObject ob = (GameObject)get(i);
            if(ob.collider.boxCompare(gameObject)) {
                return ob;
            }
        }
        return null;
    }

    public void destroy(boolean gameOver){
        if(size() != 0) {
            if (!gameOver) {
                GameObject plat = (GameObject) getFirst();
                if (plat.y > destroyLevelDown)
                    this.remove(plat);
            } else {
                GameObject plat = (GameObject) getLast();
                if (plat.y < destroyLevelUp)
                    this.remove(plat);
            }
        }
    }

    public void update(float delta){
        for (int i = 0; i<this.size(); i++) {
            GameObject ob = (GameObject) this.get(i);
            if(ob.tag.equals("joker")){
                Joker obJ = (Joker) this.get(i);
                obJ.update(delta);
            }
            if(ob.tag.equals("floating")){
                FloatingPlatform obFP = (FloatingPlatform) this.get(i);
                obFP.update(delta);
            }
            if(ob.tag.equals("spring")){
                Spring obS = (Spring) this.get(i);
                obS.update(delta);
            }
            if(ob.tag.equals("rocket")){
                Rocket obR = (Rocket) this.get(i);
                obR.update(delta);
            }
            if(ob.tag.equals("breaking")){
                BreakingPlatform obBP = (BreakingPlatform) this.get(i);
                obBP.update(delta);
            }
            if(ob.tag.equals("shield")){
                Shield obSh = (Shield) this.get(i);
                obSh.update(delta);
            }
        }
    }

    private int rand(float max){
        return (int)(Math.random() * max);
    }

    private int binarySearch(float key, float[] array){
        for (int i = 0; i < array.length; i++){
            if(key < array[i])
                return i;
        }
        return array.length - 1;
    }

    private class Platform extends GameObject{

        Platform(float x, float y) {
            super(x, y, platImg.get(0), 0, 5, platImg.get(0).getWidth(null), platImg.get(0).getHeight(null) - 5);
        }
    }

    private class FloatingPlatform extends GameObject{

        float velocity = 20;
        float [] velocityArray = {40, 60, 80, 100, 120};

        FloatingPlatform(float x, float y) {
            super(x, y, platImg.get(1), 0, 5, platImg.get(1).getWidth(null), platImg.get(1).getHeight(null) - 5);
            tag = "floating";

            if(Score.score > 200)
                velocity = velocityArray[binarySearch(velocity, scoreArray)];

            velocity = (float)(velocity/2.0f + Math.random()*velocity);
        }

        void update(float delta){
            if(x >= Game.width - obImg.getWidth(null))
                velocity*=-1;
            if(x <= 0)
                velocity*=-1;
            x+=velocity*delta;
        }

        float getVelocity(){
            return velocity;
        }
    }

    private class BreakingPlatform extends GameObject{

        float velocity = 0;
        float [] velocityArray = {40, 60, 80, 100, 120};

        BreakingPlatform(float x, float y) {
            super(x, y, platImg.get(2), 0, 0, platImg.get(2).getWidth(null), platImg.get(2).getHeight(null));
            tag = "breaking";

            if(Score.score > 1000)
                velocity = velocityArray[binarySearch(velocity, scoreArray)];

            if(rand(2) == 0)
                velocity = (float)(velocity/2.0f + Math.random()*velocity);
            else
                velocity = 0;
        }

        void update(float delta){
            if(x >= Game.width - obImg.getWidth(null))
                velocity*=-1;
            if(x <= 0)
                velocity*=-1;
            x+=velocity*delta;
        }

    }

    public class Joker extends GameObject{
        private LinkedList<Image> img;
        public int life = 2;
        private float bound = 0, newY = y, velocityY = -60, maxBound = 30, minBound = -30, velocityX = 90;
        private float[] boundsArray = {minBound, minBound*0.7f, minBound*0.3f, 0,  maxBound*0.3f, maxBound*0.7f };

        Joker(float x, float y, LinkedList<Image> img) {
            super(x, y, img.get(0), 0, 0, img.get(0).getWidth(null), (int)(img.get(0).getHeight(null) + addHeight*0.5f));
            tag = "joker";
            this.img = img;
            Collections.reverse(this.img);
        }

        void update(float delta){
            if(bound >= maxBound){
                velocityY *= -1;
            }
            if(bound <= minBound){
                velocityY *= -1;
            }
            bound += velocityY*delta;
            newY = y+bound;

            obImg = img.get(binarySearch((int)bound, boundsArray));

            if(x>= Game.width - obImg.getWidth(null))
                velocityX *=-1;
            if(x <= 0)
                velocityX *=-1;
            x+=velocityX*delta;
        }

        @Override
        public void paint(Graphics g) {
            g.drawImage(obImg, (int)x, (int)newY,null);
        }
    }

    private class BlackHole extends GameObject{

        int multiplier;

        BlackHole(float x, float y, int multiplier) {
            super(x, y, blackHole, 0, 0, blackHole.getWidth(null), (int)(blackHole.getHeight(null) + addHeight*0.5f));
            this.multiplier = multiplier;
            collider.widthCollider*=multiplier;
            collider.heightCollider*=multiplier;
            tag = "hole";
        }

        @Override
        public void paint(Graphics g) {
            g.drawImage(obImg, (int)x, (int)y, obImg.getWidth(null)*multiplier, obImg.getHeight(null)*multiplier, null);
        }
    }

    private class Shield extends GameObject{

        float velocity, maxX, minX;

        Shield(float x, float y, float velocity, float maxX, float minX) {
            super(x, y - bonuses.get(0).getHeight(null), bonuses.get(0),
                    0, 0, bonuses.get(0).getWidth(null), bonuses.get(0).getHeight(null));
            tag = "shield";
            this.velocity = velocity;
            this.maxX = maxX - obImg.getWidth(null);
            this.minX = minX;
        }

        void update(float delta){
            if(x >= Game.width - obImg.getWidth(null) - maxX)
                velocity*=-1;
            if(x <= minX)
                velocity*=-1;
            x+=velocity*delta;
        }
    }

    private class Spring extends GameObject{

        float velocity, maxX, minX;

        Spring(float x, float y, float velocity, float maxX, float minX) {
            super(x, y - bonuses.get(2).getHeight(null), bonuses.get(2),
                    0, 0, bonuses.get(2).getWidth(null), bonuses.get(2).getHeight(null));
            this.velocity = velocity;
            this.maxX = maxX - obImg.getWidth(null);
            this.minX = minX;
            tag = "spring";
        }

        void update(float delta){
            if(x >= Game.width - obImg.getWidth(null) - maxX)
                velocity*=-1;
            if(x <= minX)
                velocity*=-1;
            x+=velocity*delta;
        }

    }

    private class Rocket extends GameObject{

        float velocity, maxX, minX;

        Rocket(float x, float y, float velocity, float maxX, float minX) {
            super(x, y - bonuses.get(1).getHeight(null), bonuses.get(1),
                    0, 0, bonuses.get(1).getWidth(null), bonuses.get(1).getHeight(null));
            this.velocity = velocity;
            this.maxX = maxX - bonuses.get(1).getWidth(null);
            this.minX = minX;
            tag = "rocket";
        }

        void update(float delta){
            if(x >= Game.width - obImg.getWidth(null) - maxX)
                velocity*=-1;
            if(x <= minX)
                velocity*=-1;
            x+=velocity*delta;
        }
    }
}
