package com.projects.gui;

import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

/**
 * Created by Dan on 6/11/2015.
 */
public class SplitPaneUI extends BasicSplitPaneUI
{
    @Override
    public BasicSplitPaneDivider createDefaultDivider()
    {
        return new EmptySplitPaneDivider(this);
    }

    class EmptySplitPaneDivider extends BasicSplitPaneDivider {

          public EmptySplitPaneDivider(BasicSplitPaneUI ui) {
               super(ui);
          }

          public void paint(Graphics g) {
               g.setColor(getBackground());
               g.fillRect(0, 0, getWidth(), getHeight());
          }

     }
}
