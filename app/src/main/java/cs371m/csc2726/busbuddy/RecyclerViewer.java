package cs371m.csc2726.busbuddy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RecyclerViewer extends AppCompatActivity {
        private android.support.v7.widget.RecyclerView recyclerView;
        private ListView listView;


        public void initListView() {
            listView = (ListView) findViewById(R.id.theListView);
            final SimpleAdapt adapter = new SimpleAdapt(getBaseContext());
            adapter.addAll(ColorDict.dict);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            });
        }

        public void initRecyclerView () {
            // Get the widgets reference from XML layout
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

            // Define a layout for RecyclerView
            RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

            recyclerView.setLayoutManager(layoutManager);

            // Initialize a new instance of RecyclerView Adapter instance
            RecyclerView.Adapter adapter = new ColorAdapter(getApplicationContext());

            // Set the adapter for RecyclerView
            recyclerView.setAdapter(adapter);
        }
}
