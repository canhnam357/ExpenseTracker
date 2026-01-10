package xyz.erotskoob.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String email;
    private String username;
    private String token;
    private String type; // EMAIL_VERIFICATION, PASSWORD_RESET, etc.
}