package source;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class MainHero extends GameObject {

    private boolean suriken = false, fall = false, inBlackHole = false;
    public static boolean spacePressed, shield = false, rocket = false;
    public static boolean gameOver = false;
    private float midY, newX, deathHeight, scoreCoef = 0.5f, surikenOffTime = 300, shieldOffTime = 10 * 1000, rocketOffTime = 3 * 1000;
    private LinkedList<Image> heroImg, heroHand;
    private Image surikenImg;
    public float score = 0;
    private long surikenTime, shieldTime, rocketTime;

    private GameObject ob;

    private float velocity = -900, gravity = 1500, initialVelocity = -900, velocityOver = -100, gravityOver = 1500;

    public MainHero(float x, float y, LinkedList<Image> obImg, LinkedList<Image> handImg, Image surikenImg, float colX, float colY, int colW, int colH, float midY, float deathHeight) {
        super(x, y, obImg.get(0), colX, colY, colW, colH);
        addKeyListener(new KeyListener());
        addMouseMotionListener(new MouseMotionListener());
        addMouseListener(new MouseListener());
        this.midY =  midY;
        this.deathHeight = deathHeight - obImg.get(0).getHeight(null);
        this.surikenImg = surikenImg;
        heroImg = obImg;
        heroHand = handImg;
        //setBounds(0,0,1000,1000); //поменять на годные числа!!!!
        setBackground(new Color(0,true));

        gameOver = false;
        //spacePressed = false;
        requestFocusInWindow();
    }

    public void updateX (float x){
        newX = x;
    }

    public void update (float delta){
        //if(!spacePressed)
            requestFocusInWindow();
        if(spacePressed && !gameOver) {
            if(inBlackHole){
                setBounds(0,0,0,0);
                multiplier = lerp(multiplier, 0, delta * 4);
                x = lerp(x, ob.x + ob.obImg.getWidth(null)*multiplier*0.5f + obImg.getWidth(null),delta*2);
                y = lerp(y, ob.y + ob.obImg.getHeight(null)*multiplier*0.5f + obImg.getHeight(null), delta*2);
                if(multiplier <= 0.1f) {
                    multiplier = 0;
                    gameOver = true;
                }
                return;
            }
            y += velocity * delta ;
            velocity += gravity * delta;
            ob = Game.platforms.checkCollision(this);
            if(ob!=null && ob.tag.equals("shield")){
                shield = true;
                Game.platforms.remove(ob);
                ob = null;
                shieldTime = System.currentTimeMillis();
            }
            if(ob!=null && ob.tag.equals("rocket")){
                gravity = 300;
                velocity = initialVelocity*2;
                rocket = true;
                Game.platforms.remove(ob);
                ob = null;
                rocketTime = System.currentTimeMillis();
            }
            if (velocity > 0 && ob != null && !fall) {
                if(ob.tag.equals("breaking")) {
                    Game.platforms.remove(ob);
                    return;
                }
                if(ob.tag.equals("spring")){
                    velocity = initialVelocity*1.7f;
                    y = ob.y - obImg.getHeight(null);
                    obImg = heroImg.get(0);
                    return;
                }
                if(!ob.tag.equals("joker") ) {
                    velocity = initialVelocity;
                    y = ob.y - obImg.getHeight(null) + 5; //offset у obImg = 5
                    obImg = heroImg.get(0);
                } else {
                    ob.collider.heightCollider = (int)(ob.collider.heightCollider*0.1f);
                    if(ob.collider.boxCompare(this)) {
                        velocity = initialVelocity*1.5f;
                        Game.platforms.remove(ob);
                        ob=null;
                    } else
                        ob.collider.heightCollider *= 10;
                }
            }
            if (ob != null && ob.tag.equals("joker") && !shield && !rocket && !fall){
               fall = true;
               velocity = initialVelocity/2;
               setBounds(0,0,0,0);
            }
            if(ob!=null && ob.tag.equals("hole") && !rocket){
                inBlackHole = true;
                return;
            }

            if(velocity/initialVelocity > 0){
                if(suriken)
                    obImg = heroHand.get(0);
                else
                    obImg = heroImg.get(0);
            } else if(velocity/initialVelocity < 0 && velocity/initialVelocity >= -0.3f){
                if(suriken)
                    obImg = heroHand.get(4);
                else
                    obImg = heroImg.get(4);
            } else if(velocity/initialVelocity < -0.3f && velocity/initialVelocity >= -0.4f){
                if(suriken)
                    obImg = heroHand.get(3);
                else
                    obImg = heroImg.get(3);
            } else if(velocity/initialVelocity < -0.4f && velocity/initialVelocity >= -0.5f){
                if(suriken)
                    obImg = heroHand.get(2);
                else
                    obImg = heroImg.get(2);
            } else if(velocity/initialVelocity < -0.5f && velocity/initialVelocity >= -0.8f){
                if(suriken)
                    obImg = heroHand.get(1);
                else
                    obImg = heroImg.get(1);
            }

            if(y < midY){
                for(int i = 0; i < Game.platforms.size(); i++) {
                    GameObject go = (GameObject) Game.platforms.get(i);
                    go.y += midY - y;
                }
                score += (midY - y)*scoreCoef;
                y = midY;
            }

            if(y>deathHeight){
                gameOver = true;
            }

            if(System.currentTimeMillis() - surikenTime > surikenOffTime)
                suriken = false;
            if(System.currentTimeMillis() - shieldTime > shieldOffTime)
                shield = false;
            if(rocket && System.currentTimeMillis() - rocketTime > rocketOffTime) {
                rocket = false;
                gravity = 1500;
                velocity = initialVelocity/2.0f;
            }

        } else if (gameOver) {
            if(Game.platforms.size() !=0) {
                if (y - midY < 2) {
                    y = midY;
                } else if (y - midY < 20)
                    y = lerp(y, midY, delta * 10);
                else
                    y = lerp(y, midY, delta * 4);
                for (int i = 0; i < Game.platforms.size(); i++) {
                    GameObject go = (GameObject) Game.platforms.get(i);
                    go.y -= 950 * delta * 1.5f;
                    //System.out.println(velocity);
                }
                //velocityOver = -100;
                //gravityOver = 1500;
            } else{
                setBounds(0,0,0,0);
                y += velocityOver * delta ;
                velocityOver += gravityOver * delta;
            }

        }
        if(Game.begin)
            x = lerp(x, newX, delta * 3);
        if(!spacePressed)
            x = Game.width /2.0f - obImg.getWidth(null)/2.0f;
    }


    private float lerp(float x, float newX, float delta){
        return x + delta * (newX - x);
    }

    private class KeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                spacePressed = true;
            }
        }
    }
    private class MouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
                updateX(e.getX() - obImg.getWidth(null) / 2.0f);
        }
    }
    private class MouseListener extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e) {
            if(!suriken && !gameOver) {
                suriken = true;
                surikenTime = System.currentTimeMillis();
                float directionX = e.getX() - x;
                float directionY = e.getX() - y;
                float length = (float)Math.sqrt(directionX*directionX + directionY*directionY);
                directionX/=length;
                directionY/=length;

                //System.out.println(directionX);
                //directionX = clamp(1f,0f,directionX);
                Game.surikens.addSuriken(x,y + 30, surikenImg, -directionX/2.0f, 1);
            }
        }

        float clamp(float max, float min, float value){
            if(value > max)
                return max;
            if(value < min)
                return min;
            return value;
        }
    }
}
