package fr.ycoupe.pronobike.pronostic.bus.out;

import android.os.Parcel;
import android.os.Parcelable;

import fr.ycoupe.pronobike.models.Game;

public class GameCloseEvent implements Parcelable {

    public Game game;

    public GameCloseEvent() {
    }

    // =============================================================================================
    // Parcelable Interface

    public final static Creator<GameCloseEvent> CREATOR = new Creator<GameCloseEvent>() {
        public GameCloseEvent createFromParcel(final Parcel in) {
            return new GameCloseEvent(in);
        }

        public GameCloseEvent[] newArray(final int size) {
            return new GameCloseEvent[size];
        }
    };

    public GameCloseEvent(final Parcel in) {
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
