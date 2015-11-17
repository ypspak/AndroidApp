package hk.ust.cse.hunkim.questionroom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by CAI on 17/11/2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Top Rated fragment activity
                return new JoinRoomFragment();
            case 1:
                // Games fragment activity
                return new RoomListFragment();
            case 2:
                // Movies fragment activity
                return new CreateRoomFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
