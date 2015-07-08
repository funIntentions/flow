package com.projects.input.listeners;

import com.projects.management.SystemController;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
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
                Date toDate = (Date)model.getValueAt(row, 1);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(toDate);

                int toHours = calendar.get(Calendar.HOUR_OF_DAY);
                int toMinutes = calendar.get(Calendar.MINUTE);

                double toSecondsOfTheDay = TimeUnit.HOURS.toSeconds(toHours) + TimeUnit.MINUTES.toSeconds(toMinutes);

                Date fromDate = (Date)model.getValueAt(row, 0);
                calendar.setTime(fromDate);

                int fromHours = calendar.get(Calendar.HOUR_OF_DAY);
                int fromMinutes = calendar.get(Calendar.MINUTE);

                double fromSecondsOfTheDay = TimeUnit.HOURS.toSeconds(fromHours) + TimeUnit.MINUTES.toSeconds(fromMinutes);

                if (column == 1)
                {
                    controller.editTimeSpanUsageTo(row, toSecondsOfTheDay);

                    if (toSecondsOfTheDay < fromSecondsOfTheDay)
                    {
                        model.setValueAt(toDate, row, 0);
                    }
                }
                else
                {
                    controller.editTimeSpanUsageFrom(row, fromSecondsOfTheDay);

                    if (fromSecondsOfTheDay > toSecondsOfTheDay)
                    {
                        model.setValueAt(fromDate, row, 1);
                    }
                }
            }
        }
    }
}
