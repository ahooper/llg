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
/*
 * Copyright (C) 1999 John Donohue.  All rights reserved.
 * 
 * Used with permission.
 */
package llg;

import java.awt.Event;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public final class FlyingLander 
    extends Lander
{
    private final static String StatusX = "X %3.1f, %3.1f %3.1f, %3.1f";


    volatile long saveTimeMainRocketOn;



    FlyingLander(Surface landto){
        super((landto.midX-Model.Lander.width2),(landto.y1-Model.Lander.height2-2.0));
        this.intro = true;
        this.motion(0.0,0.001);
        Game.Instance.messagesClear();
        Gravity();
        rotate();
        tick();
    }
    FlyingLander (Lander lander){
        super(lander);
        Game.Instance.messagesClear();
        Gravity();
        rotate();
        tick();
    }


    public boolean isFlying(){
        return true;
    }
    public void tick(){
        /*
         * Physics
         */
        this.ty += gravityAcceleration;

        if (mainRocketOperating){

            this.thrust(RocketAcceleration);

            fuel -= RocketFuelConsumption;
            if ( fuel < 0)
                mainRocketOff();
        }
        super.tick();


        Surface over = Luna.Instance.seek(this);
        if (1.0 > over.distance){
            /*
             * Landing Detect
             */
            if (over.pad && (0 == attitude) &&
                (Math.abs(this.ty) < maxSafeLandingSpeed) &&
                (Math.abs(this.tx) < maxSafeLandingSpeed))
            {
                if (!this.intro){
                    fuel = over.winFuel(fuel);
                    Game.Instance.scored(over.winPoints());
                }
                this.changeStateToLandedLander(over);
                return;
            }
            else {
                this.changeStateToExplodingLander(over);
                return;
            }
        }

        /*
         * Handle Main Rocket
         */
        if (System.currentTimeMillis() >  saveTimeMainRocketOn + 300 ) 
            this.mainRocketOff();

    }
    public void keyDown(Event evt, int key){
        switch(key){
        case ' ':
        case Event.UP:
        case Event.DOWN:

            this.saveTimeMainRocketOn = System.currentTimeMillis();

            this.mainRocketOn();
            break;
        case Event.LEFT:

            this.rotateLeft();
            break;
        case Event.RIGHT:

            this.rotateRight();
            break;
        }
    }
}
