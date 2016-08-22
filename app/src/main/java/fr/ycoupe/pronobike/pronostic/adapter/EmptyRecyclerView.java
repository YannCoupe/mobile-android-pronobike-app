package fr.ycoupe.pronobike.pronostic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Customisation of {@link RecyclerView} for managing an empty state
 * Inspired by : https://gist.github.com/adelnizamutdinov/31c8f054d1af4588dc5c#file-emptyrecyclerview-java
 */
public class EmptyRecyclerView extends RecyclerView {
    private final static String TAG = EmptyRecyclerView.class.getSimpleName();

    private View emptyView;

    private final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkSize();
        }

        @Override
        public void onItemRangeChanged(final int positionStart, final int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            checkSize();
        }

        @Override
        public void onItemRangeChanged(final int positionStart, final int itemCount, final Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            checkSize();
        }

        @Override
        public void onItemRangeInserted(final int positionStart, final int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkSize();
        }

        @Override
        public void onItemRangeRemoved(final int positionStart, final int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkSize();
        }

        @Override
        public void onItemRangeMoved(final int fromPosition, final int toPosition, final int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            checkSize();
        }
    };

    public EmptyRecyclerView(final Context context) {
        super(context);
    }

    public EmptyRecyclerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(final Adapter adapter) {

        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkSize();
    }

    /**
     * Set the view to be shown when {@link RecyclerView} is empty.
     *
     * @param emptyView The empty view.
     */
    public void setEmptyView(final View emptyView) {
        this.emptyView = emptyView;
    }

    private void checkSize() {

        if (getAdapter() == null) {
            return;
        }

        final int count = getAdapter().getItemCount();

        if(emptyView != null){
            emptyView.setVisibility(count > 0 ? View.GONE : View.VISIBLE);
        }
    }
}
