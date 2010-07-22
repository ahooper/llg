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
 * Runtime version info.
 * 
 * @author jdp
 */
public final class Java {

    public final static class Version {

        public final int major, minor, point;
        public final String string, build, qualifier;

        public Version(){
            super();
            String string = System.getProperty("java.version");
            this.string = string;
            java.util.StringTokenizer strtok = new java.util.StringTokenizer(string,"._-");
            String major = strtok.nextToken();
            this.major = Integer.parseInt(major);
            String minor = strtok.nextToken();
            this.minor = Integer.parseInt(minor);
            if (strtok.hasMoreTokens()){
                String point = strtok.nextToken();
                this.point = Integer.parseInt(point);
                if (strtok.hasMoreTokens()){
                    this.build = strtok.nextToken();
                    if (strtok.hasMoreTokens())
                        this.qualifier = strtok.nextToken();
                    else
                        this.qualifier = null;
                }
                else {
                    this.build = null;
                    this.qualifier = null;
                }
            }
            else {
                this.point = 0;
                this.build = null;
                this.qualifier = null;
            }
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
        public int compareTo(int major, int minor, int point){
            if (this.major == major){
                if (this.minor == minor){
                    if (this.point == point)
                        return 0;
                    else if (this.point < point)
                        return -1;
                    else
                        return 1;
                }
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
        public boolean equalsOrNewer(int major, int minor, int point){
            return (0 <= this.compareTo(major,minor,point));
        }
        public boolean equals(int major, int minor){
            return (0 == this.compareTo(major,minor));
        }
        public boolean equals(int major, int minor, int point){
            return (0 == this.compareTo(major,minor,point));
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

    public final static String Vendor = System.getProperty("java.vendor");
    public final static Version Version = new Version();
    /**
     * Java class file version number, eg, <code>"50.0"</code>.
     */
    public final static String Arch = System.getProperty("java.class.version");
    /**
     * <pre>
     * Apple Computer, Inc. 1.6.0-dp 50.0
     * Sun Microsystems Inc. 1.6.0_10-beta 50.0
     * </pre>
     */
    public final static String Full = Vendor+' '+Version+' '+Arch;

    public final static boolean IsVendorSun = (Vendor.startsWith("Sun Microsystems")||Vendor.startsWith("Oracle"));
    public final static boolean IsVendorApple = (Vendor.startsWith("Apple"));
    public final static boolean IsVendorOther = (!(IsVendorSun||IsVendorApple));

    public final static boolean IsVersion12 = (Version.equals(1,2));
    public final static boolean IsVersion14 = (Version.equals(1,4));
    public final static boolean IsVersion15 = (Version.equals(1,5));
    public final static boolean IsVersion16 = (Version.equals(1,6));

    public final static boolean IsVersion12OrNewer = (Version.equalsOrNewer(1,2));
    public final static boolean IsVersion14OrNewer = (Version.equalsOrNewer(1,4));
    public final static boolean IsVersion15OrNewer = (Version.equalsOrNewer(1,5));
    public final static boolean IsVersion16OrNewer = (Version.equalsOrNewer(1,6));



    public final static void main(String[] argv){

        System.out.println(Full);

    }
}
