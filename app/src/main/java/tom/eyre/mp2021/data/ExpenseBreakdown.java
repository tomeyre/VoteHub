package tom.eyre.mp2021.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseBreakdown {

    private String name;
    private Double total;
}
