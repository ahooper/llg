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
import java.awt.Frame;
import java.awt.event.WindowEvent;

/**
 * Fullscreen panel driver and "main" command line program.
 * 
 * @author jdp
 */
public final class Fullscreen 
    extends java.awt.Dialog
{
    private final static long EVENT_MASK =  (AWTEvent.WINDOW_EVENT_MASK
                                           | AWTEvent.WINDOW_STATE_EVENT_MASK
                                           | AWTEvent.COMPONENT_EVENT_MASK
                                           | AWTEvent.FOCUS_EVENT_MASK
                                           | AWTEvent.MOUSE_EVENT_MASK
                                           | AWTEvent.MOUSE_MOTION_EVENT_MASK
                                           | AWTEvent.MOUSE_WHEEL_EVENT_MASK
                                           | AWTEvent.KEY_EVENT_MASK);

    private final static String Title = "Lunar Lander";


    protected final Screen screen;

    protected final Panel panel;


    public Fullscreen(Frame frame, Screen screen, Panel panel){
        super(frame,Title,false,screen.getConfiguration());
        this.screen = screen;
        this.enableEvents(EVENT_MASK);
        this.setFocusTraversalKeysEnabled(false);
        this.setUndecorated(true);
        this.setLayout(new LM());

        this.panel = panel;
        panel.setSize(this.getSize());
        this.add(panel);
        /*
         * in the small.. leaving this here..
         */
        java.awt.Rectangle display = screen.display;
        this.setLocation(display.x, display.y);
        this.setSize(display.width, display.height);
        this.setVisible(true);
    }


    public void close(){
        this.panel.stop();

        try {
            Frame parent = (Frame)this.getParent();
            parent.hide();
            this.hide();
            parent.dispose();
            this.dispose();
            Thread.sleep(120);
        }
        catch (Exception x){}
        System.exit(0);
    }
    @Override
    public void processWindowEvent(WindowEvent event) {
        super.processWindowEvent(event);

        switch(event.getID()) {
        case WindowEvent.WINDOW_OPENED:

            this.panel.init(this.screen);

            this.panel.start();

            break;

        case WindowEvent.WINDOW_CLOSING:

            this.panel.stop();

            new Disposer((java.awt.Frame)this.getParent()).start();

            try {
                Thread.sleep(150);
            }
            catch (InterruptedException exc){
            }
            System.exit(0);

            break;

        case WindowEvent.WINDOW_CLOSED:
            System.exit(0);
            break;
        }
    }


    public static void main(String[] argv){
        Options opts = new Options(argv);
        Frame frame = new Frame(Title);

        if (opts.hasString("--model"))
            new Fullscreen(frame, new Screen(frame), new Modeller());
        else
            new Fullscreen(frame, new Screen(frame), new Game());
    }
}