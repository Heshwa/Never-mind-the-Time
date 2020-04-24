package heshwa.nevermind_thetime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class HomeAdapter extends FragmentPagerAdapter
{
    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;
    public HomeAdapter(@NonNull FragmentManager fm)
    {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
//        switch (position)
//        {
//            case 0:return new ChaterHome();
//            //case 1:return new FeedHome();
//            //case 2:return new startup(R.drawable.ic_launcher_foreground,"Show ur skills,and present status");
//
//        }
//        return null;
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
//        switch (position)
//        {
//            //case 0:return "Chat Home";
//            //case 1:return "Feed Home";
//
//        }
//        return null;
        return titles.get(position);
    }
    public void addFragment(Fragment fragment, String title){
        fragments.add(fragment);
        titles.add(title);
    }

}
