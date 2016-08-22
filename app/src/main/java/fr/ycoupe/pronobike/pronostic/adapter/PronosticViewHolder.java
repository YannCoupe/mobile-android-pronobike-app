package fr.ycoupe.pronobike.pronostic.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
    public void bind(final Context context, final JSONObject object, final int [] ranks) {
        Logger.log(Logger.Level.VERBOSE, TAG, "bind");

        loadCard(context, object, ranks);
    }

    /**
     * Load information of {@link JSONObject} into view
     *
     * @param context The context to load image url
     * @param item The object {@link JSONObject}
     */
    private void loadCard(final Context context, final JSONObject item, final int [] ranks) {
        Logger.log(Logger.Level.VERBOSE, TAG, "loadCard");

        try {
            final TextView name = (TextView) view.findViewById(R.id.pronostic_item_name);
            name.setText(item.getString("firstname") + " " + item.getString("lastname"));

            final TextView pilots = (TextView) view.findViewById(R.id.pronostic_item_pilots);

            final String first = String.format("%d %s", item.getInt("first_number"), item.getString("first_lastname"));
            final String second = String.format("%d %s", item.getInt("second_number"), item.getString("second_lastname"));
            final String third = String.format("%d %s", item.getInt("third_number"), item.getString("third_lastname"));
            final String base = String.format(context.getString(R.string.pronostic_classement), first, second, third);

            final SpannableStringBuilder rank = new SpannableStringBuilder(base);
            rank.setSpan(new ForegroundColorSpan(context.getResources().getColor(ranks[0] == item.getInt("first_number") ? R.color.red : R.color.gray)), base.indexOf(first), base.indexOf(first) + first.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            rank.setSpan(new StyleSpan(ranks[0] == item.getInt("first_number") ? Typeface.BOLD : Typeface.NORMAL), base.indexOf(first), base.indexOf(first) + first.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            rank.setSpan(new ForegroundColorSpan(context.getResources().getColor(ranks[1] == item.getInt("second_number") ? R.color.red : R.color.gray)), base.indexOf(second), base.indexOf(second) + second.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            rank.setSpan(new StyleSpan(ranks[1] == item.getInt("second_number") ? Typeface.BOLD : Typeface.NORMAL), base.indexOf(second), base.indexOf(second) + second.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            rank.setSpan(new ForegroundColorSpan(context.getResources().getColor(ranks[2] == item.getInt("third_number") ? R.color.red : R.color.gray)), base.indexOf(third), base.indexOf(third) + third.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            rank.setSpan(new StyleSpan(ranks[2] == item.getInt("third_number") ? Typeface.BOLD : Typeface.NORMAL), base.indexOf(third), base.indexOf(third) + third.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            pilots.setText(rank);

            final TextView points = (TextView) view.findViewById(R.id.pronostic_item_point);
            points.setText(context.getResources().getQuantityString(R.plurals.pronostic_point, item.getInt("total"), item.getInt("total")));
        } catch(JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e.getMessage());
        }
    }

}
