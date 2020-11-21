package tom.eyre.mpapp.fragment.ui.mppartyquiz.mpselect;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MpSelectViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MpSelectViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}