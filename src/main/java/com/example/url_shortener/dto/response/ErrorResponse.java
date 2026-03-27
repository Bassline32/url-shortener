package com.example.url_shortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    String message;
}
//  public String getMessage() {
//   return message;
// }