package tom.eyre.mpapp.fragment.ui.mppartyquiz.partyselect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import tom.eyre.mpapp.R;
import tom.eyre.mpapp.entity.MpEntity;

public class PartySelectFragment extends Fragment {

    private PartySelectViewModel partySelectViewModel;
    private MpEntity mp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        mp = (MpEntity) getArguments().getSerializable("mp");
        partySelectViewModel =
                ViewModelProviders.of(this).get(PartySelectViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        partySelectViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}