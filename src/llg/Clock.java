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
 * A point in time for all calculations in a rendering cycle.
 * 
 * @author jdp
 */
public final class Clock
    extends Object
{
    public volatile static long LastTime = 0L;

    public volatile static long CurrentTime = System.currentTimeMillis();

    public volatile static long DeltaTime = 0L;

    public final static void Tick(){
        LastTime = CurrentTime;
        CurrentTime = System.currentTimeMillis();
        DeltaTime = (CurrentTime - LastTime);
    }
}
