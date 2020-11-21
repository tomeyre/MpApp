package tom.eyre.mpapp.data;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseByYear implements Serializable {

    private String year;
    private List<ExpenseType> expenseTypes;
}
