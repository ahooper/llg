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

import java.awt.Window;

/**
 * 
 * 
 * @author jdp
 */
public final class Disposer
    extends java.lang.Thread
{

    private final Window window;


    Disposer(Window window){
        super("Disposer");

        if (null != window){
            this.window = window;
            this.setDaemon(true);
        }
        else
            throw new IllegalArgumentException();
    }


    public final void run(){
        try {
            int count = Thread.activeCount();
            Thread list[] = new Thread[count+10];
            count = Thread.enumerate(list);
            for (int cc = 0; cc < count; cc++){
                Thread T = list[cc];
                if (this != T)
                    T.interrupt();
            }
        }
        catch (Exception exc){
            exc.printStackTrace();
        }
        finally {
            this.stop();
        }
    }
}
