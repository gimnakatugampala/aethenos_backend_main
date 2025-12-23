package lk.exon.aethenosapi.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Created by Intellij.
 * Author: abhis
 * Date: 01/09/2021
 * Time: 9:18 am
 */
@Data
@JsonPropertyOrder({
        "status",
        "message"
})
public class ApiResponse implements Serializable {
    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("message")
    private String message;

    @JsonIgnore
    private HttpStatus httpStatus;

    public ApiResponse() {

    }

    public ApiResponse(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse(Boolean status, String message, HttpStatus httpStatus) {
        this.status = status;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
