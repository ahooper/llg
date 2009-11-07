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
import java.awt.event.ComponentEvent;

/**
 * Applet panel driver.
 * 
 * @author jdp
 */
public final class Applet 
    extends java.applet.Applet
{
    private final static long EVENT_MASK =  (AWTEvent.COMPONENT_EVENT_MASK
                                           | AWTEvent.FOCUS_EVENT_MASK
                                           | AWTEvent.MOUSE_EVENT_MASK
                                           | AWTEvent.MOUSE_MOTION_EVENT_MASK
                                           | AWTEvent.MOUSE_WHEEL_EVENT_MASK
                                           | AWTEvent.KEY_EVENT_MASK);

    private final static String Title = "Lunar Lander";


    private Screen screen;

    private Panel panel;


    public Applet(){
        super();
        this.enableEvents(EVENT_MASK);
        this.setFocusTraversalKeysEnabled(false);

        this.setLayout(new LM());
    }


    public void init(){

        if (0 == this.countComponents()){
            Screen screen = new Screen(this);
            Panel panel = new Game(screen);
            this.screen = screen;
            this.panel = panel;
            panel.setSize(this.getSize());
            this.add(panel);
        }

        this.setVisible(true);

        this.panel.init(this.screen);
    }
    public void start(){
        this.panel.start();
    }
    public void stop(){
        this.panel.stop();

        new Aut.Shutdown().start();
    }
    public void destroy(){
        this.screen = null;
        this.panel = null;
    }
    public String getAppletInfo(){
        return Version.Applet;
    }
    @Override
    protected void processComponentEvent(ComponentEvent e) {

        switch(e.getID()){
        case ComponentEvent.COMPONENT_RESIZED:
        case ComponentEvent.COMPONENT_MOVED:
            if (null != this.panel){
                Screen screen = new Screen(this);
                this.panel.init(screen);
            }
            break;
        case ComponentEvent.COMPONENT_SHOWN:
            if (null != this.panel){
                this.panel.start();
            }
            break;
        case ComponentEvent.COMPONENT_HIDDEN:
            if (null != this.panel){
                this.panel.stop();
            }
            break;
        }
    }
}
