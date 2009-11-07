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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;

/**
 * Instantiate as a {@link BackingStore} manager.
 * 
 * @author jdp
 */
public final class Screen
    extends Object
{
    private final static GraphicsDevice Device(GraphicsEnvironment environment, int screen){
        GraphicsDevice devices[] = environment.getScreenDevices();
        if (-1 < screen){
            if (screen < devices.length)
                return devices[screen];
            else
                return devices[devices.length-1];
        }
        else
            return devices[0];
    }


    public final GraphicsEnvironment environment;

    public final GraphicsDevice device;

    public final GraphicsConfiguration configuration;

    public final Rectangle display;

    /**
     * Applet
     */
    public Screen(Applet applet){
        this(applet, applet.getInsets(), applet.getX(), applet.getY(), applet.getWidth(), applet.getHeight());
    }
    private Screen(java.awt.Component comp, Insets ins, int x, int y, int w, int h){
        this(GraphicsEnvironment.getLocalGraphicsEnvironment(),comp.getGraphicsConfiguration(),x,y,w,h);
    }
    private Screen(GraphicsEnvironment environment, GraphicsConfiguration gc, int x, int y, int w, int h){
        this(environment,gc,gc.getDevice(),x,y,w,h);
    }
    private Screen(GraphicsEnvironment environment, GraphicsConfiguration gc, GraphicsDevice device, int x, int y, int w, int h){
        super();
        this.environment = environment;
        this.device = device;
        this.configuration = gc;
        this.display = new Rectangle(x,y,w,h);
        this.init();
    }
    /**
     * Fullscreen, undecorated (no insets).
     */
    public Screen(Window window){
        this(GraphicsEnvironment.getLocalGraphicsEnvironment(),window.getGraphicsConfiguration());
    }
    private Screen(GraphicsEnvironment environment, GraphicsConfiguration gc){
        this(environment,gc,gc.getDevice());
    }
    private Screen(GraphicsEnvironment environment, GraphicsConfiguration gc, GraphicsDevice device){
        super();
        this.environment = environment;
        this.device = device;
        this.configuration = gc;
        this.display = this.configuration.getBounds();
        this.init();
    }


    private void init(){
        /*
         */
        int x = (this.display.x);
        int y = (this.display.y);
        int w = (this.display.width);
        int h = (this.display.height);
        this.display.x      = x;
        this.display.y      = y;
        this.display.width  = w;
        this.display.height = h;
    }
    public final GraphicsEnvironment getEnvironment(){
        return this.environment;
    }
    public final GraphicsDevice getDevice(){
        return this.device;
    }
    public final GraphicsConfiguration getConfiguration(){
        return this.configuration;
    }
    /**
     * Get a type for the container, distinct from the component.
     */
    public BackingStore.Type typeBackingStore(java.awt.Container container){
        if (null == container)
            throw new IllegalArgumentException();

        /*
         * Return GPU if not too buggy.
         */
        if (Java.IsVersion16OrNewer)

            return BackingStore.Type.GPU;

        else if (Os.IsDarwin && Java.IsVersion15OrNewer){
            if (Os.Version.equalsOrNewer(10,5)){
                if (container instanceof java.applet.Applet){
                    Object peer = container.getPeer();
                    if (null != peer && "apple.awt.MyCPanel".equals(peer.getClass().getName()))
                        return BackingStore.Type.GPU;
                    else
                        return BackingStore.Type.CPU;
                }
                else
                    return BackingStore.Type.CPU;
            }
            else
                return BackingStore.Type.GPU;
        }
        else
            return BackingStore.Type.CPU;
    }
    public BackingStore getBackingStore(java.awt.Container container, BackingStore.J2D component){
        BackingStore.Type type = this.typeBackingStore(container);
        switch (type){
        case CPU:
            return (new BackingStore.CPU(component));
        case GPU:
            return (new BackingStore.GPU(component));
        default:
            throw new IllegalStateException();
        }
    }
}
