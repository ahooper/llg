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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * Visually, a part of the {@link FlightDisplay}.
 * @author jdp
 */
public class DSKY2 
    extends Rectangle
    implements Viewport
{

    protected static class Text {
        final String string;
        final Rectangle bounds;

        protected Text(String string, Rectangle bounds){
            super();
            this.string = string;
            this.bounds = bounds;
        }
    }

    static DSKY2 Instance;

    final DSKY dsky;
    final Model model;
    final boolean visible;
    volatile Font font;
    volatile int right;
    volatile double cx, cy;
    volatile double mx, my, ms;
    volatile Stroke strokeM;
    volatile Text[] text;

    public DSKY2(DSKY dsky){
        super();
        this.dsky = dsky;
        this.model = Model.DSKY;
        Instance = this;
        this.visible = dsky.visible;
    }


    public double getLeft(){
        return this.x;
    }
    public double getTop(){
        return this.y;
    }
    public double getCenterX(){
        return this.cx;
    }
    public double getCenterY(){
        return this.cy;
    }
    public void init(Panel panel, FlightDisplay hud){
        DSKY dsky = this.dsky;

        this.mx = dsky.mx;
        this.my = dsky.my;
        this.ms = dsky.ms;
        this.right = (panel.left + panel.innerWidth - 5);
        FlightDisplay.Vector vector = hud.vector;
        this.x = (vector.left+vector.diam+FlightDisplay.Vector.Margin);
        this.y = dsky.y;
        this.width = (this.right-this.x);
        this.height = dsky.h;
        this.cx = (double)this.x + ((double)this.width/2.0);
        this.cy = (double)this.y + ((double)this.height/2.0);

        this.strokeM = dsky.strokeM;
        this.font = dsky.fontOut;
    }
    public void update(){
    }
    public void draw(Graphics2D ig){
        if (this.visible){
            ig.setColor(Color.green);
            Graphics2D g2 = (Graphics2D)ig.create(this.x, this.y, this.width, this.height);
            boolean help, map = true;
            try {
                Graphics2D g;
                if (null != this.text){
                    help = true;
                    map = false;
                    g = (Graphics2D)g2;
                }
                else {
                    help = false;
                    map = true;
                    g = (Graphics2D)g2.create();
                }
                try {
                    g.scale(this.ms,this.ms);
                    g.translate(this.mx,this.my);
                    g.setStroke(this.strokeM);

                    Line[] model = this.model.lines;
                    g.draw(model[0].polygon);

                    if (help){
                        Font font = this.font;
                        for (Text text : this.text){
                            Rectangle b = text.bounds;
                            font.drawString(text.string,b.x,b.y,g);
                        }
                    }
                }
                finally {
                    g.dispose();
                }
                if (map){
                    Luna.Instance.map(g2,this);
                }
            }
            finally {
                if (map)
                    g2.dispose();
            }
        }
    }

}
