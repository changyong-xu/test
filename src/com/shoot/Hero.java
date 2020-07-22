package com.shoot;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Hero extends FlyingObject{
    private int life;//命
    private int doubleFire;//火力值
    private BufferedImage[] images;
    private int  index;//协助图片切换
    public Hero(){
        image = ShootGame.hero0;
        width = image.getWidth();
        height = image.getHeight();
        Random rand = new Random();
        x = 150;
        y = 400;
        life = 3;
        doubleFire = 0;
        images = new BufferedImage[]{
            ShootGame.hero0,
            ShootGame.hero1
        };
        index = 0;//协助切换
    }

    public void step(){
        image = images[index++/10%images.length];
    }
    /**发射子弹*/
    public Bullet[] shoot(){
        int xStep = this.width/4;
        int yStep = 20;
        if(doubleFire>0){
            Bullet[] bs = new Bullet[2];
            bs[0]=new Bullet(this.x+ xStep,this.y-yStep);
            bs[1]=new Bullet(this.x+3*xStep,this.y-yStep);
            doubleFire -= 2;
            return bs;
        }else {
            Bullet[] bs = new Bullet[1];
            bs[0]=new Bullet(this.x+2*xStep,this.y-yStep);
            return bs;
        }
    }

    public void moveTo(int x,int y){
        this.x = x -this.width/2;
        this.y = y -this.height/2;

    }
    public boolean outOfBounds(){
        return  false;
    }
    public void addLife(){//奖励
        life++;
    }
    public int getLife(){
        return life;
    }
    public void  subtractLife(){
        life--;
    }
    public void addDoubleFire(){//增加火力
        doubleFire+=40;
    }
    public void clearDoubleFire(){
        doubleFire = 0;
    }
    public int getdoubleFire(){
        return doubleFire;
    }
    public boolean hit(FlyingObject obj){
        int x1 = obj.x - this.width/2;
        int x2 = obj.x + obj.width+this.width/2;
        int y1 = obj.y - this.height/2;
        int y2 = obj.y + obj.height+this.height/2;
        int x = this.x + this.width/2;
        int y = this.y + this.height/2;

        return x>x1 && x<x2 && y>y1 &&y<y2;
    }
}
