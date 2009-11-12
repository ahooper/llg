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

import java.awt.geom.Rectangle2D;

/**
 * The camera is a viewport function producing the translation and
 * scale for the Panel.
 * 
 * @author jdp
 */
public final class Camera
    extends Rectangle2D.Double
    implements World.Distance
{
    static Camera Current;

    static Camera View(Game g, Craft c){

        if (null == Current){

            double scale = Math.abs(g.innerHeight / (Screen.Current.height / 3.0));

            return new Camera(scale,g,(-c.dx),(-c.dy));
        }
        else {
            Camera view = Current.view((-c.dx),(-c.dy));
            if (null != view)
                return view;

            else {
                double scale = Math.abs(g.innerHeight / (c.altitudeWorld()*3));

                return new Camera(scale,g,(-c.dx),(-c.dy));
            }
        }
    }


    public final char id;

    volatile double dx, dy, scale, ty;

    private final Camera prev;


    private Camera(double s, Game g, double x, double y){
        super();

        this.scale = s;

        this.dx = (x * s)+(Game.Instance.getCenterX());

        this.dy = g.getCenterX();

        this.x = (-dx / s);
        this.y = (-dy / s);
        this.width = (Panel.Instance.width / s);
        this.height = (Panel.Instance.height / s);
        this.ty = (this.y + this.height);

        if (null == Current)
            this.id = 'A';
        else 
            this.id = (char)(Current.id + 1);

        this.prev = Current;

        Current = this;
    }


    public boolean is(char a){
        return (a == this.id);
    }
    private boolean contains(double y){
        return (this.y < y && y < this.ty);
    }
    private Camera view(double x, double y){
        Camera camera = this.prev;
        if (null != camera && camera.contains(y))
            return camera.view(x,y);
        else if (this.contains(y)){

            this.dx = (x * scale)+(Game.Instance.getCenterX());

            this.x = ((-this.dx) / this.scale);

            Current = this;
            return this;
        }
        else 
            return null;
    }
    public String toString(){
        return String.valueOf(this.id);
    }
}
