package fr.ycoupe.pronobike.utils;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Utility singleton for posting and receiving events all around the application
 */
public class BusManager {
    private static BusManager instance;

    private final Subject<Object, Object> bus;

    private BusManager() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static BusManager instance() {
        if (instance == null)
            instance = new BusManager();
        return instance;
    }

    /**
     * Send the given event. It will be received by all classes subscribing to the event class.
     *
     * @param o The event to send
     */
    public void send(final Object o) {
        bus.onNext(o);
    }

    /**
     * Create a subscription to a given event.
     *
     * @param eventClass The class of the event to observe.
     * @param onNext     callback to execute when event is received
     * @param <T>        The event
     * @return An RX subscription to the given event.
     */
    public <T> Subscription observe(final Class<T> eventClass, final Action1<T> onNext) {
        return bus.filter(event -> event.getClass().equals(eventClass))
                .map(obj -> (T) obj)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, Throwable::printStackTrace);
    }
}
