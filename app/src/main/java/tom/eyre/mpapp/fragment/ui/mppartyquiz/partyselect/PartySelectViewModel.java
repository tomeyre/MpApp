package tom.eyre.mpapp.fragment.ui.mppartyquiz.partyselect;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PartySelectViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PartySelectViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Coming Soon!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}