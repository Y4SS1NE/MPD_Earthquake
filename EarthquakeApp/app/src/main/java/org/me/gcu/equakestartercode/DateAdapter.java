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

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.Holder> {
    Context context;
    List<Item> itemList;
    int highestIndex;
    int lowestIndex;
    int highestLatitudeId, lowestLatitudeId, highestLongitudeId, lowestLongitudeId;

/*
    Declare all the variables needed to sort the data by Magnitude, Depth, Shallowness etc..
 */
    public DateAdapter(Context context, List<Item> itemList, int highestIndex, int lowestIndex, int highestLatitudeId, int lowestLatitudeId, int highestLongitudeId, int lowestLongitudeId) {
        this.context = context;
        this.itemList = itemList;
        this.highestIndex = highestIndex;
        this.lowestIndex = lowestIndex;
        this.highestLatitudeId = highestLatitudeId;
        this.lowestLatitudeId = lowestLatitudeId;
        this.highestLongitudeId = highestLongitudeId;
        this.lowestLongitudeId = lowestLongitudeId;
    }
    /*
        onCreateViewHolder creates a new View Holder for the recyclerView,
        this runs whenever a new ViewHolder is required
    */
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_filter, parent, false);
        return new Holder(view);
    }
    /*
        ViewHolder setting, a Location and Magnitude with a colored bar/View that is
        color coded to the magnitude of the earthquake.
        It also Finds the earthquake with the most depth, shallow, most northenly, southerly, westerly and easterly.
        It also sorts these results by Magnitude.
        Redirect to detailed activity on ViewHolder click
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.textViewMagnitude.setText("Magnitude: " + itemList.get(position).getMagnitude());
        holder.textViewArea.setText("Location: " + itemList.get(position).getLocation());

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

        if (itemList.get(position).getId() == lowestIndex) {
            holder.textViewLabel.setVisibility(View.VISIBLE);
            holder.textViewLabel.setText("Shallowest: " + itemList.get(position).getDepth() + " km");
        } else if (itemList.get(position).getId() == highestIndex) {
            holder.textViewLabel.setVisibility(View.VISIBLE);
            holder.textViewLabel.setText("Deepest: " + itemList.get(position).getDepth() + " km");
        } else {
            holder.textViewLabel.setVisibility(View.GONE);
        }

        if (itemList.get(position).getId() == highestLatitudeId) {
            holder.textViewLabel2.setVisibility(View.VISIBLE);
            holder.textViewLabel2.setText("Most Northerly");
        } else if (itemList.get(position).getId() == lowestLatitudeId) {
            holder.textViewLabel2.setVisibility(View.VISIBLE);
            holder.textViewLabel2.setText("Most Southerly");
        } else if (itemList.get(position).getId() == highestLongitudeId) {
            holder.textViewLabel2.setVisibility(View.VISIBLE);
            holder.textViewLabel2.setText("Most Easterly");
        } else if (itemList.get(position).getId() == lowestLongitudeId) {
            holder.textViewLabel2.setVisibility(View.VISIBLE);
            holder.textViewLabel2.setText("Most Westerly");
        } else {
            holder.textViewLabel2.setVisibility(View.GONE);
        }

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

    public void reloadData(int highestIndex, int lowestIndex, int highestLatitudeId, int lowestLatitudeId, int highestLongitudeId, int lowestLongitudeId) {
        this.highestIndex = highestIndex;
        this.lowestIndex = lowestIndex;
        this.highestLatitudeId = highestLatitudeId;
        this.lowestLatitudeId = lowestLatitudeId;
        this.highestLongitudeId = highestLongitudeId;
        this.lowestLongitudeId = lowestLongitudeId;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textViewMagnitude, textViewLabel, textViewLabel2, textViewArea;
        View bar;

        public Holder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            textViewMagnitude = itemView.findViewById(R.id.text_view_magnitude);
            textViewLabel = itemView.findViewById(R.id.text_view_label);
            textViewLabel2 = itemView.findViewById(R.id.text_view_label2);
            textViewArea = itemView.findViewById(R.id.text_view_area);
            bar = itemView.findViewById((R.id.bar));

        }
    }
}
