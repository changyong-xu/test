package com.shoot;
import java.util.Random;
public class Airplane extends FlyingObject implements Enemy {
    private int  speed = 2;//走的步数
    public Airplane(){
        image = ShootGame.airplane;
        width = image.getWidth();
        height = image.getHeight();
        Random rand = new Random();
        x = rand.nextInt(ShootGame.WIDTH - this.width);
        y = -this.height;
    }
    @Override
    public int getScore() {
        return 5;//获得5分
    }

    public void step(){
        y+=speed;//y+(向下）
    }
    public boolean outOfBounds(){
        return  this.y>=ShootGame.HEIGHT;//敌人y坐标越界
    }
}
