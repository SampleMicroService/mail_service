package micro.service.mail_service.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeMailRequestDto {
	
	private String mailCode;
	
	private String toMail;
	
	private Map<String,String> placeHolder;
	
}
