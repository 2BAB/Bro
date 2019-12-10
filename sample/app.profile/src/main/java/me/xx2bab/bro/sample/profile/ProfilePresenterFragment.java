package me.xx2bab.bro.sample.profile;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.xx2bab.bro.sample.common.mine.IMinePresenter;

public class ProfilePresenterFragment extends Fragment implements IMinePresenter {

    private int count = 0;
    private TextView countView;

    public static IMinePresenter newInstance(Bundle args) {
        ProfilePresenterFragment fragment = new ProfilePresenterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        countView = view.findViewById(R.id.mine_count);
        countView.setText(String.valueOf(count));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void updateCount() {
        count++;
        if (countView != null) {
            countView.setText(String.valueOf(count));
        }
    }
}
