package rin.crecovery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FragmentInteractionListener,
        MainFragment.MainFragInteractionListener, NotesFragment.NotesFragInteractionListener,
        StopwatchFragment.StopwatchFragInteractionListener {

    private ActivityInteractionListener mListener;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent(this, IntroActivity.class);
        startActivity(i);

        SharedPreferences prefs;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);

                Fragment fragment = ((ViewPagerAdapter) viewPager.getAdapter())
                        .getItem(tab.getPosition());
                if (fragment instanceof ActivityInteractionListener) {
                    mListener = (ActivityInteractionListener) fragment;
                }

                if (fragment instanceof MainFragment)
                    onChangeFabIcon(R.drawable.ic_done_all_white);
                else if (fragment instanceof NotesFragment)
                    onChangeFabIcon(R.drawable.ic_save_white_24dp);
                else if (fragment instanceof  StopwatchFragment)
                    onChangeFabIcon(R.drawable.ic_timer_white_24dp);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onButtonPressed();
                }
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.onLongButtonPressed();
                    return true;
                }

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_bluetooth:
                mListener.bluetooth();
                return true;
            /*case R.id.action_ledon:
                ledOn();
                return true;
            case R.id.action_ledoff:
                ledOff();
                return true;*/
            case R.id.action_help:
                Intent i = new Intent(this, HelpActivity.class);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        MainFragment mainFragment = MainFragment.newInstance();
        mListener = mainFragment;

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mainFragment, getString(R.string.tab_one));
        adapter.addFragment(NotesFragment.newInstance(), getString(R.string.tab_two));
        adapter.addFragment(StopwatchFragment.newInstance(), getString(R.string.tab_three));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCreateSnackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .show();
    }

    public void onChangeFabIcon(int resource) {
        if (fab != null)
            fab.setImageDrawable(getResources().getDrawable(resource, null));
    }

    /*private void ledOff() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("TF".getBytes());
            } catch (IOException e) {
                onCreateSnackbar(getCurrentFocus(), "Error connecting to device.");
                e.printStackTrace();
            }
        }
    }

    private void ledOn() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("TO".getBytes());
            } catch (IOException e) {
                onCreateSnackbar(getCurrentFocus(), "Error connecting to device.");
                e.printStackTrace();
            }
        }
    }*/

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

    }

}
