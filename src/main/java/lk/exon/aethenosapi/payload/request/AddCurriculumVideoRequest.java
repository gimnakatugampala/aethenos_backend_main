package lk.exon.aethenosapi.payload.request;

import lk.exon.aethenosapi.entity.CurriculumItemFile;
import lk.exon.aethenosapi.entity.CurriculumItemFileType;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddCurriculumVideoRequest {
    private SectionCurriculumItem curriculumItem;
    private CurriculumItemFileType curriculumItemFileType;
    private String downloadableFileGeneratedName;
    private String downloadableFileOriginalName;
}
