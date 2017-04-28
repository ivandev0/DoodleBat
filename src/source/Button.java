package source;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends GameObject{

    private float velocity = -900, limit, decrease;
    public static boolean restartClicked = false, playClicked = false;

    public Button(float x, float y, Image obImg, float colX, float colY, int colW, int colH, float limit, float decrease, String type) {
        super(x, y, obImg, colX, colY, colW, colH);
        if(type.equals("restart"))
            addMouseListener(new MouseListenerRestart());
        else {
            requestFocusInWindow();
            addMouseListener(new MouseListenerPlay());
        }
        setBounds(0,0,1000,1000);//!!!!!!!!!!!
        setBackground(new Color(0,true));
        this.limit = limit;
        this.decrease = decrease;
    }

    void update(float delta){
        y = Math.max(y+velocity*delta, limit);
        if(y == limit){
            requestFocusInWindow();
        }

    }

    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        g.drawImage(obImg, (int)x,(int)y, (int)(obImg.getWidth(null)*decrease), (int)(obImg.getHeight(null)*decrease),null);
    }

    private class MouseListenerRestart extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            GameObject cursor = new GameObject(e.getX(), e.getY(), null, 0, 0, 1,1);
            GameObject.BoxCollider boxCollider = collider;
            if(boxCollider.boxCompare(cursor)){
                restartClicked = true;
                setBounds(0,0,0,0);
            }
        }
    }

    private class MouseListenerPlay extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            GameObject cursor = new GameObject(e.getX(), e.getY(), null, 0, 0, 1,1);
            GameObject.BoxCollider boxCollider = collider;
            if(boxCollider.boxCompare(cursor)){
                playClicked = true;
                setBounds(0,0,0,0);
            }
        }
    }
}
