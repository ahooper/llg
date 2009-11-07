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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * <h3>Coordinate spaces</h3>
 * 
 * The world coordinate space is in the game, the coordinate spaces of
 * the models and features of the game.
 * 
 * The lander and surface operate in world space.
 * 
 * The panel and hud operate in device space.
 * 
 * The device coordinate space is in the graphics, on the screen, in
 * pixels.
 * 
 * The relation between world and device is the scale defined as a
 * field of this class.
 * 
 * @author jdp
 */
public abstract class Panel
    extends java.awt.Canvas
    implements BackingStore.J2D
{

    static Panel Instance;

    private Animator animator;
    private Ticker ticker;

    protected int width;
    protected int height;
    protected int padding;
    protected int innerWidth;
    protected int innerHeight;
    protected int right;
    protected int left;
    protected int top;
    protected int bottom;

    private volatile Rectangle2D.Double viewport;

    private volatile double dx, dy, scale;

    private volatile BackingStore graphics;

    protected HUD hud;


    public Panel(Screen screen){
        super(screen.configuration);
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        Instance = this;
    }


    public void close(){
        java.awt.Container parent = this.getParent();
        if (parent instanceof Fullscreen)
            ((Fullscreen)parent).close();
        else if (this instanceof Game){
            Game game = (Game)this;
            game.newGame();
        }
    }
    public final void message(String m){
        this.hud.message(m);
    }
    public final void messagesClear(){
        this.hud.clear();
    }
    public final void dx(double dx){
        if (dx != this.dx){
            this.viewport = null;
            this.dx = dx;
        }
    }
    public final void dy(double dy){
        if (dy != this.dy){
            this.viewport = null;
            this.dy = dy;
        }
    }
    public double ds(){
        return this.scale;
    }
    public final void ds(double scale){
        if (scale != this.scale){
            this.viewport = null;
            this.scale = scale;
        }
    }
    /**
     * @return The viewport in world coordinates.
     */
    public Rectangle2D.Double toWorld(){

        Rectangle2D.Double viewport = this.viewport;
        if (null == viewport){
            double x = (-this.dx / this.scale);
            double y = (-this.dy / this.scale);
            double w = (this.width / this.scale);
            double h = (this.height / this.scale);
            viewport = new Rectangle2D.Double(x,y,w,h);
            this.viewport = viewport;
        }
        return viewport;
    }
    public void init(Screen screen){

        Rectangle display = screen.display;

        this.width  = (display.width);
        this.height = (display.height);

        this.padding = (int)(0.1f * (float)Math.min(display.width, display.height));

        int p2 = (this.padding * 2);
        this.innerWidth = this.width - p2;
        this.innerHeight = this.height - p2;

        this.left = (display.x + this.padding);
        this.right = (display.width - this.padding);
        this.top = (display.y + this.padding);
        this.bottom = (display.height - this.padding);

        if (null == this.graphics)
            this.graphics = screen.getBackingStore(this.getParent(),this);
    }

    public final void start(){
        this.viewport = null;
        this.requestFocus();
        this.dx = 0.0;
        this.dy = 0.0;
        this.scale = 1.0;

        if (null == this.animator){
            this.animator = new Animator(this.graphics);
            this.animator.start();
        }
        if (null == this.ticker){
            this.ticker = new Ticker(this);
            this.ticker.start();
        }
    }
    public final void stop(){
        Animator animator = this.animator;
        if (null != animator){
            this.animator = null;
            animator.halt();
        }
        Ticker ticker = this.ticker;
        if (null != ticker){
            this.ticker = null;
            ticker.halt();
        }
        /*
         * Everything that calls this has a need to let these threads
         * expire before continuing.
         */
        try {
            Thread.sleep(200);
        }
        catch (InterruptedException mustIgnore){
            /*
             * Ignored in shutdown process
             */
        }
    }
    public void tick(){
        this.hud.update();
    }
    public final void repaint(){
        Animator animator = this.animator;
        if (null != animator)
            animator.repaint();
    }
    public final void update(Graphics g){
        Animator animator = this.animator;
        if (null != animator)
            animator.repaint();
    }
    public final void paint(Graphics g){
        Animator animator = this.animator;
        if (null != animator)
            animator.repaint();
    }
    public final void paint(Graphics2D g){

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderAntialiasing);
        /*
         * Clear
         */
        g.setColor(this.getBackground());
        Rectangle bounds = this.bounds();

        g.fillRect(0, 0, bounds.width, bounds.height);

        g.setColor(this.getForeground());
        /*
         * Draw world
         */
        Graphics2D gm = (Graphics2D)g.create();
        try {
            gm.translate(this.dx,this.dy);
            gm.scale(this.scale,this.scale);

            this.draw(gm);
        }
        finally {
            gm.dispose();
        }
        /*
         * Foreground
         */
        g.setColor(this.getForeground());

        Drawable drawable = this.hud;
        if (null != drawable)
            drawable.draw(g);
    }
    /**
     * Draw world
     */
    public void draw(Graphics2D g){
    }
    public boolean keyDown(Event evt, int key){

        return true;
    }
    public boolean mouseDown(Event evt, int x, int y){

        return false;
    }
    @Override
    protected void processComponentEvent(ComponentEvent e) {

        switch(e.getID()){
        case ComponentEvent.COMPONENT_RESIZED:
        case ComponentEvent.COMPONENT_MOVED:
            
            java.awt.Container parent = this.getParent();
            if (parent instanceof Fullscreen){
                Screen screen = new Screen((Fullscreen)parent);
                this.init(screen);
            }
            else if (parent instanceof Applet){
                Screen screen = new Screen((Applet)parent);
                this.init(screen);
            }
            break;
        case ComponentEvent.COMPONENT_SHOWN:

            this.start();
            break;
        case ComponentEvent.COMPONENT_HIDDEN:

            this.stop();
            break;
        }
    }

    private final static Object RenderAntialiasing = RenderingHints.VALUE_ANTIALIAS_ON;
}
