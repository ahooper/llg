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
 * 
 * 
 * @author jdp
 */
public final class Ticker
    extends java.lang.Thread
{
    /**
     * Promotes uniform game time across different computers.
     */
    public static class Sync {

        private final static long DT = 50L;

        private volatile long last;

        public Sync(){
            super();
        }

        public synchronized void waitfor(){
            long last = System.currentTimeMillis();
            try {
                long dt = (DT - (last - this.last));
                this.last = last;
                if (0 < dt){
                    dt = Math.min(DT,dt);
                    this.wait(dt);
                }
            }
            catch (InterruptedException exc){
                throw new ThreadDeath();
            }
        }
    }

    private final Sync sync = new Sync();


    private final Panel panel;

    private volatile boolean running = true;


    Ticker(Panel panel){
        super("LLG Ticker");
        this.setDaemon(true);
        this.setPriority(MIN_PRIORITY+1);
        if (null != panel)
            this.panel = panel;
        else
            throw new IllegalArgumentException();
    }


    public void halt(){
        this.running = false;
        try {
            this.interrupt();

            Thread.sleep(Sync.DT+10L);

            this.stop();
        }
        catch (Exception exc){
            return;
        }
    }
    public final void run(){
        Sync sync = this.sync;
        Panel panel = this.panel;

        while (this.running){
            try {
                sync.waitfor();

                panel.tick();
            }
            catch (Exception exc){
            }
        }
    }
}
