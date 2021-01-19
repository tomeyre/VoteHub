package tom.eyre.mp2021.data;

import java.util.List;

import lombok.Data;

@Data
public class ExpensesByYear {
    private String year;
    private List<ExpenseResponse> expenses;
}
