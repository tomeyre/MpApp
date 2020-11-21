package tom.eyre.mpapp.fragment.ui.mpexpensesvotes.mpdetails;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MpDetailsViewModel extends ViewModel {

    private MutableLiveData<String> name;
    private MutableLiveData<String> mpParty;
    private MutableLiveData<String> mpFor;
    private MutableLiveData<String> mpAge;
    private MutableLiveData<String> bio;
    private MutableLiveData<String> mpHomePage;
    private MutableLiveData<String> twitterUrl;
    private MutableLiveData<String> color;
    private MutableLiveData<Drawable> drawable;

    public MpDetailsViewModel() {
        name = new MutableLiveData<>();
        mpParty = new MutableLiveData<>();
        mpFor = new MutableLiveData<>();
        mpAge = new MutableLiveData<>();
        bio = new MutableLiveData<>();
        mpHomePage = new MutableLiveData<>();
        twitterUrl = new MutableLiveData<>();
        color = new MutableLiveData<>();
        drawable = new MutableLiveData<>();
    }

    public LiveData<Drawable> getDrawable() {return drawable; }

    public LiveData<String> getName(){
        return name;
    }

    public LiveData<String> getMpParty(){
        return mpParty;
    }

    public LiveData<String> getMpFor(){
        return mpFor;
    }

    public LiveData<String> getMpAge(){
        return mpAge;
    }

    public LiveData<String> getBio(){
        return bio;
    }

    public LiveData<String> getMpHomePage(){
        return mpHomePage;
    }

    public LiveData<String> getTopView(){
        return color;
    }

    public LiveData<String> getTwitterUrl(){
        return twitterUrl;
    }

}