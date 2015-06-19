package com.projects.gui.table;

import com.projects.models.Structure;

/**
 * Created by Dan on 6/19/2015.
 */
public class StructureTable extends ObjectTable<Structure>
{
    @Override
    public Object getValueAt(int row, int col)
    {
        Structure structure = data.get(row);
        Object value;

        switch(col)
        {
            case 0:
            {
                value = structure.getName();
            } break;
            default:
            {
                value = "default";
            }
        }

        return value;
    }
}
