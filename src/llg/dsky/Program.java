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
 * Programs are located in this package, and have names "P"+P for 'P'
 * the value of the two character program register -- should be
 * numbers.  These classes have a simple contructor (public, no args).
 * These classes are stateless.
 */
public interface Program {

    public final static class Cache
        extends Object
    {
        public final static Program[] cache = {
            (new P00())
        };
        public final static Program Get(int p){
            if (-1 < p && p < cache.length)
                return cache[p];
            else
                return null;
        }
        public final static void Execute(DSKY dsky){
            try {
                int program = Integer.parseInt(DSKY.Program());
                String verb = DSKY.Verb();
                String noun = DSKY.Noun();
                String r1 = DSKY.R1();
                String r2 = DSKY.R2();
                String r3 = DSKY.R3();
                Program p = Cache.Get(program);
                if (null == p){
                    dsky.error();
                    return;
                }
                else
                    p.execute(dsky,verb,noun,r1,r2,r3);
            }
            catch (NumberFormatException exc){
                dsky.error();
            }
        }
    }

    public void execute(DSKY dsky, String verb, String noun, String r1, String r2, String r3);

    public String getOrdinal();

    public String getDescription();

    public String help();

    public String help(String verb);

    public String help(String verb, String noun);
}
