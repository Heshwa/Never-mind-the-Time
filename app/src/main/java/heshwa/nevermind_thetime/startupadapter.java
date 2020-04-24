package heshwa.nevermind_thetime;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class startupadapter extends FragmentPagerAdapter
{
    public startupadapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:return new startup(R.drawable.startup_one,"Chat and get Connected with your friends");
            case 1:return new startup(R.drawable.st,"Find similar group of friends");
            case 2:return new startup(R.drawable.secure,"Be secure and have safe conversations ");

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
