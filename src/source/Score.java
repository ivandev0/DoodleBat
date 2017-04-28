package source;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;

public class Score extends JPanel{

    private LinkedList<Image> numbers;
    public static int score;
    private LinkedList<Integer> numArray;

    Score(LinkedList<Image> alphabet){
        numbers = alphabet;
        numArray = new LinkedList<>();
        numArray.add(0);
    }

    public void updateScore(float score){
        if(this.score != (int)score) {
            this.score = (int) score;
            getNumArray();
        }
    }

    private void getNumArray(){
        numArray = new LinkedList<>();
        if(score == 0){
            numArray.add(0);
            return;
        }

        int tempScore = score;
        while (tempScore != 0){
            numArray.add(tempScore%10);
            tempScore /= 10;
        }
        Collections.reverse(numArray);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(numArray != null)
        for (int i = 0; i < numArray.size(); i++) {
            g.drawImage(numbers.get(numArray.get(i)), 10 +  numbers.get(numArray.get(i)).getWidth(null) * i,
                    numbers.get(numArray.get(i)).getHeight(null) - 20, null);
        }

    }
}
