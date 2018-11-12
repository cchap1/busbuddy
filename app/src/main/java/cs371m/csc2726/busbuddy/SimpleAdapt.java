package cs371m.csc2726.busbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Created by witchel on 2/5/2018.
 */

public class SimpleAdapt extends ArrayAdapter<ColorDict.ColorName> {
    private LayoutInflater theInflater = null;

    public SimpleAdapt(Context context) {
        super(context, R.layout.row);
        // The LayoutInflator puts a layout into the right View
        theInflater = LayoutInflater.from(getContext());
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // XXX This is similar to the demo code.  You must inflate a view for the list
        // view row here and set the right values for the view.  Be sure to check if convertView is null

        if (convertView == null) {
            convertView = theInflater.inflate(R.layout.row, parent, false);
        }
        ColorDict.ColorName colorName = getItem(position);
        TextView colorText = convertView.findViewById(R.id.text);
        colorText.setText(colorName.name);
        colorText.setBackgroundColor(colorName.color);
        return convertView;
    }

    public void moveToTop(int position) {
        ColorDict.ColorName colorName = getItem(position);
        remove(colorName);
        insert(colorName, 0);
        notifyDataSetChanged();
    }
}
