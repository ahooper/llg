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

import java.awt.BufferCapabilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ImageCapabilities;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

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
        public Object getTreeLock();

        public void paint(Graphics2D g);

        public int getWidth();

        public int getHeight();

        public void setSize(java.awt.Dimension d);

        public boolean isValid();

        public Graphics getGraphics();

        public Toolkit getToolkit();

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

            this.reinit();
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


    private final static long lt32 = 0x7fffffffffffff00L;


    protected final J2D component;

    private volatile BufferedImage backing;


    protected BackingStore(J2D component){
        super();
        if (null != component){
            this.component = component;
            this.width = component.getWidth();
            this.height = component.getHeight();
        }
        else
            throw new IllegalArgumentException();
    }


    public abstract Type getType();

    public boolean isTypeCPU(){
        return (Type.CPU == this.getType());
    }
    public boolean isTypeGPU(){
        return (Type.GPU == this.getType());
    }
    public final J2D getComponent(){
        return this.component;
    }
    public final Toolkit getToolkit(){
        return this.component.getToolkit();
    }
    public boolean isReady(){
        if (null != this.backing)
            return this.hasNotComponentResized();
        else
            return false;
    }
    public final boolean isNotReady(){
        return (!this.isReady());
    }
    public final BufferedImage getBacking(){
        return this.backing;
    }
    public void flush(){
        BufferedImage backing = this.backing;
        if (null != backing)
            backing.flush();
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
    public boolean reinit(){
        BufferedImage backing = this.backing;
        if (null == backing || this.hasComponentResized()){
            if (null != backing)
                backing.flush();
            J2D component = this.component;
            this.width = component.getWidth();
            this.height = component.getHeight();
            if (0 < this.width && 0 < this.height){
                this.backing = new BufferedImage(this.width,this.height,BufferedImage.TYPE_INT_ARGB);//_PRE
                return true;
            }
        }
        return false;
    }
    public abstract void paint();

    protected final void blitIn(Graphics g){
        BufferedImage backing = this.backing;
        if (null != backing)
            g.drawImage(backing,0,0,this.component);
    }
    protected final void blitIn(Graphics g, ImageObserver observer){
        BufferedImage backing = this.backing;
        if (null != backing)
            g.drawImage(backing,0,0,observer);
    }
}
