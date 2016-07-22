package fr.ycoupe.pronobike.pronostic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.StringUtils;

/**
 * {@link android.support.v7.widget.RecyclerView.ViewHolder} for the {@link fr.ycoupe.pronobike.models.Game} object inside
 * the {@link RecyclerView}.
 */
public class GameViewHolder extends RecyclerView.ViewHolder {

    private final static String TAG = GameViewHolder.class.getSimpleName();

    private final View view;

    private SimpleDateFormat dateFormatter;

    /**
     * Create the holder from a given view.
     *
     * @param v A view.
     */
    public GameViewHolder(final View v) {
        super(v);
        view = v;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
    }

    /**
     * Attach a {@link fr.ycoupe.pronobike.models.Game} to this holder.
     *
     * @param game The bound {@link fr.ycoupe.pronobike.models.Game}
     */
    public void bind(final Context context, final Game game) {
        Logger.log(Logger.Level.VERBOSE, TAG, "bind " + game.getIdGame());

        loadCard(context, game);
    }

    /**
     * Load information of {@link fr.ycoupe.pronobike.models.Game} into view
     *
     * @param context The context to load image url
     * @param item The object {@link fr.ycoupe.pronobike.models.Game}
     */
    private void loadCard(final Context context, final Game item) {
        Logger.log(Logger.Level.VERBOSE, TAG, "loadCard");

        final TextView rank = (TextView) view.findViewById(R.id.game_item_rank);
        rank.setText(String.format(context.getResources().getString(item.getPositionUser() == 1 ? R.string.position_er : R.string.position_eme), (int) item.getPositionUser()));

        final TextView name = (TextView) view.findViewById(R.id.game_item_name);
        name.setText(item.getName());

        final TextView competition = (TextView) view.findViewById(R.id.game_item_competition);
        competition.setText(item.getCompetitionRace());

        final TextView circuit = (TextView) view.findViewById(R.id.game_item_circuit);
        final TextView date = (TextView) view.findViewById(R.id.game_item_date);

        if(!StringUtils.isNullOrEmpty(item.getDateRace()) && !StringUtils.isNullOrEmpty(item.getCircuitRace())){
            Date raceDate = null;
            try {
                raceDate = dateFormatter.parse(item.getDateRace());
            } catch(ParseException e){
                Logger.log(Logger.Level.WARNING, TAG, "ParseException: " + e.getMessage());
            }

            String dateString = "";
            if(raceDate != null){
                dateFormatter = new SimpleDateFormat("EEEE dd MMMM yyyy 'à' HH'h'mm", Locale.FRANCE);
                dateString = String.format("Le %s", dateFormatter.format(raceDate));
            }
            date.setText(dateString);
            circuit.setText(String.format(context.getResources().getString(R.string.prochain_gp), item.getCircuitRace()));
        } else {
            circuit.setText(context.getString(R.string.pas_prochain_gp));
        }
    }

}
