package tom.eyre.mpapp.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseType {

    private String type;
    private Double totalSpent;
}
