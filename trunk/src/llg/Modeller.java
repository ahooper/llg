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
import java.awt.Event;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


/**
 * Just a shabby model viewer, at the moment.
 * 
 * @author jdp
 */
public class Modeller
    extends Panel
{
    public static class GridCell
        extends Rectangle2D.Double
        implements Drawable
    {
        protected final static Font Technical = Font.Futural.clone(0.6f,0.5f);
        static {
            Technical.setVerticalAlignment(Font.VERTICAL_TOP);
        }

        public final static GridCell[] Add(GridCell[] list, GridCell item){
            if (null != item){
                if (null == list)
                    list = new GridCell[]{item};
                else {
                    int len = list.length;
                    GridCell[] copier = new GridCell[len+1];
                    System.arraycopy(list,0,copier,0,len);
                    copier[len] = item;
                    list = copier;
                }
            }
            return list;
        }


        public final Model model;

        protected int col, row, cx, cy, top, left, right, bottom;

        private double width2, height2, scale;


        public GridCell(Model model){
            super();
            this.model = model;
        }


        public void init(Modeller m){

            this.x = (this.col * m.gridCellWidth)+m.left;
            this.y = (this.row * m.gridCellHeight)+m.top;
            this.width = m.gridCellWidth;
            this.height = m.gridCellHeight;

            this.width2 = (this.width/2.0);
            this.height2 = (this.height/2.0);

            this.left = (int)Math.ceil(this.x);
            this.top = (int)Math.ceil(this.y);
            this.right = (int)(this.x + this.width);
            this.bottom = (int)(this.y + this.height);

            this.cx = ((int)(this.width2+this.x))-1;
            this.cy = ((int)(this.height2+this.y))-1;

            double sw = (this.width / this.model.width);
            double sh = (this.height / this.model.height);

            this.scale = Math.min(sw,sh);
        }
        public void draw(Graphics2D g){

            g.setColor(Color.green);
            /*
             * Outline
             */
            g.draw(this);
            /*
             * Grid Lines
             */
            int s = (int)this.scale;

            int x, y, x2, y2;

            x = this.cx;
            y = this.top;
            y2 = this.bottom;

            g.drawLine(x,y,x,y2);

            x -= s;
            while (this.left < x){
                g.drawLine(x,y,x,y2);
                x -= s;
            }
            x = (this.cx + s);
            while (this.right > x){
                g.drawLine(x,y,x,y2);
                x += s;
            }

            x = this.left;
            y = this.cy;
            x2 = this.right;

            g.drawLine(x,y,x2,y);

            y -= s;
            while (this.top < y){
                g.drawLine(x,y,x2,y);
                y -= s;
            }
            y = (this.cy + s);
            while (this.bottom > y){
                g.drawLine(x,y,x2,y);
                y += s;
            }
            /*
             * Model
             */
            g.setColor(Color.white);
            this.model.translate((this.x+this.width2),(this.y+this.height2));
            this.model.scale(this.scale);
            this.model.draw(g);
            /*
             * Overlay
             */
            g.setColor(Color.red);
            Graphics2D gp = (Graphics2D)g.create();
            try {
                gp.translate((this.x+this.width2),(this.y+this.height2));

                for (int cc = 0, count = this.model.size(); cc < count; cc++){
                    Line el = this.model.get(cc);
                    double ex1 = el.x1;
                    double ey1 = el.y1;
                    double ex2 = el.x2;
                    double ey2 = el.y2;

                    int gx1 = (int)(this.scale * ex1);
                    int gy1 = (int)(this.scale * ey1);
                    int gx2 = (int)(this.scale * ex2);
                    int gy2 = (int)(this.scale * ey2);

                    gp.drawLine(gx1,gy1,gx2,gy2);
                    gx1 -= 2;
                    gy1 -= 2;
                    gp.fillOval(gx1,gy1,4,4);

                    gx2 -= 2;
                    gy2 -= 2;
                    gp.fillOval(gx2,gy2,4,4);
                }
            }
            finally {
                gp.dispose();
            }
        }
    }


    private GridCell[] models;

    private int rows, cols;

    private double gridCellHeight, gridCellWidth;


    public Modeller(){
        super();
        this.hud = new ModelDisplay(this);
        String[] models = Options.ListString("--model");
        for (String name: models){
            this.models = GridCell.Add(this.models,(new GridCell(new Model(name))));
        }
    }


    public void init(Screen screen){
        super.init(screen);

        GridCell[] models = this.models;
        if (null != models){

            switch (models.length){
            case 1:
                this.rows = 1;
                this.cols = 1;
                break;
            case 2:
                this.rows = 1;
                this.cols = 2;
                break;
            case 3:
            case 4:
                this.rows = 2;
                this.cols = 2;
                break;
            default:
                double n = models.length;
                double r = Math.floor(Math.sqrt(n));
                this.rows = (int)r;
                this.cols = (int)Math.ceil(n / r);
                break;
            }

            this.gridCellWidth = (this.innerWidth / this.cols);
            this.gridCellHeight = (this.innerHeight / this.rows);

            for (int cc = 0, count = models.length; cc < count; cc++){
                GridCell model = models[cc];
                model.col = Index(cc % cols);
                model.row = Index((cc / rows)-1);
                model.init(this);
            }
        }
    }
    public void draw(Graphics2D bg){
        GridCell[] models = this.models;
        if (null != models){
            for (int cc = 0, count = models.length; cc < count; cc++){
                GridCell model = models[cc];
                model.draw(bg);
            }
        }
    }
    private final static int Index(int value){
        if (0 > value)
            return 0;
        else
            return value;
    }
}
