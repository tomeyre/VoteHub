package tom.eyre.mp2021.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ExpenseCalculatedResponse implements Serializable {

    private List<ExpenseTypesByYear> expenseTypesByYears = new ArrayList<>();
}
