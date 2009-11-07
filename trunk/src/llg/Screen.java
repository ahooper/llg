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

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.util.HashSet;
import java.util.Set;

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

    private final Set<String> fontFamilies = new HashSet<String>();

    private Robot robot;


    /**
     * Get the screen from a component
     */
    public Screen(java.awt.Component comp){
        this(GraphicsEnvironment.getLocalGraphicsEnvironment(),comp.getGraphicsConfiguration());
    }
    public Screen(GraphicsEnvironment environment, GraphicsConfiguration gc){
        this(environment,gc,gc.getDevice());
    }
    public Screen(GraphicsEnvironment environment, GraphicsConfiguration gc, GraphicsDevice device){
        super();
        this.environment = environment;
        this.device = device;
        this.configuration = gc;
        this.display = this.configuration.getBounds();
        this.init();
    }
    /**
     * Get the default screen 
     */
    public Screen(){
        this(GraphicsEnvironment.getLocalGraphicsEnvironment());
    }
    public Screen(GraphicsEnvironment environment){
        this(environment,environment.getDefaultScreenDevice());
    }
    /**
     * Open a screen by index from zero on multihead host systems.
     */
    public Screen(int device){
        this(GraphicsEnvironment.getLocalGraphicsEnvironment(),device);
    }
    public Screen(GraphicsEnvironment environment, int device){
        this(environment,Screen.Device(environment,device));
    }
    public Screen(GraphicsEnvironment environment, GraphicsDevice device){
        super();
        this.environment = environment;
        this.device = device;
        this.configuration = this.device.getDefaultConfiguration();
        this.display = this.configuration.getBounds();
        this.init();
    }

    private void init(){
        /*
         */
        int x = (this.display.x);
        int y = (this.display.y);
        int w = (this.display.width - this.display.x);
        int h = (this.display.height - this.display.y);
        this.display.x      = x;
        this.display.y      = y;
        this.display.width  = w;
        this.display.height = h;
        /*
         */
        String[] fontnames = this.environment.getAvailableFontFamilyNames();
        if (null != fontnames){
            Set<String> fontFamilies = this.fontFamilies;
            for (int cc = 0, count = fontnames.length; cc < count; cc++){
                fontFamilies.add(fontnames[cc]);
            }
        }
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
    public boolean hasFontFamilyName(String name){
        return (this.fontFamilies.contains(name));
    }
    public Set<String> getFontFamilyNames(){
        return this.fontFamilies;
    }
    public String[] getAvailableFontFamilyNames(){
        return this.environment.getAvailableFontFamilyNames();
    }
    /**
     * Get a type for the container, distinct from the component.
     * @see BackingStore$Container$Requires
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
    public BackingStore getBackingStore(java.awt.Container container, BackingStore.Component component){
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
    /**
     * @return An AWT Robot for this graphics device.
     */
    public Robot getRobot()
        throws java.awt.AWTException
    {
        Robot robot = this.robot;
        if (null == robot){
            robot = new Robot(this.device);
            this.robot = robot;
        }
        return robot;
    }
}
