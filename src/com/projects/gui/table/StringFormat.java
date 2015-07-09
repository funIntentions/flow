package com.projects.gui.table;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Created by Dan on 7/8/2015.
 */
public class StringFormat extends Format
{

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
    {
        String input = (String)obj;
        toAppendTo.append(input);

        return toAppendTo;
    }

    public Object parseObject(String source, ParsePosition pos)
    {
        if (source != null)
        {
            String trimmedSource = source.trim();

            if (!source.isEmpty() && !trimmedSource.isEmpty() && source.matches("^[a-zA-Z0-9 _]*$"))
            {
                pos.setIndex(source.length());
                return source;
            }
        }
        return null;
    }
}
