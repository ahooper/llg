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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Distance code based on the work of Damian Coventry, Pieter Iserbyt
 * and Paul Bourke.  See
 * http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/
 */
public final class Vector {

    public static double Add( double signed, double scalar){
        if (0 > signed)
            return (signed - scalar);
        else
            return (signed + scalar);
    }
    public static double Dim( double a, double b){
        if (0 > a){
            if (0 > b){
                if (a < b)
                    return (a - b);
                else
                    return (b - a);
            }
            else {
                return (b - a);
            }
        }
        else if (0 > b)
            return (b - a);
        else if (a > b)
            return (a - b);
        else
            return (b - a);
    }
    public static double Magnitude( Point2D.Double p1, Point2D.Double p2){
        return Math.sqrt(Magnitude2(p1.x, p1.y, p2.x, p2.y));
    }
    public static double Magnitude( Line2D.Double line){
        return Math.sqrt(Magnitude2(line.x1, line.y1, line.x2, line.y2));
    }
    public static double Magnitude( Point2D.Double p1, double p2_x, double p2_y){
        return Math.sqrt(Magnitude2(p1.x, p1.y, p2_x, p2_y));
    }
    public static double Magnitude( double p1_x, double p1_y, double p2_x, double p2_y){
        return Math.sqrt(Magnitude2(p1_x, p1_y, p2_x, p2_y));
    }
    /**
     * Magnitude squared
     */
    public static double Magnitude2( Point2D.Double p1, Point2D.Double p2){

        return Magnitude(p1.x, p1.y, p2.x, p2.y);
    }
    public static double Magnitude2( Line2D.Double line){

        return Magnitude(line.x1, line.y1, line.x2, line.y2);
    }
    public static double Magnitude2( Point2D.Double p1, double p2_x, double p2_y){

        return Magnitude(p1.x, p1.y, p2_x, p2_y);
    }
    public static double Magnitude2( double p1_x, double p1_y, double p2_x, double p2_y){

        double vX = Dim(p2_x,p1_x);
        double vY = Dim(p2_y,p1_y);

        return Magnitude2(vX,vY);
    }
    private static double Magnitude2( double vX, double vY){

        return ((vX*vX) + (vY*vY));
    }
    /**
     * @return Null in case missing normal
     */
    public static java.lang.Double Distance( Line2D.Double line, Point2D.Double point){
 
        Point2D.Double normal = Normal(line,point);
        if (null != normal)
            return Magnitude( point, normal);
        else
            return null;
    }

    public static Point2D.Double Normal( Line2D.Double line, Point2D.Double point){
 
        double Lx = (line.x2 - line.x1);
        double Ly = (line.y2 - line.y1);
        if (0.0 == Lx && 0.0 == Ly)
            return null;
        else {
            double U = (((point.x - line.x1) * Lx)+((point.y - line.y1) * Ly)) / Magnitude2(Lx,Ly);

            if (U < 0.0 || U > 1.0)
                return null;
            else 
                return new Point2D.Double( (line.x1 + U * Lx), (line.y1 + U * Ly));
        }
    }

    /**
     * A command line tester which will visualize with argument "viz".
     */
    public final static class Test 
        extends java.awt.Frame
    {
        static Test Instance = new Test();

        public final static class Distance
            extends java.awt.Dialog
        {

            private final static Distance[] Test = {
                (new Distance(0,0,new Line2D.Double(10, 10, 20, 20), new Point2D.Double(5, 5), null)),
                (new Distance(0,1,new Line2D.Double(10, 10, 20, 20), new Point2D.Double(15, 15), 0.000000)),
                (new Distance(0,2,new Line2D.Double(20, 10, 20, 20), new Point2D.Double(15, 15), 5.000000)),
                (new Distance(0,3,new Line2D.Double(20, 10, 20, 20), new Point2D.Double(0, 15), 20.000000)),
                (new Distance(1,0,new Line2D.Double(20, 10, 20, 20), new Point2D.Double(0, 25), null)),
                (new Distance(1,1,new Line2D.Double(-50, 10, 20, 20), new Point2D.Double(-13, -25), 39.880822))
            };

            public final Line2D.Double line;
            public final Point2D.Double point, normal;
            public final java.lang.Double correct, test;
            public final boolean isCorrect;


            public Distance( int row, int col, Line2D.Double line, Point2D.Double point, java.lang.Double distance){
                super(Vector.Test.Instance);
                this.line = line;
                this.point = point;
                this.correct = distance;
                this.normal = Vector.Normal(line,point);
                if (null != this.normal)
                    this.test = Vector.Magnitude(point,this.normal);
                else
                    this.test = null;

                if (null != this.correct && null != this.test){

                    double e = Math.abs(this.correct - this.test);

                    this.isCorrect = (0.00001 > e);
                }
                else
                    this.isCorrect = (distance == this.test);

                this.setUndecorated(true);
                this.setLocation((col*D+10),(row*D+10));
                this.setSize(D,D);
            }


            public void viz(){
                this.setVisible(true);
            }
            public void update(Graphics g){
                this.paint((Graphics2D)g);
            }
            public void paint(Graphics g){
                this.paint((Graphics2D)g);
            }
            private void paint(Graphics2D g){
                g.setColor(Color.white);
                g.fillRect(0,0,D,D);

                if (this.isCorrect)
                    g.setColor(Color.green);
                else
                    g.setColor(Color.red);

                g.drawRect(0,0,(D-1),(D-1));

                g.translate(D2,D2);
                g.scale(3.0,3.0);

                int test = (null == this.test)?(0):(this.test.intValue());

                g.drawString("D  ("+Integer.toString(test)+")",-70,-70);

                g.setColor(Color.black);

                int p3x = P(this.point.x);
                int p3y = P(this.point.y);
                g.fillOval((p3x-1),(p3y-1),2,2);
                g.drawString("P3 ("+Integer.toString(p3x)+","+Integer.toString(p3y)+")",-70,-60);

                int p1x = P(this.line.x1);
                int p1y = P(this.line.y1);
                g.fillOval((p1x-1),(p1y-1),2,2);
                g.drawString("P1 ("+Integer.toString(p1x)+","+Integer.toString(p1y)+")",-70,-50);

                int p2x = P(this.line.x2);
                int p2y = P(this.line.y2);
                g.fillOval((p2x-1),(p2y-1),2,2);
                g.drawLine(p1x,p1y,p2x,p2y);
                g.drawString("P2 ("+Integer.toString(p2x)+","+Integer.toString(p2y)+")",-70,-40);

                if (this.isCorrect)
                    g.setColor(Color.green);
                else
                    g.setColor(Color.red);

                Point2D.Double normal = this.normal;
                if (null != normal){
                    int nx = P(normal.x);
                    int ny = P(normal.y);
                    g.fillOval((nx-1),(ny-1),2,2);
                    g.drawString("N  ("+Integer.toString(nx)+","+Integer.toString(ny)+")",-70,-30);

                    if (0 != test){
                        double cx = normal.x;
                        double cy = normal.y;
                        double r = this.test;
                        int left = (int)(cx - r);
                        int top = (int)(cy - r);
                        int right = (int)(cx + r);
                        int bottom = (int)(cy + r);

                        g.setStroke(S);

                        g.drawOval(left,top,(right-left),(bottom-top));

                        g.drawLine(nx,ny,p3x,p3y);
                    }
                }
            }

            private final static int D = 500;
            private final static int D2 = (D/2);
            private final static int P(double p){
                return (int)(p);
            }
            private final static java.awt.Stroke S = new java.awt.BasicStroke(0.05f);
        }



        public static void main(String[] argv){

            boolean viz = false;
            if (0 != argv.length)
                viz = ("viz".equals(argv[0]));

            int errors = 0;

            for (Test.Distance test: Test.Distance.Test){
                if (viz)
                    test.viz();

                if (test.isCorrect)
                    System.out.println(String.format("OK (%3.5f)/(%3.5f) = (%3.5f, %3.5f) - (%3.5f, %3.5f, %3.5f, %3.5f)",test.test, test.correct, test.point.x, test.point.y, test.line.x1, test.line.y1, test.line.x2, test.line.y2));
                else {
                    errors += 1;
                    System.out.println(String.format("ER (%3.5f)/(%3.5f) = (%3.5f, %3.5f) - (%3.5f, %3.5f, %3.5f, %3.5f)",test.test, test.correct, test.point.x, test.point.y, test.line.x1, test.line.y1, test.line.x2, test.line.y2));
                }
            }
            if (!viz){
                if (0 == errors)
                    System.exit(0);
                else
                    System.exit(1);
            }
        }
    }
}
