/*
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package llg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Flight instruments display game and craft statistics.
 * 
 * @author jdp
 */
public final class FlightDisplay
    extends HUD
{
    protected final static Font Detail = Font.Futural.clone(0.7f,0.6f);
    static {
        Detail.setVerticalAlignment(Font.VERTICAL_HALF);
    }

    public static class Score {

        private final static int marginW = 34;
        private final static int marginH = 24;
        private final static int padding = 3;
        private final static int padding1 = 2;
        private final static int padding2 = (2*padding);

        private final static String[] Value = {
            "",
            "0",
            "00",
            "000",
            "0000",
            "00000",
            "000000",
        };

        private int value;
        private String valueString;
        private Rectangle dim;


        public Score(){
            super();
        }


        public void init(boolean newGame){
            if (newGame)
                this.value = 0;

            this.update(0);
        }
        public void update(int points){
            this.value += points;
            this.valueString = String.valueOf(this.value);
            int v = (Value.length - this.valueString.length());
            if (0 < v){
                this.valueString = (Value[v]+this.valueString);
            }
            this.dim = Technical.stringBounds(this.valueString,marginW,marginH);
            this.dim.width += padding2;
            this.dim.height += padding2;
        }
        public void draw(Graphics2D g){

            g.setColor(Color.green);

            Technical.drawString(this.valueString,(this.dim.x+padding1),(this.dim.y+padding1),g);

            g.drawRect((this.dim.x),(this.dim.y),(this.dim.width),(this.dim.height));
        }
    }
    public static class Fuel {

        private final static float PropMassKg = 8165.0f;

        private final static Rectangle Dim = Markers.stringBounds("d");
        private final static int DH = ((Dim.height / 2)+3);


        private final float sw, sh;

        private int top, left, bottom, height, xm, xt;

        private volatile int marker;

        private volatile Color color;

        private volatile String string;

        private volatile Rectangle stringBounds;


        public Fuel(float sw, float sh){
            super();
            this.sw = sw;
            this.sh = sh;
            this.color = Color.green;
        }


        public void init(Game game){

            this.left = (int)(game.width * this.sw);
            this.height = (int)(game.height * this.sh);
            this.top = (int)((game.height - this.height)/2);
            this.bottom = (this.top + this.height);
            this.xm = (this.left - Dim.width);
            this.xt = (this.left + 3);
            this.update();
        }
        public void update(){

            float fuel = Lander.fuel;

            this.marker = (int)(this.bottom - (this.height * fuel) + DH);

            if (0.25 > fuel){
                if (0.15 > fuel)
                    this.color = Color.red;
                else
                    this.color = Color.yellow;
            }
            else
                this.color = Color.green;

            fuel *= PropMassKg;

            this.string = String.format("%3.1f",fuel);
            this.stringBounds = Detail.stringBounds(this.string,this.xt,this.marker);
            this.stringBounds.y -= (this.stringBounds.height / 2);
        }
        public void draw(Graphics2D g){

            g.setColor(this.color);

            g.drawLine(this.left,this.top,(this.left+2),this.top);
            g.drawLine(this.left,this.top,this.left,this.bottom);
            g.drawLine(this.left,this.bottom,(this.left+2),this.bottom);

            Markers.drawString("d",this.xm,this.marker,g);
            if (null != this.string)
                Detail.drawString(this.string,this.stringBounds.x,this.stringBounds.y,g);
        }
    }
    public static class Vector {

        private final static int margin = 14;

        private final float sw;

        private int top, left, diam, radius, cx, cy;

        private double prop;

        private Shape clip;

        private volatile int nx, ny;

        private volatile Color color;


        public Vector(float sw){
            super();
            this.sw = sw;
            this.color = Color.green;
        }


        public void init(Game game){
            this.diam = (int)(game.width * sw);
            this.radius = (this.diam / 2);
            this.top = margin;
            this.left = game.width-this.diam-margin;
            this.cy = (this.top + this.radius);
            this.cx = (this.left + this.radius);

            this.clip = new Ellipse2D.Float(this.left,this.top,this.diam,this.diam);

            this.prop = ( ((double)this.radius) / Lander.maxSafeLandingSpeed);

            this.update();
        }
        public void update(){
            boolean limit = false;
            double dx = Lander.Current.tx;
            double dy = Lander.Current.ty;
            double mx = (Math.abs(dx) / Lander.maxSafeLandingSpeed);
            double my = (Math.abs(dy) / Lander.maxSafeLandingSpeed);
            double mag = Math.max(my,my);
            if (0.8 < mag){
                if (1.0 < mag)
                    this.color = Color.red;
                else
                    this.color = Color.yellow;
            }
            else
                this.color = Color.green;

            dx = (dx * this.prop);
            dy = (dy * this.prop);

            this.nx = this.cx+(int)(dx);
            this.ny = this.cy+(int)(dy);
        }
        public void draw(Graphics2D g){

            g.setColor(this.color);

            g.drawOval(this.left,this.top,this.diam,this.diam);

            g.drawOval((this.cx-1),(this.cy-1),2,2);

            g.setClip(this.clip);

            g.drawOval((this.nx-1),(this.ny-1),2,2);

            g.drawLine(this.cx,this.cy,this.nx,this.ny);

            g.setClip(null);
        }
    }
    public static class Altitude {

        private final static Rectangle Dim = Markers.stringBounds("d");
        private final static int DH = ((Dim.height / 2)+3);

        public final static double CEIL = 10000.0;

        private final float sw, sh;

        private int top, left, bottom, height, xm, xt;

        private volatile int marker;

        private volatile Color color;

        private volatile String string;

        private volatile Rectangle stringBounds;


        public Altitude(float sw, float sh){
            super();
            this.sw = sw;
            this.sh = sh;
            this.color = Color.green;
        }


        public void init(Game game){

            this.left = (int)(game.width - (game.width * this.sw));
            this.height = (int)(game.height * this.sh);
            this.top = (int)((game.height - this.height)/2);
            this.bottom = (this.top + this.height);
            this.xm = (this.left-4);
            this.xt = (this.left - Dim.width);
            this.update();
        }
        public void update(){

            double raw = Lander.Current.altitude();

            double norm = (raw / CEIL);
            if (1f < norm)
                norm = 1f;

            this.marker = (int)(this.bottom - (this.height * norm) + DH);

            if (0.70 < norm){
                if (0.90 < norm)
                    this.color = Color.red;
                else
                    this.color = Color.yellow;
            }
            else
                this.color = Color.green;

            this.string = String.format("%3.1f",raw);
            this.stringBounds = Detail.stringBounds(this.string,this.xt,this.marker);
            this.stringBounds.x = (this.left - this.stringBounds.width - 4);
            this.stringBounds.y -= (this.stringBounds.height / 2);
        }
        public void draw(Graphics2D g){

            g.setColor(this.color);

            g.drawLine(this.left,this.top,(this.left+2),this.top);
            g.drawLine(this.left,this.top,this.left,this.bottom);
            g.drawLine(this.left,this.bottom,(this.left+2),this.bottom);

            Markers.drawString("d",this.xm,this.marker,g);
            if (null != this.string)
                Detail.drawString(this.string,this.stringBounds.x,this.stringBounds.y,g);
        }
    }


    private Fuel fuel;

    private Vector vector;

    private Score score;

    private Altitude altitude;

  
    public FlightDisplay(Game g){
        super(g);
        this.fuel = new Fuel(0.02f,0.8f);
        this.vector = new Vector(0.05f);
        this.score = new Score();
        this.altitude = new Altitude(0.02f,0.8f);
    }


    public void init(boolean newGame){
        super.init(newGame);
        Game game = (Game)this.panel;
        this.fuel.init(game);
        this.vector.init(game);
        this.score.init(newGame);
        this.altitude.init(game);
    }
    public void scored(int points){
        this.score.update(points);
    }
    public void update(){
        this.fuel.update();
        this.vector.update();
        this.altitude.update();
    }
    public void draw(Graphics2D g)   
    {
        super.draw(g);
        this.fuel.draw(g);
        this.vector.draw(g);
        this.score.draw(g);
        this.altitude.draw(g);
    }
}
