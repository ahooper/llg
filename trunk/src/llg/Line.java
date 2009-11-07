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
/*
 * Copyright (C) 1999 John Donohue.  All rights reserved.
 * 
 * Used with permission.
 */
package llg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;


public class Line
    extends Line2D.Double
    implements Tickable
{
    public final static class Iterator
        extends Object
        implements java.util.Iterator<Line>
    {
        private final Line[] list;
        private final int count;
        private int index;

        public Iterator(Line[] list){
            super();
            this.list = list;
            this.count = list.length;
        }

        public boolean hasNext(){
            return (this.index < this.count);
        }
        public Line next(){
            int idx = this.index;
            if (idx < this.count){
                this.index += 1;
                return this.list[idx];
            }
            else
                throw new java.util.NoSuchElementException(String.valueOf(idx));
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
    public final static Line[] Add(Line[] list, Line item){
        if (null != item){
            if (null == list)
                return new Line[]{item};
            else {
                int len = list.length;
                Line[] copier = new Line[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                list = copier;
            }
        }
        return list;
    }
    public final static Line[] Copy(Line[] list){
        Line[] clone = list.clone();
        for (int cc = 0, count = clone.length; cc < count; cc++){
            clone[cc] = clone[cc].clone();
        }
        return clone;
    }


    /**
     * Used in particle explosion
     */
    double deltaX;
    double deltaY;
    /**
     * Used in models: lander nozzle exhaust flame
     */
    boolean visible;

    boolean collision;


    protected Line(){
        super();
    }
    public Line (double x1, double y1, double x2, double y2){
        super(x1,y1,x2,y2);
        this.visible = true;
    }
    public Line(String string){
        super();
        StringTokenizer strtok = new StringTokenizer(string," ,}{");
        if (4 == strtok.countTokens()){
            this.x1 = java.lang.Double.parseDouble(strtok.nextToken());
            this.y1 = java.lang.Double.parseDouble(strtok.nextToken());
            this.x2 = java.lang.Double.parseDouble(strtok.nextToken());
            this.y2 = java.lang.Double.parseDouble(strtok.nextToken());
            this.visible = true;
        }
        else
            throw new IllegalArgumentException(string);
    }


    public void tick(){

        this.x1 += deltaX;
        this.y1 += deltaY;
        this.x2 += deltaX;
        this.y2 += deltaY;

        if (this.collision)
            this.collision = false;
    }
    /**
     * This line is contained in the craft.
     */
    public Line2D.Double toWorld(Craft frame){
        double x1 = (this.x1+frame.dx);
        double y1 = (this.y1+frame.dy);
        double x2 = (this.x2+frame.dx);
        double y2 = (this.y2+frame.dy);
        return new Line2D.Double(x1,y1,x2,y2);
    }

    public final boolean intersects(Line2D.Double otherLine ){

        /*
         * Detect intersections between perfectly horizontal lines
         */
        if ( otherLine.x1 >= this.x1 &&
             otherLine.x2  <= this.x2 &&
             (Math.abs(this.y1 - otherLine.y1) < 1 )  &&
             (Math.abs(this.y2 - otherLine.y2) < 1 ))

            return true;

        /*
         * Detect intersections between perfectly vertical lines
         */
        else if ( otherLine.y1 >= this.y1 &&
                  otherLine.y2 <= this.y2 &&
                  (Math.abs(this.x1 - otherLine.x1) < 1 ) &&
                  (Math.abs(this.x2 - otherLine.x2) < 1 ))

            return true;

        else {

            float ax = (float)this.x1;
            float ay = (float)this.y1;
            float bx = (float)this.x2;
            float by = (float)this.y2;
            float cx = (float)otherLine.x1;
            float cy = (float)otherLine.y1;
            float dx = (float)otherLine.x2;
            float dy = (float)otherLine.y2;

            /*
             * In order to avoid a division by zero error we will slightly slant any
             * perfectly vertical or horizontal lines.  This may make the intersection
             * detection too eager when dealing with long close together parrallel lines.
             */
            if (ax == bx) ax += 0.01f;
            if (cx == dx) cx += 0.01f;
            if (ay == by) ay += 0.01f;
            if (cy == dy) cy += 0.01f;

            float r = ( ((ay - cy) * (dx - cx)) - ((ax - cx) * (dy - cy)) ) /
                ( ((bx - ax) * (dy - cy)) - ((by - ay) * (dx - cx)) );

            if ( r<0 || r>1 )

                return false;

            else {

                float s = ( ((ay - cy) * (bx - ax)) - ((ax - cx) * (by - ay)) ) /
                    ( ((bx - ax) * (dy - cy)) - ((by - ay) * (dx - cx)) );

                if (s<0 || s>1 )

                    return false;
                else
                    return true;
            }
        }
    }
    public Line clone(){
        return (Line)super.clone();
    }
    public void rotate(double radian){
    }
}
