package tom.eyre.mp2021.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseType implements Serializable {

    private String type;
    private Double totalSpent;
}
