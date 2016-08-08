package fr.ycoupe.pronobike.pronostic.bus.out;

import android.os.Parcel;
import android.os.Parcelable;

import fr.ycoupe.pronobike.models.Game;

public class GameOpenEvent implements Parcelable {

    public Game game;

    public GameOpenEvent() {
    }

    // =============================================================================================
    // Parcelable Interface

    public final static Creator<GameOpenEvent> CREATOR = new Creator<GameOpenEvent>() {
        public GameOpenEvent createFromParcel(final Parcel in) {
            return new GameOpenEvent(in);
        }

        public GameOpenEvent[] newArray(final int size) {
            return new GameOpenEvent[size];
        }
    };

    public GameOpenEvent(final Parcel in) {
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
