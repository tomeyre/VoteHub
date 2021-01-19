package tom.eyre.mp2021.data;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseTypesByYear implements Serializable {

    private String year;
    private List<ExpenseType> expenseTypes;
}
