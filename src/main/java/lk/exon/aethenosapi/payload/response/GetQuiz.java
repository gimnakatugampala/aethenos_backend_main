package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetQuiz {
    private String id;
    private String question;
    private List<GetAnswer> answers;

}
