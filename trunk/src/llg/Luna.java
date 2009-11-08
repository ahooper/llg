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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Oh thee, luna.  How do we dream of walking all over you, like
 * toddlers on the beach.
 * 
 * @author jdp
 */
public final class Luna 
    extends Object
    implements Drawable
{
    public final static Luna Instance = new Luna();
    static void SInit(){
    }
    private final static Color ColorB = new Color(0x20,0x20,0x20);


    private volatile Surface list;

    final Font fontP = Font.Futural.clone(0.6f,0.4f);


    private Luna(){
        super();
    }


    public void destroy(){
        if (null != this.list){
            this.list = null;
            this.list.destroy();
        }
    }
    public void reset(){
        if (null == this.list)
            this.list = new Surface();
        else {
            this.list.destroy();
            this.list = new Surface();
        }
    }
    public void draw(Graphics2D ig){
        
        Rectangle2D.Double viewport = Panel.Instance.toWorld();

        Surface center = this.list;
        Graphics2D g = (Graphics2D)ig.create();
        g.setColor(ColorB);
        g.fillRect((int)viewport.x,(int)Surface.Ymax,(int)viewport.width,(int)viewport.height);

        try {
            Surface east = center;
            while (viewport.contains(east.x1,east.y1)){
                east.draw(g);
                east = east.east();
            }
            Surface west = center.west();
            while (viewport.contains(west.x2,west.y2)){
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
    public Surface collides(Craft actor){
        double ax = actor.dx;
        double aw = actor.width;
        double ay = actor.dy;
        double ah = actor.height;

        Point2D.Double c = new Point2D.Double((Vector.Add(ax,aw)/2.0), (Vector.Add(ay,ah)/2.0));
        Surface[] normals = this.list.normals(c);
        if (null != normals){
            /*
             * [TODO] This should be looking for proper intersection
             *        with the model.
             */
            double radius = Vector.Magnitude((ax),(ay),Vector.Add(ax,aw),Vector.Add(ay,ah));

            Surface nearest = null;
            for (Surface normal: normals){

                if (normal.distance < radius){

                    if (null == nearest || (normal.distance < nearest.distance))
                        nearest = normal;
                }
            }
            if (null != nearest){
                // 
                //
                return nearest;
            }
        }
        return null;
    }
}
