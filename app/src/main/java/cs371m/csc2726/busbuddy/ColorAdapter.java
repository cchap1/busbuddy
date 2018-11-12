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

/**
 * Created by witchel on 1/29/18.
 */

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>{
    private ColorDict.ColorName[] colorDict;
    private Context mContext;
    private Random random;


    public ColorAdapter(Context context){
        colorDict = ColorDict.dict;
        mContext = context;
        random = new Random();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // XXX This ViewHolder class is built in to the RecyclerView.
        //   Put what you want in it, then initialize them in the constructor
        //   Set an onclicklistener on the view to swapItem with the clicked position
        TextView rowText;

        public ViewHolder(View view) {
            super(view);
            rowText = (TextView) view.findViewById(R.id.tv);
            rowText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swapItem(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.color_card,parent,false);
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
        float luminance = getLuminance(colorDict[position].color);
        // Print the color name and the luminance, which is just interesting
        String display = colorDict[position].name + " " + String.format("%1.2f", luminance);
        // XXX Do something with the ViewHolder object
        //   If the luminance is less than 0.3, use white to write the name, otherwise black
        holder.rowText.setText(display);
        holder.rowText.setBackgroundColor(colorDict[position].color);
        if (luminance < .3)
            holder.rowText.setTextColor(Color.WHITE);
        else
            holder.rowText.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount(){
        return colorDict.length;
    }

    public void swapItem(int position) {
        int swappedPosition = ColorDict.randomSwap(random, position);
        notifyItemChanged(position);
        notifyItemChanged(swappedPosition);
    }
}