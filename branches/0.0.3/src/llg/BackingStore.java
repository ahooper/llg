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

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.image.ImageObserver;

/**
 * @author jdp
 */
public abstract class BackingStore 
    extends java.awt.Dimension
{
    public enum Type {
        GPU,
        CPU
    };

    /**
     * Subclass of AWT Component.
     */
    public interface J2D
        extends ImageObserver
    {

        public void paint(Graphics2D g);

        public int getWidth();

        public int getHeight();

        public Graphics getGraphics();

        public void createBufferStrategy(int nb);

        public BufferStrategy getBufferStrategy();

    }

    /**
     * Blit to host graphics
     */
    public final static class CPU
        extends BackingStore
    {
        public final static Type TYPE = Type.CPU;


        public CPU(J2D component){
            super(component);
        }

        public Type getType(){
            return TYPE;
        }
        public void paint(){

            J2D component = this.component;

            Graphics2D g = (Graphics2D)component.getGraphics();
            try {
                component.paint(g);
            }
            finally {
                g.dispose();
            }
        }
    }
    /**
     * Blit to native graphics buffers
     */
    public final static class GPU
        extends BackingStore
    {
        public final static Type TYPE = Type.GPU;


        private BufferStrategy bufferStrategy;


        public GPU(J2D component){
            super(component);

            this.reinit();
        }


        public Type getType(){
            return TYPE;
        }
        public boolean reinit(){
            if (super.reinit()){
                J2D component = this.component;
                component.createBufferStrategy(2);
                this.bufferStrategy = component.getBufferStrategy();
                return (null != this.bufferStrategy);
            }
            else
                return false;
        }
        public void paint(){

            J2D component = this.component;

            BufferStrategy bufferStrategy = this.bufferStrategy;
            if (null != bufferStrategy){
                Graphics2D g;
                do {
                    do {
                        g = (Graphics2D)bufferStrategy.getDrawGraphics();
                        if (g != null){
                            try {
                                component.paint(g);
                            }
                            finally {
                                g.dispose();
                            }
                        }
                    }
                    while (bufferStrategy.contentsRestored());
                    bufferStrategy.show();
                }
                while (bufferStrategy.contentsLost());
            }
            else
                throw new IllegalStateException(component.toString());
        }
    }



    protected final J2D component;

    protected volatile BufferedImage background;


    protected BackingStore(J2D component){
        super();
        if (null != component)
            this.component = component;
        else
            throw new IllegalArgumentException();
    }


    public abstract Type getType();

    public abstract void paint();

    public boolean reinit(){
        J2D component = this.component;
        int w = component.getWidth();
        int h = component.getHeight();
        if (w != this.width || h != this.height){
            this.width = w;
            this.height = h;
            return true;
        }
        else
            return false;
    }
    public boolean isTypeCPU(){
        return (Type.CPU == this.getType());
    }
    public boolean isTypeGPU(){
        return (Type.GPU == this.getType());
    }
    public final boolean hasComponentResized(){
        J2D component = this.component;
        return (this.width != component.getWidth() ||
                this.height != component.getHeight());
    }
    public final boolean hasNotComponentResized(){
        J2D component = this.component;
        return (this.width == component.getWidth() &&
                this.height == component.getHeight());
    }
    public BufferedImage getBackgroundBuffer1(){
        if (null == this.background || this.width != this.background.getWidth(this.component) ||
            this.height != this.background.getHeight(this.component)){

            this.background = new BufferedImage(this.width,this.height,BufferedImage.TYPE_INT_ARGB);
        }
        return this.background;
    }
    public BufferedImage getBackgroundBuffer2(){

        return this.background;
    }
}
