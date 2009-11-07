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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class HUD 
    implements Drawable
{
    protected static HUD Instance;

    public final static void Status1(String string){
        Instance.status1(string);
    }
    public final static void Status2(String string){
        Instance.status2(string);
    }

    protected final static Font Messages0 = Font.Futural.clone(3f,2f);
    protected final static Font MessagesN = Font.Futural.clone(1.1f,0.9f);
    static {
        Messages0.setVerticalAlignment(Font.VERTICAL_BOTTOM);
        MessagesN.setVerticalAlignment(Font.VERTICAL_TOP);
    }

    protected final static Font Markers = Font.Markers.clone(2f,1f);

    protected final static Font Technical = Font.Futural.clone(0.9f,0.8f);
    static {
        Technical.setVerticalAlignment(Font.VERTICAL_TOP);
    }



    protected final Panel panel;

    private volatile String[] messages;

    private volatile String status1, status2;

  
    public HUD(Panel panel){
        super();
        this.panel = panel;
        Instance = this;
    }


    public void toggle(){
    }
    public void init(boolean newGame){
    }
    public void scored(int points){
    }
    public void update(){
    }
    public final void clear(){
        this.status1 = null;
        this.messages = null;
    }
    public final void status1(String m){
        this.status1 = m;
    }
    public final void status2(String m){
        this.status2 = m;
    }
    public final void message(String m){
        if (null != m){
            String[] messages = this.messages;
            if (null == messages)
                this.messages = new String[]{m};
            else {
                int len = messages.length;
                String[] copier = new String[len+1];
                System.arraycopy(messages,0,copier,0,len);
                copier[len] = m;
                this.messages = copier;
            }
        }
    }
    public void draw(Graphics2D g){
        g.setColor(Color.green);

        Panel panel = this.panel;
        String[] messages = this.messages;
        if (null != messages){

            int x = 0, y = 0, h = 0;
            for (int cc = 0, count = messages.length; cc < count; cc++){
                String message = messages[cc];
                Font font;
                if (0 == cc)
                    font = Messages0;
                else
                    font = MessagesN;

                Rectangle bounds = font.stringBounds(message);

                x = ((panel.width - bounds.width) /2);
                y = (((panel.height - bounds.height) /2)+(cc * h));

                h = bounds.height;

                font.drawString( message, x, y, g);
            }
        }

        String status1 = this.status1;
        if (null != status1){
            Rectangle b = Technical.stringBounds(status1, panel.left, panel.bottom);
            Technical.drawString( status1, b.x, b.y, g);
        }

        String status2 = this.status2;
        if (null != status2){
            Rectangle b = Technical.stringBounds(status2, panel.left, panel.bottom);
            Technical.drawString( status2, b.x, (b.y+b.height), g);
        }
    } 
}
