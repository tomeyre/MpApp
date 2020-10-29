package tom.eyre.mpapp.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseByYear {

    private String year;
    private List<ExpenseType> expenseTypes;
}
