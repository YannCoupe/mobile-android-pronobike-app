package fr.ycoupe.pronobike.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.adapter.ViewPagerAdapter;
import fr.ycoupe.pronobike.authentication.service.ProfileManager;
import fr.ycoupe.pronobike.profile.ProfileFragment;
import fr.ycoupe.pronobike.pronostic.GameFragment;
import fr.ycoupe.pronobike.pronostic.RankFragment;
import fr.ycoupe.pronobike.pronostic.bus.out.GameOpenEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    public final static String TAG_PROFILE = ProfileFragment.TAG;
    public final static String TAG_GAME = GameFragment.TAG;
    public final static String TAG_RANK = RankFragment.TAG;

    private int profileTabPosition;
    private int gameTabPosition;
    private int rankTabPosition;

    private ViewPagerAdapter adapter;

    @BindView(R.id.main_viewpager)
    ViewPager viewPager;
    @BindView(R.id.main_navbar_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.main_fab_menu)
    FloatingActionsMenu fabMenu;
    @BindView(R.id.main_fab_create)
    FloatingActionButton fabCreate;
    @BindView(R.id.main_fab_join)
    FloatingActionButton fabJoin;
    @BindView(R.id.main_fab_rules)
    FloatingActionButton fabRules;

    private RankFragment rankFragment;
    private GameFragment gameFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        // View Pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Handling configuration changes (language, etc..)
        if(savedInstanceState == null){
            addFragments();
        } else {
            profileFragment = (ProfileFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_PROFILE);
            gameFragment = (GameFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_GAME);
            rankFragment = (RankFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_RANK);
        }

        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setAdapter(adapter);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.main_margin));

        viewPager.setPageMarginDrawable(R.color.gray);

        // Tab Layout
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);
        addTabs();

        tabLayout.getTabAt(gameTabPosition).getCustomView().setSelected(true);
        viewPager.setCurrentItem(gameTabPosition);

        viewPager.addOnPageChangeListener(this);

        subscriptions.add(BusManager.instance().observe(GameOpenEvent.class, this::onGameOpened));

        RxView.clicks(fabRules).subscribe(next -> rules());
        RxView.clicks(fabJoin).subscribe(next -> join());
        RxView.clicks(fabCreate).subscribe(next -> create());

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (fabMenu.isExpanded()) {

                Rect outRect = new Rect();
                fabMenu.getGlobalVisibleRect(outRect);

                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    fabMenu.collapse();
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private void addTabs() {
        Logger.log(Logger.Level.DEBUG, TAG, "addTabs");

        // Game tab
        final View gameTabView = LayoutInflater.from(this).inflate(R.layout.tabview_game, tabLayout, false);
        ((ImageView) gameTabView.findViewById(R.id.tabview_icon)).setImageResource(adapter.getItemIcon(gameTabPosition));
        tabLayout.getTabAt(gameTabPosition).setCustomView(gameTabView);

        // Rank tab
        final View rankTabView = LayoutInflater.from(this).inflate(R.layout.tabview_rank, tabLayout, false);
        ((ImageView) rankTabView.findViewById(R.id.tabview_icon)).setImageResource(adapter.getItemIcon(rankTabPosition));
        tabLayout.getTabAt(rankTabPosition).setCustomView(rankTabView);

        // Profile tab
        final View profileTabView = LayoutInflater.from(this).inflate(R.layout.tabview_profile, tabLayout, false);
        ((ImageView) profileTabView.findViewById(R.id.tabview_icon)).setImageResource(adapter.getItemIcon(profileTabPosition));
        tabLayout.getTabAt(profileTabPosition).setCustomView(profileTabView);
    }

    private void addFragments() {
        Logger.log(Logger.Level.DEBUG, TAG, "addFragments");

        // Game fragment
        gameFragment = new GameFragment();
        gameTabPosition = adapter.addFragment(gameFragment, R.drawable.tab_game);

        // Rank fragment
        rankFragment = new RankFragment();
        rankTabPosition = adapter.addFragment(rankFragment, R.drawable.tab_rank);

        // Profile fragment
        profileFragment = new ProfileFragment();
        profileTabPosition = adapter.addFragment(profileFragment, R.drawable.tab_profile);
    }

    private void setProfileVisible(final boolean isVisible) {
        Logger.log(Logger.Level.DEBUG, TAG, "setProfileVisible " + isVisible);

        profileFragment.setVisible(isVisible);
    }

    private void setRankVisible(final boolean isVisible) {
        Logger.log(Logger.Level.DEBUG, TAG, "setRankVisible " + isVisible);
    }

    private void setGameVisible(final boolean isVisible) {
        Logger.log(Logger.Level.DEBUG, TAG, "setGameVisible " + isVisible);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.log(Logger.Level.DEBUG, TAG, "onResume");
        updatePage(tabLayout.getSelectedTabPosition());

        if (ProfileManager.instance() == null) {
            Logger.log(Logger.Level.WARNING, TAG, "Profile instance is null");
            logout();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.log(Logger.Level.DEBUG, TAG, "onPause");

        updatePage(TabLayout.Tab.INVALID_POSITION);
    }

    // =============================================================================================
    // Page selection

    @Override
    public void onPageSelected(final int position) {
        Logger.log(Logger.Level.DEBUG, TAG, "onPageSelected " + position);
        updatePage(position);
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        Logger.log(Logger.Level.VERBOSE, TAG, "onPageScrollStateChanged " + state);
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
        Logger.log(Logger.Level.VERBOSE, TAG, "onPageScrolled " + position);
    }

    @Override
    public void onTabSelected(final TabLayout.Tab tab) {
        Logger.log(Logger.Level.DEBUG, TAG, "onTabSelected");

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(final TabLayout.Tab tab) {
        Logger.log(Logger.Level.DEBUG, TAG, "onTabUnselected");
    }

    @Override
    public void onTabReselected(final TabLayout.Tab tab) {
        Logger.log(Logger.Level.DEBUG, TAG, "onTabReselected");
    }

    // =============================================================================================
    // Game events

    private void onGameOpened(final GameOpenEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onConversationOpened");

        final Intent intent = new Intent(this, GameDetailActivity.class);
        intent.putExtra(GameDetailActivity.GAME_EVENT_EXTRA, event);
        startActivity(intent);
    }

    // =============================================================================================
    // Current page visibility

    private void updatePage(final int position) {
        Logger.log(Logger.Level.DEBUG, TAG, "updatePage " + position);
        setProfileVisible(position == profileTabPosition);
        setRankVisible(position == rankTabPosition);
        setGameVisible(position == gameTabPosition);
    }

    // =============================================================================================
    // Fab actions

    private void rules(){
        final Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }

    private void join(){
        final Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }

    private void create(){
        final Intent intent = new Intent(this, GameCreateActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, TAG_PROFILE, profileFragment);
        getSupportFragmentManager().putFragment(outState, TAG_GAME, gameFragment);
        getSupportFragmentManager().putFragment(outState, TAG_RANK, rankFragment);
    }

    private void logout() {
        Logger.log(Logger.Level.DEBUG, TAG, "logout");

        ProfileManager.reset();

        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logout();
    }
}
