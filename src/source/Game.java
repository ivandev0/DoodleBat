package source;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.metal.MetalBorders;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Game extends JPanel implements Runnable {
    private JFrame frame;
    public static int width, height;

    private Image heroI, background, restartButton, playButton, shield;
    private int FPS = 60;
    private float screenCoef = 0.85f;

    private float y = 580, x = 200, midY, platY = 700, backY = 80;
    //private float velocity = -900, gravity = 1500, initialVelocity = -900;
    public boolean run = false, restartPaint = false;
    public static boolean begin = false;

    public static Platforms platforms;
    public MainHero hero;
    public Score score;
    public static Surikens surikens;
    private Button restart, play;

    private long initTime, beginTime;

    Game() {
        setLayout(null);
        setBackground(new Color(0, true));

        frame = new JFrame();
        frame.setLayout(null);
        frame.setContentPane(this);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(1200, 25, (int) (640 * screenCoef), (int) (1136 * screenCoef));
        width = frame.getWidth();
        height = frame.getHeight();
        frame.setVisible(true);
        frame.createBufferStrategy(2);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void run() {
        long lastTime = System.nanoTime();
        double ns = 1000000000;
        float delta = 0.0f;

        init();
        //до тех пор пока поток запущен
        while (run) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= (float) 1 / FPS) {
                if (System.currentTimeMillis() - initTime > 1000)
                    update(delta); //обновление параметров сцены
                delta -= (float) 1 / FPS;
            }
            render(); //отрисовка сцены
        }
    }

    private void start() {
        run = true;
        initTime = System.currentTimeMillis();

        new Thread(this).start();
    }

    private void init() {
        background = getToolkit().getImage(getClass().getResource("/asset/images/Others/background.jpg"));
        shield = getToolkit().getImage(getClass().getResource("/asset/images/Others/batprotect.png"));
        restartPaint = false;
        surikens = new Surikens(frame.getWidth());

        LinkedList<Image> jokerImg = new LinkedList<>();
        LinkedList<Image> platImg = new LinkedList<>();
        LinkedList<Image> bonuses = new LinkedList<>();
        Image blackHole = null;
        try {
            restartButton = ImageIO.read(getClass().getResource("/asset/images/Others/Button_restart.png"));
            playButton = ImageIO.read(getClass().getResource("/asset/images/Others/Button_play.png"));
            platImg.add(ImageIO.read(getClass().getResource("/asset/images/Others/platform1.png")));
            platImg.add(ImageIO.read(getClass().getResource("/asset/images/Others/platform4.png")));
            platImg.add(ImageIO.read(getClass().getResource("/asset/images/Others/platform2_break.png")));
            jokerImg.add(ImageIO.read(getClass().getResource("/asset/images/Joker/J1.png")));
            jokerImg.add(ImageIO.read(getClass().getResource("/asset/images/Joker/J2.png")));
            jokerImg.add(ImageIO.read(getClass().getResource("/asset/images/Joker/J3.png")));
            jokerImg.add(ImageIO.read(getClass().getResource("/asset/images/Joker/J4.png")));
            jokerImg.add(ImageIO.read(getClass().getResource("/asset/images/Joker/J5.png")));
            jokerImg.add(ImageIO.read(getClass().getResource("/asset/images/Joker/J6.png")));
            blackHole = ImageIO.read(getClass().getResource("/asset/images/Others/Fantom zone.png"));
            bonuses.add(ImageIO.read(getClass().getResource("/asset/images/Others/Protect.png")));
            bonuses.add(ImageIO.read(getClass().getResource("/asset/images/Others/Backpack.png")));
            bonuses.add(ImageIO.read(getClass().getResource("/asset/images/Others/jump.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkedList<Image> heroI = new LinkedList<>();
        LinkedList<Image> heroHand = new LinkedList<>();
        Image surikenImg = null;
        try {
            heroI.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero1.png")));
            heroI.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero2.png")));
            heroI.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero3.png")));
            heroI.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero4.png")));
            heroI.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero5.png")));
            heroHand.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero1_hand.png")));
            heroHand.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero2_hand.png")));
            heroHand.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero3_hand.png")));
            heroHand.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero4_hand.png")));
            heroHand.add(ImageIO.read(getClass().getResource("/asset/images/Hero/Hero5_hand.png")));
            surikenImg = ImageIO.read(getClass().getResource("/asset/images/Others/Suriken.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        float initX = /*(int) (Math.random() * maxX)*/frame.getWidth()/2.0f - platImg.get(0).getWidth(null)/2.0f;
        platforms = new Platforms(initX, frame.getHeight() - platImg.get(0).getHeight(null), frame.getHeight(), platImg, jokerImg, blackHole,
        bonuses ,heroI.get(0).getHeight(null));
        platforms.generateFirstPlatforms();


        midY = (frame.getHeight() - frame.getHeight() * 0.1f) / 2.0f;
        initX = initX + platImg.get(0).getWidth(null) / 2.0f - heroI.get(0).getWidth(null) / 2.0f;
        float initY = frame.getHeight() - heroI.get(0).getHeight(null) - platImg.get(0).getHeight(null) + 9 + 2;
        hero = new MainHero(initX, initY, heroI, heroHand, surikenImg, 20, heroI.get(0).getHeight(null), 42, 1, midY, frame.getHeight());
        add(hero);

        LinkedList<Image> numbers;
        try {
            BufferedImage alphabet = ImageIO.read(getClass().getResource("/asset/images/Others/alphabet.png"));
            LinkedList<Image> letters = new LinkedList<>();
            for(int i = 0; i < 6; i++)
                for(int j = 0; j < 6; j++) {
                    letters.add(alphabet.getSubimage(3 + (48 + 42)*j, 50 + (56 + 60)*i, 48, 60));
                    /*
                    height = 50
                    отступ по высоте 55
                    width 40
                    отступ по ширине 42


                    отступ по высоте между буквами 60
                     */
                }
            numbers = new LinkedList<>(Arrays.asList(letters.get(26), letters.get(27), letters.get(28), letters.get(29), letters.get(30),
                    letters.get(31), letters.get(32), letters.get(33), letters.get(34), letters.get(35)));
            score = new Score(numbers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        float decrease = 0.6f;
        restart = new Button(frame.getWidth()/2.0f - restartButton.getWidth(null) * decrease / 2.0f,
                             frame.getHeight() - restartButton.getHeight(null) * decrease,
                                restartButton, 0,-30, (int)(588*decrease), (int)(294*decrease),
                           frame.getHeight()/2.0f - restartButton.getHeight(null) * decrease/2.0f, decrease, "restart");
        if(!begin) {
            play = new Button(frame.getWidth() / 2.0f - playButton.getWidth(null) * decrease / 2.0f,
                    frame.getHeight()/2.0f - playButton.getHeight(null)*decrease/2.0f,
                    playButton, 0, -30, (int) (588 * decrease), (int) (294 * decrease),
                    frame.getHeight() / 2.0f - playButton.getHeight(null) * decrease / 2.0f, decrease, "play");
            add(play);
        }
        add(restart);
        beginTime = System.currentTimeMillis();
        MainHero.spacePressed = false;
    }

    private void update(float delta) {
        if(Button.playClicked && !begin && !hero.gameOver){
            play.y -= 1000;
            begin = true;
            hero.setBounds(0,0,1000,1000);
            beginTime = System.currentTimeMillis();
            //MainHero.spacePressed = true;
        }
        if(begin && System.currentTimeMillis() - beginTime > 200 && !hero.gameOver) {
            hero.setBounds(0,0,1000,1000);
            MainHero.spacePressed = true;
        }
        hero.update(delta);
        if(hero.gameOver) {
            onGameOver(delta);
            platforms.destroy(true);
        }
        else {
            platforms.generateNewPlatforms();
            platforms.destroy(false);
        }
        platforms.update(delta);
        surikens.update(delta);

        score.updateScore(hero.score);
    }

    private void render() {
        BufferStrategy bs = frame.getBufferStrategy();

        Graphics g = bs.getDrawGraphics(); //получаем Graphics из созданной нами BufferStrategy
        //махинации
        g.drawImage(background, 0,0, 1500, 966/*900*/, null);

        for (int i = 0; i < Game.platforms.size(); i++) {
            GameObject plat = (GameObject) platforms.get(i);
            plat.paint(g);
        }
        if(MainHero.shield)
            g.drawImage(shield,(int)(hero.x + hero.obImg.getWidth(null)/2.0f - shield.getWidth(null)/2.0f),(int)hero.y, null);
        hero.paint(g);

        for (int i = 0; i < Game.surikens.size(); i++) {
            GameObject suriken = (GameObject) surikens.get(i);
            suriken.paint(g);
        }

        score.paint(g);

        if(restartPaint){
            restart.paint(g);
        }
        if(!begin){
            play.paint(g);
        }
        g.dispose();
        bs.show();
        //bs.dispose();
    }

    private void onRestart() {
        init();
        run = true;
        restartPaint = false;
        Button.restartClicked = false;
    }

    private void onGameOver(float delta){
        if(platforms.size() == 0){
            restartPaint = true;
            restart.update(delta);
        }
        if(Button.restartClicked) {
            onRestart();
        }
    }


}
