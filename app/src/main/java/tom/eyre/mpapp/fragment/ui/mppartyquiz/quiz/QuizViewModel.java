package tom.eyre.mpapp.fragment.ui.mppartyquiz.quiz;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuizViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QuizViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}