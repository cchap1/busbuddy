package cs371m.csc2726.busbuddy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private Context mContext;
    public ArrayList<String> kidNames;
    MainActivity mainActivity;
    ViewHolder vh;


    public ColorAdapter(Context context, MainActivity act){
        mainActivity = act;
        mContext = context;
        kidNames = new ArrayList<String>();
        kidNames.add("Johnny Appleseed");
        kidNames.add("Suzie Smith");
        kidNames.add("Chad Chapman");
        kidNames.add("Kanye West");
        kidNames.add("Justin Bieber");
        kidNames.add("Roger Rogers");
        kidNames.add("Mike Wazowski");
        kidNames.add("Kaylin Maxwell");
        kidNames.add("Little Timmy");
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView rowText;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            rowText = (TextView) view.findViewById(R.id.kidText);
            imageView = (ImageView) view.findViewById(R.id.kidPic);
            rowText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mainActivity.driver)
                        return;
                    ColorDrawable background = (ColorDrawable) rowText.getBackground();
                    if (background.getColor() == Color.GREEN)
                        rowText.setBackgroundColor(Color.RED);
                    else
                        rowText.setBackgroundColor(Color.GREEN);
                }
            });
        }
    }

    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.row,parent,false);
        vh = new ViewHolder(v);
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
        Log.d("TAG", "onBindViewHolder: "+ kidNames.get(position));
        String display = kidNames.get(position).toString();


        BitmapDrawable photo = (BitmapDrawable) holder.imageView.getDrawable();
        Bitmap photoBits = photo.getBitmap();
        BitmapDrawable original = (BitmapDrawable) mainActivity.getDrawable(R.drawable.profile);
        Bitmap originalBits = original.getBitmap();
        if (photoBits == originalBits) {
            if (position == 0)
                holder.imageView.setImageResource(R.drawable.student1);
            if (position == 1)
                holder.imageView.setImageResource(R.drawable.student2);
            if (position == 2)
                holder.imageView.setImageResource(R.drawable.student3);
            if (position == 3)
                holder.imageView.setImageResource(R.drawable.student4);
            if (position == 4)
                holder.imageView.setImageResource(R.drawable.student5);
            if (position == 5)
                holder.imageView.setImageResource(R.drawable.student6);
            if (position == 6)
                holder.imageView.setImageResource(R.drawable.student7);
            if (position == 7)
                holder.imageView.setImageResource(R.drawable.student8);
        }
        holder.rowText.setText(display);
        holder.rowText.setBackgroundColor(color);
        if (luminance < .3)
            holder.rowText.setTextColor(Color.WHITE);
        else
            holder.rowText.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount(){
        return kidNames.size();
    }

    public void removeAll(RecyclerView.LayoutManager layoutManager) throws InterruptedException {
        int count = getItemCount();
        int removed = 0;
        Log.d("TAG", "removeAlls: " + count);
        for (int i = 0; i <=  count; i++) {
            View view = layoutManager.findViewByPosition(i);
            Log.d("TAG", "removeAll: "+ view);
            if (view != null) {

                TextView rowText = (TextView) view.findViewById(R.id.kidText);
                ColorDrawable background = (ColorDrawable) rowText.getBackground();
                if (background.getColor() == Color.RED) {
                    /*
                    for (int j = i + 1; j <= count; j++) {
                        View viewer = layoutManager.findViewByPosition(j);
                        View old = layoutManager.findViewByPosition(i);
                        Log.d("TAG", "removeAll: " + viewer);
                        if (viewer != null) {
                            ImageView image = (ImageView) viewer.findViewById(R.id.kidPic);
                            Drawable draw = image.getDrawable();
                            ImageView oldimage = (ImageView) old.findViewById(R.id.kidPic);
                            //vh.imageView.setImageDrawable(draw);
                        }
                    }*/

                    kidNames.remove(i-removed);
                    notifyItemRemoved(i-removed);
                    notifyItemRangeChanged(i-removed, kidNames.size());
                    removed++;

                }
            }
        }
    }

    public void add(String name, RecyclerView.LayoutManager layoutManager,
                    ColorAdapter adapter, Bitmap bm){
        kidNames.add(name);
        int position = kidNames.indexOf(name);
        adapter.notifyItemInserted(kidNames.size() - 1);
        Log.d("TAG", "removeAll: "+ position);
        View view = layoutManager.findViewByPosition(position);
        if (view == null)
            return;
        Log.d("TAG", "remoaaaaaveAll: "+ view);
        ImageView pic = view.findViewById(R.id.kidPic);
        pic.setImageBitmap(bm);
        notifyDataSetChanged();
    }

}