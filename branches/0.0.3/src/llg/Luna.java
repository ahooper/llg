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
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * In two dimensions, the lander can travel over the range of zero
 * degrees latitude, only.  The equator of the Moon.
 * 
 * @author jdp
 */
public final class Luna 
    extends Object
    implements Drawable,
               World.Distance
{
    public final static Luna Instance = new Luna();

    static void SInit(){
    }
    private final static Color ColorB = new Color(0x20,0x20,0x20);

    public final static double Radius = (1738.14 * Kilometers);

    public final static double Circumference = (2 * Math.PI * Radius);


    private volatile Surface list;

    volatile Font fontP;


    private Luna(){
        super();
    }


    public void reset(){
        this.fontP = Font.Futural.clone(1.8f,1.0f);
        this.fontP.setVerticalAlignment(Font.VERTICAL_TOP);
        if (null == this.list)
            this.list = new Surface();
        else {
            this.list.destroy();
            this.list = new Surface();
        }
    }
    public void destroy(){
        if (null != this.list){
            this.list = null;
            this.list.destroy();
        }
    }
    public void draw(Graphics2D ig){

        Rectangle2D.Double viewport = Panel.Instance.toWorld();

        Graphics2D g = (Graphics2D)ig.create();
        try {
            Surface center = this.list;
            g.setColor(ColorB);
            g.fillRect((int)viewport.x,(int)Surface.Ymax,(int)viewport.width,(int)viewport.height);

            double vw = viewport.x;
            double ve = vw+viewport.width;

            Surface east = center;
            while (ve > east.x1){
                east.draw(g);
                east = east.east();
            }

            Surface west = center.west();
            while (vw < west.x2){
                west.draw(g);
                west = west.west();
            }
        }
        finally {
            g.dispose();
        }
    }
    public Surface landing(){
        return this.list.landing();
    }
    public Surface seek(Lander lander){
        Point2D.Double fp = lander.feetMidpoint();
        Surface over = this.list.over(fp);
        this.list = over;
        return over;
    }
}
