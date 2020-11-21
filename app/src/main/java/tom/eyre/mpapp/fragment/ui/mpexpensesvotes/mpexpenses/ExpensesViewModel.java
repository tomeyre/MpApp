package tom.eyre.mpapp.fragment.ui.mpexpensesvotes.mpexpenses;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExpensesViewModel extends ViewModel {

    private MutableLiveData<String> mpName;
    private MutableLiveData<String> expenseYear;
    private MutableLiveData<Drawable> mpImage;
    private MutableLiveData<String> color;

    public ExpensesViewModel() {
        mpName = new MutableLiveData<>();
        expenseYear = new MutableLiveData<>();
        mpImage = new MutableLiveData<>();
        color = new MutableLiveData<>();
    }

    public LiveData<String> getMpName() {
        return mpName;
    }

    public LiveData<String> getExpenseYear() {
        return expenseYear;
    }

    public LiveData<Drawable> getMpImage() {
        return mpImage;
    }

    public LiveData<String> getTopView(){
        return color;
    }
}