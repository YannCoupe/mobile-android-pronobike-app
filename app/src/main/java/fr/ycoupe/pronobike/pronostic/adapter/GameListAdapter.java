package fr.ycoupe.pronobike.pronostic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.pronostic.bus.out.GameOpenEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Adapter for the campaigns list RecyclerView.
 */
public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private final static String TAG = GameListAdapter.class.getSimpleName();

    private List<Game> games;

    private Context context;

    public GameListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public GameViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View v = View.inflate(parent.getContext(), R.layout.game_item, null);
        final GameViewHolder holder = new GameViewHolder(v);
        RxView.clicks(v).subscribe(v1 -> onItemClick(holder.getAdapterPosition()));
        return holder;
    }

    @Override
    public void onBindViewHolder(final GameViewHolder holder, final int position) {
        holder.bind(context, getItem(position));
    }

    @Override
    public void onViewRecycled(final GameViewHolder holder) {
        super.onViewRecycled(holder);
    }

    private Game getItem(final int position) {
        if (games == null || games.size() < position) {
            return null;
        }
        return games.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return games == null ? 0 : games.size();
    }

    /**
     * Set the {@link Game} of this adapter.
     *
     * @param items The {@link Game} to display inside RecyclerView
     */
    public void setGames(final List<Game> items) {
        games = items;
        notifyDataSetChanged();
    }

    /**
     * Remove {@link Game} of this list.
     *
     * @param item The {@link Game} to remove
     */
    public void remove(final Game item) {
        Logger.log(Logger.Level.DEBUG, TAG, "remove : " + item.getIdGame());

        int index = -1;

        for(int i = 0; i < games.size(); i++){
            if(games.get(i).getIdGame() == item.getIdGame()){
                index = i;
                break;
            }
        }

        if(index >= 0) {
            games.remove(index);

            notifyDataSetChanged();
        }
    }

    private void onItemClick(final int position){
        Logger.log(Logger.Level.DEBUG, TAG, "onItemClick : " + position);
        final Game game = getItem(position);

        final GameOpenEvent event = new GameOpenEvent();
        event.game = game;
        BusManager.instance().send(event);
    }

}
