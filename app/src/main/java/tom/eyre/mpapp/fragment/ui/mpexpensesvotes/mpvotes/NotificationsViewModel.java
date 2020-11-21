package tom.eyre.mpapp.fragment.ui.mpexpensesvotes.mpvotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Coming Soon!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}