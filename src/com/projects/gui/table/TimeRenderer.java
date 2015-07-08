package com.projects.gui.table;

import javax.swing.table.DefaultTableCellRenderer;
import java.text.SimpleDateFormat;

/**
 * Created by Dan on 7/3/2015.
 */
public class TimeRenderer extends DefaultTableCellRenderer
{
    SimpleDateFormat simpleDateFormat;

    public TimeRenderer()
    {
        super();
        simpleDateFormat = new SimpleDateFormat("HH:mm");
    }

    public void setValue(Object value)
    {
        //try
        //{
            String time = simpleDateFormat.format(value);
            setText(time);
        //}
        //catch (ParseException p)
        //{
        //    p.printStackTrace();
        //}
    }
}
