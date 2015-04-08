package com.jooik.tabbeddialog.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;

import org.thezero.wifipv.R;

/**
 * Fragment dialog displaying tab host...
 */
public class FragmentDialog extends DialogFragment
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                getResources().getDimensionPixelSize(R.dimen.dialog_height));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container);

        // tab slider
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager)view.findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
        tabs.setIndicatorColorResource(R.color.accent);
        tabs.setViewPager(viewPager);

        return view;
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    /**
     * Used for tab paging...
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
            {
                // find first fragment...
                Fragment_Tab_1 ft1 = new Fragment_Tab_1();
                return ft1;
            }
            if (position == 1)
            {
                // find first fragment...
                Fragment_Tab_2 ft2 = new Fragment_Tab_2();
                return ft2;
            }
            else if (position == 2)
            {
                // find first fragment...
                Fragment_Tab_3 ft3 = new Fragment_Tab_3();
                return ft3;
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.app_name);
                case 1:
                    return getString(R.string.license);
                case 2:
                    return getString(R.string.me);
            }
            return null;
        }
    }
}