package source;

import java.awt.*;
import java.util.LinkedList;

public class Surikens extends LinkedList {

    private float velocity = 1000, maxX;

    Surikens(float maxX){
        this.maxX = maxX;
    }

    void update(float delta){
        for (int i = 0; i < size(); i++) {
            Suriken suriken = (Suriken)get(i);
            suriken.y -= velocity*delta*suriken.directionY;
            suriken.x -= velocity*delta*suriken.directionX;
            for(int j = 0; j<Game.platforms.size(); j++){
                GameObject ob = (GameObject) Game.platforms.get(j);
                if(ob.tag.equals("joker") && ob.collider.boxCompare(suriken)){
                    Platforms.Joker joker = (Platforms.Joker)Game.platforms.get(j);
                    joker.life--;
                    remove(i);
                    if(joker.life == 0)
                        Game.platforms.remove(j);
                }
            }
        }
        delete();


    }

    private void delete(){
        for (int i = 0; i < size(); i++) {
            GameObject suriken = (GameObject)get(i);
            if(suriken.x<0 || suriken.y < 0 || suriken.x>maxX)
                remove(i);
        }
    }

    void addSuriken(float x, float y, Image obImg, float directionX, float directionY){
        add(new Suriken(x, y, obImg, 0, 0, obImg.getWidth(null), obImg.getHeight(null), directionX, directionY));
    }

    private class Suriken extends GameObject{
        public float directionX, directionY;
        public Suriken(float x, float y, Image obImg, float colX, float colY, int colW, int colH, float directionX, float directionY) {
            super(x, y, obImg, colX, colY, colW, colH);
            this.directionX = directionX;
            this.directionY = directionY;
        }
    }
}
