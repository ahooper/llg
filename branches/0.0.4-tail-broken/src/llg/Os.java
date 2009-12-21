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
 * Platform version info.
 * 
 * @author jdp
 */
public final class Os {

    public final static String Darwin = "Mac OS X";
    public final static String Windows = "Windows";
    public final static String Linux = "Linux";
    public final static String Solaris = "SunOS";


    public static class Platform 
        extends Object
    {

        public final static Platform Nil = new Platform();
        public final static Platform Darwin = new Platform(Os.Darwin);
        public final static Platform Windows = new Platform(Os.Windows);
        public final static Platform Linux = new Platform(Os.Linux);
        public final static Platform Solaris = new Platform(Os.Solaris);


        public final String os_name;

        public Platform(){
            super();
            this.os_name = null;
        }
        protected Platform(String name){
            super();
            this.os_name = name;
        }
        protected Platform(Platform copy){
            super();
            if (null != copy)
                this.os_name = copy.os_name;
            else
                throw new IllegalArgumentException();
        }


        public final boolean hasOs(){
            return (null != this.os_name && 0 < this.os_name.length());
        }
        public final boolean hasNotOs(){
            return (null == this.os_name || 1 > this.os_name.length());
        }
        public final boolean isOs(){
            if (this.hasNotOs())
                return true;
            else 
                return Os.Name.equals(this.os_name);
        }
        public int hashCode(){
            if (this.hasNotOs())
                return 0;
            else
                return this.os_name.hashCode();
        }
        public String toString(){
            if (this.hasNotOs())
                return "";
            else
                return this.os_name;
        }
        public boolean equals(Object ano){
            if (this == ano)
                return true;
            else if (null == ano)
                return false;
            else
                return this.toString().equals(ano.toString());
        }
    }

    public final static class VersionC {

        public final int major, minor;
        public final String string;

        public VersionC(){
            super();
            String string = System.getProperty("os.version");
            this.string = string;
            java.util.StringTokenizer strtok = new java.util.StringTokenizer(string,"._-");
            String major = strtok.nextToken();
            this.major = Integer.parseInt(major);
            String minor = strtok.nextToken();
            this.minor = Integer.parseInt(minor);
        }

        public int hashCode(){
            return this.string.hashCode();
        }
        public String toString(){
            return this.string;
        }
        public int compareTo(int major, int minor){
            if (this.major == major){
                if (this.minor == minor)
                    return 0;
                else if (this.minor < minor)
                    return -1;
                else
                    return 1;
            }
            else if (this.major < major)
                return -1;
            else
                return 1;
        }
        public boolean equalsOrNewer(int major, int minor){
            return (0 <= this.compareTo(major,minor));
        }
        public boolean equals(int major, int minor){
            return (0 == this.compareTo(major,minor));
        }
        public boolean equals(Object ano){
            if (ano == this)
                return true;
            else if (null == ano)
                return false;
            else
                return this.string.equals(ano.toString());
        }
    }


    public final static String Name = System.getProperty("os.name");
    public final static VersionC Version = new VersionC();
    public final static String Arch = System.getProperty("os.arch");
    public final static String Full = Name+' '+Version+' '+Arch;

    public final static boolean IsDarwin = (Os.Darwin.equals(Name));
    public final static boolean IsWindows = (Os.Windows.equals(Name));
    public final static boolean IsLinux = (Os.Linux.equals(Name));
    public final static boolean IsSunOs = (Os.Solaris.equals(Name));


    public final static void main(String[] argv){

        System.out.println(Full);
    }
}
