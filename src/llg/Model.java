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

import java.awt.Event;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;


/**
 * 
 */
public class Model
    extends Rectangle2D.Double
    implements Drawable,
               Tickable,
               Iterable<Line>,
               Cloneable
{
    public final static Charset Ascii = Charset.forName("US-ASCII");

    public final static Model[] Add(Model[] list, Model item){
        if (null != item){
            if (null == list)
                list = new Model[]{item};
            else {
                int len = list.length;
                Model[] copier = new Model[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                list = copier;
            }
        }
        return list;
    }

    public final static Model Orbiter = new Model("orbiter");
    public final static Model Lander = new Model("lander");


    protected Line[] lines;

    protected volatile double dx, dy, tx, ty, rotate, width2, height2;


    public Model(Model dynamic, Model model){
        super();
        this.lines = Line.Copy(model.lines);
        this.dx = dynamic.dx;
        this.dy = dynamic.dy;
        this.rotate = dynamic.rotate;
        this.tx = dynamic.tx;
        this.ty = dynamic.ty;
    }
    public Model(Model model){
        super();
        this.lines = Line.Copy(model.lines);
        this.dx = model.dx;
        this.dy = model.dy;
        this.rotate = model.rotate;
        this.tx = model.tx;
        this.ty = model.ty;
    }
    public Model(String name){
        super();
        InputStream in = null;
        try {
            String src = "/models/"+name+".emf";
            in = this.getClass().getResourceAsStream(src);
            if (null != in)
                this.read (new InputStreamReader(in,Ascii));
            else
                throw new IllegalArgumentException(String.format("Model '%s' not found at '%s'.",name,src));
        }
        catch (IOException e){
            throw new IllegalArgumentException(name,e);
        }
        finally {
            if (null != in){
                try {
                    in.close();
                }
                catch (IOException exc){
                }
            }
        }
    }


    public Model clone(){
        Model clone = (Model)super.clone();
        clone.lines = this.lines.clone();
        return clone;
    }
    public final int size(){
        return this.lines.length;
    }
    public final Line get(int idx){
        return this.lines[idx];
    }
    public final java.util.Iterator<Line> iterator(){
        return new Line.Iterator(this.lines);
    }
    public void tick(){
        this.move(this.tx,this.ty);
    }
    public Point2D.Double getLocation(){
        return new Point2D.Double(this.dx,this.dy);
    }
    /**
     * @return Center of rotation in the world coordinate frame
     */
    public Point2D.Double toWorld(){
        return new Point2D.Double(this.dx,this.dy);
    }
    public final void translate(double x, double y){
        this.dx = x;
        this.dy = y;
    }
    public final void move(double dx, double dy){
        this.dx += dx;
        this.dy += dy;
    }
    public final void motion(double tx, double ty){
        this.tx += tx;
        this.ty += ty;
    }
    public final void rotate(double r){
        this.rotate = r;
    }
    public void keyDown(Event evt, int key) {
    }
    public boolean mouseDown(Event evt, int x, int y){
        return false;
    }
    public void draw(Graphics2D g){

        Graphics2D gm = (Graphics2D)g.create();
        try {
            gm.translate(this.dx,this.dy);
            gm.rotate(this.rotate);

            for (Line el: this.lines){
                if (el.visible)
                    gm.draw(el);
            }
        }
        finally {
            gm.dispose();
        } 
    }
    public final void read (Reader in) throws IOException {
        BufferedReader reader;
        if (in instanceof BufferedReader)
            reader = (BufferedReader)in;
        else
            reader = new BufferedReader(in);

        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;

        Line[] lines = null;
        String line;
        double minima, maxima;

        while (null != (line = reader.readLine())){
            boolean e, i;
            {
                e = line.startsWith("e2");
                if (e)
                    i = false;
                else
                    i = line.startsWith("i2");
            }
            if (e || i){
                
                line = line.substring(2);
                Line el = new Line(line);

                el.visible = e;

                lines = Line.Add(lines,el);

                minima = Math.min(el.x1,el.x2);
                minX = Math.min(minX,minima);

                maxima = Math.abs(Math.max(el.x1,el.x2));
                maxX = Math.max(maxX,maxima);

                minima = Math.min(el.y1,el.y2);
                minY = Math.min(minY,minima);

                maxima = Math.abs(Math.max(el.y1,el.y2));
                maxY = Math.max(maxY,maxima);
            }
        }
        if (null != lines){
            this.x = minX;
            this.y = minY;
            double w;
            if (0 > minX)
                w = Math.abs(minX)+maxX;
            else
                w = maxX;
            this.width = w;
            this.width2 = (w / 2.0);
            double h;
            if (0 > minY)
                h = Math.abs(minY)+maxY;
            else
                h = maxY;
            this.height = h;
            this.height2 = (h / 2.0);

            this.lines = lines;
        }
        else
            throw new IllegalStateException();
    }
}
