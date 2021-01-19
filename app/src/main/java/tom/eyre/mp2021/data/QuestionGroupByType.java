package tom.eyre.mp2021.data;

import java.util.ArrayList;

import lombok.Data;
import tom.eyre.mp2021.entity.QuestionEntity;

@Data
public class QuestionGroupByType {

    private String type;
    private ArrayList<QuestionEntity> questions;
}
