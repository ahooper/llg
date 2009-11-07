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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Stroke;

/**
 * Paint a world coordinate grid within the viewport.
 * 
 * @author jdp
 */
public final class World
    implements Drawable
{
    private final static int Grid = 100;
    private final static double GriD = 100.0;

    private final static Stroke S = new BasicStroke(0.05f);
    private final static Color C = new Color(0xaf,0xaf,0x0);
    private final static Color G = new Color(0x0,0x0,0xaf);


    public World(Panel p){
        super();
    }

    public void draw(Graphics2D g){

        Rectangle2D.Double viewport = Panel.Instance.toWorld();
        int loX = (int)((viewport.x / GriD) * GriD);
        int hiX = (int)(((viewport.width + GriD) / GriD) * GriD);
        int loY = (int)((viewport.y / GriD) * GriD);
        int hiY = (int)(((viewport.height + GriD) / GriD) * GriD);

        g.setStroke(S);

        for (int x = loX; x <= hiX; x += Grid){

            for (int y = loY; y <= hiY; y += Grid){

                if (0 == x || 0 == y)
                    g.setColor(C);
                else
                    g.setColor(G);

                g.drawLine(loX,y,hiX,y);

                g.drawLine(x,loY,x,hiY);
            }
        }
    }
}
