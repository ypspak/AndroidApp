package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by CAI on 17/11/2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    private  String baseUrl;
    public TabsPagerAdapter(FragmentManager fm, String baseUrl) {
        super(fm);
        this.baseUrl = baseUrl;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Top Rated fragment activity
                Bundle temp0 = new Bundle(2);
                temp0.putString("BASE_URL", baseUrl);
                JoinRoomFragment fragment0 = new JoinRoomFragment();
                fragment0.setArguments(temp0);
                return fragment0;
            case 1:
                // Games fragment activity
                Bundle temp1 = new Bundle(2);
                temp1.putString("BASE_URL", baseUrl);
                RoomListFragment fragment1 = new RoomListFragment();
                fragment1.setArguments(temp1);
                return fragment1;
            case 2:
                // Movies fragment activity
                Bundle temp2 = new Bundle(2);
                temp2.putString("BASE_URL", baseUrl);
                CreateRoomFragment fragment2 = new CreateRoomFragment();
                fragment2.setArguments(temp2);
                return fragment2;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
