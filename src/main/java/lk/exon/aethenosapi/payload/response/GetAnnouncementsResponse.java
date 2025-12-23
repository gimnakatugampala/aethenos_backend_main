package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GetAnnouncementsResponse {
   private String tittle;
   private String content;
   private Date createdDate;

}
