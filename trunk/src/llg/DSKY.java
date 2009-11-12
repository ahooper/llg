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
public class DSKY {

    static DSKY Instance;

    public static void R1(String format, Object... args){
        Set(IOOutR10,8,String.format(format,args));
    }
    public static String R1(){
        return Get(IOOutR10,8);
    }
    public static void R2(String format, Object... args){
        Set(IOOutR20,8,String.format(format,args));
    }
    public static String R2(){
        return Get(IOOutR20,8);
    }
    public static void R3(String format, Object... args){
        Set(IOOutR30,8,String.format(format,args));
    }
    public static String R3(){
        return Get(IOOutR30,8);
    }
    public static void Verb(String format, Object... args){
        Set(IOOutV0,2,String.format(format,args));
    }
    public static String Verb(){
        return Get(IOOutV0,2);
    }
    public static void Noun(String format, Object... args){
        Set(IOOutN0,2,String.format(format,args));
    }
    public static String Noun(){
        return Get(IOOutN0,2);
    }
    public static void Program(String format, Object... args){
        Set(IOOutP0,2,String.format(format,args));
    }
    public static String Program(){
        return Get(IOOutP0,2);
    }
    private static String Get(int base, int count){
        StringBuilder data = new StringBuilder();
        for (int cc = 0; cc < count; cc++){
            int idx = (base+cc);
            data.append(InOut[idx].toString());
        }
        return data.toString();
    }
    private static void Set(int base, int count, String data){
        int len = data.length();
        int ofs = (count - len);
        if (0 > ofs){
            ofs = (2+ofs);
            if (0 > ofs)
                ofs = 2;
        }
        for (int cc = 0; cc < count; cc++){
            int idx = (base+cc);
            int dc = (cc-ofs);
            if (0 > dc || dc >= len)
                InOut[idx].update("0");
            else 
                InOut[idx].update(String.valueOf(data.charAt(dc)));
        }
    }

    public static class Input {

        protected final int base;

        public volatile char[] string;

        public Input(Input copy){
            super();
            this.base = copy.base;
            this.string = copy.string;
        }
        public Input(int base){
            super();
            switch(base){
            case IOOutV0:
            case IOOutN0:
            case IOOutP0:
            case IOOutR10:
            case IOOutR20:
            case IOOutR30:
                this.base = base;
                this.flush();
                return;
            default:
                throw new IllegalArgumentException(String.valueOf(base));
            }
        }

        public final void add(char ch){
            char[] string = this.string;
            if (null == string)
                this.string = new char[]{ch};
            else {
                int len = string.length;
                char[] copier = new char[len+1];
                System.arraycopy(string,0,copier,0,len);
                copier[len] = ch;
                this.string = copier;
            }
            this.flush();
        }
        public final void flush(){
            char[] string = this.string;
            switch(this.base){
            case IOOutV0:
                Verb("%s",((null == string)?(""):(new String(this.string))));
                return;
            case IOOutN0:
                Noun("%s",((null == string)?(""):(new String(this.string))));
                return;
            case IOOutP0:
                Program("%s",((null == string)?(""):(new String(this.string))));
                return;
            case IOOutR10:
                R1("%s",((null == string)?(""):(new String(this.string))));
                return;
            case IOOutR20:
                R2("%s",((null == string)?(""):(new String(this.string))));
                return;
            case IOOutR30:
                R3("%s",((null == string)?(""):(new String(this.string))));
                return;
            }
        }
        public final boolean full(){
            switch(this.base){
            case IOOutV0:
            case IOOutN0:
            case IOOutP0:
                return (2 == this.string.length);
            case IOOutR10:
            case IOOutR20:
            case IOOutR30:
                return (8 == this.string.length);
            default:
                throw new IllegalStateException();
            }
        }
    }

    protected static abstract class IO {
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
            public String toString(){
                return this.string;
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
                this.hi = true;
                this.string = string;
                if (null != string && null != this.modelBounds)
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
            public String toString(){
                return this.string;
            }
        }

        protected volatile Font font;
        protected volatile Rectangle stringBounds, modelBounds;
        protected volatile boolean hi;

        protected void update(String string){
            this.hi = true;
        }
        protected abstract void init(Polygon shape, Font font);

        protected abstract void draw(Graphics2D g);
    }


    private final static int Outline    = 0;
    private final static int KeyPlus    = 1;
    private final static int KeyMinus   = 2;
    private final static int KeyZero    = 3;

    private final static int IOCount    = (3*11)+(3*5)+4;

    private final static int IOInFirst  =  1;
    private final static int IOInLast   = 19;
    private final static int IOOutFirst = 20;

    private final static int IOInV      = 20;
    private final static int IOOutV0    = 21;
    private final static int IOInN      = 31;
    private final static int IOOutN0    = 32;
    private final static int IOInP      = 42;
    private final static int IOOutP0    = 43;
    private final static int IOOutR10   = 23;
    private final static int IOOutR20   = 34;
    private final static int IOOutR30   = 45;


    private final static IO[] InOut = {
        null,
        (new IO.In("V")), // 1
        (new IO.In("N")),
        (new IO.In("+")),
        (new IO.In("-")),
        (new IO.In("0")),
        (new IO.In("7")),
        (new IO.In("4")),
        (new IO.In("1")),
        (new IO.In("8")),
        (new IO.In("5")),
        (new IO.In("2")),
        (new IO.In("9")),
        (new IO.In("6")),
        (new IO.In("3")),
        (new IO.In("C")),
        (new IO.In("P")),
        (new IO.In("K")),
        (new IO.In("E")),
        (new IO.In("R")),

        (new IO.In("V")),  //20
        (new IO.Out("-")), //21 VO[0]
        (new IO.Out("-")), //22 VO[1]

        (new IO.Out("-")), //23 R1[0]
        (new IO.Out("-")), //24 R1[1]
        (new IO.Out("-")), //25 R1[2]
        (new IO.Out("-")), //26 R1[3]
        (new IO.Out("-")), //27 R1[4]
        (new IO.Out("-")), //28 R1[5]
        (new IO.Out("-")), //29 R1[6]
        (new IO.Out("-")), //30 R1[7]

        (new IO.In("N")),  //31
        (new IO.Out("-")), //32 NO[0]
        (new IO.Out("-")), //33 NO[1]

        (new IO.Out("-")), //34 R2[0]
        (new IO.Out("-")), //35 R2[1]
        (new IO.Out("-")), //36 R2[2]
        (new IO.Out("-")), //37 R2[3]
        (new IO.Out("-")), //38 R2[4]
        (new IO.Out("-")), //39 R2[5]
        (new IO.Out("-")), //40 R2[6]
        (new IO.Out("-")), //41 R2[7]

        (new IO.In("P")),  //42
        (new IO.Out("-")), //43 PO[0]
        (new IO.Out("-")), //44 PO[1]

        (new IO.Out("-")), //45 R3[0]
        (new IO.Out("-")), //46 R3[1]
        (new IO.Out("-")), //47 R3[2]
        (new IO.Out("-")), //48 R3[3]
        (new IO.Out("-")), //49 R3[4]
        (new IO.Out("-")), //50 R3[5]
        (new IO.Out("-")), //51 R3[6]
        (new IO.Out("-"))  //52 R3[7]
    };

    final double ds;
    final Model model;
    final boolean visible;
    volatile Font fontIn, fontOut;
    volatile int x, y, w, h, right;
    volatile double mx, my, ms;
    volatile Stroke strokeM;
    volatile Input input;

    public DSKY(double ds){
        super();
        this.ds = ds;
        this.model = Model.DSKY;
        Instance = this;
        this.visible = (500 < Math.min(Screen.Current.width,Screen.Current.height));
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

        this.right = this.x + this.w;
        if (this.visible){
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
    }
    public void update(){
    }
    public void draw(Graphics2D ig){
        if (this.visible){
            ig.setColor(Color.green);
            Graphics2D g = (Graphics2D)ig.create(this.x, this.y, this.w, this.h);
            try {
                g.scale(this.ms,this.ms);
                g.translate(this.mx,this.my);
                g.setStroke(this.strokeM);

                Line[] model = this.model.lines;
                IO[] io = InOut;
                IO inout;

                for (int cc = 0, count = model.length; cc < count; cc++){
                    if (0 < cc){
                        inout = io[cc];
                        if (inout.hi){
                            g.fill(model[cc].polygon);
                            inout.hi = false;
                        }
                        else if (cc < IOOutFirst){
                            g.draw(model[cc].polygon);
                            inout.draw(g);
                        }
                        else
                            inout.draw(g);
                    }
                    else {
                        g.draw(model[0].polygon);
                    }
                }
            }
            finally {
                g.dispose();
            }
        }
    }
    public void keyDown(int key){
        switch (key){
        case 'c':
        case 'C':
            return;
        case 'e':
        case 'E':
        case Event.ENTER:
            this.execute();
            return;
        case 'k':
        case 'K':
            return;
        case 'v':
        case 'V':
            this.input = new Input(IOOutV0);
            return;
        case 'n':
        case 'N':
            this.input = new Input(IOOutN0);
            return;
        case 'p':
        case 'P':
            this.input = new Input(IOOutP0);
            return;
        case 'r':
        case 'R':
            this.reset();
            return;
        default:
            char ch = (char)key;
            if (Character.isLetterOrDigit(ch)){
                Input input = this.input;
                if (null != input){
                    input.add( (char)key);
                }
            }
        }
    }
    private void execute(){
        llg.dsky.Program.Cache.Execute(this);
    }
    public void error(){
        this.reset();
    }
    public DSKY.Input getInput(){
        return this.input;
    }
    public void setInput(DSKY.Input input){
        this.input = input;
    }
    public void reset(){
        Verb("%s","");
        Noun("%s","");
        Program("%s","");
        R1("%s","");
        R2("%s","");
        R3("%s","");
    }
}
