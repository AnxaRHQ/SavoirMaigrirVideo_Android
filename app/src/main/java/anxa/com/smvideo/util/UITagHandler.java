package anxa.com.smvideo.util;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * Created by angelaanxa on 5/30/2017.
 */

public class UITagHandler implements Html.TagHandler
{
    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader)
    {
        if(tag.equals("ul") && !opening) output.append("\n");
        if(tag.equals("li") && opening) output.append("\n\t•  ");
    }
}
