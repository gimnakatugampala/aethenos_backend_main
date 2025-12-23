package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCountriesResponse {
   private String country;
   private String currency;
}
