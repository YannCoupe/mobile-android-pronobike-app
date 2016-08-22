package fr.ycoupe.pronobike.pronostic.bus.out;

import android.os.Parcel;
import android.os.Parcelable;

import fr.ycoupe.pronobike.models.Game;

public class BetOpenedEvent implements Parcelable {

    public Game game;

    public BetOpenedEvent() {
    }

    // =============================================================================================
    // Parcelable Interface

    public final static Creator<BetOpenedEvent> CREATOR = new Creator<BetOpenedEvent>() {
        public BetOpenedEvent createFromParcel(final Parcel in) {
            return new BetOpenedEvent(in);
        }

        public BetOpenedEvent[] newArray(final int size) {
            return new BetOpenedEvent[size];
        }
    };

    public BetOpenedEvent(final Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(game, 0);
    }

    private void readFromParcel(final Parcel in) {
        game = in.readParcelable(Game.class.getClassLoader());
    }
}
