package com.shoot;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Changyong.Xu
 * @date 2020.7.19 12:32
 * @Des 飞行射击游戏
 */
public class  ShootGame  extends JPanel {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 654;

    public static BufferedImage background;//注释1
    public static BufferedImage start;//注释1
    public static BufferedImage pause;//注释1
    public static BufferedImage gameover;//注释1
    public static BufferedImage airplane;//注释1
    /** 状态 */
    public static BufferedImage bee;
    /** 状态 */
    public static BufferedImage bullet;
    /** 状态 */
    public static BufferedImage hero0;
    /** 状态 */
    public static BufferedImage hero1;//注释1
    /** 状态 */
    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int PAUSE = 2;
    public static final int GAME_OVER = 3;
    private  int state = START;//默认

    private Hero hero = new Hero();
    private FlyingObject[] flyings = {};
    private Bullet[] bullets = {};

    static {
        try{
            background = ImageIO.read(ShootGame.class.getResource("background.png"));
            start = ImageIO.read(ShootGame.class.getResource("start.png"));
            pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
            gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
            airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
            bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
            bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
            hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
            hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fly");
        ShootGame game = new ShootGame();
        frame.add(game);
        frame.setSize(WIDTH,HEIGHT);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.action();
    }

    public FlyingObject nextOne(){
        Random rand = new Random();
        int type =rand.nextInt(20);
        if(type == 0){//生成奖励与敌人
            return new Bee();
        }else {
            return new Airplane();
        }
    }

    int flyEnteredIndex = 0;
    /** 敌人入场*/
    public void enterAction(){
        flyEnteredIndex++;
        if (flyEnteredIndex%40==0){
            FlyingObject one = nextOne();
            flyings = Arrays.copyOf(flyings,flyings.length+1);
            flyings[flyings.length-1] = one;
        }
    }
    /** 飞行物移动 */
    public void stepAction(){

//        for (FlyingObject fly : flyings){
//            fly.step();
//        }
        for(int i=0;i<flyings.length;i++){
            flyings[i].step();
        }
        for(int i=0;i<bullets.length;i++){
            bullets[i].step();
        }
        hero.step();
    }

    private int shootIndex = 0;//

    /**
     * 射击操作
     */
    public void shootAction(){
        shootIndex++;
        if(shootIndex%30 == 0){
            Bullet[] bs = hero.shoot();
            bullets = Arrays.copyOf(bullets,bullets.length+bs.length);
            System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length);
        }
    }

    public void outOfBoundsAction(){
        int index = 0;
        FlyingObject[] flyingLives = new FlyingObject[flyings.length];
        for (int i = 0;i<flyings.length;i++){
            FlyingObject f = flyings[i];
            if (!f.outOfBounds()){
                flyingLives[index] = f;
                index++;
            }
        }
        flyings = Arrays.copyOf(flyingLives,index);
    }
    public void bangAction(){
        for (int i = 0;i<bullets.length;i++){
            Bullet b = bullets[i];
            bang(b);
        }
    }
    int score = 0;//得分
    public void bang(Bullet b){
        int index = -1;
        for (int i = 0;i<flyings.length;i++){
            FlyingObject f = flyings[i];
            if (f.shootBy(b)){
                index = i;
                break;
            }
        }
        if (index!=-1){//子弹打中了
            FlyingObject one = flyings[index];
            if (one instanceof Enemy){
                Enemy e = (Enemy)one;
                score += e.getScore();//得分
            }
            if (one instanceof Award){
                Award a  = (Award)one;
                int type = a.getType();//获得奖励
                switch (type){
                    case Award.DOUBLE_FIRE:
                        hero.addDoubleFire();
                        break;
                    case Award.LIFE:
                        hero.addLife();
                        break;
                }
            }
            FlyingObject t = flyings[index];
            flyings[index] = flyings[flyings.length-1];
            flyings[flyings.length-1] = t;
            flyings = Arrays.copyOf(flyings,flyings.length-1);
        }
    }
    public void checkGameOverAction(){
        if (isGameOver()){
            state = GAME_OVER;
        }
    }
    public boolean isGameOver(){
        for (int i = 0;i<flyings.length;i++){
            FlyingObject f = flyings[i];
            if (hero.hit(f)){
                hero.subtractLife();
                hero.clearDoubleFire();
                //交换缩容
                FlyingObject t = flyings[i];
                flyings[i] = flyings[flyings.length-1];
                flyings[flyings.length-1] = t;
                flyings = Arrays.copyOf(flyings,flyings.length-1);
            }
        }
        return hero.getLife() <=0;
    }
    public void action(){
        //建立监听器
        MouseAdapter l = new MouseAdapter(){
            @Override
            public void mouseMoved(MouseEvent e){
                if (state == RUNNING){
                    int x = e.getX();//获取坐标x
                    int y = e.getY();//获取坐标y
                    hero.moveTo(x,y);
                }
            }
            /**重写点击*/
            @Override
            public void mouseClicked(MouseEvent e){
                switch (state){
                    case START:
                        state = RUNNING;
                        break;
                    case GAME_OVER:
                        score = 0;
                        hero = new Hero();
                        flyings = new FlyingObject[0];
                        bullets = new Bullet[0];
                        state = START;
                        break;
                    default:
                        break;
                }
            }
            /**重写移出*/
            @Override
            public void mouseExited(MouseEvent e){
                if (state == RUNNING){
                    state = PAUSE;
                }
            }
            /**重写移入*/
            @Override
            public void mouseEntered (MouseEvent e){
                if (state == PAUSE){
                    state = RUNNING;
                }
            }
        };
        this.addMouseListener(l);//鼠标操作事件
        this.addMouseMotionListener(l);//鼠标滑动事件
        Timer timer = new Timer();//创建定时器
        int intervel = 10; //时间间隔
        timer.schedule(new TimerTask() {
            @Override
            public void run() {//每次启动
                if (state == RUNNING) {
                    enterAction();
                    stepAction();
                    shootAction();
                    outOfBoundsAction();
                    bangAction();
                    checkGameOverAction();
                }
                repaint();
            }
        },intervel,intervel);
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(background,0,0,null);
        paintHero(g);
        paintBullets(g);
        paintFlyingObjects(g);
        paintScoreAndLift(g);
        paintState(g);
    }

    /**
     * 绘制英雄对象
     * @param g
     */
    public void paintHero(Graphics g){

    }

//    /** 英雄对象*/
//    public void paintHero(Graphics g){
//        g.drawImage(hero.image,hero.x,hero.y,null);
//    }
    /** 敌人对象*/
    public void paintFlyingObjects(Graphics g){
        for (int i = 0;i<flyings.length;i++){
            FlyingObject f = flyings[i];
            g.drawImage(f.image,f.x,f.y,null);
        }
    }

    /**
     * 子弹对象
     * @param g
     */
    public void paintBullets(Graphics g){
        for (int i = 0;i<bullets.length;i++){
            Bullet b = bullets[i];
            g.drawImage(b.image,b.x,b.y,null);
        }
    }

    /**
     *
     * @param g 测试
     */
    public void paintScoreAndLift(Graphics g){
        g.setColor(new Color(0x9F03B1));//颜色
        g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24));//字体大小
        g.drawString("SCORE:"+score,10,25);
        g.drawString("LIFE："+hero.getLife(),10,45);
        g.drawString("doubleFire:"+hero.getdoubleFire(),10,65);
    }

    public void paintState(Graphics g){
        switch (state){
            case START:
                g.drawImage(start,0,0,null);
                break;
            case PAUSE:
                g.drawImage(pause,0,0,null);
                break;
            case GAME_OVER:
                g.drawImage(gameover,0,0,null);
                break;
        }
    }

}

