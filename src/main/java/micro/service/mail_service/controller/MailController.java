package micro.service.mail_service.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import micro.service.mail_service.dto.EmailRequestDto;
import micro.service.mail_service.service.MailService;

@RestController
@RequestMapping("/mail")
public class MailController {
	
	@Autowired
	private MailService service;
	
	@PostMapping("/send")
	public ResponseEntity<String> sendMail(@RequestBody EmailRequestDto request){
		try {
			return ResponseEntity.ok(service.sendMail(request));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().build();
	}
	
	@PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> sendResume(@RequestPart("data") String req,@RequestPart("file") MultipartFile file) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			EmailRequestDto request = objectMapper.readValue(req, EmailRequestDto.class);

	        File tempFile = File.createTempFile("resume_", "_" + file.getOriginalFilename());
			file.transferTo(tempFile);
			request.setFile(tempFile);
			return ResponseEntity.ok(service.sendMail(request));
		} catch (MessagingException | IOException e ) {
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().build();
	}
}
