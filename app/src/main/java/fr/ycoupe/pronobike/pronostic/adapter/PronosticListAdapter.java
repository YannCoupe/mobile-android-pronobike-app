package fr.ycoupe.pronobike.pronostic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.ycoupe.pronobike.R;

/**
 * Adapter for the campaigns list RecyclerView.
 */
public class PronosticListAdapter extends RecyclerView.Adapter<PronosticViewHolder> {

    private JSONArray pronostics;
    private int[] ranks;

    private Context context;

    public PronosticListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public PronosticViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View v = View.inflate(parent.getContext(), R.layout.pronostic_item, null);
        final PronosticViewHolder holder = new PronosticViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PronosticViewHolder holder, final int position) {
        holder.bind(context, getItem(position), ranks);
    }

    @Override
    public void onViewRecycled(final PronosticViewHolder holder) {
        super.onViewRecycled(holder);
    }

    private JSONObject getItem(final int position) {
        if (pronostics == null || pronostics.length() < position) {
            return null;
        }
        try {
            return pronostics.getJSONObject(position);
        } catch(JSONException e){
            return null;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pronostics == null ? 0 : pronostics.length();
    }

    public void setPronostics(final JSONArray array, final int [] rankArray) {
        pronostics = array;
        ranks = rankArray;
        notifyDataSetChanged();
    }
}
