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


/**
 * A model that's not stationary in the world.
 */
public class Craft
    extends Model
    implements World.Distance
{
    protected final static double Deg15 = 0.2617993877991;

    protected final static double gravityAccelerationDefault = 0.003;


    protected boolean intro;

    protected volatile int attitude;

    private volatile double attitudeRadians;

    private volatile double attitudeX;
    private volatile double attitudeY;


    public Craft(Craft dynamic, Model model){
        super(dynamic, model);
        this.attitude = dynamic.attitude;
        this.attitudeX = dynamic.attitudeX;
        this.attitudeY = dynamic.attitudeY;
    }
    public Craft(Model model){
        super(model);
    }


    public boolean isOrbiting(){
        return false;
    }
    public boolean isFlying(){
        return false;
    }
    public boolean isLanded(){
        return false;
    }
    public boolean isCrashed(){
        return false;
    }
    public final double attitudeRadians(){

        return this.attitudeRadians;
    }
    public final double altitudeWorld(){
        double dy = this.dy;
        if (dy < Surface.Yavg)
            return Vector.Dim(dy,Surface.Yavg);
        else
            return -Vector.Dim(dy,Surface.Yavg);
    }
    public final double altitudeMeters(){

        return (this.altitudeWorld() * Meters);
    }
    public final double latitudeMeters(){
        return 0.0;
    }
    public final double latitudeRadians(){
        return 0.0;
    }
    public final double longitudeWorld(){
        return -this.dx;
    }
    public final double longitudeMeters(){
        return ((-this.dx) * Meters);
    }
    public final World.Longitude longitude(){
        return (new World.Longitude(-this.dx));
    }
    protected void rotateLeft(){
        this.attitude += 1;

        if (this.attitude > 23) 
            this.attitude = 0;

        this.rotate();
    }
    protected void rotateRight(){

        this.attitude -= 1;

        if (this.attitude < 0)
            this.attitude = 23;

        this.rotate();
    }
    protected void rotate(){

        this.attitudeRadians = (this.attitude * Deg15);
        this.rotate(-this.attitudeRadians);
        double cos = Math.cos(this.attitudeRadians);
        double sin = Math.sin(this.attitudeRadians);
        double sum = Math.abs(sin) + Math.abs(cos);
        this.attitudeY =  cos / sum;
        this.attitudeX =  sin / sum;
    }
    protected void thrust(double accel){
        this.tx -= (this.attitudeX * accel);
        this.ty -= (this.attitudeY * accel);
    }
}
