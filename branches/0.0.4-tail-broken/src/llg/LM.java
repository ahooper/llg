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
 * @author jdp
 */
public final class LM
    implements java.awt.LayoutManager2
{

    public LM(){
        super();
    }

    private void layout(java.awt.Container parent, java.awt.Component comp){
        if (null != parent && null != comp){
            comp.setVisible(true);
            java.awt.Dimension size = parent.getSize();
            java.awt.Insets insets = parent.getInsets();
            size.width -= (insets.left+insets.right);
            size.height -= (insets.top+insets.bottom);
            comp.setSize(size);
            comp.setLocation(insets.left,insets.top);
        }
    }

    public void addLayoutComponent(String name, java.awt.Component comp){
        this.layout(comp.getParent(),comp);
    }
    public void removeLayoutComponent(java.awt.Component comp){
    }
    public java.awt.Dimension preferredLayoutSize(java.awt.Container parent){
        return parent.getSize();
    }
    public java.awt.Dimension minimumLayoutSize(java.awt.Container parent){
        return parent.getSize();
    }
    public void layoutContainer(java.awt.Container parent){
        if (0 < parent.countComponents())
            this.layout(parent,parent.getComponent(0));
    }
    public void addLayoutComponent(java.awt.Component comp, java.lang.Object constraints){
    }
    public java.awt.Dimension maximumLayoutSize(java.awt.Container parent){

        return parent.getSize();
    }
    public float getLayoutAlignmentX(java.awt.Container parent){

        return 0f;
    }
    public float getLayoutAlignmentY(java.awt.Container parent){

        return 0f;
    }
    public void invalidateLayout(java.awt.Container parent){
        if (0 < parent.countComponents())
            this.layout(parent,parent.getComponent(0));
    }

}
