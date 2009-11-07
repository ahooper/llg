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

import static java.lang.Thread.State.* ;
import java.lang.reflect.Method;

/**
 * On the subject of breaking deadlocks on the AWT Lock.
 * 
 * @author jdp
 */
public abstract class Aut
    extends java.lang.Thread
{

    public final static class Shutdown
        extends Aut
    {
        Shutdown(){
            super("Shutdown");
        }

        public void run(){
            Method wake = this.wake;
            if (null != wake){
                try {
                    sleep(400);

                    wake.invoke(null);
                }
                catch (Exception exc){
                    exc.printStackTrace();
                }
            }
            this.stop();
        }
    }

    public final static class Animation
        extends Aut
    {
        private final static long DT = 40;

        private Animator animator;

        private volatile long enter, exit;


        Animation(Animator animator){
            super("Animation");
            if (null != animator)
                this.animator = animator;
            else
                throw new IllegalArgumentException();
        }

        public void enter() throws InterruptedException {
            this.enter = System.currentTimeMillis();
        }
        public void exit(){
            this.exit = System.currentTimeMillis();
        }
        public void run(){
            try {
                Animator animator = this.animator;
                do {
                    long dt = (this.exit - this.enter);
                    if (DT < dt){
                        if (WAITING == animator.getState()){
                            System.out.println("<!>");
                            animator.interrupt();
                            break;
                        }
                    }
                    sleep(DT);
                }
                while (true);
            }
            catch (InterruptedException exc){
            }
        }
    }

    protected final Method wake;


    protected Aut(String n){
        super("LLG Aut "+n);
        this.setDaemon(true);
        Method wake = null;
        try {
            Class stk = Class.forName("sun.awt.SunToolkit");
            wake = stk.getMethod("awtLockNotify");
        }
        catch (Exception exc){
        }
        this.wake = wake;
    }
}
