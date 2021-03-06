package de.thdeg.game.assets;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Ship {
    private int range;
    private int dmg = 0;
    private int PX;
    private int PY;
    private int RouteX = -1;
    private int RouteY = -1;
    private boolean detectedPlayer = false;
    private List<int[]> routing = new ArrayList<int[]>();

    Enemy(int hp) {
        super(hp);
        short[][][] rgbs = {{{31, 222, 215}, {21, 138, 134}, {11, 74, 72}}, {{31, 69, 222}, {19, 43, 143}, {10, 22, 74}}, {{153, 23, 209}, {94, 15, 128}, {55, 10, 74}}};
        changeColor(rgbs);
        int[][] pos = {{22, 22}, {23, 22}, {24, 22}};
        this.pos = pos;
    }

    Enemy(int hp, int x, int y, int o, int r) {
        super(hp, x, y, o);
        short[][][] rgbs = {{{31, 222, 215}, {21, 138, 134}, {11, 74, 72}}, {{153, 23, 209}, {94, 15, 128}, {55, 10, 74}}, {{31, 69, 222}, {19, 43, 143}, {10, 22, 74}}};
        changeColor(rgbs);
        this.range = r;
    }

    public void resetDmg() {
        this.dmg = 0;
    }

    public int getDamageReceived() {
        return this.dmg;
    }

    protected int hitIsland(short[] myImage, int x, int y, boolean harbor) {
        if (x <= 47 && y <= 23 && x >= 0 && y >= 0) {
            int idx = (y * 48 + x) * 3;
            if (harbor) {
                return ((myImage[idx + 0] == 125 && myImage[idx + 1] == 66 && myImage[idx + 2] == 24) ||
                        (myImage[idx + 0] == 122 && myImage[idx + 1] == 236 && myImage[idx + 2] == 35) ||
                        (myImage[idx + 0] == 30 && myImage[idx + 1] == 68 && myImage[idx + 2] == 221)) ? (((myImage[idx + 0] == 29 && myImage[idx + 1] == 67 && myImage[idx + 2] == 220)) ? 2 : 1) : 0;
            } else {
                return ((myImage[idx + 0] == 196 && myImage[idx + 1] == 156 && myImage[idx + 2] == 53) ||
                        (myImage[idx + 0] == 186 && myImage[idx + 1] == 148 && myImage[idx + 2] == 48) ||
                        (myImage[idx + 0] == 125 && myImage[idx + 1] == 66 && myImage[idx + 2] == 24) ||
                        (myImage[idx + 0] == 122 && myImage[idx + 1] == 236 && myImage[idx + 2] == 35) ||
                        (myImage[idx + 0] == 30 && myImage[idx + 1] == 68 && myImage[idx + 2] == 221)) ? (((myImage[idx + 0] == 29 && myImage[idx + 1] == 67 && myImage[idx + 2] == 220)) ? 2 : 1) : 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Create Routes
     */
    private void pathFinder() {
        this.routing = new ArrayList<int[]>();
        /**
         *  Start:  this.pos[1][0]=x
         *          this.pos[1][1]=y
         *  End:    this.RouteX
         *          this.RouteY
         * */
        this.PX = this.RouteX;
        this.PY = this.RouteY;
        int pX = this.pos[1][0];
        int pY = this.pos[1][1];
        while (pX != this.RouteX && pY != this.RouteY) {
            switch (routeDirection(pX, pY, this.RouteX, this.RouteY)) {
                case 1 -> {
                    int[] rt = {pX, --pY};
                    this.routing.add(rt);
                }
                case 2 -> {
                    int[] rt = {++pX, --pY};
                    this.routing.add(rt);
                }
                case 3 -> {
                    int[] rt = {++pX, pY};
                    this.routing.add(rt);
                }
                case 4 -> {
                    int[] rt = {++pX, ++pY};
                    this.routing.add(rt);
                }
                case 5 -> {
                    int[] rt = {pX, ++pY};
                    this.routing.add(rt);
                }
                case 6 -> {
                    int[] rt = {--pX, ++pY};
                    this.routing.add(rt);
                }
                case 7 -> {
                    int[] rt = {--pX, pY};
                    this.routing.add(rt);
                }
                case 8 -> {
                    int[] rt = {--pX, --pY};
                    this.routing.add(rt);
                }
                default -> {
                    break;
                }
            }
        }
    }

    public short[] run(short[] myImage) {
        // if(this.RouteX != -1 && this.RouteY != -1){
        //     pathFinder();
        //     pR();
        //     System.out.println(this.routing.get(0)[0]);
        // }
        if (collectBonus(myImage, 0) && !this.hadBonus) {
            this.hp += (this.hp < this.MAXHP) ? 1 : 0;
            this.hadBonus = true;
        }
        myImage = clearTrace(myImage);
        if (inVision(myImage, 0) && this.bullet == null) {
            shoot();
        }
        move(myImage);
        if (collide(myImage) != 0) {
            resetMove();
        }
        if (this.bullet != null) {
            if (this.bullet.getRange() > 0) {
                myImage = this.bullet.run(-1, myImage);
            } else {
                this.bullet = null;
            }
        }
        if (this.bullet2 != null) {
            if (this.bullet2.getRange() > 0) {
                myImage = this.bullet2.run(-1, myImage);
            } else {
                this.bullet2 = null;
            }
        }
        myImage = paint(myImage);
        return myImage;
    }

    private int routeDirection(int x, int y, int gx, int gy) {
        if (x > gx) {
            if (y > gy) {
                return 8;
            } else if (y < gy) {
                return 6;
            } else {
                return 7;
            }
        } else if (x < gx) {
            if (y > gy) {
                return 2;
            } else if (y < gy) {
                return 4;
            } else {
                return 3;
            }
        } else {
            if (y > gy) {
                return 1;
            } else if (y < gy) {
                return 5;
            }
        }
        return -1;
    }

    // private int routeDirection(int x, int y, int[] gPos){
    //     if(x > gPos[0]){
    // if(y > gPos[1]){
    //     return 8;
    // }else if(y < gPos[1]){
    //     return 6;
    // }else {
    //     return 7;
    // }
    // }else if(x < gPos[0]){
    // if(y > gPos[1]) {
    //     return 2;
    // }else if(y < gPos[1]){
    //     return 4;
    // }else {
    //     return 3;
    // }
    // }else {
    // if(y > gPos[1]) {
    //     return 1;
    // }else if(y < gPos[1]){
    //     return 5;
    // }
    // }
    // return -1;
    // }

    public void shoot() {
        int orient = routeDirection(this.pos[1][0], this.pos[1][1], this.PX, this.PY);
        if (this.bullet == null) {
            this.bullet = new Bullet(orient, 8, this.pos[1][0], this.pos[1][1], 1);
        }
    }

    private void move(short[] myImage) {
        // if(this.routing.size() <= 0 ){
        if (canMove(myImage)) {
            forward(myImage);
        } else {
            if (Math.random() > 0.5) {
                rotate(0, true, myImage);
            } else {
                rotate(1, true, myImage);
            }
        }
        // }else {
        //     switch(routeDirection(this.pos[1][0], this.pos[1][1], this.routing.get(this.routing.size() - 1)[0], this.routing.get(this.routing.size() - 1)[1])){
        //         case 1 -> {
        //             rotateTo(1);
        //             forward(myImage);
        //         }
        //         case 2 -> {
        //             rotateTo(2);
        //             forward(myImage);
        //         }
        //         case 3 -> {
        //             rotateTo(3);
        //             forward(myImage);
        //         }
        //         case 4 -> {
        //             rotateTo(4);
        //             forward(myImage);
        //         }
        //         case 5 -> {
        //             rotateTo(5);
        //             forward(myImage);
        //         }
        //         case 6 -> {
        //             rotateTo(6);
        //             forward(myImage);
        //         }
        //         case 7 -> {
        //             rotateTo(7);
        //             forward(myImage);
        //         }
        //         case 8 -> {
        //             rotateTo(8);
        //             forward(myImage);
        //         }
        //         default -> {}
        //     }
        // this.routing.remove(this.routing.size() - 1);
        // }
    }

    private void pR() {
        for (int[] i : this.routing) {
            System.out.println("| " + i[0] + " | " + i[1] + " |");
        }
    }


    /**
     * Method to detect if the player is visible for the enemy ship.
     */
    private boolean inVision(short[] myImage, int who) {
        int difx;
        int dify;
        if (this.hp > 0) {
            for (int i = 0 - this.range; i <= this.range; i++) {
                difx = this.pos[1][0] + i;
                for (int j = 0 - this.range; j <= this.range; j++) {
                    dify = this.pos[1][1] + j;
                    if ((Math.pow(difx - this.pos[0][1], 2) + Math.pow(dify - this.pos[1][1], 2)) <= Math.pow(this.range, 2)) {
                        switch (who) {
                            case 0 -> {
                                if (hitPlayer(myImage, difx, dify)) {
                                    this.detectedPlayer = true;
                                    this.PX = difx;
                                    this.PY = dify;
                                    return true;
                                }
                            }
                            case 1 -> {
                                if (hitIsland(myImage, difx, dify, true) > 0) {
                                    rotateTo(routeDirection(this.pos[1][0], this.pos[1][1], difx, dify), myImage);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setRouteX(int PX) {
        this.PX = PX;
    }

    public void setRouteY(int PY) {
        this.PY = PY;
    }

    public int getPX() {
        return this.PX;
    }

    public int getPY() {
        return this.PY;
    }

    public boolean getPlayerDetected() {
        return this.detectedPlayer;
    }

    /**
     * The method collide looks at the pixels of the ship and look if it collided with another object
     */
    public int collide(short[] myImage) {
        int ret = 0;
        for (int i = 0; i < this.pos.length; i++) {
            int idx = (this.pos[i][1] * 48 + this.pos[i][0]) * 3;
            if (hitIsland(myImage, this.pos[i][0], this.pos[i][1], false) > 0) {
                ret = 1;
            }
            if (hitBullet(myImage, this.pos[i][0], this.pos[i][1])) {
                damage(1);
                ret = 2;
            }
            if (hitPlayer(myImage, this.pos[i][0], this.pos[i][1])) {
                damage(1);
                this.dmg += 1;
                ret = 1;
            }
        }
        return ret;
    }

    public boolean includesPos(int x, int y) {
        for (int i = 0; i < this.pos.length; i++) {
            if (this.pos[i][0] == x && this.pos[i][1] == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Debug method to print shiplocation and locationdifference between the new and old location.
     */
    public void print() {
        System.out.println("Enemy ship:\nA: " + this.align);
        for (int i = 0; i < this.pos.length; i++) {
            System.out.println("X: " + this.pos[i][0] + " Y: " + this.pos[i][1]);
            System.out.println("Xo: " + this.oldpos[i][0] + " Yo: " + this.oldpos[i][1]);
            // System.out.println("(" + i + ") -> X: " + (this.pos[i][0] - this.oldpos[i][0]) + " Y: " + (this.pos[i][1] - this.oldpos[i][1]));
        }
    }
}
