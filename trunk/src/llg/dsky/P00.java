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
package llg.dsky;

import llg.DSKY;

/**
 * Reset program
 */
public final class P00 
    implements Program 
{

    public P00(){
        super();
    }

    public String getOrdinal(){
        return "00";
    }
    public String getDescription(){
        return "Reset";
    }
    public void execute(DSKY dsky, String verb, String noun, String r1, String r2, String r3){
        dsky.reset();
    }
    public String help(){
        return "Reset input.";
    }
    public String help(String verb){
        return null;
    }
    public String help(String verb, String noun){
        return null;
    }
}
