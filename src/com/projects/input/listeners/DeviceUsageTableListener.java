package com.projects.input.listeners;

import com.projects.management.SystemController;
import com.projects.models.Time;
import com.projects.systems.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.lang.System;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 7/3/2015.
 */
public class DeviceUsageTableListener implements TableModelListener
{
    private SystemController controller;

    public DeviceUsageTableListener(SystemController control)
    {
        controller = control;
    }

    @Override
    public void tableChanged(TableModelEvent e)
    {
        if (e.getType() == TableModelEvent.UPDATE)
        {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (row >= 0 && column >= 0)
            {
                TableModel model = (TableModel)e.getSource();
                Date date = (Date)model.getValueAt(row, column);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int hours = calendar.get(Calendar.HOUR);
                int minutes = calendar.get(Calendar.MINUTE);

                double secondsOfTheDay = TimeUnit.HOURS.toSeconds(hours) + TimeUnit.MINUTES.toSeconds(minutes);

                if (column == 1)
                {
                    controller.editTimeSpanUsageTo(row, secondsOfTheDay);
                }
                else
                {
                    controller.editTimeSpanUsageFrom(row, secondsOfTheDay);
                }
            }
        }
    }
}
