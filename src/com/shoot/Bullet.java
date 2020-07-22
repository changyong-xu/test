package com.shoot;

/**
 * 子弹：飞行物
 */
public class Bullet extends FlyingObject {
    private int  speed = 3;//走的步数
    public Bullet(int x,int y){
        image = ShootGame.bullet;
        width = image.getWidth();
        height = image.getHeight();
        this.x = x;
        this.y = y;
    }
    public void step(){
        y-=speed;//向上
    }
    public boolean outOfBounds(){
        return  this.y>=ShootGame.HEIGHT;//敌人y坐标越界
    }
}
