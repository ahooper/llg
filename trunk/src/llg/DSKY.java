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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * Visually, a part of the {@link FlightDisplay}.
 * @author jdp
 */
public class DSKY {

    private static DSKY Instance;

    public static void R1(String format, Object... args){
        String data = String.format(format,args);
        for (int cc = 0, count = Math.min(8,data.length()); cc < count; cc++){
            int idx = (IOOutR10+cc);
            InOut[idx].update(String.valueOf(data.charAt(cc)));
        }
    }
    public static void R2(String format, Object... args){
        String data = String.format(format,args);
        for (int cc = 0, count = Math.min(8,data.length()); cc < count; cc++){
            int idx = (IOOutR20+cc);
            InOut[idx].update(String.valueOf(data.charAt(cc)));
        }
    }
    public static void R3(String format, Object... args){
        String data = String.format(format,args);
        for (int cc = 0, count = Math.min(8,data.length()); cc < count; cc++){
            int idx = (IOOutR30+cc);
            InOut[idx].update(String.valueOf(data.charAt(cc)));
        }
    }

    protected static abstract class IO {

        protected volatile Font font;
        protected volatile Rectangle stringBounds, modelBounds;

        protected void update(String string){
        }
        protected abstract void init(Polygon shape, Font font);

        protected abstract void draw(Graphics2D g);
    }
    protected final static class In 
        extends IO
    {
        protected final String string;

        protected In(String string){
            super();
            this.string = string;
        }

        protected void init(Polygon shape, Font font){
            this.font = font;
            this.modelBounds = shape.getBounds();
            this.stringBounds = font.stringBounds(this.string,this.modelBounds.x,this.modelBounds.y);
        }
        protected void draw(Graphics2D g){
            String string = this.string;
            if (null != string){
                Rectangle bounds = this.stringBounds;
                if (null != bounds)
                    this.font.drawString(string,bounds.x,bounds.y,g);
            }
        }
    }
    protected final static class Out 
        extends IO
    {
        protected volatile String string;

        protected Out(String string){
            super();
            this.string = string;
        }

        protected void init(Polygon shape, Font font){
            this.font = font;
            this.modelBounds = shape.getBounds();
            this.stringBounds = font.stringBounds(this.string,this.modelBounds.x,this.modelBounds.y);
        }
        protected void update(String string){
            this.string = string;
            if (null != string)
                this.stringBounds = this.font.stringBounds(string,this.modelBounds.x,this.modelBounds.y);
        }
        protected void draw(Graphics2D g){
            String string = this.string;
            if (null != string){
                Rectangle bounds = this.stringBounds;
                if (null != bounds)
                    this.font.drawString(string,bounds.x,bounds.y,g);
            }
        }
    }

    final static int Outline    = 0;
    final static int KeyPlus    = 1;
    final static int KeyMinus   = 2;
    final static int KeyZero    = 3;

    final static int IOCount    = (3*11)+(3*5)+4;

    final static int IOInFirst  =  1;
    final static int IOInLast   = 19;
    final static int IOOutFirst = 20;

    final static int IOInV      = 20;
    final static int IOOutV0    = 21;
    final static int IOOutV1    = 22;

    final static int IOInN      = 31;
    final static int IOOutN0    = 32;
    final static int IOOutN1    = 33;

    final static int IOInP      = 42;
    final static int IOOutP0    = 43;
    final static int IOOutP1    = 44;

    final static int IOOutR10   = 23;
    final static int IOOutR11   = 24;
    final static int IOOutR12   = 25;
    final static int IOOutR13   = 26;
    final static int IOOutR14   = 27;
    final static int IOOutR15   = 28;
    final static int IOOutR16   = 29;
    final static int IOOutR17   = 30;

    final static int IOOutR20   = 34;
    final static int IOOutR21   = 35;
    final static int IOOutR22   = 36;
    final static int IOOutR23   = 37;
    final static int IOOutR24   = 38;
    final static int IOOutR25   = 39;
    final static int IOOutR26   = 40;
    final static int IOOutR27   = 41;

    final static int IOOutR30   = 45;
    final static int IOOutR31   = 46;
    final static int IOOutR32   = 47;
    final static int IOOutR33   = 48;
    final static int IOOutR34   = 49;
    final static int IOOutR35   = 50;
    final static int IOOutR36   = 51;
    final static int IOOutR37   = 52;


    final static IO[] InOut = {
        null,
        (new In("V")), // 1
        (new In("N")),
        (new In("+")),
        (new In("-")),
        (new In("0")),
        (new In("7")),
        (new In("4")),
        (new In("1")),
        (new In("8")),
        (new In("5")),
        (new In("2")),
        (new In("9")),
        (new In("6")),
        (new In("3")),
        (new In("C")),
        (new In("P")),
        (new In("K")),
        (new In("E")),
        (new In("R")),

        (new In("V")),  //20
        (new Out("0")), //21 VO[0]
        (new Out("0")), //22 VO[1]

        (new Out("1")), //23 R1[0]
        (new Out("2")), //24 R1[1]
        (new Out("3")), //25 R1[2]
        (new Out("4")), //26 R1[3]
        (new Out("5")), //27 R1[4]
        (new Out("6")), //28 R1[5]
        (new Out("7")), //29 R1[6]
        (new Out("8")), //30 R1[7]

        (new In("N")),  //31
        (new Out("0")), //32 NO[0]
        (new Out("0")), //33 NO[1]

        (new Out("1")), //34 R2[0]
        (new Out("2")), //35 R2[1]
        (new Out("3")), //36 R2[2]
        (new Out("4")), //37 R2[3]
        (new Out("5")), //38 R2[4]
        (new Out("6")), //39 R2[5]
        (new Out("7")), //40 R2[6]
        (new Out("8")), //41 R2[7]

        (new In("P")),  //42
        (new Out("0")), //43 PO[0]
        (new Out("0")), //44 PO[1]

        (new Out("1")), //45 R3[0]
        (new Out("2")), //46 R3[1]
        (new Out("3")), //47 R3[2]
        (new Out("4")), //48 R3[3]
        (new Out("5")), //49 R3[4]
        (new Out("6")), //50 R3[5]
        (new Out("7")), //51 R3[6]
        (new Out("8"))  //52 R3[7]
    };

    final double ds;
    final Model model;
    volatile Font fontIn, fontOut;
    volatile int x, y, w, h;
    volatile double mx, my, ms;
    volatile Stroke strokeM;


    public DSKY(double ds){
        super();
        this.ds = ds;
        this.model = Model.DSKY;
        Instance = this;
    }


    public void init(Panel panel, HUD hud){

        Rectangle camera = hud.getCamera();
        this.x = (camera.x + camera.width + 5);
        int bottom = (camera.y + camera.height);

        Model model = this.model;
        this.mx = -(model.x);
        this.my = -(model.y);

        this.h = (int)(panel.innerHeight * this.ds)+1;
        this.ms = (this.h / model.height);

        float stroke = (float)(1.0 / this.ms);
        this.h += stroke;
        this.w = (int)((model.width * this.ms)+stroke);
        this.y = (bottom - this.h);
        this.strokeM = new BasicStroke(stroke);
        this.fontIn = Font.Futural.clone(3.1f,2.5f);
        this.fontIn.setVerticalAlignment(Font.VERTICAL_TOP);
        this.fontOut = Font.Futural.clone(1.7f,2.2f);
        this.fontOut.setVerticalAlignment(Font.VERTICAL_TOP);

        Line[] modelL = this.model.lines;
        IO[] io = InOut;

        for (int cc = IOInFirst, count = modelL.length; cc < count; cc++){
            IO inout = io[cc];
            if (cc < IOOutFirst)
                inout.init(modelL[cc].polygon,this.fontIn);
            else
                inout.init(modelL[cc].polygon,this.fontOut);
        }
    }
    public void update(){
    }
    public void draw(Graphics2D ig){
        ig.setColor(Color.green);
        Graphics2D g = (Graphics2D)ig.create(this.x, this.y, this.w, this.h);
        try {
            g.scale(this.ms,this.ms);
            g.translate(this.mx,this.my);
            g.setStroke(this.strokeM);

            Line[] model = this.model.lines;
            IO[] io = InOut;

            for (int cc = 0, count = model.length; cc < count; cc++){

                if (cc < IOOutFirst){

                    Line line = model[cc];
                    Polygon shape = line.polygon;
                    g.draw(shape);
                }
                if (0 < cc){

                    IO inout = io[cc];
                    inout.draw(g);
                }
            }
        }
        finally {
            g.dispose();
        }
    }
}
