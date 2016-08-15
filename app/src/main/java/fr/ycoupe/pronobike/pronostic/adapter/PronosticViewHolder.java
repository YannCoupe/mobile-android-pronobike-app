package fr.ycoupe.pronobike.pronostic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * {@link RecyclerView.ViewHolder} for the {@link org.json.JSONObject} object inside
 * the {@link RecyclerView}.
 */
public class PronosticViewHolder extends RecyclerView.ViewHolder {

    private final static String TAG = PronosticViewHolder.class.getSimpleName();

    private final View view;

    /**
     * Create the holder from a given view.
     *
     * @param v A view.
     */
    public PronosticViewHolder(final View v) {
        super(v);
        view = v;
    }

    /**
     * Attach a {@link org.json.JSONObject} to this holder.
     *
     * @param object The bound {@link org.json.JSONObject}
     */
    public void bind(final Context context, final JSONObject object) {
        Logger.log(Logger.Level.VERBOSE, TAG, "bind");

        loadCard(context, object);
    }

    /**
     * Load information of {@link JSONObject} into view
     *
     * @param context The context to load image url
     * @param item The object {@link JSONObject}
     */
    private void loadCard(final Context context, final JSONObject item) {
        Logger.log(Logger.Level.VERBOSE, TAG, "loadCard");

        try {
            final TextView name = (TextView) view.findViewById(R.id.pronostic_item_name);
            name.setText(item.getString("firstname") + " " + item.getString("lastname"));

            final TextView pilots = (TextView) view.findViewById(R.id.pronostic_item_pilots);
            pilots.setText(String.format(context.getString(R.string.pronostic_classement),
                    item.getInt("first_number"), item.getString("first_lastname"),
                    item.getInt("second_number"), item.getString("second_lastname"),
                    item.getInt("third_number"), item.getString("third_lastname")));

            final TextView points = (TextView) view.findViewById(R.id.pronostic_item_point);
            points.setText(context.getResources().getQuantityString(R.plurals.pronostic_point, item.getInt("total"), item.getInt("total")));
        } catch(JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e.getMessage());
        }
    }

}
