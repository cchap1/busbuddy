package cs371m.csc2726.busbuddy;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;


public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>{
    private String[] kids;
    private Context mContext;
    private Random random;
    private String[] kidNames;


    public ColorAdapter(Context context){
        mContext = context;
        random = new Random();
        kidNames = mContext.getResources().getStringArray(R.array.kids);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView rowText;

        public ViewHolder(View view) {
            super(view);
            rowText = (TextView) view.findViewById(R.id.kidText);
            rowText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }

    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.row,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public static float getLuminance(int color) {
        int red   = Color.red(color);
        int green = Color.green(color);
        int blue  = Color.blue(color);

        float hsl[] = new float[3];
        ColorUtils.RGBToHSL(red, green, blue, hsl);
        return hsl[2];
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        int color = Color.GREEN;
        float luminance = getLuminance(color);
        // Print the color name and the luminance, which is just interesting
        String display = kidNames[position];
        // XXX Do something with the ViewHolder object
        //   If the luminance is less than 0.3, use white to write the name, otherwise black
        holder.rowText.setText(display);
        holder.rowText.setBackgroundColor(color);
        if (luminance < .3)
            holder.rowText.setTextColor(Color.WHITE);
        else
            holder.rowText.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount(){
        return kidNames.length;
    }

    /*public void moveToTop(int position) {
        ColorDict.ColorName colorName = getItem(position);
        remove(colorName);
        insert(colorName, 0);
        notifyDataSetChanged();
    }*/

}