package tom.eyre.mpapp.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ExpenseCalculatedResponse {

    private List<ExpenseByYear> expenseByYears = new ArrayList<>();
}
