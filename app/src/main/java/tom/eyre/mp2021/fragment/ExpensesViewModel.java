package tom.eyre.mp2021.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExpensesViewModel extends ViewModel {

    private MutableLiveData<String> years;
    private MutableLiveData<String> pay;

    public ExpensesViewModel() {
        years = new MutableLiveData<>();
        pay = new MutableLiveData<>();
    }

    public LiveData<String> getYears() {
        return years;
    }
    public LiveData<String> getPay() {
        return pay;
    }
}