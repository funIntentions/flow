package com.projects.gui.table;

import com.projects.models.Device;

/**
 * Created by Dan on 6/18/2015.
 */
public class DeviceTable extends ObjectTable<Device>
{
    @Override
    public Object getValueAt(int row, int col)
    {
        Device device = data.get(row);
        Object value;

        switch(col)
        {
            case 0:
            {
                value = device.getName();
            } break;
            default:
            {
                value = "default";
            }
        }

        return value;
    }
}
