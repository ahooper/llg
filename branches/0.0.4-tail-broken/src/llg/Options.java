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

public final class Options
    extends Object
{
    private static Options Instance;

    public static int GetInt(int pos, int defv){
        return Instance.getInt(pos,defv);
    }
    public static int GetInt(String name, int defv){
        return Instance.getInt(name,defv);
    }
    public static String GetString(int pos){
        return Instance.getString(pos);
    }
    public static String GetString(int pos, String defv){
        return Instance.getString(pos,defv);
    }
    public static String GetString(String name){
        return Instance.getString(name);
    }
    public static String GetString(String name, String defv){
        return Instance.getString(name,defv);
    }
    public static boolean HasString(String name){
        return Instance.hasString(name);
    }
    public static String[] ListString(String name){
        return Instance.listString(name);
    }
    public final static String[][] Add(String[][] list, String[] item){
        if (null != item){
            if (null == list)
                list = new String[][]{item};
            else {
                int len = list.length;
                String[][] copier = new String[len+1][];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                list = copier;
            }
        }
        return list;
    }
    public final static String[] Add(String[] list, String item){
        if (null != item){
            if (null == list)
                list = new String[]{item};
            else {
                int len = list.length;
                String[] copier = new String[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                list = copier;
            }
        }
        return list;
    }



    private final String argv[], positional[], named[][];


    public Options(String[] argv){
        super();
        if (null == Instance){
            Instance = this;
            this.argv = argv;
            String[] positional = null;
            String[][] named = null;
            String name = null, value = null;
            for (String arg: argv){
                if (null == name){
                    if ('-' == arg.charAt(0))
                        name = arg;
                    else {
                        value = arg;
                        positional = Add(positional,value);
                    }
                }
                else if ('-' == arg.charAt(0)){
                    value = arg;
                    positional = Add(positional,name);
                    positional = Add(positional,value);
                    named = Add(named,(new String[]{name,value}));
                    name = null;
                }
                else {
                    value = arg;
                    named = Add(named,(new String[]{name,value}));
                    name = null;
                }
            }

            if (null == named)
                this.named = new String[][]{{}};
            else
                this.named = named;

            if (null == positional)
                this.positional = new String[]{};
            else
                this.positional = positional;
        }
        else
            throw new IllegalStateException();
    }


    public int getInt(int pos, int defv){
        String value = this.getString(pos,null);
        if (null != value)
            return Integer.parseInt(value);
        else
            return defv;
    }
    public int getInt(String name, int defv){
        String value = this.getString(name,null);
        if (null != value)
            return Integer.parseInt(value);
        else
            return defv;
    }
    public String getString(int pos){
        return this.getString(pos);
    }
    public String getString(int pos, String defv){
        String[] argv = this.positional;
        if (pos < argv.length)
            return argv[pos];
        else
            return defv;
    }
    public String getString(String name){
        return this.getString(name,null);
    }
    public String getString(String name, String defv){

        if (null != name){
            for (String[] nvp : this.named){
                if (2 == nvp.length){
                    String pairName = nvp[0];
                    String pairValue = nvp[1];
                    if (name.equals(pairName))
                        return pairValue;
                }
            }
        }
        return defv;
    }
    public boolean hasString(String name){

        if (null != name){
            for (String[] nvp : this.named){
                if (2 == nvp.length){
                    String pairName = nvp[0];
                    String pairValue = nvp[1];
                    if (name.equals(pairName))
                        return true;
                }
            }
        }
        return false;
    }
    public String[] listString(String name){
        String[] list = null;

        if (null != name){
            for (String[] nvp : this.named){
                if (2 == nvp.length){
                    String pairName = nvp[0];
                    String pairValue = nvp[1];
                    if (name.equals(pairName))
                        list = Add(list,pairValue);
                }
            }
        }
        return list;
    }
}
