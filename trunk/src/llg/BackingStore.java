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
     * Subclass of AWT Container. 
     */
    public interface Container {

    }
    /**
     * Subclass of AWT Component.
     */
    public interface Component 
        extends ImageObserver
    {


        public void paint(Graphics g);

        public void paintIn(Graphics2D g);

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


        public CPU(Component component){
            super(component);

            this.reinit();
        }

        public Type getType(){
            return TYPE;
        }
        public void blit(){

            Graphics2D g = (Graphics2D)this.component.getGraphics();
            try {
                this.blitIn(g);
            }
            finally {
                g.dispose();
            }
        }
        public void paint(int x, int y, int w, int h){

            Component component = this.component;
            boolean clip = (0 < w && 0 < h);
            Graphics2D g = (Graphics2D)component.getGraphics();
            try {
                if (clip){
                    g.clipRect(x,y,w,h);
                }
                component.paintIn(g);
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


        public GPU(Component component){
            super(component);

            this.reinit();
        }


        public Type getType(){
            return TYPE;
        }
        public boolean reinit(){
            if (super.reinit()){
                Component component = this.component;
                component.createBufferStrategy(2);
                this.bufferStrategy = component.getBufferStrategy();
                return (null != this.bufferStrategy);
            }
            else
                return false;
        }
        public void blit(){

            BufferStrategy bufferStrategy = this.bufferStrategy;
            Graphics2D g;
            do {
                do {
                    g = (Graphics2D)bufferStrategy.getDrawGraphics();
                    if (g != null){
                        try {
                            this.blitIn(g);
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
        public void paint(int x, int y, int w, int h){

            Component component = this.component;
            boolean clip = (0 < w && 0 < h);
            BufferStrategy bufferStrategy = this.bufferStrategy;
            if (null != bufferStrategy){
                Graphics2D g;
                do {
                    do {
                        g = (Graphics2D)bufferStrategy.getDrawGraphics();
                        if (g != null){
                            try {
                                if (clip){
                                    g.clipRect(x,y,w,h);
                                }
                                component.paintIn(g);
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
        public void blit(Graphics g){
            this.blit();
        }
    }


    private final static long lt32 = 0x7fffffffffffff00L;


    protected final Component component;

    private BufferedImage backing;


    protected BackingStore(Component component){
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
    public final Component getComponent(){
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
    public final boolean hasComponentResized(){
        Component component = this.component;
        return (this.width != component.getWidth() ||
                this.height != component.getHeight());
    }
    public final boolean hasNotComponentResized(){
        Component component = this.component;
        return (this.width == component.getWidth() &&
                this.height == component.getHeight());
    }
    public boolean reinit(){
        BufferedImage backing = this.backing;
        if (null == backing || this.hasComponentResized()){
            if (null != backing)
                backing.flush();
            Component component = this.component;
            this.width = component.getWidth();
            this.height = component.getHeight();
            if (0 < this.width && 0 < this.height){
                this.backing = new BufferedImage(this.width,this.height,BufferedImage.TYPE_INT_ARGB);//_PRE
                return true;
            }
        }
        return false;
    }
    public final Graphics2D createGraphics(){
        BufferedImage backing = this.backing;
        if (null != backing){
            return backing.createGraphics();
        }
        else
            throw new IllegalStateException();
    }
    public final Graphics2D createGraphics(int x, int y, int w, int h){
        BufferedImage backing = this.backing;
        if (null != backing){
            Graphics2D g = backing.createGraphics();
            g.clipRect(x,y,w,h);
            return g;
        }
        else
            throw new IllegalStateException();
    }
    /**
     * Write CPU buffer to graphics
     * 
     * Animator entry wants to grab the AWT lock.
     */
    public abstract void blit();

    /**
     * Call component paint with graphics
     * 
     * Animator entry wants to grab the AWT lock.
     */
    public abstract void paint(int x, int y, int h, int w);

    public void paint(){
        this.paint(0,0,0,0);
    }
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
    public void blit(Graphics g){
        this.blitIn(g);
    }
    public void blit(Graphics g, ImageObserver observer){
        this.blitIn(g,observer);
    }
}
