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
 * Orbital physics
 */
public class Orbiter
    extends Craft
{

    public Orbiter(Craft dynamic, Model model){
        super(dynamic, model);
    }
    public Orbiter(Model model){
        super(model);
    }


    public boolean isOrbiting(){
        return true;
    }
}
