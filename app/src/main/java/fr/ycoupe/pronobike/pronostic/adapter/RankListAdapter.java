package fr.ycoupe.pronobike.pronostic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.RankGame;

/**
 * Adapter for the campaigns list RecyclerView.
 */
public class RankListAdapter extends RecyclerView.Adapter<RankViewHolder> {

    private final static String TAG = RankListAdapter.class.getSimpleName();

    private List<RankGame> ranks;

    private Context context;

    public RankListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RankViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View v = View.inflate(parent.getContext(), R.layout.rank_item, null);
        final RankViewHolder holder = new RankViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RankViewHolder holder, final int position) {
        holder.bind(context, getItem(position));
    }

    @Override
    public void onViewRecycled(final RankViewHolder holder) {
        super.onViewRecycled(holder);
    }

    private RankGame getItem(final int position) {
        if (ranks == null || ranks.size() < position) {
            return null;
        }
        return ranks.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ranks == null ? 0 : ranks.size();
    }

    /**
     * Set the {@link RankGame} of this adapter.
     *
     * @param items The {@link RankGame} to display inside RecyclerView
     */
    public void setRanks(final List<RankGame> items) {
        ranks = items;
        notifyDataSetChanged();
    }
}
