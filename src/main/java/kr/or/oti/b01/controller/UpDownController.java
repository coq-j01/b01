package kr.or.oti.b01.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import kr.or.oti.b01.dto.upload.UploadFileDTO;
import kr.or.oti.b01.dto.upload.UploadResultDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UpDownController {
	
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	
	
	@ApiOperation(value = "Upload post", notes = "POST 방식으로 파일 등록")
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
		
		log.info("uploadPath = " + uploadPath);
		log.info("upload = " + uploadFileDTO);
		
		if (uploadFileDTO.getFiles() != null) {
			final List<UploadResultDTO> list = new ArrayList<UploadResultDTO>();
			
			for(MultipartFile file : uploadFileDTO.getFiles()) {
				log.info("원본 파일명 : " + file.getOriginalFilename());
				log.info("파일 유형 : " + file.getContentType());
				log.info("파일 사이즈 : " + file.getSize());
			
				list.add(new UploadResultDTO(uploadPath, file));
			}
			return list;
		}
		return null;
	}
	
	@ApiOperation(value = "view 파일", notes = "GET 방식으로 파일 조회")
	@GetMapping(value = "/view/{filename}")
	public ResponseEntity<Resource> viewFileGet(@PathVariable("filename") String filename) {
		//좋은 방법은 아님.
		Resource resource = new FileSystemResource(uploadPath + File.separator + filename);
		HttpHeaders headers = new HttpHeaders();
		String resourceName = resource.getFilename();
		
		try {
			headers.add("Content-type", Files.probeContentType(resource.getFile().toPath()));
			headers.add("Content-Length", String.valueOf(resource.getFile().length()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().headers(headers).body(resource);
	}
	
	@ApiOperation(value = "remove 파일", notes = "DELETE 방식으로 파일 삭제")
	@DeleteMapping(value = "/remove/{filename}")
	public Map<String,Boolean> removeFile(@PathVariable("filename") String filename) {
		Resource resource = new FileSystemResource(uploadPath + File.separator + filename);
		String resourceName = resource.getFilename();
		
		Map<String,Boolean> resultMap = new HashMap<>();
		boolean removed = false;
		try {
			String contentType = Files.probeContentType(resource.getFile().toPath());
			removed = resource.getFile().delete();
			if(contentType.startsWith("image/")) {
				File thumbnailFile = new File(uploadPath + File.separator + "s_"+ filename);
				thumbnailFile.delete();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		resultMap.put("result",removed);
		
		return resultMap;
	}
}
