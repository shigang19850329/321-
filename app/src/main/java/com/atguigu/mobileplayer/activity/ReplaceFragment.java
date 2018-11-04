package com.atguigu.mobileplayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atguigu.mobileplayer.base.BasePager;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class ReplaceFragment extends Fragment {
    private BasePager currPager;
   // public ReplaceFragment(){}
//    public static ReplaceFragment getInstance(BasePager currPager){
//            ReplaceFragment replaceFragment = new ReplaceFragment();
//           replaceFragment.currPager = currPager;
//        return replaceFragment;
//    }
    public ReplaceFragment(BasePager currPager) {
        this.currPager = currPager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return currPager.rootView;
    }
}
