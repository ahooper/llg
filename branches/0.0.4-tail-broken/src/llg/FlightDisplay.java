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

    public static class Score {

        private final static String[] Value = {
            "",
            "0",
            "00",
            "000",
            "0000",
            "00000",
            "000000",
        };

        private Font technical;
        private int marginW;
        private int marginH;
        private int value;
        private String valueString;
        private Rectangle dim;


        public Score(){
            super();
        }


        public void init(boolean newGame){
            this.technical = Font.Futural.clone(1.9f,2.2f);
            this.technical.setVerticalAlignment(Font.VERTICAL_TOP);

            this.marginW = (int)(34.0 * Screen.Current.scale);
            this.marginH = (int)(24.0 * Screen.Current.scale);

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
            this.dim = this.technical.stringBounds(this.valueString,marginW,marginH);
        }
        public void draw(Graphics2D g){

            g.setColor(Color.green);

            this.technical.drawString(this.valueString,this.dim.x,this.dim.y,g);
        }
    }
    public static class Fuel {

        private final static float PropMassKg = 8165.0f;


        private final float sw, sh;

        protected Font markers, detail;

        private Rectangle dim;
        private int dh;

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

            this.markers = Font.Markers.clone(2f,1f);
            this.detail = Font.Futural.clone(0.7f,0.6f);
            this.detail.setVerticalAlignment(Font.VERTICAL_HALF);

            this.dim = this.markers.stringBounds("d");
            this.dh = (int)(((float)this.dim.height / 2f)+3f);

            this.left = (int)(game.width * this.sw);
            this.height = (int)(game.height * this.sh);
            this.top = (int)((game.height - this.height)/2);
            this.bottom = (this.top + this.height);
            this.xm = (this.left - this.dim.width);
            this.xt = (this.left + 3);
            this.update();
        }
        public void update(){

            float fuel = Lander.fuel;

            this.marker = (int)(this.bottom - (this.height * fuel) + this.dh);

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
            this.stringBounds = this.detail.stringBounds(this.string,this.xt,this.marker);
            this.stringBounds.y -= (this.stringBounds.height / 2);
        }
        public void draw(Graphics2D g){

            g.setColor(this.color);

            g.drawLine(this.left,this.top,(this.left+2),this.top);
            g.drawLine(this.left,this.top,this.left,this.bottom);
            g.drawLine(this.left,this.bottom,(this.left+2),this.bottom);

            this.markers.drawString("d",this.xm,this.marker,g);
            if (null != this.string)
                this.detail.drawString(this.string,this.stringBounds.x,this.stringBounds.y,g);
        }
    }
    public static class Vector {

        protected final static int Margin = 14;

        protected int top, left, diam, radius, cx, cy;

        private double prop;

        private Shape clip;

        private volatile int nx, ny;

        private volatile Color color;


        public Vector(Attitude att){
            super();
            this.color = Color.green;
        }


        public void init(Attitude att, Game game){
            this.diam = att.diam;
            this.radius = att.radius;
            this.top = att.top;
            this.left = att.left+this.diam+Margin;
            this.cy = (this.top + this.radius);
            this.cx = (this.left + this.radius);

            this.clip = new Ellipse2D.Float(this.left,this.top,this.diam,this.diam);

            this.prop = ( ((double)this.radius) / Lander.maxSafeLandingSpeed);

            this.update();
        }
        public void update(){

            double dx = Lander.Current.tx;
            double dy = Lander.Current.ty;
            double range = (Math.max(Math.abs(dx),Math.abs(dy)) / Lander.maxSafeLandingSpeed);

            if (0.80 < range){
                if (0.90 < range)
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


        public final static double CEIL = 10000.0;

        private final float sw, sh;

        protected Font markers, detail;

        private Rectangle dim;
        private int dh;

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

            this.markers = Font.Markers.clone(2f,1f);
            this.detail = Font.Futural.clone(0.7f,0.6f);
            this.detail.setVerticalAlignment(Font.VERTICAL_HALF);

            this.dim = this.markers.stringBounds("d");
            this.dh = (int)(((float)this.dim.height / 2f)+3f);

            this.left = (int)(game.width - (game.width * this.sw));
            this.height = (int)(game.height * this.sh);
            this.top = (int)((game.height - this.height)/2);
            this.bottom = (this.top + this.height);
            this.xm = (this.left-4);
            this.xt = (this.left - this.dim.width);
            this.update();
        }
        public void update(){

            double raw = Lander.Current.altitudeMeters();

            double norm = (raw / CEIL);
            if (1f < norm)
                norm = 1f;

            this.marker = (int)(this.bottom - (this.height * norm) + this.dh);

            if (0.70 < norm){
                if (0.90 < norm)
                    this.color = Color.red;
                else
                    this.color = Color.yellow;
            }
            else
                this.color = Color.green;

            this.string = String.format("%3.0f",raw);
            this.stringBounds = this.detail.stringBounds(this.string,this.xt,this.marker);
            this.stringBounds.x = (this.left - this.stringBounds.width - 4);
            this.stringBounds.y -= (this.stringBounds.height / 2);
        }
        public void draw(Graphics2D g){

            g.setColor(this.color);

            g.drawLine(this.left,this.top,(this.left+2),this.top);
            g.drawLine(this.left,this.top,this.left,this.bottom);
            g.drawLine(this.left,this.bottom,(this.left+2),this.bottom);

            this.markers.drawString("d",this.xm,this.marker,g);
            if (null != this.string)
                this.detail.drawString(this.string,this.stringBounds.x,this.stringBounds.y,g);
        }
    }

    public static class Attitude {

        private final float sw;

        int top, left, diam, radius, cx, cy;

        private double rotate;


        public Attitude(DSKY dsky, float sw){
            super();
            this.sw = sw;
        }


        public void init(DSKY dsky, Game game){
            this.diam = (int)(game.width * sw);
            this.radius = (this.diam / 2);
            this.top = dsky.y + Vector.Margin;
            this.left = dsky.right + Vector.Margin;
            this.cy = (this.top + this.radius);
            this.cx = (this.left + this.radius);

            this.update();
        }
        public void update(){

            this.rotate = -(Lander.Current.attitudeRadians());
        }
        public void draw(Graphics2D ig){

            Graphics2D g = (Graphics2D)ig.create();
            try {
                g.setColor(Color.green);

                g.translate(this.cx, this.cy);

                g.rotate(this.rotate);

                int x = -(this.radius);
                int y = x;

                g.drawOval(x,y,this.diam,this.diam);

                g.setClip(x,1,this.diam,this.radius);

                g.fillOval(x,y,this.diam,this.diam);

            }
            finally {
                g.dispose();
            }
        }
    }

    public final static class Longitude {

        private final float sw;

        private int left, center, top, right, width, mark;


        public Longitude(float sw){
            super();
            this.sw = sw;
        }
        public void init(Game game){
            this.width = (int)(game.width * this.sw);
            this.left = (game.width - this.width)/2;
            this.top = (int)(Surface.Ymax * World.Scale);
            this.right = (this.left + this.width);
            this.center = (this.left + (this.width / 2));
        }
        public void update(){

            double lon = Lander.Current.longitude().degrees/360.0;

            this.mark = (this.center + (int)(lon * (double)this.width));
        }
        public void draw(Graphics2D g){

            g.setColor(Color.green);

            g.drawLine(this.left,this.top,this.right,this.top);

            g.drawLine(this.center,(this.top-2),this.center,(this.top+2));

            g.setColor(Color.red);

            g.drawLine(this.mark,(this.top-2),this.mark,(this.top+2));
        }
    }


    protected final Fuel fuel;

    protected final Vector vector;

    protected final Score score;

    protected final Altitude altitude;

    protected final Attitude attitude;

    //protected final Longitude longitude;

    protected final DSKY dsky;

    protected final DSKY2 dsky2;

  
    public FlightDisplay(Game g){
        super(g);
        this.score = new Score();
        this.fuel = new Fuel(0.02f,0.8f);
        this.altitude = new Altitude(0.02f,0.8f);
        this.dsky = new DSKY(0.4);
        this.dsky2 = new DSKY2(this.dsky);
        this.attitude = new Attitude(this.dsky,0.05f);
        this.vector = new Vector(this.attitude);
        //         this.longitude = new Longitude(0.8f);
    }


    public void init(boolean newGame){
        super.init(newGame);
        Game game = (Game)this.panel;
        this.score.init(newGame);
        this.fuel.init(game);
        this.altitude.init(game);
        this.dsky.init(game,this);
        this.dsky2.init(game,this);
        this.attitude.init(this.dsky,game);
        this.vector.init(this.attitude,game);
        //         this.longitude.init(game);
    }
    public void scored(int points){
        this.score.update(points);
    }
    public void update(){
        this.fuel.update();
        this.altitude.update();
        this.dsky.update();
        this.dsky2.update();
        this.attitude.update();
        this.vector.update();
        //         this.longitude.update();
    }
    public void draw(Graphics2D g)   
    {
        super.draw(g);
        this.score.draw(g);
        this.fuel.draw(g);
        this.altitude.draw(g);
        this.dsky.draw(g);
        this.dsky2.draw(g);
        this.attitude.draw(g);
        this.vector.draw(g);
        //         this.longitude.draw(g);
    }
}
