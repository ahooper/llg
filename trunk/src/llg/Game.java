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

import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;


/**
 * This is the classic Lunar Lander Game.
 * 
 * @version     1.3, 01 Jan 1999
 * @version 2.0,  4 Nov 2009
 * @author John Donohue
 * @author John Pritchard
 */
public final class Game
    extends Panel
{
    static {
        Luna.SInit();
    }


    static Game Instance;

    private World world;


    public Game(Screen screen){
        super(screen);

        this.ds(2.0);

        Instance = this;
        
        this.hud = new FlightDisplay(this);
    }


    public void scored(int points){
        if (0 != points){
            this.hud.scored(points);
            this.message("Scored "+Integer.toString(points) + " points.");
        }
    }
    public void init(Screen screen){

        super.init(screen);

        this.newGame();
    }
    public void tick(){

        Lander.Current.tick();

        Camera t = Camera.Current;
        Camera c = Camera.View(this,Lander.Current);

        if (t != c){
            this.ds(c.scale);
            this.dx(c.dx);
            this.dy(c.dy);
            HUD.Status(c.toString());
        }
        else
            this.dx(c.dx);

        this.hud.update();
    }
    /**
     * Draw World 
     */
    public void draw(Graphics2D bg){
        Drawable drawable;

        bg.setColor(this.getForeground());

        drawable = Luna.Instance;
        if (null != drawable)
            drawable.draw(bg);

        bg.setColor(this.getForeground());

        drawable = Lander.Current;
        if (null != drawable)
            drawable.draw(bg);

        bg.setColor(this.getForeground());

        drawable = this.world;
        if (null != drawable)
            drawable.draw(bg);

    }
    public boolean keyDown(Event evt, int key){

        this.messagesClear();

        switch (key){

        case Event.ESCAPE:
            this.newGame();
            return true;

        case Event.F1:
        case Event.F2:
        case Event.F3:
        case Event.F4:
        case Event.F5:
        case Event.F6:
        case Event.F7:
        case Event.F8:
        case Event.F9:
        case Event.F10:
        case Event.F11:
        case Event.F12:
            this.hud.toggle();
            return true;

        case Event.INSERT:
        case Event.DELETE:
        case Event.HOME:
        case Event.END:
        case Event.PGUP:
        case Event.PGDN:

            if (null != this.world)
                this.world = null;
            else
                this.world = new World(this);

            return true;

        default:
            Lander.Current.keyDown(evt, key);
            return true;
        }

    }
    public void newGame(){

        Luna.Instance.reset();

        Lander.fuel = 1f;

        new FlyingLander(Luna.Instance.landing());

        this.messagesClear();
        this.message("Lunar Lander!");
        this.message("Arrow Keys NAV");
        this.message("Escape Key NEW");
        this.message("Space Key BUMP");

        this.hud.init(true);
    }
    public void newFlight(){

        if (!Lander.Current.isLanded())
            new FlyingLander(Luna.Instance.landing());

        this.hud.init(false);
    }
    public void landerCrash(){
        this.messagesClear();

        if (0f < Lander.fuel){
            this.message("Crash!");
        }
        else {
            this.message("Game over!");
        }
    }
    public void landerLanded(){

        if (0f >= Lander.fuel)
            this.message("Game over!");
    }
}
