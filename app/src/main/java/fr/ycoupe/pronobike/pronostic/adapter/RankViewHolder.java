package fr.ycoupe.pronobike.pronostic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.authentication.service.ProfileManager;
import fr.ycoupe.pronobike.models.RankGame;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * {@link RecyclerView.ViewHolder} for the {@link fr.ycoupe.pronobike.models.RankGame} object inside
 * the {@link RecyclerView}.
 */
public class RankViewHolder extends RecyclerView.ViewHolder {

    private final static String TAG = RankViewHolder.class.getSimpleName();

    private final View view;

    private SimpleDateFormat dateFormatter;

    /**
     * Create the holder from a given view.
     *
     * @param v A view.
     */
    public RankViewHolder(final View v) {
        super(v);
        view = v;
    }

    /**
     * Attach a {@link RankGame} to this holder.
     *
     * @param rankGame The bound {@link RankGame}
     */
    public void bind(final Context context, final RankGame rankGame) {
        Logger.log(Logger.Level.VERBOSE, TAG, "bind");

        loadRank(context, rankGame);
    }

    /**
     * Load information of {@link RankGame} into view
     *
     * @param context The context to load image url
     * @param item The object {@link RankGame}
     */
    private void loadRank(final Context context, final RankGame item) {
        Logger.log(Logger.Level.VERBOSE, TAG, "loadRank");

        final RelativeLayout background = (RelativeLayout) view.findViewById(R.id.rank_item);
        background.setBackgroundResource(item.getUserId() == ProfileManager.instance().profile.getIdUser() ? R.color.swiss_gray : android.R.color.white);

        final TextView position = (TextView) view.findViewById(R.id.rank_item_position);
        position.setText(String.valueOf(item.getPosition()));

        final TextView name = (TextView) view.findViewById(R.id.rank_item_name);
        name.setText(item.getFirstname() + " " + item.getLastname());

        final TextView details = (TextView) view.findViewById(R.id.rank_item_details);
        details.setText(String.format("%d / %d / %d", (int) item.getFirst(), (int) item.getSecond(), (int) item.getThird()));

        final TextView total = (TextView) view.findViewById(R.id.rank_item_total);
        total.setText(String.valueOf(item.getTotal()));
    }
}
