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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 *    This abstract class will be subclassed for each of the three
 *  states the Player's lander can be in- Flying, Landed, and
 *  Exploding.  Each class will contain the behavior the lander shows
 *  when in that state.  The FlyingLander class has code to check for
 *  collisons with the landscape, and soft landings.  If it detects a
 *  crash it transmutes to a "ExplodingLander" class, or if it sees a
 *  good landing it becomes a "LandedLander" class.  These classes
 *  have code to change back to a "FlyingLander" when certain keys are
 *  pressed.
 *
 * @version 1.3,  11 Jan 1999 
 * @version 2.0,  4 Nov 2009
 * @author John Donohue
 * @author John Pritchard
 */
public abstract class Lander 
    extends Craft
    implements Drawable 
{

    static volatile Lander Current;

    private final static int ImaginaryFootLine = 25;
    private final static int ImaginaryMatingLine = 24;

    protected final static double RocketAcceleration = 0.010;
    protected final static double RocketFuelConsumption  = 0.001;
    protected final static double RocketFuelWinPointsRatio  = (1.0 / Surface.PointsMax);
    protected final static double maxSafeLandingSpeed = 0.25;

    protected static volatile double gravityAcceleration = gravityAccelerationDefault;

    protected static volatile boolean mainRocketOperating;

    protected static volatile float fuel = 1.0f;

    protected static volatile long saveTimeWeLandedOrCrashed;

    protected final static void Gravity(){
        gravityAcceleration = gravityAccelerationDefault;
    }



    protected Lander(Lander lander){
        super(lander,Model.Lander);

        Current = this;

        this.mainRocketOff();
    }
    protected Lander (double px, double py){
        super(Model.Lander);

        Current = this;

        attitude = 0;

        this.rotate();

        this.translate(px,py);

        this.mainRocketOff();
    }


    public Line2D.Double feet(){
        return (this.lines[ImaginaryFootLine].toWorld(this));
    }
    public Point2D.Double feetMidpoint(){
        Line2D.Double feet = (this.lines[ImaginaryFootLine].toWorld(this));
        return new Point2D.Double(Vector.MidX(feet),Vector.MidY(feet));
    }
    public Line2D.Double mate(){
        return this.lines[ImaginaryMatingLine].toWorld(this);
    }
    protected void changeStateToExplodingLander(Tickable collision){
        new ExplodingLander(this,collision);
    }
    protected void changeStateToLandedLander(Tickable collision){
        new LandedLander(this,collision);
    }
    protected void changeStateToFlyingLander(){
        new FlyingLander(this);
    }
    protected void mainRocketOff(){

        mainRocketOperating = false;

        /* (flame off)
         */
        this.lines[0].visible = false;
        this.lines[1].visible = false;
    }
    protected void mainRocketOn(){

        if (0.0 < fuel){

            mainRocketOperating = true;

            /* (flame on)
             */
            this.lines[0].visible = true;
            this.lines[1].visible = true;
        }
    }
}
