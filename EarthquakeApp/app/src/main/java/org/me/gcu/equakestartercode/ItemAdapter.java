// Yassine Lakhmarti S1903349
package org.me.gcu.equakestartercode;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.Holder> {
    Context context;
    List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
/*
    onCreateViewHolder creates a new View Holder for the recyclerView,
    this runs whenver a new ViewHolder is required
*/
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new Holder(view);
    }
/*
    ViewHolder setting, a Location and Magnitude with a colored bar/View that is
    color coded to the magnitude of the earthquake
 */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.text_view_title.setText("Location: " + itemList.get(position).getLocation());
        holder.text_view_magnitude.setText("Magnitude:  " + itemList.get(position).getMagnitude());

        double magnitude = itemList.get(position).getMagnitude();
        if (magnitude <= 0.9 && magnitude > 0.0) {
            holder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.moderate));
        } else if (magnitude <= 1.9 && magnitude >= 1.0) {
            holder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
        } else if (magnitude <= 2.9 && magnitude >= 2.0) {
            holder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.strong));
        } else if (magnitude <= 3.9 && magnitude >= 3.0 ) {
            holder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
        } else if (magnitude <= 4.9 && magnitude >= 4.0 ){
            holder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.major));
        } else if (magnitude <= 5.9 && magnitude >= 5.0){
            holder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        } else if (magnitude >= 6.0) {
            holder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.great));
        } else {
            holder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
        }

// Create an onClickListener so that the it redirect to the Earthquake activity whenever the user clicks on a Holder
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EarthquakeActivity.class);
            intent.putExtra("item", itemList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView text_view_title, text_view_magnitude;
        View bar;

        public Holder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            text_view_title = itemView.findViewById(R.id.text_view_title);
            text_view_magnitude = itemView.findViewById(R.id.text_view_magnitude);
            bar = itemView.findViewById((R.id.bar));
        }
    }


}
