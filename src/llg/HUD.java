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

    public final static void Status(String string){
        Instance.status(string);
    }

    protected final Panel panel;

    protected Font messages0, messagesN;
    protected Font statusFont;

    private volatile String[] messages;

    private volatile String statusString;
    private volatile Rectangle statusBounds;

  
    public HUD(Panel panel){
        super();
        this.panel = panel;
        Instance = this;
    }


    public void toggle(){
    }
    public void init(boolean newGame){
        this.messages0 = Font.Futural.clone(3f,2f);
        this.messages0.setVerticalAlignment(Font.VERTICAL_BOTTOM);
        this.messagesN = Font.Futural.clone(1.1f,0.9f);
        this.messagesN.setVerticalAlignment(Font.VERTICAL_TOP);

        this.statusFont = Font.Futural.clone(1.9f,2.2f);
        this.statusFont.setVerticalAlignment(Font.VERTICAL_TOP);
    }
    public void scored(int points){
    }
    public void update(){
    }
    public final void clear(){

        this.messages = null;
    }
    public final void status(String m){
        this.statusString = m;
        this.statusBounds = this.statusFont.stringBounds(m, panel.left, panel.bottom);
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
                    font = this.messages0;
                else
                    font = this.messagesN;

                Rectangle bounds = font.stringBounds(message);

                x = ((panel.width - bounds.width) /2);
                y = (((panel.height - bounds.height) /2)+(h));

                font.drawString( message, x, y, g);

                h += bounds.height;
            }
        }

        String status = this.statusString;
        if (null != status){
            Rectangle b = this.statusBounds;
            this.statusFont.drawString( status, b.x, (b.y), g);
        }
    } 
}
