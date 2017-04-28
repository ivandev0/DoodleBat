package source;

import javax.swing.*;
import java.awt.*;

public class GameObject extends JPanel{
    public float x,y;
    public Image obImg;
    public BoxCollider collider;
    public String tag = "";
    public float multiplier = 1;

    public GameObject(float x, float y, Image obImg, float colX, float colY, int colW, int colH){
        this.x = x;
        this.y = y;

        this.obImg = obImg;

        collider = new BoxCollider(colX, colY, colW, colH);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.drawImage(obImg, (int)x,(int)y, (int)(obImg.getWidth(null)*multiplier),(int)(obImg.getHeight(null)*multiplier), null);
        //g.setColor(Color.GREEN);
        //g.drawRect((int)(collider.xCollider + x), (int)(collider.yCollider + y), collider.widthCollider, collider.heightCollider);
    }

    public class BoxCollider {

        float xCollider, yCollider;
        int widthCollider, heightCollider;

        public BoxCollider(float xCollider, float yCollider, int widthCollider, int heightCollider) {
            this.xCollider = xCollider;
            this.yCollider = yCollider;
            this.widthCollider = widthCollider;
            this.heightCollider = heightCollider;
        }

        boolean boxCompare(GameObject ob) {
            if (x + xCollider < ob.x + ob.collider.xCollider + ob.collider.widthCollider &&
                    x + xCollider + widthCollider > ob.x + ob.collider.xCollider &&
                    y + yCollider < ob.y + ob.collider.yCollider + ob.collider.heightCollider &&
                    y + yCollider + heightCollider > ob.y + ob.collider.yCollider)
                return true;
            return false;
        }
    }
}
