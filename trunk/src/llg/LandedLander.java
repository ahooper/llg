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


public final class LandedLander 
    extends Lander
{
    private Tickable collision;


    LandedLander(Lander lander, Tickable collision){
        super(lander);
        if (null != collision)
            this.collision = collision;
        else
            throw new IllegalArgumentException();

        saveTimeWeLandedOrCrashed = System.currentTimeMillis(); 
        if (!lander.intro){
            if (0f < fuel)
                Game.Instance.message("Success!");
            else 
                Game.Instance.message("Game over!");
        }
    }
    LandedLander(){
        this(1f,90,90);
    }
    private LandedLander (float ff, double dx, double dy){
        super( ff, dx, dy);
        saveTimeWeLandedOrCrashed = System.currentTimeMillis(); 
        Gravity();
        Game.Instance.messagesClear();
        Game.Instance.message("Lunar Lander!");
    }



    public boolean isLanded(){
        return true;
    }
    public void tick(){

        if (null != this.collision)
            this.collision.tick();
    }
    public void launch(){

        this.ty = -0.15;

        this.changeStateToFlyingLander();
    }
    public void keyDown(Event evt, int key){

        if ( System.currentTimeMillis() > (saveTimeWeLandedOrCrashed + 500) )

            this.launch();
    }

}
