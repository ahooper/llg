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
 * In creating an animation thread over the 2D graphics pipeline, AWT
 * internals are not available and inconveniences arise.  This class
 * deals with them in the most portable way found.
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
            do {
                try {
                    sleep(400);

                    int count = Thread.activeCount();
                    if (1 < count){
                        Thread[] list = new Thread[count];
                        count = Thread.enumerate(list);
                        for (Thread thread: list){
                            if (this != thread)
                                thread.interrupt();
                        }
                    }
                    else
                        return;
                }
                catch (Throwable thrown){
                    if (thrown instanceof InterruptedException)
                        return;
                    else if (thrown instanceof ThreadDeath)
                        throw (Error)thrown;
                    else
                        thrown.printStackTrace();
                }
            }
            while (this.running);
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
            Animator animator = this.animator;
            do {
                try {
                    long dt = (this.exit - this.enter);
                    if (0 > dt){
                        dt = (System.currentTimeMillis()- this.enter);
                        if (DT < dt && WAITING == animator.getState()){

                            animator.interrupt();
                            break;
                        }
                    }
                    sleep(DT);
                }
                catch (Throwable thrown){
                    thrown.printStackTrace();
                }
            }
            while (this.running);
        }
    }


    protected boolean running = true;


    protected Aut(String n){
        super("LLG Aut "+n);
        this.setDaemon(true);
        this.setPriority(MIN_PRIORITY);
    }


    public void halt(){
        this.running = false;
    }
}
